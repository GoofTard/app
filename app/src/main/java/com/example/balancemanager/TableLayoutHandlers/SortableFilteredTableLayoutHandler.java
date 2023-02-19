package com.example.balancemanager.TableLayoutHandlers;

import android.app.Activity;
import android.os.Build;
import android.widget.TableLayout;

import androidx.annotation.RequiresApi;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public abstract class SortableFilteredTableLayoutHandler<T> extends SortableTableLayoutHandler<T> {
    protected String filter;

    protected SortableFilteredTableLayoutHandler(Activity activity,
                                                 TableLayout tableLayout,
                                                 List<T> items,
                                                 List<String> titles,
                                                 Map<String, Comparator<T>> comparatorMap,
                                                 String defaultSortParameter) {
        super(activity, tableLayout, items, titles, comparatorMap, defaultSortParameter);
    }

    protected abstract List<T> getFilteredItems();

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void refill() {
        List<T> items = getFilteredItems();

        super.refill(sort(items));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setFilter(String filter) {
        this.filter = filter;

        refill();
    }
}
