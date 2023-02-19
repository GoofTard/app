package com.example.balancemanager.utils;

import java.text.DecimalFormat;

public class StringUtils {
    public static String getLimitString(float limit, float funds) {
        if (limit == -1) {
            return "-";
        }

        float percentage = funds / (limit / 100);

        return String.format("%s\n(%s)",
                formatPrice(limit),
                addPostfix("%", new DecimalFormat("0.00").format(percentage)));
    }

    public static String addPrefix(String prefix, String value) {
        return String.format("%s%s", prefix, value);
    }

    public static String addPostfix(String postfix, String value) {
        return String.format("%s%s", value, postfix);
    }

    public static String formatPrice(float value) {
        return addPrefix("₪", String.format("%.2f", value));
    }
}
