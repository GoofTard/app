package com.example.balancemanager.models;

import com.example.balancemanager.enums.TransactionType;

import java.io.Serializable;
import java.util.Objects;

public class Action implements Serializable {
    private static final long serialVersionUID = 10L;
    private TransactionType type;
    private String name;
    private String categoryName;
    private float funds;
    private String message;

    public Action(TransactionType type, String name, String categoryName, float funds, String message) {
        this.type = type;
        this.name = name;
        this.categoryName = categoryName;
        this.funds = funds;
        this.message = message;
    }

    public TransactionType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public float getFunds() {
        return funds;
    }

    public void setFunds(float funds) {
        this.funds = funds;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return Float.compare(action.funds, funds) == 0 && type == action.type && Objects.equals(name, action.name) && Objects.equals(categoryName, action.categoryName) && Objects.equals(message, action.message);
    }
}
