package com.example.balancemanager.TableLayoutHandlers;

import static android.content.Context.MODE_PRIVATE;
import static com.example.balancemanager.utils.StringUtils.addPostfix;
import static com.example.balancemanager.utils.StringUtils.formatPrice;
import static com.example.balancemanager.utils.StringUtils.getLimitString;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.balancemanager.Activities.CategoryActivity;
import com.example.balancemanager.R;
import com.example.balancemanager.models.Category;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CategoriesTableHandler extends SortableTableLayoutHandler<Category> {
    private static final Map<String, Comparator<Category>> COMPARATOR_MAP = Map.of(
            "Category", (cat1, cat2) -> cat1.getName().compareToIgnoreCase(cat2.getName()),
            "Funds", (cat1, cat2) -> Float.compare(cat1.getFunds(), cat2.getFunds()),
            "Percentage", (cat1, cat2) -> Float.compare(cat1.getSplit(), cat2.getSplit()),
            "Locked", (cat1, cat2) -> cat1.isLocked() && !cat2.isLocked() ? 1 : cat1.isLocked() && cat2.isLocked() ? 0 : -1,
            "Limit", (cat1, cat2) -> Float.compare(cat1.getLimit(), cat2.getLimit())
    );

    public CategoriesTableHandler(Activity activity, TableLayout tableLayout, List<Category> items, List<String> titles) {
        super(activity,
                tableLayout,
                items,
                titles,
                COMPARATOR_MAP,
                activity.getSharedPreferences("SORT", MODE_PRIVATE).getString("sortBy", "Category"),
                activity.getSharedPreferences("SORT", MODE_PRIVATE).getInt("sortIndex", 1));
    }

    @Override
    protected View generate(Category item) {
        LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.category_item_layout, null);

        TextView tvCategory = view.findViewById(R.id.tvCategory);
        TextView tvFunds = view.findViewById(R.id.tvFunds);
        TextView tvLimit = view.findViewById(R.id.tvLimit);
        TextView tvSplit = view.findViewById(R.id.tvSplit);
        ImageView ivLocked = view.findViewById(R.id.ivLocked);

        String nameString = item.getName();
        String fundsString = formatPrice(item.getFunds());
        String limitString = getLimitString(item.getLimit(), item.getFunds());
        String splitString = addPostfix("%", String.valueOf(item.getSplit()));
        Drawable lockedDrawable = item.isLocked() ?
                ContextCompat.getDrawable(activity, R.drawable.baseline_check_24) :
                ContextCompat.getDrawable(activity, R.drawable.baseline_clear_24);

        tvCategory.setText(nameString);
        tvFunds.setText(fundsString);
        tvLimit.setText(limitString);
        tvSplit.setText(splitString);
        ivLocked.setImageDrawable(lockedDrawable);

        if (item.getLimit() == -1) {
            tvLimit.setTextColor(Color.parseColor("#696969"));
        } else if (item.getFunds() >= item.getLimit()) {
            tvLimit.setTextColor(Color.parseColor("#a6242b"));
        } else {
            tvLimit.setTextColor(Color.parseColor("#25a321"));
        }

        view.setOnClickListener(view1 -> {
            Intent switchToEditActivity = new Intent(activity, CategoryActivity.class);
            switchToEditActivity.putExtra("category", item);
            activity.startActivityForResult(switchToEditActivity, 1);
        });

        return view;
    }
}
