package com.issergeev.exams;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;

public class TextMask implements View.OnKeyListener {
    EditText text;
    String mask;

    int length;
    HashMap<Integer, String> chars = new HashMap<>(8);

    public TextMask(EditText text, String mask) {
        this.text = text;
        this.mask = mask;

        text.setOnKeyListener(this);

        for (int i = 0; i < mask.length(); i++){
            if (mask.charAt(i) != '#') {
                chars.put(i, String.valueOf(mask.charAt(i)));
            }
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        length = text.getText().length();
        Log.d("length", String.valueOf(length));

        if (length < text.length() + 1) {
            if (chars.containsKey(length)) {
                text.append(chars.get(length));
            }
        }
        return false;
    }
}