package com.example.balancemanager.TableLayoutHandlers;

import static com.example.balancemanager.enums.SortDirection.ASC;
import static com.example.balancemanager.enums.SortDirection.DESC;
import static com.example.balancemanager.enums.SortDirection.NONE;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.balancemanager.enums.SortDirection;
import com.example.balancemanager.utils.DesignUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class SortableTableLayoutHandler<T> extends TableLayoutHandler<T> {
    protected static final SortDirection[] SORT_DIRECTIONS = {DESC, NONE, ASC};
    protected int sortIndex;
    protected String sortParameter;
    private final Map<String, Comparator<T>> comparatorMap;
    private boolean saveChange;

    protected SortableTableLayoutHandler(Activity activity,
                                         TableLayout tableLayout,
                                         List<T> items,
                                         List<String> titles,
                                         Map<String, Comparator<T>> comparatorMap,
                                         String defaultSortParameter) {
        super(activity, tableLayout, items, titles, false);

        this.sortParameter = defaultSortParameter;
        this.comparatorMap = comparatorMap;

        sortIndex = 1;
        saveChange = false;

        super.makeTitles();
    }

    protected SortableTableLayoutHandler(Activity activity,
                                         TableLayout tableLayout,
                                         List<T> items,
                                         List<String> titles,
                                         Map<String, Comparator<T>> comparatorMap,
                                         String defaultSortParameter,
                                         int sortIndex) {
        super(activity, tableLayout, items, titles, false);

        this.sortParameter = defaultSortParameter;
        this.comparatorMap = comparatorMap;

        this.sortIndex = sortIndex;
        saveChange = false;

        super.makeTitles();
    }

    protected final List<T> sort(List<T> items) {
        Comparator<T> comparator = comparatorMap.get(sortParameter);

        if (Objects.isNull(comparator)) {
            return items;
        }

        return items.stream()
                .sorted((i1, i2) -> {
                    assert comparator != null;
                    if (SORT_DIRECTIONS[sortIndex] == SortDirection.NONE) {
                        return 0;
                    }

                    if (SORT_DIRECTIONS[sortIndex] == SortDirection.ASC) {
                        return comparator.compare(i1, i2);
                    }

                    return comparator.reversed().compare(i1, i2);
                })
                .collect(Collectors.toList());
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void refill() {
        List<T> items = sort(this.items);

        super.refill(items);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected TextView getTitle(String title) {
        TextView tv = DesignUtils.generateTitle(activity, title);
        tv.setCompoundDrawablesRelativeWithIntrinsicBounds(DesignUtils.getSortIcon(SortDirection.NONE), 0, 0, 0);

        tv.setOnClickListener(view -> {
            TextView textView = (TextView) view;

            if (textView.getText().equals(sortParameter)) {
                nextSortDirection();
            } else {
                setSortParameter(textView.getText().toString());

                titlesMap.values().forEach((v) -> {
                    v.setCompoundDrawablesRelativeWithIntrinsicBounds(DesignUtils.getSortIcon(SortDirection.NONE), 0, 0, 0);
                });
            }

            if (saveChange) {
                activity.getSharedPreferences("SORT", Context.MODE_PRIVATE).edit()
                        .putString("sortBy", sortParameter)
                        .commit();
                activity.getSharedPreferences("SORT", Context.MODE_PRIVATE).edit()
                        .putInt("sortIndex", sortIndex)
                        .commit();
            }

            tv.setCompoundDrawablesRelativeWithIntrinsicBounds(DesignUtils.getSortIcon(SORT_DIRECTIONS[sortIndex]), 0, 0, 0);
            refill();
        });

        if (sortParameter.equals(tv.getText().toString())) {
            tv.setCompoundDrawablesRelativeWithIntrinsicBounds(DesignUtils.getSortIcon(SORT_DIRECTIONS[sortIndex]), 0, 0, 0);
        }

        return tv;
    }

    protected final void setSortParameter(String sortParameter) {
        this.sortParameter = sortParameter;
        sortIndex = 2;
    }

    protected final void nextSortDirection() {
        sortIndex = (sortIndex + 1) % SORT_DIRECTIONS.length;
    }

    public void setSaveChange(boolean saveChange) {
        this.saveChange = saveChange;
    }
}
