package com.example.balancemanager.TableLayoutHandlers;

import static com.example.balancemanager.utils.StringUtils.formatPrice;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.balancemanager.R;
import com.example.balancemanager.enums.TransactionType;
import com.example.balancemanager.models.Category;
import com.example.balancemanager.models.Transaction;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionsTableHandler extends FilterableTableLayoutHandler<Transaction> {
    public TransactionsTableHandler(Activity activity, TableLayout tableLayout, List<Transaction> items, List<String> titles) {
        super(activity, tableLayout, items, titles);

        filter = Category.NO_CATEGORY;
    }

    @Override
    protected View generate(Transaction item) {
        LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.transaction_item_layout, null);

        TextView tvCategory = view.findViewById(R.id.tvCategory);
        TextView tvAmount = view.findViewById(R.id.tvAmount);
        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvMessage = view.findViewById(R.id.tvMessage);

        Date date = item.getDate();

        String dateString = DateFormat.format("dd/MM/yy", date).toString();

        tvDate.setText(dateString);
        tvMessage.setText(item.getMessage().isEmpty() ? "-" : item.getMessage());
        tvAmount.setText(formatPrice(item.getFunds()));
        tvCategory.setText(item.getCategoryName().isEmpty() ? "-" : item.getCategoryName());

        tvAmount.setTextColor(item.getType() == TransactionType.ADD ?
                Color.parseColor("#25a321") :
                Color.parseColor("#a6242b"));

        return view;
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected List<Transaction> getFilteredItems() {
        return filter.equals(Category.NO_CATEGORY) ? items : items.stream()
                .filter(transaction -> transaction.getCategoryName().equals(filter))
                .collect(Collectors.toList());
    }
}
