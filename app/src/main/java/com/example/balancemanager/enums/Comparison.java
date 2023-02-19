package com.example.balancemanager.enums;

import java.util.Comparator;

public enum Comparison {
    SMALLER,
    EQUALS,
    BIGGER;

    public static Comparison fromInt(int comparison) {
        return comparison < 0 ? SMALLER : comparison == 0 ? EQUALS : BIGGER;
    }
}
