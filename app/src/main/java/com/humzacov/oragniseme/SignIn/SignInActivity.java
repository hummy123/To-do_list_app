package com.humzacov.oragniseme.SignIn;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.humzacov.oragniseme.databinding.ActiviySignInBinding;

public class SignInActivity extends AppCompatActivity {
    private SignInPresenter signInPresenter; //stores presenter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //view binding object, so no need for findViewByID()
        ActiviySignInBinding binding = ActiviySignInBinding.inflate(getLayoutInflater()); //set binding object
        setContentView(binding.getRoot());

        signInPresenter = new SignInPresenter(this, binding); //instantiate presenter
        binding.signInButton.setOnClickListener(signInPresenter.signButtonListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        signInPresenter.isSigned(); //check if signed in
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* only StartActivityForResult call in SignInActivity
         * is to sign in; no need for result code. */
        signInPresenter.isSigned();
    }
}