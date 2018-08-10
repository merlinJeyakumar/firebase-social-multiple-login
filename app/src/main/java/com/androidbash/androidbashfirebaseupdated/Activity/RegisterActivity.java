package com.androidbash.androidbashfirebaseupdated.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidbash.androidbashfirebaseupdated.R;
import com.androidbash.androidbashfirebaseupdated.Utility.AppUtils;
import com.androidbash.androidbashfirebaseupdated.Utility.Utils;
import com.androidbash.androidbashfirebaseupdated.Utility.Validation;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.fabric.sdk.android.Fabric;

public class RegisterActivity extends AppCompatActivity implements OnCompleteListener<AuthResult> {

    private RegisterActivity mInstance;
    private String TAG = getClass().getSimpleName();
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private AppUtils mAppUtils;
    private EditText edEmail;
    private EditText edPassword;
    private Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_layout);

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
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        btnDone = findViewById(R.id.btnDone);
    }

    private void initParse() {
    }

    private void initAdapter() {
    }

    private void initListener() {
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppUtils.switchLoading(true, "Registering..");

                String localEmail = edEmail.getText().toString();
                String localPassword = edPassword.getText().toString();
                if (new Validation().isValidEmail(localEmail) && new Validation().isValidPassword(localPassword, 0)) {
                    mAuth.createUserWithEmailAndPassword(localEmail, localPassword).addOnCompleteListener(mInstance);
                } else {
                    mAppUtils.showMessage("ERROR :" + Validation.VALIDATION_ERROR_MSG);
                }
            }
        });
    }

    private void prepareView() {

    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            mAppUtils.switchLoading(false, null);
            initLogin();
        } else {
            Crashlytics.logException(task.getException());
            Log.e(TAG, "onComplete: task.getException " + task.getException());
            mAppUtils.showMessage(task.getException().getLocalizedMessage());
        }
    }

    private void initLogin() {
        startActivity(new Intent(mInstance, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
