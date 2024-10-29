package com.duytai.cse441_project;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SimpleTextWatcher implements TextWatcher {
    private final EditText editText;
    private final TextView errorTextView;

    public SimpleTextWatcher(EditText editText, TextView errorTextView) {
        this.editText = editText;
        this.errorTextView = errorTextView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        String inputText = s.toString().trim();

        if (editText.getId() == R.id.et_phone_number) {
            if (inputText.isEmpty()) {
                errorTextView.setText("Vui lòng nhập số điện thoại");
                errorTextView.setVisibility(View.VISIBLE);
            } else if (!inputText.matches("^\\d{10}$")) {
                errorTextView.setText("Số điện thoại không hợp lệ");
                errorTextView.setVisibility(View.VISIBLE);
            } else {
                errorTextView.setVisibility(View.GONE);
            }
        } else if (editText.getId() == R.id.et_password) {
            if (inputText.isEmpty()) {
                errorTextView.setText("Vui lòng nhập mật khẩu");
                errorTextView.setVisibility(View.VISIBLE);
            } else if (inputText.length() < 8) {
                errorTextView.setText("Mật khẩu phải có ít nhất 8 ký tự");
                errorTextView.setVisibility(View.VISIBLE);
            } else {
                errorTextView.setVisibility(View.GONE);
            }
        }
    }
}
