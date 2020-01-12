package com.joyn.me.utils;

import android.support.annotation.StringRes;
import android.view.View;


public interface SnackbarController {
    void showSnackbar(@StringRes int stringRes, int duration, @StringRes int actionResText, View.OnClickListener onClickListener);
}
