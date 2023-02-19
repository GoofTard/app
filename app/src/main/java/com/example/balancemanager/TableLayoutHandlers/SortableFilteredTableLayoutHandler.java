package com.example.balancemanager.TableLayoutHandlers;

import android.app.Activity;
import android.os.Build;
import android.widget.TableLayout;

import androidx.annotation.RequiresApi;

import java.util.List;

public abstract class SortableFilteredTableLayoutHandler<T> extends SortableTableLayoutHandler<T> {
    protected String filter;

    protected SortableFilteredTableLayoutHandler(Activity activity, TableLayout tableLayout, List<T> items, List<String> titles) {
        super(activity, tableLayout, items, titles);
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
