package com.example.edwin.neighbourhooddiary;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class WelcomeScreen extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener  {

    Button signedInAsButton;
    GoogleApiClient mGoogleApiClient;
    SignInButton signInButton;
    Button signoutbutton;
    private static final int RC_SIGN_IN = 9001;
    GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, (GoogleApiClient.OnConnectionFailedListener) this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();


        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        signoutbutton = (Button) findViewById(R.id.sign_out_button);
        signoutbutton.setOnClickListener(this);

        signedInAsButton = (Button) findViewById(R.id.account_details_button);
        signedInAsButton.setOnClickListener(this);
    }

    public void openMapPage(View view){
        Intent mapIntent = new Intent(this, MapsActivity.class);
        startActivity(mapIntent);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_in_button:
                signIn();
                break;

            case R.id.sign_out_button:
                signOut();
                break;

            case R.id.account_details_button:
                Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
                displayAccountInformationDialog();
                break;
        }
    }

    private void displayAccountInformationDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(WelcomeScreen.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_account_info, null);

        TextView displayName = (TextView) mView.findViewById(R.id.displayName);
        TextView givenName  = (TextView) mView.findViewById(R.id.givenName);
        TextView familyName = (TextView) mView.findViewById(R.id.familyName);
        TextView email = (TextView) mView.findViewById(R.id.email);

        displayName.setText(acct.getDisplayName());
        givenName.setText(acct.getGivenName());
        familyName.setText(acct.getFamilyName());
        email.setText(acct.getEmail());

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();


    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Toast.makeText(WelcomeScreen.this, "Signed out", Toast.LENGTH_SHORT).show();
                signedInAsButton.setVisibility(View.GONE);
                signInButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            acct = result.getSignInAccount();
            Toast.makeText(this, "Hello " + acct.getDisplayName() , Toast.LENGTH_SHORT).show();
            signInButton.setVisibility(View.GONE);
            signedInAsButton.setVisibility(View.VISIBLE);
            signedInAsButton.setText("Signed In As " + acct.getDisplayName());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "OnConnectionFailed: " + connectionResult, Toast.LENGTH_SHORT).show();
    }
}
