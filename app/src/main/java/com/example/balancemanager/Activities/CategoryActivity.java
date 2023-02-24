package com.example.balancemanager.Activities;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.balancemanager.R;
import com.example.balancemanager.databinding.ActivityCategoryBinding;
import com.example.balancemanager.forms.FormValidator;
import com.example.balancemanager.forms.InputField;
import com.example.balancemanager.models.Category;
import com.example.balancemanager.models.GlobalAppData;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;
import java.util.Optional;

public class CategoryActivity extends FormActivity {

    private Category category;

    private InputField ifCategory;
    private InputField ifSplit;
    private InputField ifLimit;

    private CheckBox cbLimited;
    private CheckBox cbLocked;
    private Button btnAdd;
    private Button btnUse;
    private Button btnTransfer;

    private TextView tvTitle;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.category = (Category) extras.get("category");
        }

        initDrawer();

        ifCategory = new InputField(findViewById(R.id.tilCategory), findViewById(R.id.tietCategory))
                .addValidators(
                        FormValidator.REQUIRED
                );

        ifSplit = new InputField(findViewById(R.id.tilSplit), findViewById(R.id.tietSplit))
                .addValidators(
                        FormValidator.REQUIRED,
                        new FormValidator("Split Must Be Positive!", text -> Float.parseFloat(text) > 0)
                );

        ifLimit = new InputField(findViewById(R.id.tilLimit), findViewById(R.id.tietLimit))
                .addValidators(
                        new FormValidator("Limit Cannot Be Empty!", text -> (cbLimited.isChecked() && !text.isEmpty()) || !cbLimited.isChecked()),
                        new FormValidator("Limit Must Be Positive!", text -> (cbLimited.isChecked() && Float.parseFloat(text) > 0) || !cbLimited.isChecked())
                )
                .setText("1");

        addValidationFields(ifCategory, ifSplit, ifLimit);

        cbLimited = findViewById(R.id.cbLimited);
        cbLocked = findViewById(R.id.cbLocked);
        btnAdd = findViewById(R.id.btnAdd);
        btnUse = findViewById(R.id.btnUse);
        tvTitle = findViewById(R.id.tvTitle);
        btnTransfer = findViewById(R.id.btnTransfer);

        if (isEdit()) {
            initValues();
        } else {
            btnUse.setVisibility(GONE);
            btnUse.setClickable(false);
            ifLimit.setVisibility(GONE);
            btnTransfer.setVisibility(GONE);
            btnTransfer.setClickable(false);
        }

        cbLimited.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            ifLimit.setVisibility(getVisibility(isChecked));
        });

        btnAdd.setOnClickListener(this::onSubmit);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void add() {
        Category toAdd = getData();

        GlobalAppData.instance(this).addCategory(this, toAdd);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void update() {
        Category updated = getData();

        GlobalAppData.instance(this).updateCategory(this, this.category.getName(), updated);
    }

    private Category getData() {
        return new Category(
                ifCategory.getText(),
                Float.parseFloat(ifSplit.getText()),
                cbLimited.isChecked() ? Float.parseFloat(ifLimit.getText()) : -1,
                cbLocked.isChecked(),
                Objects.isNull(category) ? 0 : category.getFunds());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initValues() {
        tvTitle.setText("Edit Category");

        ifCategory.setText(category.getName());
        ifSplit.setText(String.valueOf(category.getSplit()));
        cbLocked.setChecked(category.isLocked());

        boolean limited = category.getLimit() != -1;

        if (limited) {
            ifLimit.setText(String.valueOf(category.getLimit()));
        }

        ifLimit.setVisibility(getVisibility(limited));

        cbLimited.setChecked(limited);

        btnAdd.setText("Update");

        btnUse.setVisibility(VISIBLE);
        btnUse.setClickable(true);
        btnUse.setOnClickListener(view -> {
            Intent switchToEditActivity = new Intent(this, UseActivity.class);
            switchToEditActivity.putExtra("category", category.getName());
            startActivityForResult(switchToEditActivity, 2);
        });

        btnTransfer.setVisibility(VISIBLE);
        btnTransfer.setClickable(true);
        btnTransfer.setOnClickListener(v -> {
            Intent switchToEditActivity = new Intent(this, AddFundsActivity.class);
            switchToEditActivity.putExtra("fromCategory", category.getName());
            startActivityForResult(switchToEditActivity, 2);
        });
    }

    private int getVisibility(boolean visible) {
        return visible ? VISIBLE : INVISIBLE;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean isEdit() {
        return Optional.ofNullable(this.category).isPresent();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(Activity.RESULT_OK, new Intent());
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initDrawer() {
        ActivityCategoryBinding binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DrawerLayout drawer = binding.drawerLayout;
        new AppBarConfiguration.Builder()
                .setOpenableLayout(drawer)
                .build();
        NavigationView navigationView = findViewById(R.id.nav_view);

        findViewById(R.id.btnDrawer).setOnClickListener(v -> drawer.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_clear:
                    GlobalAppData.instance(this).clearFunds(this, category.getName());
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();

                    return true;
                case R.id.item_delete:
                    GlobalAppData.instance(this).deleteCategory(this, category);
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();

                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        });

        if (!isEdit()) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            findViewById(R.id.btnDrawer).setVisibility(GONE);
        }
    }

    @Override
    protected void onSubmit(View view) {
        if (isInputValid()) {
            if (isEdit()) {
                update();
            } else {
                add();
            }

            setResult(Activity.RESULT_OK, new Intent());
            finish();
        }
    }
}