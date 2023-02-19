package com.example.balancemanager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.balancemanager.Activities.ActionsActivity;
import com.example.balancemanager.Activities.AddFundsActivity;
import com.example.balancemanager.Activities.CategoryActivity;
import com.example.balancemanager.Activities.SettingsActivity;
import com.example.balancemanager.Activities.TransactionsActivity;
import com.example.balancemanager.TableLayoutHandlers.CategoriesTableHandler;
import com.example.balancemanager.databinding.ActivityMainBinding;
import com.example.balancemanager.models.Category;
import com.example.balancemanager.models.GlobalAppData;
import com.example.balancemanager.utils.StringUtils;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private ImageButton ibAddFunds;
    private ImageButton ibAddCategory;
    private ImageButton ibClearFunds;
    private CategoriesTableHandler categoriesTableHandler;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDrawer();

        TableLayout tlCategories = findViewById(R.id.tlCategories);

        ibAddFunds = findViewById(R.id.btnAddFunds);
        ibAddCategory = findViewById(R.id.btnAddCategory);
        ibClearFunds = findViewById(R.id.btnClearFunds);

        categoriesTableHandler = new CategoriesTableHandler(this,
                tlCategories,
                new LinkedList<>(),
                Arrays.asList("Category", "Funds", "Percentage", "Locked", "Limit"));

        initButtonListeners();

        initTlCategories();
        setFundsButtonActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initButtonListeners() {
        ibAddFunds.setOnClickListener(view -> {
            Intent switchActivityIntent = new Intent(MainActivity.this, AddFundsActivity.class);
            startActivityForResult(switchActivityIntent, 0);
        });

        ibAddCategory.setOnClickListener(view -> {
            Intent switchToEditActivity = new Intent(this, CategoryActivity.class);
            startActivityForResult(switchToEditActivity, 1);
        });

        ibClearFunds.setOnClickListener(view -> {
            GlobalAppData.instance(this).clearFunds(this);
            initTlCategories();
            setFundsButtonActivity();
        });

        findViewById(R.id.item_clear).setOnClickListener(view -> {
            GlobalAppData.instance(this).resetUser(this);
            initTlCategories();
            setFundsButtonActivity();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initTlCategories() {
        categoriesTableHandler.setItems(GlobalAppData.instance(this).getCategories());

        categoriesTableHandler.refill();

        String total = StringUtils.formatPrice(GlobalAppData.instance(this).getTotal());

        ((TextView) findViewById(R.id.tvTotal)).setText("Total: " + total);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setFundsButtonActivity() {
        double totalSplit = GlobalAppData.instance(this).getCategories().stream()
                .mapToDouble(Category::getSplit)
                .sum();

        boolean shouldBeActive = totalSplit == 100;

        ibAddFunds.setClickable(shouldBeActive);
        ibAddFunds.setImageResource(shouldBeActive ? R.drawable.wallet_enabled : R.drawable.wallet_disabled);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        initTlCategories();
        setFundsButtonActivity();
    }

    private void initDrawer() {
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DrawerLayout drawer = binding.drawerLayout;
        new AppBarConfiguration.Builder()
                .setOpenableLayout(drawer)
                .build();
        NavigationView navigationView = findViewById(R.id.nav_view);

        findViewById(R.id.btnDrawer).setOnClickListener(v -> drawer.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_actions:
                    Intent intent1 = new Intent(this, ActionsActivity.class);
                    startActivityForResult(intent1, 6);

                    return true;
                case R.id.item_settings:
                    Intent intent2 = new Intent(this, SettingsActivity.class);
                    startActivityForResult(intent2, 5);

                    return true;
                case R.id.item_transactions:
                    Intent intent3 = new Intent(this, TransactionsActivity.class);
                    startActivityForResult(intent3, 6);

                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        });
    }
}