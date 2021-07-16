package com.humzacov.oragniseme.SignIn;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.humzacov.oragniseme.AddReminder.AddReminderActivity;
import com.humzacov.oragniseme.Host.HostActivity;
import com.humzacov.oragniseme.R;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

public class SignInPresenter {

    private final Activity signInActivity; //reference to activity that instantiated presenter
    private final com.humzacov.oragniseme.databinding.ActiviySignInBinding binding; //reference to UI components

    //two objects for google sign-in process
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount gsa;

    private final String TAG = "GSA"; //tag for debugging (GSA = Google Sign-in Account)

    //constructor
    public SignInPresenter(Activity activity, com.humzacov.oragniseme.databinding.ActiviySignInBinding binding) {
        this.signInActivity = activity;
        this.binding = binding;
        requestSignIn();
    }

    protected void requestSignIn() {
        /* Configure sign-in to request the user's ID, email address, and basic
         * profile. ID and basic profile are included in DEFAULT_SIGN_IN.
         * Code from: https://developers.google.com/identity/sign-in/android/sign-in
         */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(signInActivity, gso);
    }

    protected void isSigned() {
        /* Check if the user is already signed in.
         * Launch main activity if signed,
         * and do nothing if not signed. */

        gsa = GoogleSignIn.getLastSignedInAccount(signInActivity);
        if (gsa == null) {
            //if not signed in, let user click button to sign in
            Log.e(TAG,"not signed in");
        } else {
            Log.i(TAG, "Signed in as: " + gsa.getEmail());
            //launch home activity
            launch_home();
            signInActivity.finish(); //prevent user from going back to previous activity
        }
    }

    private void launch_home() {
        //create intent and launch to "home screen" if signed in
        Intent intent = new Intent(signInActivity, HostActivity.class);
        intent.putExtra("user", gsa);
        signInActivity.startActivity(intent);
    }

    private void signIntent() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInActivity.startActivityForResult(signInIntent, 1);
    }

    protected void signed(Intent data) {
        //code edited from https://developers.google.com/identity/sign-in/android/sign-in
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        task.addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<GoogleSignInAccount> task) {
                try {
                    gsa = task.getResult(ApiException.class);
                    launch_home(); //launch home now if sign in successful
                } catch (ApiException e) {
                    // The ApiException status code indicates the detailed failure reason.
                    // Please refer to the GoogleSignInStatusCodes class reference for more information.
                    Log.w(TAG, "signInResult:failed code=" + e);
                }
            }
        });
    }

    protected View.OnClickListener signButtonListener = v -> signIntent();
}
