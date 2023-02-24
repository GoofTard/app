package com.example.balancemanager.forms;

import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InputField {
    protected final TextInputLayout textInputLayout;
    protected final EditText textInputEditText;
    protected final List<FormValidator> validatorList;

    public InputField(TextInputLayout textInputLayout, EditText textInputEditText) {
        this.textInputLayout = textInputLayout;
        this.textInputEditText = textInputEditText;

        this.validatorList = new LinkedList<>();
    }

    public InputField setText(String text) {
        textInputEditText.setText(text);

        return this;
    }

    public InputField setHint(String hint) {
        textInputLayout.setHint(hint);

        return this;
    }

    public InputField setVisibility(int visibility) {
        textInputLayout.setVisibility(visibility);
        textInputEditText.setVisibility(visibility);

        return this;
    }

    public InputField setActive(boolean isActive) {
        textInputLayout.setFocusable(isActive);
        textInputLayout.setClickable(isActive);
        textInputEditText.setFocusable(isActive);
        textInputEditText.setClickable(isActive);

        return this;
    }

    public InputField addValidator(FormValidator validator) {
        this.validatorList.add(validator);

        return this;
    }

    public InputField addValidators(FormValidator... validator) {
        this.validatorList.addAll(Arrays.asList(validator));

        return this;
    }

    public String getText() {
        if (Objects.isNull(textInputEditText.getText())) {
            return "";
        }

        return this.textInputEditText.getText().toString();
    }

    public boolean validate() {
        textInputLayout.setErrorEnabled(false);

        Optional<FormValidator> failedValidations = validatorList.stream()
                .filter(validator -> !validator.getValidator().isValid(getText()))
                .findFirst();

        if (!failedValidations.isPresent()) {
            return true;
        }

        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(failedValidations.get().getErrorText());

        return false;
    }
}
