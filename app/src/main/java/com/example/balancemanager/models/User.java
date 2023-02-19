package com.example.balancemanager.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.balancemanager.enums.TransactionType;
import com.example.balancemanager.settings.RedistributeOnFilled;
import com.example.balancemanager.settings.RemoveFundsOnCategoryDelete;
import com.example.balancemanager.settings.Settings;
import com.example.balancemanager.settings.UserSettings;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private float total;
    private final HashMap<String, Category> categories;
    private final List<Transaction> transactions;
    private final UserSettings userSettings;

    private final HashMap<String, Action> favoriteActions;


    public User() {
        this.total = 0;
        this.categories = new HashMap<>();
        this.transactions = new LinkedList<>();
        this.userSettings = new UserSettings();
        this.favoriteActions = new HashMap<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public User(User user) {
        if (Objects.isNull(user)) {
            this.total = 0;
            this.categories = new HashMap<>();
            this.transactions = new LinkedList<>();
            this.userSettings = new UserSettings();
            this.favoriteActions = new HashMap<>();
        } else {
            this.total = user.total;

            if (Objects.isNull(user.categories)) {
                this.categories = new HashMap<>();
            } else {
                this.categories = user.categories;
            }

            if (Objects.isNull(user.transactions)) {
                this.transactions = new LinkedList<>();
            } else {
                this.transactions = user.transactions;
            }

            if (Objects.isNull(user.userSettings)) {
                this.userSettings = new UserSettings();
            } else {
                this.userSettings = new UserSettings(user.userSettings);
            }

            if (Objects.isNull(user.favoriteActions)) {
                this.favoriteActions = new HashMap<>();
            } else {
                this.favoriteActions = user.favoriteActions;
            }
        }
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Transaction addFunds(float funds, String category, String message) {
        this.total += funds;
        AtomicReference<Float> overflow = new AtomicReference<>((float) 0);
        AtomicInteger limitedCatAmount = new AtomicInteger();
        List<Category> items = new LinkedList<>(this.categories.values());

        if (!category.isEmpty() && !category.equals("-")) {
            Category item = this.categories.get(category);

            item.addFunds(funds);
        } else {
            items.forEach(cat -> {
                float categoryFunds = funds * (cat.getSplit() / 100.0f);

                if (cat.getLimit() != -1 && cat.getFunds() >= cat.getLimit()) {
                    overflow.updateAndGet(v -> v + categoryFunds);
                    limitedCatAmount.addAndGet(1);
                } else if (cat.getLimit() != -1 && cat.getFunds() + categoryFunds >= cat.getLimit()) {
                    float diff = cat.getLimit() - cat.getFunds();

                    overflow.updateAndGet(v -> v + categoryFunds - diff);
                    limitedCatAmount.addAndGet(1);

                    cat.addFunds(diff);
                } else {
                    cat.addFunds(categoryFunds);
                }
            });

            while (overflow.get() > 0.01) {
                float percent = 100.0f / (items.size() - limitedCatAmount.get()) / 100.0f;
                AtomicReference<Float> tempOverflow = new AtomicReference<>((float) 0);
                float categoryFunds = overflow.get() * percent;

                items.forEach(cat -> {
                    if (cat.getLimit() != -1 && cat.getFunds() >= cat.getLimit()) {
                        return;
                    } else if (cat.getLimit() != -1 && cat.getFunds() + categoryFunds >= cat.getLimit()) {
                        float diff = cat.getLimit() - cat.getFunds();
                        tempOverflow.updateAndGet(v -> v + categoryFunds - diff);
                        limitedCatAmount.updateAndGet(v -> v + 1);
                        cat.addFunds(diff);
                    } else {
                        cat.addFunds(categoryFunds);
                    }
                });
                overflow.set(tempOverflow.get());
            }
        }

        return new Transaction(new Date(), TransactionType.ADD, category, funds, message);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Transaction useFunds(float funds, String category, String message) {
        this.total -= funds;

        this.categories
                .get(category)
                .useFunds(funds);

        return new Transaction(new Date(), TransactionType.USE, category, funds, message);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addCategory(Category category) {
        this.categories.put(category.getName(), category);

        if (isRedistributeEnabled()) {
            if (totalPercentage() == 100) {
                redistribute();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateCategory(String oldName, Category category) {
        this.categories.remove(oldName);
        this.addCategory(category);
        this.transactions.stream()
                .filter(tr -> tr.getCategoryName().equals(oldName))
                .forEach(transaction -> transaction.setCategoryName(category.getName()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteCategory(Category category) {
        if (isRemoveFundsEnabled()) {
            this.total -= this.categories.get(category.getName()).getFunds();
        }

        this.categories.remove(category.getName());

        if (isRedistributeEnabled()) {
            if (totalPercentage() == 100) {
                redistribute();
            }
        }
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public void clearTransactions() {
        this.transactions.clear();
    }

    public float getTotal() {
        return total;
    }

    public List<Category> getCategories() {
        return new LinkedList<>(categories.values());
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public boolean isCategoryLocked(String category) {
        return categories.get(category).isLocked();
    }

    public void clearCategoryFunds(String category) {
        Category cat = categories.get(category);
        cat.useFunds(cat.getFunds());
    }

    public void clearTotal() {
        this.total = 0;
    }

    public List<Action> getFavoriteActions() {
        return new LinkedList<>(favoriteActions.values());
    }

    public void addFavoriteAction(Action action) {
        this.favoriteActions.put(action.getName(), action);
    }

    public void removeFavoriteAction(Action action) {
        this.favoriteActions.remove(action.getName());
    }

    public void updateAction(String oldName, Action action) {
        this.favoriteActions.remove(oldName);
        this.favoriteActions.put(action.getName(), action);
    }

    public void transfer(String from, String to, float funds) {
        this.categories.get(from).useFunds(funds);
        this.categories.get(to).addFunds(funds);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void redistribute() {
        float total = this.total;
        this.total -= total;
        clearFunds();
        addFunds(total, "", "");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void clearFunds() {
        this.categories.values().forEach(Category::clearFunds);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private double totalPercentage() {
        return this.categories.values().stream()
                .mapToDouble(Category::getSplit)
                .sum();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean isRedistributeEnabled() {
        Optional<Settings> settings = getUserSettings().getSettingsByClass(RedistributeOnFilled.class);

        return settings.map(Settings::isChecked).orElse(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean isRemoveFundsEnabled() {
        Optional<Settings> settings = getUserSettings().getSettingsByClass(RemoveFundsOnCategoryDelete.class);

        return settings.map(Settings::isChecked).orElse(false);
    }
}
