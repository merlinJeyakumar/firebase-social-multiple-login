package com.androidbash.androidbashfirebaseupdated.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private MainActivity mInstance;
    private String TAG = getClass().getSimpleName();
    private AppUtils mAppUtils;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private EditText edEmail;
    private EditText edPassword;
    private Button btnUpdate;
    private Button btnSignout;
    private TextView tvEmail;
    private TextView tvId;
    private boolean isNewUser = false;
    private TextView tvNewUser;
    private TextView tvName;
    private boolean mUserHaveGoogleSignin = false;
    private boolean mUserHaveFacebookSignin = false;
    private boolean mUserHaveEmailSignin = false;
    private int RC_SIGN_IN = 9001;
    private CallbackManager mCallbackManager;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        initInstance();
        initGoogleInstance();
        initData();
        initView();
        initParse();
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
        isNewUser = getIntent().getBooleanExtra("NEW_USER", false);
    }

    private void initView() {
        tvEmail = findViewById(R.id.tvEmail);
        tvName = findViewById(R.id.tvName);
        tvNewUser = findViewById(R.id.tvNewUser);
        tvId = findViewById(R.id.tvId);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnSignout = findViewById(R.id.btnSignout);
    }

    private void initParse() {

    }

    private void initAdapter() {

    }

    private void initListener() {
        btnUpdate.setOnClickListener(this);
        btnSignout.setOnClickListener(this);
        findViewById(R.id.tvConnectFacebook).setOnClickListener(this);
        findViewById(R.id.tvConnectGoogle).setOnClickListener(this);
        findViewById(R.id.tvConnectEmail).setOnClickListener(this);
    }

    private void prepareView() {
        String localNewUser = isNewUser ? "IS_NEW_ACCOUNT : TRUE" : "IS_NEW_ACCOUNT : FALSE";
        String localName = mCurrentUser.getDisplayName();
        String localEmail = mCurrentUser.getEmail();
        String localId = mCurrentUser.getUid();

        tvNewUser.setText(localNewUser);
        tvName.setText(localName);
        tvId.setText(localId);
        tvEmail.setText(localEmail);
        edEmail.setText(localEmail);
        findViewById(R.id.providerGoogle).setVisibility(View.GONE);
        findViewById(R.id.providerFacebook).setVisibility(View.GONE);
        findViewById(R.id.providerFirebase).setVisibility(View.GONE);

        List<? extends UserInfo> infos = mCurrentUser.getProviderData();
        for (UserInfo ui : infos) {
            if (ui.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID)) {
                findViewById(R.id.providerGoogle).setVisibility(View.VISIBLE);
                mUserHaveGoogleSignin = true;
            }

            if (ui.getProviderId().equals(FacebookAuthProvider.PROVIDER_ID)) {
                findViewById(R.id.providerFacebook).setVisibility(View.VISIBLE);
                mUserHaveFacebookSignin = true;
            }

            if (ui.getProviderId().equals(EmailAuthProvider.PROVIDER_ID)) {
                findViewById(R.id.providerFirebase).setVisibility(View.VISIBLE);
                mUserHaveEmailSignin = true;
            }
        }

        if (mUserHaveEmailSignin) {
            findViewById(R.id.tvConnectEmail).setVisibility(View.GONE);
            btnUpdate.setVisibility(View.GONE);
            edEmail.setEnabled(true);
            edPassword.setEnabled(false);
            edPassword.setError(!isNewUser ? "CAN'T EDIT" : null);
        } else {
            findViewById(R.id.tvConnectEmail).setVisibility(View.VISIBLE);
            edEmail.setEnabled(true);
            edPassword.setEnabled(true);
        }

        if (mUserHaveFacebookSignin) {
            findViewById(R.id.tvConnectFacebook).setVisibility(View.GONE);
        }
        if (mUserHaveGoogleSignin) {
            findViewById(R.id.tvConnectGoogle).setVisibility(View.GONE);
        }
    }

    private void mFirebaseLinkwithProvider(AuthCredential credential) {
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            prepareView();
                            mAppUtils.showMessage("linkWithCredential:success", 0);
                            mAppUtils.switchLoading(false, null);
                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            mAppUtils.showMessage("Authentication failed.", 2);
                            mAppUtils.switchLoading(false, null);
                        }
                    }
                });
    }

    private void initEmailLinking() {
        String localEmail = edEmail.getText().toString();
        String localPassword = edPassword.getText().toString();
        boolean localValidation = false;

        if (mUserHaveEmailSignin) {
            localValidation = new Validation().isValidEmail(localEmail) && new Validation().isValidPassword(localPassword, 0);
        } else {
            localValidation = new Validation().isValidEmail(localEmail);
        }

        if (localValidation) {
            if (!mUserHaveEmailSignin) {
                AuthCredential credential = EmailAuthProvider.getCredential(localEmail, localPassword);
                mFirebaseLinkwithProvider(credential);
            } else {
                mAppUtils.showMessage("EMAIL/PASSWORD LOGIN ALSO ENABLED!");
            }
        } else {
            mAppUtils.showMessage("ERROR :" + Validation.VALIDATION_ERROR_MSG);
        }
    }

    private void initFacebookLogin() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(mInstance, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);

                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                mFirebaseLinkwithProvider(credential);
            }

            @Override
            public void onCancel() {
                // App code
                Log.e(TAG, "onCancel: FACEBOOK_CANCELLED");
                mAppUtils.showMessage("FACEBOOK_CANCELLED", 2);
                mAppUtils.switchLoading(false, null);
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
                mAppUtils.showMessage("FACEBOOK_ERROR :" + exception.getLocalizedMessage(), 1);
                mAppUtils.switchLoading(false, null);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdate: {
                initEmailLinking();
                break;
            }
            case R.id.btnSignout: {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(mInstance, LaunchActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            }
            case R.id.tvConnectFacebook: {
                mAppUtils.switchLoading(true, "Accessing facebook..");
                initFacebookLogin();
                break;
            }
            case R.id.tvConnectGoogle: {
                mAppUtils.switchLoading(true, "Accessing google..");
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            }
            case R.id.tvConnectEmail: {
                mAppUtils.showMessage("Fill Email | Password and press Email Linking button!");
                break;
            }
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
                mFirebaseLinkwithProvider(credential);

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
        mAppUtils.switchLoading(false, null);
        mAppUtils.showMessage(connectionResult.getErrorMessage(), 2);
    }
}