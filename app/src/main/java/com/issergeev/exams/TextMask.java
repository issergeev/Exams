package com.issergeev.exams;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.widget.EditText;

public class TextMask implements View.OnKeyListener, TextWatcher {
    private EditText text;
    private String mask;

    private String edit;
    private int length;
    private boolean isDeletePressed = false;

    private BaseInputConnection inputConnection;

    protected TextMask(EditText text, String mask) {
        this.text = text;
        this.mask = mask + "#";

        text.setOnKeyListener(this);
        text.addTextChangedListener(this);

        inputConnection = new BaseInputConnection(text, true);
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
            text.setText(text.getText() + String.valueOf(mask.charAt(length + 1)));
            text.setSelection(text.getText().length());
        }

        try {
            if (mask.charAt(length + 1) != '#' && isDeletePressed) {
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                text.setSelection(text.getText().length());
            }
        } catch (StringIndexOutOfBoundsException e) {}

        isDeletePressed = false;
    }

    @Override
    public void afterTextChanged(Editable editable) {}
}