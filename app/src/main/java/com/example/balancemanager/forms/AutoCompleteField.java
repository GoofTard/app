package com.example.balancemanager.forms;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class AutoCompleteField extends InputField {
    private ArrayAdapter<String> adapter;

    public AutoCompleteField(TextInputLayout textInputLayout, EditText textInputEditText, ArrayAdapter<String> adapter) {
        super(textInputLayout, textInputEditText);

        this.adapter = adapter;
        ((AutoCompleteTextView) textInputEditText).setAdapter(adapter);
    }

    public AutoCompleteField addItem(String item) {
        adapter.add(item);
        adapter.notifyDataSetChanged();

        return this;
    }

    public AutoCompleteField addItem(String item, int index) {
        adapter.insert(item, index);
        adapter.notifyDataSetChanged();

        return this;
    }

    public AutoCompleteField removeItem(String item) {
        adapter.remove(item);
        adapter.notifyDataSetChanged();

        return this;
    }

    @Override
    public InputField setText(String text) {
        ((AutoCompleteTextView) textInputEditText).setText(text, false);

        return this;
    }
}
