package com.android.margintop.loginpage;


import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by L on 2017/3/31.
 *
 * @描述 文本监听适配器。
 */

public abstract class TextWatcherAdapter implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

}
