package com.androidbash.androidbashfirebaseupdated.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.utility.AlertDialogInterface;
import com.androidbash.androidbashfirebaseupdated.R;
import com.androidbash.androidbashfirebaseupdated.Utility.AppUtils;
import com.androidbash.androidbashfirebaseupdated.Utility.Utils;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    private SplashActivity mInstance;
    private String TAG = getClass().getSimpleName();
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private AppUtils mAppUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initInstance();
        initData();
        initView();
        initParse();
        initAdapter();
        initListener();
        prepareView();
    }

    private void initInstance() {
        mInstance = this;
        mInstance = this;
        Fabric.with(this, new Crashlytics());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mAppUtils = new AppUtils(mInstance);
    }

    private void initData() {
        new Utils().printHashKey(mInstance);
    }

    private void initView() {
    }

    private void initParse() {
    }

    private void initAdapter() {
    }

    private void initListener() {
    }

    private void prepareView() {
        if (mCurrentUser != null) {
            com.android.utility.Utils.showMultiButtonAlertDialog(mInstance, "Continue", "Logout", "Logged", "User Logged Previously", new AlertDialogInterface() {
                @Override
                public void positiveButtonPressed() {
                    startActivity(new Intent(mInstance, MainActivity.class));
                    finish();
                }

                @Override
                public void negativeButtonPressed() {
                    mAuth.signOut();
                    startActivity(new Intent(mInstance, LaunchActivity.class));
                    finish();
                }
            });
        } else {
            startActivity(new Intent(mInstance, LaunchActivity.class));
            finish();
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
