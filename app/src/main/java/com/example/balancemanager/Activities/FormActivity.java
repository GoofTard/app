package com.example.balancemanager.Activities;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancemanager.forms.InputField;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class FormActivity extends AppCompatActivity {
    private final List<InputField> fieldsToValidate = new LinkedList<>();

    protected final void addValidationField(InputField inputField) {
        this.fieldsToValidate.add(inputField);
    }

    protected final void addValidationFields(InputField... inputField) {
        this.fieldsToValidate.addAll(Arrays.asList(inputField));
    }

    protected final boolean isInputValid() {
        return fieldsToValidate.stream().allMatch(InputField::validate);
    }

    protected abstract void onSubmit(View view);
}
