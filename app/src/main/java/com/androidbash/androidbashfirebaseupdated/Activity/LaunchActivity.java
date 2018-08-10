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
import com.androidbash.androidbashfirebaseupdated.Utility.Validation;
import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

public class LaunchActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener<AuthResult>, GoogleApiClient.OnConnectionFailedListener {

    public AppUtils mAppUtils;
    private LaunchActivity mInstance;
    private String TAG = getClass().getSimpleName();
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private EditText edEmail;
    private EditText edPassword;
    private Button btnSignin;
    private Button btnGoogleSignin;
    private Button btnFacebookSignin;
    private Button btnRegister;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        initInstance();
        initData();
        initView();
        initAdapter();
        initListener();
        prepareView();
    }

    private void initInstance() {
        mInstance = this;
        Fabric.with(this, new Crashlytics());
        mAppUtils = new AppUtils(mInstance);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mAppUtils = new AppUtils(mInstance);
        initGoogleInstance();
    }

    private void initGoogleInstance() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mInstance)
                .enableAutoManage(mInstance, mInstance)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(mInstance, gso);
    }

    private void initData() {

    }

    private void initView() {
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        btnSignin = findViewById(R.id.btnSignin);
        btnGoogleSignin = findViewById(R.id.btnGoogleSignin);
        btnFacebookSignin = findViewById(R.id.btnFacebookSignin);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void initAdapter() {

    }

    private void initListener() {
        btnSignin.setOnClickListener(this);
        btnFacebookSignin.setOnClickListener(this);
        btnGoogleSignin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    private void prepareView() {
    }

    private void mProcessEmailSignIn(String localEmail, String localPassword) {
        mAuth.signInWithEmailAndPassword(localEmail, localPassword).addOnCompleteListener(this);
    }

    private void initFacebookLogin() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(mInstance, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);

                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                mFirebaseSignInWithCredential(credential);
            }

            @Override
            public void onCancel() {
                // App code
                Log.e(TAG, "onCancel: FACEBOOK_CANCELLED");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                exception.printStackTrace();
                if (exception instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        });
    }

    private void mFirebaseSignInWithCredential(AuthCredential credential) {
        //AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken()); -FACEBOOK
        //AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);  -GOOGLE

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            String localUserNametask = task.getResult().getAdditionalUserInfo().getUsername();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.e(TAG, "signInWithCredential:success " + user.getUid() + " isNewUser:" + isNewUser + " localUserNametask:" + localUserNametask);

                            Intent mainIntent = new Intent(mInstance, MainActivity.class);
                            mainIntent.putExtra("NEW_USER", isNewUser);
                            initLogin(true, mainIntent);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            mAppUtils.showMessage(task.getException().getLocalizedMessage(), 2);
                        }

                        mAppUtils.switchLoading(false, null);
                    }
                });
    }

    private void initLogin(Boolean mManualIntent, Intent mStartIntent) {
        if (mManualIntent) {
            startActivity(mStartIntent);
        } else {
            startActivity(new Intent(mInstance, MainActivity.class));
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignin: {
                mAppUtils.switchLoading(true, "SigningIn..");
                String localEmail = edEmail.getText().toString();
                String localPassword = edPassword.getText().toString();
                if (new Validation().isValidEmail(localEmail) && new Validation().isValidPassword(localPassword, 0)) {
                    mProcessEmailSignIn(localEmail, localPassword);
                } else {
                    mAppUtils.showMessage("ERROR :" + Validation.VALIDATION_ERROR_MSG);
                }
                break;
            }
            case R.id.btnGoogleSignin: {
                mAppUtils.switchLoading(true, "Accessing google..");
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            }
            case R.id.btnFacebookSignin: {
                mAppUtils.switchLoading(true, "Loading..");
                initFacebookLogin();
                break;
            }
            case R.id.btnRegister: {
                startActivity(new Intent(mInstance, RegisterActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            }
        }
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            mAppUtils.switchLoading(false, null);
            initLogin(false, null);
        } else {
            Crashlytics.logException(task.getException());
            Log.e(TAG, "onComplete: task.getException " + task.getException());
            mAppUtils.showMessage(task.getException().getLocalizedMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null); //gettingGoogleSigninToken
                mFirebaseSignInWithCredential(credential);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                Log.e(TAG, "Google sign in failed_callback", e);
                mAppUtils.showMessage(e.getLocalizedMessage(), 2);

            }
        }
        if (mCallbackManager != null) {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mAppUtils.showMessage(connectionResult.getErrorMessage(), 2);
    }
}
