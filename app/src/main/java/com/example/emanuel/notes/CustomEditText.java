package com.example.emanuel.notes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

public class CustomEditText extends EditText {

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public CustomEditText(Context context) {
        super(context);

    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        NoteSQLHelper sqlHelper = NoteSQLHelper.getInstance(getContext());
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // update data here
            Toast.makeText(getContext(), "Changes Saved", Toast.LENGTH_SHORT).show();
            dispatchKeyEvent(event);
            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
