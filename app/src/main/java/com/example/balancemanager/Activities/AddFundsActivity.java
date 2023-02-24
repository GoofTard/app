package com.example.balancemanager.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.balancemanager.R;
import com.example.balancemanager.forms.AutoCompleteField;
import com.example.balancemanager.forms.FormValidator;
import com.example.balancemanager.forms.InputField;
import com.example.balancemanager.models.Category;
import com.example.balancemanager.models.GlobalAppData;
import com.example.balancemanager.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class AddFundsActivity extends FormActivity {
    private Button btnAdd;
    private InputField ifFunds;
    private InputField ifMessage;
    private AutoCompleteField acCategory;

    private boolean transfer = false;
    private String fromCat;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_funds);

        Bundle extras = getIntent().getExtras();

        if (!Objects.isNull(extras)) {
            transfer = true;
            fromCat = extras.getString("fromCategory");
        }

        ArrayList<String> items = new ArrayList<>(Arrays.asList("-"));

        GlobalAppData.instance(this).getCategories().stream()
                .map(Category::getName)
                .forEach(items::add);


        ifFunds = new InputField(findViewById(R.id.tilFunds), findViewById(R.id.tietFunds))
                .addValidators(
                        FormValidator.REQUIRED,
                        new FormValidator("Funds Must Be Positive!", text -> Float.parseFloat(text) > 0)
                );

        addValidationField(ifFunds);

        ifMessage = new InputField(findViewById(R.id.tilMessage), findViewById(R.id.tietMessage));

        acCategory = new AutoCompleteField(findViewById(R.id.tilCategory),
                findViewById(R.id.actvCategory),
                new ArrayAdapter<>(this, R.layout.category_dropdown_item_layout, items));

        btnAdd = findViewById(R.id.btnAdd);

        if (transfer) {
            ifMessage.setVisibility(View.GONE);

            acCategory.removeItem("-");
            acCategory.removeItem(fromCat);

            acCategory.setHint("Transfer To");

            btnAdd.setText("Transfer Funds");

            TextView tvTitle = findViewById(R.id.tvTitle);
            tvTitle.setText("Transfer Funds From " + fromCat);

            Category from = GlobalAppData.instance(this).getCategoryByName(this, fromCat);
            new InputField(findViewById(R.id.tilCurrentFunds), findViewById(R.id.tietCurrentFunds))
                    .setVisibility(View.VISIBLE)
                    .setText(StringUtils.formatPrice(from.getFunds()));
        }

        acCategory.setText(items.get(0));
        btnAdd.setOnClickListener(this::onSubmit);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onSubmit(View view) {
        boolean valid = isInputValid();

        if (valid) {
            String funds = ifFunds.getText();
            String category = acCategory.getText();
            String message = ifMessage.getText();

            if (transfer) {
                GlobalAppData.instance(this).transfer(this, fromCat, category, Float.parseFloat(funds));
            } else {
                GlobalAppData.instance(this).addFunds(this, Float.parseFloat(funds), category.equals("-") ? "" : category, message);
            }

            setResult(Activity.RESULT_OK, new Intent());
            finish();
        }
    }
}