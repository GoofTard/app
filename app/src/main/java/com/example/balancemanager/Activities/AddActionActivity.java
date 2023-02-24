package com.example.balancemanager.Activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.balancemanager.R;
import com.example.balancemanager.enums.TransactionType;
import com.example.balancemanager.forms.AutoCompleteField;
import com.example.balancemanager.forms.FormValidator;
import com.example.balancemanager.forms.InputField;
import com.example.balancemanager.models.Action;
import com.example.balancemanager.models.Category;
import com.example.balancemanager.models.GlobalAppData;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddActionActivity extends FormActivity {
    private TextView tvTitle;
    private InputField ifName;
    private InputField ifFunds;
    private InputField ifMessage;
    private AutoCompleteField ifCategory;
    private RadioButton rbAdd;
    private RadioButton rbUse;
    private Button btnAdd;
    private Action action;
    private final List<String> categories = new LinkedList<>();


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_action);

        tvTitle = findViewById(R.id.tvTitle);

        ifName = new InputField(findViewById(R.id.tilName), findViewById(R.id.tietName))
                .addValidators(
                        FormValidator.REQUIRED,
                        new FormValidator("Name Must Be Unique!", text -> !nameExists(text.trim()))
                );

        ifFunds = new InputField(findViewById(R.id.tilFunds), findViewById(R.id.tietFunds))
                .addValidators(
                        FormValidator.REQUIRED,
                        new FormValidator("Funds Must Be Positive!", text -> Float.parseFloat(text) > 0)
                );

        ifMessage = new InputField(findViewById(R.id.tilMessage), findViewById(R.id.tietMessage));

        categories.addAll(GlobalAppData.instance(this).getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList()));

        ifCategory = (AutoCompleteField) new AutoCompleteField(
                findViewById(R.id.tilCategory),
                findViewById(R.id.actvCategory),
                new ArrayAdapter<>(this, R.layout.category_dropdown_item_layout, categories))
                .addValidators(FormValidator.REQUIRED);

        addValidationFields(ifName, ifFunds, ifCategory);

        rbAdd = findViewById(R.id.rbAdd);
        rbUse = findViewById(R.id.rbUse);
        btnAdd = findViewById(R.id.btnAdd);

        tvTitle.setText("Add Action");
        rbUse.setOnCheckedChangeListener((compoundButton, b) -> setActive(TransactionType.USE));
        rbAdd.setOnCheckedChangeListener((compoundButton, b) -> setActive(TransactionType.ADD));

        setActive(TransactionType.ADD);

        Bundle extras = getIntent().getExtras();

        if (!Objects.isNull(extras)) {
            action = (Action) extras.get("action");
        }

        if (isEdit()) {
            initEdit();
        }

        btnAdd.setOnClickListener(this::onSubmit);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initEdit() {
        btnAdd.setText("Update");
        tvTitle.setText("Update Action");
        ifName.setText(action.getName());
        ifFunds.setText(String.valueOf(action.getFunds()));
        ifMessage.setText(action.getMessage());
        setActive(action.getType());
        ifCategory.setText(action.getCategoryName());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean nameExists(String name) {
        if (isEdit()) {
            if (name.trim().equals(action.getName())) {
                return false;
            }
        }

        return GlobalAppData.instance(this).getFavoriteActions().stream()
                .map(Action::getName)
                .anyMatch(n -> n.equals(name.trim()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setActive(TransactionType type) {
        categories.remove("-");

        switch (type) {
            case ADD: {
                rbAdd.setChecked(true);
                rbUse.setChecked(false);
                if (!categories.contains("-")) {
                    categories.add(0, "-");
                }

                break;
            }
            case USE: {
                rbAdd.setChecked(false);
                rbUse.setChecked(true);

                break;
            }
        }

        ifCategory.setText(categories.get(0));
    }

    private TransactionType getSelected() {
        return rbUse.isChecked() ? TransactionType.USE : TransactionType.ADD;
    }

    private boolean isEdit() {
        return !Objects.isNull(action);
    }

    @Override
    protected void onSubmit(View view) {
        if (isInputValid()) {
            Action action = new Action(
                    getSelected(),
                    ifName.getText().trim(),
                    ifCategory.getText(),
                    Float.parseFloat(ifFunds.getText()),
                    ifMessage.getText()
            );

            if (isEdit()) {
                GlobalAppData.instance(this).updateAction(this, this.action.getName(), action);
            } else {
                GlobalAppData.instance(this).addAction(this, action);
            }

            setResult(RESULT_OK);
            finish();
        }
    }
}