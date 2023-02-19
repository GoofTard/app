package com.example.balancemanager.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.balancemanager.settings.Settings;
import com.example.balancemanager.utils.FileHandler;

import java.util.List;
import java.util.Objects;

public class GlobalAppData {
    private static GlobalAppData data;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private GlobalAppData(Activity activity) {
        User user = FileHandler.readUser(activity);

        this.user = new User(user);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static GlobalAppData instance(Activity activity) {
        if (Objects.isNull(data)) {
            data = new GlobalAppData(activity);
        }

        return data;
    }

    private final User user;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addFunds(Activity activity, float funds, String category, String message) {
        user.addTransaction(user.addFunds(funds, category, message));
        save(activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void useFunds(Activity activity, float funds, String category, String message) {
        if (!this.user.isCategoryLocked(category)) {
            user.addTransaction(user.useFunds(funds, category, message));
            save(activity);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addCategory(Activity activity, Category category) {
        this.user.addCategory(category);
        save(activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateCategory(Activity activity, String oldName, Category category) {
        this.user.updateCategory(oldName, category);
        save(activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteCategory(Activity activity, Category category) {
        this.user.deleteCategory(category);
        save(activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateSettings(Activity activity, Class settingsClass, boolean isChecked) {
        this.user.getUserSettings().markSettingAs(settingsClass, isChecked);
        save(activity);
    }

    public void clearTransactions(Activity activity) {
        this.user.clearTransactions();
        save(activity);
    }

    public void clearFunds(Activity activity, String category) {
        this.user.clearCategoryFunds(category);
        save(activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void clearFunds(Activity activity) {
        this.user.getCategories().forEach(Category::clearFunds);
        this.user.clearTotal();
        save(activity);
    }

    public void transfer(Activity activity, String from, String to, float funds) {
        this.user.transfer(from, to, funds);
        save(activity);
    }

    public void resetUser(Activity activity) {
        this.reset(activity);
    }

    public List<Settings> getSettings() {
        return this.user.getUserSettings().settings();
    }

    public float getTotal() {
        return this.user.getTotal();
    }

    public List<Category> getCategories() {
        return this.user.getCategories();
    }

    public List<Transaction> getTransactions() {
        return this.user.getTransactions();
    }

    public List<Action> getFavoriteActions() {
        return this.user.getFavoriteActions();
    }

    public void addAction(Activity activity, Action action) {
        this.user.addFavoriteAction(action);
        save(activity);
    }

    public void removeAction(Activity activity, Action action) {
        this.user.removeFavoriteAction(action);
        save(activity);
    }

    public void updateAction(Activity activity, String oldName, Action action) {
        this.user.updateAction(oldName, action);
        save(activity);
    }

    private void save(Activity activity) {
        FileHandler.writeUser(activity, user);
    }

    private void reset(Activity activity) {
        FileHandler.writeUser(activity, new User());
    }

}
