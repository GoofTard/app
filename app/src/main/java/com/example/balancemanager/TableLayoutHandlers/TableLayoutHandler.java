package com.example.balancemanager.TableLayoutHandlers;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.balancemanager.utils.DesignUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class TableLayoutHandler<T> {
    protected final List<T> items;
    protected final List<String> titles;
    protected final List<View> views;
    protected final TableLayout tableLayout;
    protected final Activity activity;
    protected final Map<String, TextView> titlesMap;

    protected TableLayoutHandler(Activity activity, TableLayout tableLayout, List<T> items, List<String> titles) {
        this.items = items;
        this.tableLayout = tableLayout;
        this.activity = activity;
        this.titles = titles;

        this.views = new LinkedList<>();
        titlesMap = new HashMap<>();

        makeTitles();
    }

    protected TableLayoutHandler(Activity activity, TableLayout tableLayout, List<T> items, List<String> titles, boolean initTitles) {
        this.items = items;
        this.tableLayout = tableLayout;
        this.activity = activity;
        this.titles = titles;

        this.views = new LinkedList<>();
        titlesMap = new HashMap<>();

        if (initTitles) makeTitles();
    }

    protected abstract View generate(T item);

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void refill() {
        refill(items);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setItems(List<T> items) {
        this.items.clear();
        this.items.addAll(items);

        refill();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public final void clear() {
        this.items.clear();

        refill();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected final void refill(List<T> items) {
        views.forEach(tableLayout::removeView);
        views.clear();

        AtomicInteger index = new AtomicInteger(1);

        items.stream()
                .map(this::generate)
                .forEachOrdered((view -> {
                    views.add(view);
                    tableLayout.addView(view, index.getAndIncrement());
                }));
    }

    protected TextView getTitle(String title) {
        return DesignUtils.generateTitle(activity, title);
    }

    protected void makeTitles() {
        TableRow titlesRow = new TableRow(activity);
        titlesRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        ));
        titlesRow.setBackgroundColor(Color.parseColor("#1f1f1f"));
        titlesRow.setPadding(5, 5, 5, 10);

        titles.forEach(title -> {
            TextView v = getTitle(title);
            titlesRow.addView(v);
            titlesMap.put(title, v);
        });

        tableLayout.addView(titlesRow, 0);
    }
}