package com.example.balancemanager.models;

import com.example.balancemanager.enums.TransactionType;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 2L;
    private final Date date;
    private final TransactionType type;
    private String categoryName;
    private final float funds;
    private final String message;

    public Transaction(Date date, TransactionType type, String categoryName, float funds, String message) {
        this.date = date;
        this.type = type;
        this.categoryName = categoryName;
        this.funds = funds;
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public TransactionType getType() {
        return type;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public float getFunds() {
        return funds;
    }

    public String getMessage() {
        return message;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
