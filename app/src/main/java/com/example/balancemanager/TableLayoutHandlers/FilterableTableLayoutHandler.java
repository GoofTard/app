package com.example.balancemanager.TableLayoutHandlers;

import android.app.Activity;
import android.os.Build;
import android.widget.TableLayout;

import androidx.annotation.RequiresApi;

import java.util.List;

public abstract class FilterableTableLayoutHandler<T> extends TableLayoutHandler<T> {
    protected String filter;

    protected FilterableTableLayoutHandler(Activity activity, TableLayout tableLayout, List<T> items, List<String> titles) {
        super(activity, tableLayout, items, titles);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void refill() {
        List<T> items = getFilteredItems();

        super.refill(items);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setFilter(String filter) {
        this.filter = filter;

        refill();
    }

    protected abstract List<T> getFilteredItems();
}
