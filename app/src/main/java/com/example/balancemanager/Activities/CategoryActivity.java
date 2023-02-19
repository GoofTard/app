package com.example.balancemanager.Activities;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.balancemanager.R;
import com.example.balancemanager.databinding.ActivityCategoryBinding;
import com.example.balancemanager.databinding.ActivityMainBinding;
import com.example.balancemanager.models.Category;
import com.example.balancemanager.models.GlobalAppData;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.Optional;

public class CategoryActivity extends AppCompatActivity {

    private Category category;

    private TextInputLayout tilCategory;
    private TextInputEditText tietCategory;
    private TextInputLayout tilSplit;
    private TextInputEditText tietSplit;
    private TextInputLayout tilLimit;
    private TextInputEditText tietLimit;
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

        tilCategory = findViewById(R.id.tilCategory);
        tietCategory = findViewById(R.id.tietCategory);
        tilSplit = findViewById(R.id.tilSplit);
        tietSplit = findViewById(R.id.tietSplit);
        tilLimit = findViewById(R.id.tilLimit);
        tietLimit = findViewById(R.id.tietLimit);
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
            tietLimit.setVisibility(GONE);
            tilLimit.setVisibility(GONE);
            btnTransfer.setVisibility(GONE);
            btnTransfer.setClickable(false);
        }

        cbLimited.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            tietLimit.setVisibility(getVisibility(isChecked));
            tilLimit.setVisibility(getVisibility(isChecked));
        });

        btnAdd.setOnClickListener(view -> {
            if (isValid()) {
                if (isEdit()) {
                    update();
                } else {
                    add();
                }

                setResult(Activity.RESULT_OK, new Intent());
                finish();
            }
        });
    }

    private boolean isValid() {
        tilCategory.setErrorEnabled(true);
        tilLimit.setErrorEnabled(true);
        tilSplit.setErrorEnabled(true);

        Editable cat = tietCategory.getText();
        Editable split = tietSplit.getText();
        Editable limit = tietLimit.getText();
        boolean limited = cbLimited.isChecked();

        if (!Objects.isNull(cat) && cat.length() != 0) {
            tilCategory.setErrorEnabled(false);
            if (!Objects.isNull(split) && split.length() != 0) {
                tilSplit.setErrorEnabled(false);
                if (Float.parseFloat(split.toString()) > 0) {
                    tilSplit.setErrorEnabled(false);
                    if (limited) {
                        if (!Objects.isNull(limit) && limit.length() != 0) {
                            if (Float.parseFloat(limit.toString()) > 0) {
                                tilLimit.setErrorEnabled(false);

                                return true;
                            } else {
                                tilLimit.setError("Limit must be positive!");
                            }
                        } else {
                            tilLimit.setError("Limit cannot be empty!");
                        }
                    } else {
                        return true;
                    }
                } else {
                    tilSplit.setError("Split must be positive!");
                }
            } else {
                tilSplit.setError("Split cannot be empty!");
            }
        } else {
            tilCategory.setError("Category cannot be empty!");
        }

        return false;
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
                this.tietCategory.getText().toString(),
                Float.parseFloat(this.tietSplit.getText().toString()),
                this.cbLimited.isChecked() ? Float.parseFloat(this.tietLimit.getText().toString()) : -1,
                this.cbLocked.isChecked(),
                Objects.isNull(this.category) ? 0 : this.category.getFunds());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initValues() {
        tvTitle.setText("Edit Category");

        tietCategory.setText(category.getName());
        tietSplit.setText(String.valueOf(category.getSplit()));
        cbLocked.setChecked(category.isLocked());

        boolean limited = category.getLimit() != -1;

        if (limited) {
            tietLimit.setText(String.valueOf(category.getLimit()));
        } else {
            tietLimit.setText("1");
        }

        tietLimit.setVisibility(getVisibility(limited));
        tilLimit.setVisibility(getVisibility(limited));

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

}