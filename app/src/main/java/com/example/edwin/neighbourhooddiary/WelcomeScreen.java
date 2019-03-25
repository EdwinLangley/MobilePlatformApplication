package com.example.edwin.neighbourhooddiary;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.EditText;
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
import java.util.Arrays;
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
    DatabaseReference mGroupReference;
    DatabaseReference mNewsReference;
    ArrayList<User> allUsers = new ArrayList<>();
    ArrayList<Group> allGroups = new ArrayList<>();
    ArrayList<News> allNews = new ArrayList<>();

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
        mGroupReference = FirebaseDatabase.getInstance().getReference().child("groups");
        mNewsReference = FirebaseDatabase.getInstance().getReference().child("news");


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

        ValueEventListener groupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                loadInGroups(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        mGroupReference.addValueEventListener(groupListener);

        ValueEventListener newsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                loadInNews(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        mNewsReference.addValueEventListener(newsListener);

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

    private void loadInGroups(@NonNull DataSnapshot dataSnapshot) {
        allGroups = new ArrayList<Group>();
        for(DataSnapshot ds : dataSnapshot.getChildren() ){
            Group group = ds.getValue(Group.class);

            allGroups.add(group);

        }
    }

    private void loadInNews(@NonNull DataSnapshot dataSnapshot) {
        allNews = new ArrayList<News>();
        for(DataSnapshot ds : dataSnapshot.getChildren() ){
            News news = ds.getValue(News.class);

            allNews.add(news);

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

        final ListView allUsersListView = (ListView) mView.findViewById(R.id.NewsListView);

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

        final ListView allGroupsListView = (ListView) mView.findViewById(R.id.NewsListView);

        final Button newGroupButton = (Button) mView.findViewById(R.id.SwapToAddButton);

        final List<String> displayNames = new ArrayList<String>();

        for(Group g : allGroups){
            displayNames.add(g.getGroupName());
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                displayNames );

        allGroupsListView.setAdapter(arrayAdapter);

        allGroupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentName = allGroupsListView.getItemAtPosition(position).toString();

                Group passgroup = new Group();

                for(Group g : allGroups){
                    if(g.getGroupName().equals(currentName)){
                        passgroup = g;
                    }
                }

                openSingleGroup(passgroup);
            }
        });

        newGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(WelcomeScreen.this);
                View innerView = getLayoutInflater().inflate(R.layout.dialog_add_new_group, null);

                final EditText groupNameEditText = (EditText) innerView.findViewById(R.id.newsNameEditText);
                final EditText groupDescEditText = (EditText) innerView.findViewById(R.id.newsDesEditText);

                Button saveGroupButton = innerView.findViewById(R.id.submitGroupButton);

                mBuilder.setView(innerView);
                final AlertDialog innerDialog = mBuilder.create();

                saveGroupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(WelcomeScreen.this, groupNameEditText.getText(), Toast.LENGTH_SHORT).show();

                        String groupName = groupNameEditText.getText().toString();
                        String groupDesc = groupDescEditText.getText().toString();

                        Group newGroup = new Group();

                        newGroup.setGroupName(groupName);
                        newGroup.setDescription(groupDesc);
                        newGroup.setMembers(acct.getDisplayName());

                        mGroupReference.child(groupName).setValue(newGroup);


                        ArrayList<String> displaynames2 = new ArrayList<>();

                        for(Group g : allGroups){
                            displaynames2.add(g.getGroupName());
                        }

                        displaynames2.add(newGroup.getGroupName());

                        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(
                                WelcomeScreen.this,
                                android.R.layout.simple_list_item_1,
                                displaynames2 );

                        allGroupsListView.setAdapter(arrayAdapter1);

                        innerDialog.dismiss();

                    }
                });
                innerDialog.show();
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


    public void openAddNewGroup(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(WelcomeScreen.this);
        View innerView = getLayoutInflater().inflate(R.layout.dialog_add_new_group, null);

        final EditText groupNameEditText = (EditText) innerView.findViewById(R.id.newsNameEditText);
        final EditText groupDescEditText = (EditText) innerView.findViewById(R.id.newsDesEditText);

        Button saveGroupButton = innerView.findViewById(R.id.submitGroupButton);

        mBuilder.setView(innerView);
        final AlertDialog innerDialog = mBuilder.create();

        saveGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(WelcomeScreen.this, groupNameEditText.getText(), Toast.LENGTH_SHORT).show();

                String groupName = groupNameEditText.getText().toString();
                String groupDesc = groupDescEditText.getText().toString();

                Group newGroup = new Group();

                newGroup.setGroupName(groupName);
                newGroup.setDescription(groupDesc);
                newGroup.setMembers(acct.getDisplayName());


                mGroupReference.child(groupName).setValue(newGroup);
                innerDialog.dismiss();
            }
        });
        innerDialog.show();
    }

    public void openAddNews(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(WelcomeScreen.this);
        View innerView = getLayoutInflater().inflate(R.layout.dialog_add_news, null);

        final EditText newsNameEditText = (EditText) innerView.findViewById(R.id.newsNameEditText);
        final EditText newsDescEditText = (EditText) innerView.findViewById(R.id.newsDesEditText);

        Button saveGroupButton = innerView.findViewById(R.id.submitNewsButton);

        mBuilder.setView(innerView);
        final AlertDialog innerDialog = mBuilder.create();

        saveGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(WelcomeScreen.this, groupNameEditText.getText(), Toast.LENGTH_SHORT).show();

                String newsName = newsNameEditText.getText().toString();
                String newsDesc = newsDescEditText.getText().toString();

                News newNews = new News();

                newNews.setTopic(newsName);
                newNews.setNews(newsDesc);
                newNews.setPostedBy(acct.getDisplayName());

                mNewsReference.child(newsName).setValue(newNews);

                innerDialog.dismiss();
            }
        });
        innerDialog.show();
    }

    public void openAllNews(View view){


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(WelcomeScreen.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_all_news, null);

        final ListView allUsersListView = (ListView) mView.findViewById(R.id.NewsListView);

        List<String> displayNames = new ArrayList<String>();

        for(News n : allNews){
            displayNames.add(n.getTopic() + " - by " + n.getPostedBy());
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

                News newsitem = new News();

                for(News n : allNews){
                    if((n.getTopic() + " - by " + n.getPostedBy()).equals(currentName)){
                        newsitem = n;
                    }
                }

                openSingleNews(newsitem);
            }
        });

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        Button dButton = (Button) mView.findViewById(R.id.dbutton);

        Button addButton = (Button) mView.findViewById(R.id.SwapToAddButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddNews();
            }
        });

        dButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void openSingleNews(News news){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(WelcomeScreen.this);
        View innerView = getLayoutInflater().inflate(R.layout.dialog_single_news, null);

        final TextView groupNameEditText = (TextView) innerView.findViewById(R.id.groupTitle);
        final TextView groupDescEditText = (TextView) innerView.findViewById(R.id.groupDesc);

        groupNameEditText.setText(news.getTopic());
        groupDescEditText.setText(news.getNews());

        Button dissButton = innerView.findViewById(R.id.dbutton);

        mBuilder.setView(innerView);
        final AlertDialog innerDialog = mBuilder.create();

        dissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                innerDialog.dismiss();
            }
        });
        innerDialog.show();
    }

    public String addNameToList(String listnames, String name){

        String[] splitlist = listnames.split("§");

        List<String> wordList1 = Arrays.asList(splitlist);

        ArrayList<String> wordList = new ArrayList(wordList1);

        wordList.add(name);

        String returnString = "";

        for(String s : wordList){
            returnString+= s + "§";
        }

        return returnString;

    }

    public String removeNameFromList(String listnames, String name){
        String[] splitlist = listnames.split("§");

        List<String> wordList1 = Arrays.asList(splitlist);

        ArrayList<String> wordList = new ArrayList(wordList1);

        int elementToRemove = 0;

        for(int i = 0; i < wordList.size(); i++){
            if(wordList.get(i).equals(name)){
                elementToRemove = i;
            }
        }

        wordList.remove(elementToRemove);

        String returnString = "";

        for(String s : wordList){
            returnString+= s + "§";
        }

        return returnString;

    }

    public void openSingleGroup(final Group group){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(WelcomeScreen.this);
        View innerView = getLayoutInflater().inflate(R.layout.dialog_single_group, null);

        final TextView groupNameEditText = (TextView) innerView.findViewById(R.id.groupTitle);
        final TextView groupDescEditText = (TextView) innerView.findViewById(R.id.groupDesc);

        final Button joinLeaveButton = (Button) innerView.findViewById(R.id.joinOrLeave);

        if(group.getMembers().contains(acct.getDisplayName())){
            joinLeaveButton.setText("Leave Group");
            joinLeaveButton.setBackgroundColor(Color.RED);
        } else {
            joinLeaveButton.setText("Join Group");
            joinLeaveButton.setBackgroundColor(Color.GREEN);

        }

        joinLeaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(joinLeaveButton.getText().equals("Leave Group")){
                    String toWrite = removeNameFromList(group.getMembers(),acct.getDisplayName());
                    mGroupReference.child(group.getGroupName()).child("members").setValue(toWrite);
                    joinLeaveButton.setText("Join Group");
                    joinLeaveButton.setBackgroundColor(Color.GREEN);
                } else {
                    String toWrite = addNameToList(group.getMembers(),acct.getDisplayName());
                    mGroupReference.child(group.getGroupName()).child("members").setValue(toWrite);
                    joinLeaveButton.setText("Leave Group");
                    joinLeaveButton.setBackgroundColor(Color.RED);
                }
            }
        });

        groupNameEditText.setText(group.getGroupName());
        groupDescEditText.setText(group.getDescription());

        Button dissButton = innerView.findViewById(R.id.dbutton);

        mBuilder.setView(innerView);
        final AlertDialog innerDialog = mBuilder.create();

        dissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                innerDialog.dismiss();
            }
        });
        innerDialog.show();
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
