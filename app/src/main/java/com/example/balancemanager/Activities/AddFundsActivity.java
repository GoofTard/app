package com.example.balancemanager.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.balancemanager.R;
import com.example.balancemanager.models.Category;
import com.example.balancemanager.models.GlobalAppData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class AddFundsActivity extends AppCompatActivity {

    private TextInputEditText tietFunds;
    private TextInputEditText tietMessage;
    private TextInputLayout tilFunds;
    private AutoCompleteTextView actvCategory;
    private Button btnAdd;

    boolean transfer = false;
    String fromCat;

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

        tietFunds = findViewById(R.id.tietFunds);
        tilFunds = findViewById(R.id.tilFunds);
        tietMessage = findViewById(R.id.tietMessage);
        actvCategory = findViewById(R.id.actvCategory);
        btnAdd = findViewById(R.id.btnAdd);

        ArrayList<String> items = new ArrayList<>(Arrays.asList("-"));

        GlobalAppData.instance(this).getCategories().stream()
                .map(Category::getName)
                .forEach(items::add);

        if (transfer) {
            items.remove("-");
            items.remove(fromCat);
            TextInputLayout tilCategory = findViewById(R.id.tilCategory);
            TextInputLayout tilMessage = findViewById(R.id.tilMessage);
            tilMessage.setVisibility(View.GONE);
            tilCategory.setHint("Transfer To");
            btnAdd.setText("Transfer Funds");
            TextView tvTitle = findViewById(R.id.tvTitle);
            tvTitle.setText("Transfer Funds From " + fromCat);
        }

        actvCategory.setText(items.get(0));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.category_dropdown_item_layout, items);
        actvCategory.setAdapter(adapter);

        btnAdd.setOnClickListener(view -> {
            String funds = tietFunds.getText().toString();
            String category = actvCategory.getText().toString();
            String message = tietMessage.getText().toString();

            boolean valid = isInputValid(funds);

            if (valid) {
                if (transfer) {
                    GlobalAppData.instance(this).transfer(this, fromCat, category, Float.parseFloat(funds));
                } else {
                    GlobalAppData.instance(this).addFunds(this, Float.parseFloat(funds), category.equals("-") ? "" : category, message);
                }

                setResult(Activity.RESULT_OK, new Intent());
                finish();
            }
        });
    }

    private boolean isInputValid(String funds) {
        tilFunds.setErrorEnabled(true);
        if (!funds.isEmpty()) {
            if (Float.parseFloat(funds) > 0) {
                tilFunds.setErrorEnabled(false);
                return true;
            } else {
                tilFunds.setError("Funds Must Be Positive!");
            }
        } else {
            tilFunds.setError("Funds Must Be Entered");
        }

        return false;
    }
}