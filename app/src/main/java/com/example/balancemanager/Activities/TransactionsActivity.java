package com.example.balancemanager.Activities;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.balancemanager.R;
import com.example.balancemanager.TableLayoutHandlers.CategoryTotalTableHandler;
import com.example.balancemanager.TableLayoutHandlers.TransactionsTableHandler;
import com.example.balancemanager.enums.TransactionType;
import com.example.balancemanager.models.Category;
import com.example.balancemanager.models.GlobalAppData;
import com.example.balancemanager.models.Transaction;
import com.example.balancemanager.utils.DesignUtils;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransactionsActivity extends AppCompatActivity {

    private String filterName = Category.NO_CATEGORY;
    private ImageButton ibClear;

    private TransactionsTableHandler transactionsTableHandler;
    private CategoryTotalTableHandler categoryTotalTableHandler;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        ibClear = findViewById(R.id.btnClearTransactions);
        ibClear.setOnClickListener(view -> {
            GlobalAppData.instance(this).clearTransactions(this);

            categoryTotalTableHandler.clear();
            transactionsTableHandler.clear();
            makeViews();
        });

        transactionsTableHandler = new TransactionsTableHandler(this,
                findViewById(R.id.tlTransactions),
                GlobalAppData.instance(this).getTransactions(),
                Arrays.asList("Date", "Category", "Amount", "Message")
                );

        categoryTotalTableHandler = new CategoryTotalTableHandler(this,
                findViewById(R.id.tlTotalTransactions),
                getTotalUsed(),
                Arrays.asList("Category", "Total")
                );

        AutoCompleteTextView actvCategory = findViewById(R.id.actvCategory);

        ArrayList<String> items = new ArrayList<>(Arrays.asList(Category.NO_CATEGORY));

        GlobalAppData.instance(this).getCategories().stream()
                .map(Category::getName)
                .forEach(items::add);

        actvCategory.setText(items.get(0));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.category_dropdown_item_layout, items);
        actvCategory.setAdapter(adapter);

        actvCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterName = editable.toString();
                transactionsTableHandler.setFilter(filterName);
                categoryTotalTableHandler.setFilter(filterName);
                makeViews();
            }
        });

        transactionsTableHandler.refill();
        categoryTotalTableHandler.refill();

        makeViews();
        addDesign();
    }

    private void addDesign() {
        TextInputLayout tilCategory = findViewById(R.id.tilCategory);

        int color = DesignUtils.getResourceColor(this, R.color.neutral);

        DesignUtils.setBoxStrokeColor(tilCategory, color);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void makeViews() {
        TextView tvTotalTransactions = findViewById(R.id.tvTotalTransactions);

        Map<String, Double> totals = getTotalUsed().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        tvTotalTransactions.setText(String.format("Total Used: ₪%.2f", totals.containsKey(filterName) ? totals.get(filterName) : 0));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Map.Entry<String, Double>> getTotalUsed() {
        List<Transaction> transactions = GlobalAppData.instance(this).getTransactions();

        Map<String, Double> totals = transactions.stream()
                .filter((Transaction transaction) -> transaction.getType() == TransactionType.USE)
                .collect(Collectors.groupingBy(
                        Transaction::getCategoryName,
                        Collectors.summingDouble(Transaction::getFunds)
                ));

        totals.entrySet().stream()
                .filter(entry -> entry.getValue().isNaN() || Objects.isNull(entry.getValue()))
                .map(Map.Entry::getKey)
                .forEach(totals::remove);

        double total = totals.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        entryCategories(totals).forEach(cat -> totals.put(cat, 0D));

        HashMap<String, Double> ordered = new HashMap<>();
        ordered.put(Category.NO_CATEGORY, total);
        ordered.putAll(totals);

        return new ArrayList<>(ordered.entrySet());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<String> entryCategories(Map<String, Double> totals) {
        List<String> cats = GlobalAppData.instance(this).getCategories().stream().map(Category::getName).collect(Collectors.toList());

        return cats.stream().filter(cat -> !totals.containsKey(cat)).collect(Collectors.toList());
    }
}