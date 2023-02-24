package com.example.balancemanager.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.example.balancemanager.R;
import com.example.balancemanager.forms.FormValidator;
import com.example.balancemanager.forms.InputField;
import com.example.balancemanager.models.Category;
import com.example.balancemanager.models.GlobalAppData;
import com.example.balancemanager.utils.StringUtils;

import java.util.stream.Stream;

public class UseActivity extends FormActivity {
    private Button btnUse;
    private InputField ifFunds;
    private InputField ifMessage;
    private InputField ifCategory;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use);

        Bundle extras = getIntent().getExtras();
        Category cat = GlobalAppData.instance(this).getCategoryByName(this, (String) extras.get("category"));

        new InputField(findViewById(R.id.tilCurrentFunds), findViewById(R.id.tietCurrentFunds))
                .setActive(false)
                .setText(StringUtils.formatPrice(cat.getFunds()));

        ifCategory = new InputField(findViewById(R.id.tilCategory), findViewById(R.id.tietCategory))
                .setActive(false)
                .setText((String) extras.get("category"));

        ifFunds = new InputField(findViewById(R.id.tilFunds), findViewById(R.id.tietFunds))
                .addValidators(
                        FormValidator.REQUIRED,
                        new FormValidator("Funds Must Be Positive!", text -> Float.parseFloat(text) > 0)
                );
        addValidationField(ifFunds);

        ifMessage = new InputField(findViewById(R.id.tilMessage), findViewById(R.id.tietMessage));

        btnUse = findViewById(R.id.btnUse);
        btnUse.setOnClickListener(this::onSubmit);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onSubmit(View view) {
        boolean valid = isInputValid();

        if (valid) {
            GlobalAppData.instance(this).useFunds(this,
                    Float.parseFloat(ifFunds.getText()), ifCategory.getText(), ifMessage.getText());

            setResult(Activity.RESULT_OK, new Intent());
            finish();
        }
    }
}