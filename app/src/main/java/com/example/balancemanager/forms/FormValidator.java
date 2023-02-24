package com.example.balancemanager.forms;

public class FormValidator {
    public static final FormValidator REQUIRED = new FormValidator("Field Cannot Be Empty!", text -> !text.isEmpty());

    private final String errorText;
    private final Validator validator;

    public FormValidator(String errorText, Validator validator) {
        this.errorText = errorText;
        this.validator = validator;
    }

    public String getErrorText() {
        return errorText;
    }

    public Validator getValidator() {
        return validator;
    }
}
