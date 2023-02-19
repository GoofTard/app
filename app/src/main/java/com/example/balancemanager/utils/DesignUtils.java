package com.example.balancemanager.utils;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.example.balancemanager.R;
import com.example.balancemanager.TableLayoutHandlers.SortableTableLayoutHandler;
import com.example.balancemanager.enums.SortDirection;
import com.google.android.material.textfield.TextInputLayout;

public class DesignUtils {
    public static final int[][] STATES = new int[][]{
            new int[]{android.R.attr.state_enabled}, // enabled
            new int[]{-android.R.attr.state_enabled}, // disabled
            new int[]{-android.R.attr.state_checked}, // unchecked
            new int[]{android.R.attr.state_pressed}  // pressed
    };

    public static ColorStateList generateColorStateList(int[] colors) {
        return new ColorStateList(STATES, colors);
    }

    public static int getResourceColor(Activity activity, int color) {
        return activity.getResources().getColor(color);
    }

    public static void setBoxStrokeColor(TextInputLayout til, int color) {
        int[] colors = new int[]{color, color, color, color};
        setBoxStrokeColor(til, colors);
    }

    public static void setBoxStrokeColor(TextInputLayout til, int[] colors) {
        ColorStateList colorStateList = generateColorStateList(colors);

        til.setBoxStrokeColorStateList(colorStateList);
        til.setDefaultHintTextColor(colorStateList);
        til.setEndIconTintList(colorStateList);
    }

    public static int getSortIcon(SortDirection sortDirection) {
        return sortDirection == SortDirection.NONE ?
                R.drawable.baseline_horizontal_rule_24 :
                sortDirection == SortDirection.ASC ?
                        R.drawable.baseline_arrow_upward_24 :
                        R.drawable.baseline_arrow_downward_24;
    }

    public static TextView generateTitle(Activity activity, String title) {
        TextView tv = new TextView(activity);
        tv.setText(title);
        tv.setTextSize(13);
        tv.setTextColor(activity.getResources().getColor(R.color.white));

        return tv;
    }
}
