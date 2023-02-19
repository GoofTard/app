package com.example.balancemanager.TableLayoutHandlers;

import static com.example.balancemanager.utils.StringUtils.formatPrice;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.balancemanager.R;
import com.example.balancemanager.enums.SortDirection;
import com.example.balancemanager.models.Category;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CategoryTotalTableHandler extends SortableFilteredTableLayoutHandler<Map.Entry<String, Double>> {
    private static final Map<String, Comparator<Map.Entry<String, Double>>> COMPARATOR_MAP = Map.of(
            "Category", (cat1, cat2) -> cat1.getKey().compareToIgnoreCase(cat2.getKey()),
            "Total", Comparator.comparingDouble(Map.Entry::getValue)
    );

    public CategoryTotalTableHandler(Activity activity, TableLayout tableLayout, List<Map.Entry<String, Double>> items, List<String> titles) {
        super(activity, tableLayout, items, titles, COMPARATOR_MAP, "Category");

        filter = Category.NO_CATEGORY;
    }

    @Override
    protected View generate(Map.Entry<String, Double> item) {
        LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.category_total_layout, null);

        TextView tvCategory = view.findViewById(R.id.tvCategory);
        TextView tvTotal = view.findViewById(R.id.tvTotal);

        tvCategory.setText(item.getKey());
        tvTotal.setText(formatPrice(item.getValue().floatValue()));
        tvTotal.setTextColor(Color.parseColor("#a6242b"));

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected List<Map.Entry<String, Double>> getFilteredItems() {
        return filter.equals(Category.NO_CATEGORY) ? items : items.stream()
                .filter(entry -> entry.getKey().equals(filter))
                .collect(Collectors.toList());
    }
}
