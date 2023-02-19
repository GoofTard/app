package com.example.balancemanager.settings;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserSettings implements Serializable {
    private static final long serialVersionUID = 5L;

    private static final Map<Class, Settings> ALL_SETTINGS = Map.of(
            RedistributeOnFilled.class, new RedistributeOnFilled(),
            RemoveFundsOnCategoryDelete.class, new RemoveFundsOnCategoryDelete()
    );

    private final Map<Class, Settings> classSettingsMap;

    public UserSettings() {
        classSettingsMap = new HashMap<>(ALL_SETTINGS);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public UserSettings(UserSettings userSettings) {
        classSettingsMap = ALL_SETTINGS.keySet().stream()
                .map(setting -> UserSettings.getOrDefault(userSettings, setting))
                .collect(Collectors.toMap(Object::getClass, Function.identity()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Optional<Settings> getSettingsByClass(Class settings) {
        return Optional.ofNullable(classSettingsMap.get(settings));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void markSettingAs(Class settings, boolean marked) {
        Optional<Settings> settingsOptional = getSettingsByClass(settings);
        settingsOptional.get().setChecked(marked);
    }

    public List<Settings> settings() {
        return new LinkedList<>(classSettingsMap.values());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static Settings getOrDefault(UserSettings userSettings, Class settings) {
        return userSettings.getSettingsByClass(settings).orElse(ALL_SETTINGS.get(settings));
    }

}
