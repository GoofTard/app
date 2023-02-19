package com.example.balancemanager.Activities;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.balancemanager.R;
import com.example.balancemanager.enums.TransactionType;
import com.example.balancemanager.models.Action;
import com.example.balancemanager.models.Category;
import com.example.balancemanager.models.GlobalAppData;
import com.example.balancemanager.utils.StringUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddActionActivity extends AppCompatActivity {
    private TextView tvTitle;
    private TextInputLayout tilName;
    private TextInputEditText tietName;
    private TextInputLayout tilFunds;
    private TextInputEditText tietFunds;
    private TextInputEditText tietMessage;
    private TextInputLayout tilCategory;
    private AutoCompleteTextView actvCategory;
    private RadioButton rbAdd;
    private RadioButton rbUse;
    private Button btnAdd;
    private Action action;
    private final List<String> categories = new LinkedList<>();
    private ArrayAdapter<String> adapter;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_action);

        tvTitle = findViewById(R.id.tvTitle);
        tilName = findViewById(R.id.tilName);
        tietName = findViewById(R.id.tietName);
        tilFunds = findViewById(R.id.tilFunds);
        tietFunds = findViewById(R.id.tietFunds);
        tietMessage = findViewById(R.id.tietMessage);
        tilCategory = findViewById(R.id.tilCategory);
        actvCategory = findViewById(R.id.actvCategory);
        rbAdd = findViewById(R.id.rbAdd);
        rbUse = findViewById(R.id.rbUse);
        btnAdd = findViewById(R.id.btnAdd);

        tvTitle.setText("Add Action");
        rbUse.setOnCheckedChangeListener((compoundButton, b) -> setActive(TransactionType.USE));
        rbAdd.setOnCheckedChangeListener((compoundButton, b) -> setActive(TransactionType.ADD));

        categories.addAll(GlobalAppData.instance(this).getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList()));

        adapter = new ArrayAdapter<>(this, R.layout.category_dropdown_item_layout, categories);
        actvCategory.setAdapter(adapter);

        setActive(TransactionType.ADD);

        Bundle extras = getIntent().getExtras();

        if (!Objects.isNull(extras)) {
            action = (Action)extras.get("action");
        }

        if (isEdit()) {
            initEdit();
        }

        btnAdd.setOnClickListener(v -> {
           if (isValid()) {
               Action action = new Action(
                       getSelected(),
                       tietName.getText().toString().trim(),
                       actvCategory.getText().toString(),
                       Float.parseFloat(tietFunds.getText().toString()),
                       isMessageEntered() ? tietMessage.getText().toString() : ""
               );

               if (isEdit()) {
                    GlobalAppData.instance(this).updateAction(this, this.action.getName(), action);
               } else {
                GlobalAppData.instance(this).addAction(this, action);
               }

               setResult(RESULT_OK);
               finish();
           }
        });
    }

    private boolean isMessageEntered() {
        return !Objects.isNull(tietMessage.getText()) && !tietMessage.getText().toString().isEmpty();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean isValid() {
        Editable name = tietName.getText();
        Editable funds = tietFunds.getText();
        Editable category = actvCategory.getText();

        tilName.setErrorEnabled(true);
        tilFunds.setErrorEnabled(true);
        tilCategory.setErrorEnabled(true);

        if (!Objects.isNull(name) && !name.toString().isEmpty()) {
            if (!nameExists(name.toString().trim())) {
                tilName.setErrorEnabled(false);

                if (!Objects.isNull(funds) && !funds.toString().isEmpty()) {
                    if (Float.parseFloat(funds.toString()) > 0) {
                        tilFunds.setErrorEnabled(false);

                        if (!Objects.isNull(category) && !category.toString().isEmpty()) {
                            tilCategory.setErrorEnabled(false);

                            return true;
                        } else {
                            tilCategory.setError("Category Must Be Entered!");
                        }
                    } else {
                        tilFunds.setError("Funds Must Be Positive!");
                    }
                } else {
                    tilFunds.setError("Funds Must Be Entered!");
                }
            } else {
                tilName.setError("Name Must Be Unique!");
            }
        } else {
            tilName.setError("Name Must Be Entered");
        }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initEdit() {
        btnAdd.setText("Update");
        tvTitle.setText("Update Action");
        tietName.setText(action.getName());
        tietFunds.setText(String.valueOf(action.getFunds()));
        tietMessage.setText(action.getMessage());
        setActive(action.getType());
        actvCategory.setText(action.getCategoryName(), false);
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

        actvCategory.setText(categories.get(0), false);
    }

    private TransactionType getSelected() {
        return rbUse.isChecked() ? TransactionType.USE : TransactionType.ADD;
    }

    private boolean isEdit() {
        return !Objects.isNull(action);
    }
}