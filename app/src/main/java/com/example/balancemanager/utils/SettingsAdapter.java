package com.example.balancemanager.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;

import com.example.balancemanager.R;
import com.example.balancemanager.models.GlobalAppData;
import com.example.balancemanager.settings.Settings;

import java.util.List;

public class SettingsAdapter extends ArrayAdapter<Settings> {
    private final Context mContext;
    private final Activity activity;

    public SettingsAdapter(List<Settings> settingsList, Activity activity) {
        super(activity.getApplicationContext(), R.layout.settings_list_item, settingsList);
        this.mContext = activity.getApplicationContext();
        this.activity = activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Settings settings = getItem(position);

        View view = (LayoutInflater.from(mContext)).inflate(R.layout.settings_list_item, null);

        TextView tvText = view.findViewById(R.id.tvText);
        tvText.setText(settings.getDisplayName());

        Switch switchCompat = view.findViewById(R.id.swChecked);
        switchCompat.setChecked(settings.isChecked());

        switchCompat.setOnCheckedChangeListener((compoundButton, b) -> GlobalAppData.instance(activity).updateSettings(activity, settings.getClass(), b));

        return view;
    }
}
