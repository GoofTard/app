package com.example.balancemanager.TableLayoutHandlers;

import static com.example.balancemanager.enums.TransactionType.ADD;
import static com.example.balancemanager.enums.TransactionType.USE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.balancemanager.Activities.AddActionActivity;
import com.example.balancemanager.R;
import com.example.balancemanager.enums.TransactionType;
import com.example.balancemanager.models.Action;
import com.example.balancemanager.models.GlobalAppData;

import java.util.List;
import java.util.Map;

public class ActionsTableHandler extends TableLayoutHandler<Action> {

    public ActionsTableHandler(Activity activity, TableLayout tableLayout, List<Action> items, List<String> titles) {
        super(activity, tableLayout, items, titles);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected View generate(Action item) {
        LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.actions_layout, null);

        TextView tvName = view.findViewById(R.id.tvName);
        ImageButton ibActivate = view.findViewById(R.id.btnActivate);
        ImageButton ibRemove = view.findViewById(R.id.btnRemove);
        ImageButton ibEdit = view.findViewById(R.id.btnEdit);

        tvName.setText(item.getName());

        ibRemove.setOnClickListener(v -> {
            GlobalAppData.instance(activity).removeAction(activity, item);
            activity.setResult(Activity.RESULT_OK, new Intent());
            activity.finish();
        });

        ibEdit.setOnClickListener(v -> {
            Intent switchToEditActivity = new Intent(activity, AddActionActivity.class);

            switchToEditActivity.putExtra("action", item);
            activity.startActivityForResult(switchToEditActivity, 2);
        });

        ibActivate.setOnClickListener(v -> {
            use(item);

            activity.setResult(Activity.RESULT_OK, new Intent());
            activity.finish();
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void use(Action action) {
        final Map<TransactionType, Runnable> types = Map.of(
                USE, () -> GlobalAppData.instance(activity)
                        .useFunds(activity, action.getFunds(), action.getCategoryName(), action.getMessage()),
                ADD, () -> GlobalAppData.instance(activity)
                        .addFunds(activity, action.getFunds(), action.getCategoryName(), action.getMessage())
        );

        TransactionType type = action.getType();

        types.get(type).run();
    }
}
