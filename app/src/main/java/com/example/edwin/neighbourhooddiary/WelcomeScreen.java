package com.example.edwin.neighbourhooddiary;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WelcomeScreen extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, NfcAdapter.OnNdefPushCompleteCallback, NfcAdapter.CreateNdefMessageCallback  {

    Button signedInAsButton;
    GoogleApiClient mGoogleApiClient;
    SignInButton signInButton;
    Button signoutbutton;
    private static final int RC_SIGN_IN = 9001;
    GoogleSignInAccount acct;
    FBDatabaseHelper fbDatabaseHelper;
    DatabaseReference mUserReference;
    ArrayList<User> allUsers = new ArrayList<>();

    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        fbDatabaseHelper = new FBDatabaseHelper();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, (GoogleApiClient.OnConnectionFailedListener) this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();


        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        signoutbutton = (Button) findViewById(R.id.sign_out_button);
        signoutbutton.setOnClickListener(this);

        signedInAsButton = (Button) findViewById(R.id.account_details_button);
        signedInAsButton.setOnClickListener(this);

        mUserReference = FirebaseDatabase.getInstance().getReference().child("users");


        ValueEventListener markerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                loadInUsers(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        mUserReference.addValueEventListener(markerListener);

        //Check if NFC is available on device
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null) {
            //This will refer back to createNdefMessage for what it will send
            mNfcAdapter.setNdefPushMessageCallback(this, this);

            //This will be called if the message is sent successfully
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
        else {
            Toast.makeText(this, "NFC unavailable",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void loadInUsers(@NonNull DataSnapshot dataSnapshot) {
        allUsers.clear();
        for(DataSnapshot ds : dataSnapshot.getChildren() ){
            User user = ds.getValue(User.class);

            allUsers.add(user);

        }
    }

    public void openMapPage(View view){
        Intent mapIntent = new Intent(this, MapsActivity.class);
        mapIntent.putExtra("acct",acct);
        startActivity(mapIntent);
    }

    public void openTrackingPage(View view){
        Intent intent = new Intent(this, displayTracking.class);
        intent.putExtra("acct",acct);
        startActivity(intent);
    }

    public void displayAboutAppDialog(View view){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(WelcomeScreen.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_about_app, null);

        Button dismissButton = (Button) mView.findViewById(R.id.dismissButton);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    public void openAllUsers(View view){


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(WelcomeScreen.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_all_users, null);

        final ListView allUsersListView = (ListView) mView.findViewById(R.id.usersListView);

        List<String> displayNames = new ArrayList<String>();

        for(User u : allUsers){
            displayNames.add(u.getDisplayName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                displayNames );

        allUsersListView.setAdapter(arrayAdapter);

        allUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentName = allUsersListView.getItemAtPosition(position).toString();
                User userToView;
                userToView = getSingleUser(currentName);
                openSingleUser(userToView);
            }
        });

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        Button dButton = (Button) mView.findViewById(R.id.dbutton);

        dButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void openAllGroups(View view){


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(WelcomeScreen.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_all_groups, null);

        final ListView allUsersListView = (ListView) mView.findViewById(R.id.usersListView);

        List<String> displayNames = new ArrayList<String>();

        for(User u : allUsers){
            displayNames.add(u.getDisplayName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                displayNames );

        allUsersListView.setAdapter(arrayAdapter);

        allUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentName = allUsersListView.getItemAtPosition(position).toString();
                User userToView;
                userToView = getSingleUser(currentName);
                openSingleUser(userToView);
            }
        });

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        Button dButton = (Button) mView.findViewById(R.id.dbutton);

        dButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void openAllNews(View view){


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(WelcomeScreen.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_all_news, null);

        final ListView allUsersListView = (ListView) mView.findViewById(R.id.usersListView);

        List<String> displayNames = new ArrayList<String>();

        for(User u : allUsers){
            displayNames.add(u.getDisplayName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                displayNames );

        allUsersListView.setAdapter(arrayAdapter);

        allUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentName = allUsersListView.getItemAtPosition(position).toString();
                User userToView;
                userToView = getSingleUser(currentName);
                openSingleUser(userToView);
            }
        });

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        Button dButton = (Button) mView.findViewById(R.id.dbutton);

        dButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public User getSingleUser(String displayName){
        for(User u : allUsers){
            if(u.getDisplayName() == displayName){
                return u;
            }
        }
        return null;
    }

    public void openSingleUser(User user){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(WelcomeScreen.this);
        View innerView = getLayoutInflater().inflate(R.layout.dialog_user_information, null);

        TextView displayNameText = (TextView) innerView.findViewById(R.id.nameTextView);
        TextView emailText = (TextView) innerView.findViewById(R.id.emailTextView);
        TextView lastLoggedInText = (TextView) innerView.findViewById(R.id.lastLoggedInTV);

        Date loggedInTime = new Date(user.getTime());

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        displayNameText.setText("Name: " + user.getDisplayName());
        emailText.setText("Email: " + user.getEmail());
        lastLoggedInText.setText("Last Logged In : " + df.format(loggedInTime));

        Button dissmissButton = innerView.findViewById(R.id.dismissButton);

        mBuilder.setView(innerView);
        final AlertDialog innerDialog = mBuilder.create();

        dissmissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                innerDialog.dismiss();
            }
        });
        innerDialog.show();
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
        Button dismissButton = (Button) mView.findViewById(R.id.dismissButton);

        displayName.setText(acct.getDisplayName());
        givenName.setText(acct.getGivenName());
        familyName.setText(acct.getFamilyName());
        email.setText(acct.getEmail());

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

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
            boolean matchingAccount = false;

            for(User s : allUsers){
                String sdisplayname = s.getDisplayName();
                String accdis = acct.getDisplayName();
                if(sdisplayname.matches(accdis)){
                    matchingAccount= true;
                }
            }

            if(matchingAccount == false){
                fbDatabaseHelper.writeNewUser(acct.getDisplayName(),acct.getDisplayName(),acct.getEmail());
            }

        } else {
            Toast.makeText(this, "This didn't work", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "OnConnectionFailed: " + connectionResult, Toast.LENGTH_SHORT).show();
    }

    private void recieveNFCIntent(Intent NfcIntent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(NfcIntent.getAction())) {
            Parcelable[] receivedArray =
                    NfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(receivedArray != null) {
                NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
                NdefRecord[] attachedRecords = receivedMessage.getRecords();

                for (NdefRecord r:attachedRecords) {
                    String feedback = new String(r.getPayload());
                    Toast.makeText(this, feedback , Toast.LENGTH_LONG).show();
                    if (feedback.equals(getPackageName())) {
                        continue;
                    }
                }

            }
            else {
                Toast.makeText(this, "Received Nothing", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {


        //This will be called when another NFC capable device is detected.

        //We'll write the createRecords() method in just a moment
        NdefRecord recordToAttach = createRecords();
        //When creating an NdefMessage we need to provide an NdefRecord[]
        return new NdefMessage(recordToAttach);
    }

    public NdefRecord createRecords() {
        //To Create Messages Manually if API is less than
        NdefRecord record;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {

            byte[] payload = "ATM".
                    getBytes(Charset.forName("UTF-8"));

            record = new NdefRecord(
                    NdefRecord.TNF_WELL_KNOWN,      //Our 3-bit Type name format
                    NdefRecord.RTD_TEXT,            //Description of our payload
                    new byte[0],                    //The optional id for our Record
                    payload);                       //Our payload for the Record

        }
        //Api is high enough that we can use createMime, which is preferred.
        else {
            byte[] payload = "ATM".getBytes(Charset.forName("UTF-8"));

            record = NdefRecord.createMime("text/plain",payload);
        }
        return record;
    }


    @Override
    public void onNdefPushComplete(NfcEvent event) {

    }

    @Override
    public void onNewIntent(Intent intent) {
        recieveNFCIntent(intent);
    }

    //Save our Array Lists of Messages for if the user navigates away
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    //Load our Array Lists of Messages for when the user navigates back
    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
