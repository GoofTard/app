package com.example.balancemanager.models;

import java.io.Serializable;

public class Category implements Serializable {
    public static final String NO_CATEGORY = "-";
    private static final long serialVersionUID = 3L;
    private String name;
    private float funds;
    private float split;
    private float limit;
    private boolean locked;

    public Category(String name, float split, float limit, boolean locked) {
        this.name = name;
        this.split = split;
        this.limit = limit;
        this.locked = locked;

        this.funds = 0;
    }

    public Category(String name, float split, float limit, boolean locked, float funds) {
        this.name = name;
        this.split = split;
        this.limit = limit;
        this.locked = locked;

        this.funds = funds;
    }

    public void addFunds(float funds) {
        this.funds += funds;
    }

    public void useFunds(float funds) {
        this.funds -= funds;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public float getFunds() {
        return funds;
    }

    public float getSplit() {
        return split;
    }

    public float getLimit() {
        return limit;
    }

    public boolean isLocked() {
        return locked;
    }

    public void clearFunds() {this.funds = 0;}
}
