package com.example.balancemanager.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.balancemanager.R;
import com.example.balancemanager.models.Category;
import com.example.balancemanager.models.GlobalAppData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class UseActivity extends AppCompatActivity {
    private TextInputEditText tietFunds;
    private TextInputEditText tietMessage;
    private TextInputLayout tilFunds;
    private TextInputLayout tilCategory;
    private TextInputEditText tietCategory;
    private Button btnUse;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use);

        tietFunds = findViewById(R.id.tietFunds);
        tilFunds = findViewById(R.id.tilFunds);
        tilCategory = findViewById(R.id.tilCategory);
        tietMessage = findViewById(R.id.tietMessage);
        tietCategory = findViewById(R.id.tietCategory);
        btnUse = findViewById(R.id.btnUse);

        Bundle extras = getIntent().getExtras();

        tietCategory.setText((String) extras.get("category"));
        tietCategory.setFocusable(false);

        btnUse.setOnClickListener(view -> {
            Editable funds = tietFunds.getText();
            Editable category = tietCategory.getText();
            Editable message = tietMessage.getText();

            boolean valid = isInputValid(funds, category);

            if (valid) {

                GlobalAppData.instance(this).useFunds(this, Float.parseFloat(funds.toString()), category.toString(), message.toString());

                setResult(Activity.RESULT_OK, new Intent());
                finish();
            }
        });
    }
    private boolean isInputValid(Editable funds, Editable category) {
        tilFunds.setErrorEnabled(true);
        tilCategory.setErrorEnabled(true);

        if (!Objects.isNull(funds) && funds.length() != 0) {
            tilFunds.setErrorEnabled(false);
            if (Float.parseFloat(funds.toString()) > 0) {
                tilFunds.setErrorEnabled(false);
                if (!Objects.isNull(category) && category.length() != 0) {
                    tilCategory.setErrorEnabled(false);
                    return true;
                } else {
                    tilCategory.setError("Category must be selected!");
                }
            } else {
                tilFunds.setError("Funds must be positive!");
            }
         } else {
            tilFunds.setError("Funds cannot be empty!");
        }

        return false;
    }
}