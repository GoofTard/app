package com.example.balancemanager.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TableLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.balancemanager.R;
import com.example.balancemanager.TableLayoutHandlers.ActionsTableHandler;
import com.example.balancemanager.models.GlobalAppData;

import java.util.Arrays;

public class ActionsActivity extends AppCompatActivity {

    private TableLayout tlActions;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);

        tlActions = findViewById(R.id.tlActions);
        ImageButton btnAdd = findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(v -> {
            Intent switchToEditActivity = new Intent(this, AddActionActivity.class);
            startActivityForResult(switchToEditActivity, 12);
        });

        initTlActions();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initTlActions() {
        ActionsTableHandler actionsTableHandler = new ActionsTableHandler(this,
                tlActions,
                GlobalAppData.instance(this).getFavoriteActions(),
                Arrays.asList("Name", "Actions"));
        actionsTableHandler.refill();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        setResult(Activity.RESULT_OK, new Intent());
        finish();
    }
}