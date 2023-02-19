package com.example.balancemanager.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.balancemanager.R;
import com.example.balancemanager.models.GlobalAppData;
import com.example.balancemanager.settings.RedistributeOnFilled;
import com.example.balancemanager.settings.Settings;
import com.example.balancemanager.utils.SettingsAdapter;

import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ListView settingsView = findViewById(R.id.settingsView);

        ListAdapter adapter = new SettingsAdapter(GlobalAppData.instance(this).getSettings(), this);

        settingsView.setAdapter(adapter);
    }
}