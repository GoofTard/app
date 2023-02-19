package com.example.balancemanager.TableLayoutHandlers;

import static com.example.balancemanager.enums.SortDirection.ASC;
import static com.example.balancemanager.enums.SortDirection.DESC;
import static com.example.balancemanager.enums.SortDirection.NONE;

import android.app.Activity;
import android.os.Build;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.balancemanager.enums.SortDirection;
import com.example.balancemanager.utils.DesignUtils;

import java.util.List;

public abstract class SortableTableLayoutHandler<T> extends TableLayoutHandler<T> {
    protected static final SortDirection[] SORT_DIRECTIONS = { DESC, NONE, ASC };
    protected int sortIndex;
    protected String sortParameter;
    protected SortableTableLayoutHandler(Activity activity, TableLayout tableLayout, List<T> items, List<String> titles) {
        super(activity, tableLayout, items, titles);

        sortIndex = 1;
        sortParameter = "";
    }

    protected abstract List<T> sort(List<T> items);

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
        tv.setCompoundDrawablesRelativeWithIntrinsicBounds(DesignUtils.getSortIcon(SortDirection.NONE),0, 0, 0);

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

            tv.setCompoundDrawablesRelativeWithIntrinsicBounds(DesignUtils.getSortIcon(SORT_DIRECTIONS[sortIndex]),0, 0, 0);
            refill();
        });

        return tv;
    }
    protected final void setSortParameter(String sortParameter) {
        this.sortParameter = sortParameter;
        sortIndex = 2;
    }

    protected final void nextSortDirection() {
        sortIndex = (sortIndex + 1) % SORT_DIRECTIONS.length;
    }

}
