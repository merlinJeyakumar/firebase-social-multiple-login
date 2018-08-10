package com.androidbash.androidbashfirebaseupdated.Utility;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.androidbash.androidbashfirebaseupdated.R;

public class AppUtils {

    private Context mContext;
    private Activity mActivity;
    private Snackbar snackbar;

    public AppUtils(Context mContext) {
        this.mContext = mContext;
    }

    public AppUtils(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void showMessage(String showingMessage) {
        Snackbar snackbar = Snackbar
                .make(mActivity.findViewById(R.id.coordinator), showingMessage, 3000);
        snackbar.show();
    }

    public void showMessage(String showingMessage, int alertMode) {
        //0 = Success
        //1 = Alert
        //2 = Failed

        Snackbar snackbar = Snackbar
                .make(mActivity.findViewById(R.id.coordinator), showingMessage, 3000);
        if (alertMode == 0) {
            snackbar.getView().setBackgroundColor(mActivity.getResources().getColor(R.color.success));
        } else if (alertMode == 1) {
            snackbar.getView().setBackgroundColor(mActivity.getResources().getColor(R.color.alert));
        } else if (alertMode == 2) {
            softKeyboard(mActivity.getCurrentFocus(), false);
            snackbar.getView().setBackgroundColor(mActivity.getResources().getColor(R.color.failed));
        }
        snackbar.show();
    }

    public void switchLoading(boolean isLoading, String itMessage) {
        if (isLoading) {
            snackbar = Snackbar.make(mActivity.findViewById(R.id.coordinator), "", Snackbar.LENGTH_INDEFINITE);
            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
            //layout.findViewById(android.support.design.R.id.snackbar_text).setVisibility(View.INVISIBLE);
            final View snackView = LayoutInflater.from(mActivity).inflate(R.layout.snackbar_layout, null);
            ((TextView) snackView.findViewById(R.id.tvTextView)).setText(itMessage);
            layout.setPadding(0, 0, 0, 0);
            layout.addView(snackView, 0);
            /*mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);*/
            snackbar.show();
        } else {
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (snackbar != null) {
                snackbar.dismiss();
            }
        }
    }

    public void softKeyboard(View view, boolean toOpenKeyboard) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Service.INPUT_METHOD_SERVICE);
        if (toOpenKeyboard) {
            mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,
                    InputMethodManager.SHOW_IMPLICIT);
        } else {
            mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(
                    view.getWindowToken(), 0);
        }
    }
}
