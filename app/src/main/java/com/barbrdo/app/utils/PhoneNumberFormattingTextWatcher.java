package com.barbrdo.app.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
/**
 * Created by parmils on 1/25/16.
 */
public class PhoneNumberFormattingTextWatcher implements TextWatcher {
    private boolean backspacingFlag = false;
    private boolean editedFlag = false;
    private int cursorComplement;
    private EditText mPhoneEditText;
    public PhoneNumberFormattingTextWatcher( EditText mPhoneEditText){
        this.mPhoneEditText=mPhoneEditText;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
       cursorComplement = s.length() - mPhoneEditText.getSelectionStart();
       if (count > after) {
            backspacingFlag = true;
        } else {
            backspacingFlag = false;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        try {
            String string = s.toString();
            String phone = string.replaceAll("[^\\d]", "");
            if (!editedFlag) {
                if (phone.length() >= 6 && !backspacingFlag) {
                    editedFlag = true;
                    String ans = "(" + phone.substring(0, 3) + ") " + phone.substring(3, 6) + "-" + phone.substring(6);
                    mPhoneEditText.setText(ans);
                    mPhoneEditText.setSelection(mPhoneEditText.getText().length() - cursorComplement);
                } else if (phone.length() >= 3 && !backspacingFlag) {
                    editedFlag = true;
                    String ans = "(" + phone.substring(0, 3) + ") " + phone.substring(3);
                    mPhoneEditText.setText(ans);
                    mPhoneEditText.setSelection(mPhoneEditText.getText().length() - cursorComplement);
                }
            } else {
                editedFlag = false;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
