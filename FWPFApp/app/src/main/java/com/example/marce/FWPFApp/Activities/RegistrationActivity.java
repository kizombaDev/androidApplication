package com.example.marce.FWPFApp.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.marce.FWPFApp.Helper.Globals;
import com.example.marce.FWPFApp.R;
import com.example.marce.FWPFApp.ServerCommunication.Requests.RegisterMyselfAndGetMyIdPostRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {

    private UserRegisterTask registerTask = null;

    private EditText userNameView;
    private EditText phoneNumberView;
    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        userNameView = (EditText) findViewById(R.id.userName);

        phoneNumberView = (EditText) findViewById(R.id.phoneNumber);
        phoneNumberView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.registerButton);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        setPhoneNumber();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        registerTask = null;
        phoneNumberView = null;
        userNameView = null;
        mLoginFormView = null;
        mProgressView = null;
    }

    private void setPhoneNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = telephonyManager.getLine1Number();
        phoneNumberView.setText(mPhoneNumber);
    }

    private void attemptRegister() {
        if (registerTask != null) {
            return;
        }

        // Reset errors.
        userNameView.setError(null);
        phoneNumberView.setError(null);

        // Store values at the time of the login attempt.
        String userName = userNameView.getText().toString();
        String phoneNumber = phoneNumberView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid phoneNumber, if the user entered one.
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberView.setError(getString(R.string.error_field_required));
            focusView = phoneNumberView;
            cancel = true;
        } else if (!isPhoneNumberValid(phoneNumber)) {
            phoneNumberView.setError(getString(R.string.error_invalid_phoneNumber));
            focusView = phoneNumberView;
            cancel = true;
        }

        // Check for a valid userName address.
        if (TextUtils.isEmpty(userName)) {
            userNameView.setError(getString(R.string.error_field_required));
            focusView = userNameView;
            cancel = true;
        } else if (!isUserNameValid(userName)) {
            userNameView.setError(getString(R.string.error_invalid_userName));
            focusView = userNameView;
            cancel = true;
        } else if (!isUserNameTooShort(userName)) {
            userNameView.setError(getString(R.string.error_too_short_userName));
            focusView = userNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            registerTask = new UserRegisterTask(userName, phoneNumber);
            registerTask.execute((Void) null);
        }
    }


    private boolean isUserNameValid(String userName) {
        return true;
    }

    private boolean isUserNameTooShort(String userName) {
        return userName.length() > 2;
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        return PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean>  {

        private final String userName;
        private final String phoneNumber;

        JSONObject registerRequestResponseJsonWithMyId;

        UserRegisterTask(String userName, String phoneNumber) {
            this.userName = userName;
            this.phoneNumber = phoneNumber;
            //this.requestResponseJsonWithMyId = null;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
                RegisterMyselfAndGetMyIdPostRequest registerMyselfAndGetMyIdPostRequest = new RegisterMyselfAndGetMyIdPostRequest(userName, phoneNumber);
                registerRequestResponseJsonWithMyId = registerMyselfAndGetMyIdPostRequest.execute();

            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            registerTask = null;
            showProgress(false);

            if (success) {
                String myId = "-1";
                try {
                    myId = registerRequestResponseJsonWithMyId.getString("Id");
                }
                catch(JSONException e){
                    e.printStackTrace();
                }

                SharedPreferences settings = getSharedPreferences(Globals.settingFile(), MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(Globals.setttingUserName(), userName);
                editor.putString(Globals.setttingPhoneNumber(), phoneNumber);
                editor.putBoolean(Globals.setttingIsUserRegistered(), true);
                editor.putString(Globals.settingUserId(), myId);
                editor.commit();

                setResult(Activity.RESULT_OK);
                finish();
            } else {
                TextView registerErrorView = (TextView) findViewById(R.id.registerErrorTextView);
                registerErrorView.setText("Es gab einen Fehler");
            }
        }

        @Override
        protected void onCancelled() {
            registerTask = null;
            showProgress(false);
        }
    }
}

