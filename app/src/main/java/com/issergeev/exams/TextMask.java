package com.issergeev.exams;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class TextMask implements View.OnKeyListener, TextWatcher {
    EditText text;
    String mask;

    private int length;
    private boolean isDeletePressed = false;

    public TextMask(EditText text, String mask) {
        this.text = text;
        this.mask = mask + "#";

        text.setOnKeyListener(this);
        text.addTextChangedListener(this);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            isDeletePressed = true;
        } else {
            isDeletePressed = false;
        }

        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int count, int i2) {
        if (charSequence.length() == 0) {
            length = 0;
        } else {
            length = charSequence.length() - 1;
        }

        if (mask.charAt(length + 1) != '#' && !isDeletePressed) {
            text.setText(text.getText() + mask.substring(length + 1, length + 2));
            text.setSelection(text.getText().length());
        }

        if (mask.charAt(length + 1) != '#' && isDeletePressed) {
            text.setText(text.getText().toString().substring(0, length));
            text.setSelection(text.getText().length());
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {}
}