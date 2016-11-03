package com.fivetrue.fivetrueandroid.ui.diaglog;

import android.app.ProgressDialog;
import android.content.Context;

import com.fivetrue.fivetrueandroid.R;

/**
 * Created by kwonojin on 16. 9. 20..
 */
public class LoadingDialog extends ProgressDialog{

    public LoadingDialog(Context context) {
        super(context);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void show() {
        super.show();
        getWindow().setDimAmount(0);
        getWindow().setBackgroundDrawable(getContext().getResources().getDrawable(android.R.color.transparent));
        setTitle(null);
        setMessage(null);
        setIndeterminate(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_loading);
    }
}
