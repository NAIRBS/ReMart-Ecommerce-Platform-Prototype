package com.example.remart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;


import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity; //alt entered

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class AddListingActivity extends AppCompatActivity {

    private Button addlisting;

    private EditText title,price,description;
    private TextView username_filler;
    private ProgressBar progressBar;
    FirebaseFirestore db;
    private FirebaseAuth.AuthStateListener authListener;
    FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth auth;

    private static final String TAG = "AddListingActivity";

    private String docId;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_listing);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(Listener);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        username_filler = findViewById(R.id.username_filler);



        title = findViewById(R.id.Listing_title);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);

        addlisting = findViewById(R.id.addlisting);


        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //Get Firestore Database reference
        db = FirebaseFirestore.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(AddListingActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        getUserName();


        addlisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String listtitle = title.getText().toString().trim();
                String cost = price.getText().toString().trim();
                String descriptions = description.getText().toString().trim();

                double numcost;
                numcost = Double.parseDouble(cost);

                if (TextUtils.isEmpty(listtitle)) {
                    Toast.makeText(getApplicationContext(), "Enter your Listing Title!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(cost)) {
                    Toast.makeText(getApplicationContext(), "Enter the cost of your listing!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cost.equals("0")) {
                    Toast.makeText(getApplicationContext(), "Your Listing Price should be more than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!(numcost > 0))
                {
                    Toast.makeText(getApplicationContext(), "Your Listing Price should be more than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cost.startsWith(".")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid Listing Price", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(descriptions)) {
                    descriptions = "";
                }

                //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddListingActivity.this, R.style.AlertDialogCustom);

                // set title
                alertDialogBuilder.setTitle("Confirmation");
                alertDialogBuilder.setIcon(R.drawable.mart);

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to create a listing?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, create listing
                                AddListingOnAuth();
                                startActivity(new Intent(AddListingActivity.this, MainActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });
    }



    private void AddListingOnAuth() {
        String uid = auth.getUid();

        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String email = task.getResult().getString("email");
                    String password = task.getResult().getString("password");
                    // Get auth credentials from the user for re-authentication
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password); // Current Login Credentials \\
                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), "Listing Created", Toast.LENGTH_SHORT).show();
                                    addListingToFireStore();
                                }
                            });
                }
            }
        });
    }

    public void getUserName(){

        String Uid = auth.getUid();
        String UserName;


        db.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final String names = (task.getResult().getString("name"));
                    Log.e(TAG,names);
                    username_filler.setText(names);
                    Log.e(TAG,"Obtained username");
                }
                else
                {
                    Log.e(TAG,"Could not obtain username");
                }
            }
        });
    }


    private void addListingToFireStore() {
        //FirebaseUser user = auth.getCurrentUser();

        String listtitle = title.getText().toString().trim();
        String costStr = price.getText().toString().trim();
        double cost;
        cost = Double.parseDouble(costStr);
        String descriptions = description.getText().toString().trim();

        String Uid = auth.getUid();

        UID = Uid;

        final String Username;

        Username = username_filler.getText().toString().trim();

        Listings list = new Listings(listtitle,cost,descriptions);



        /*
        db.collection("Listings")
                .document(Uid)
                .collection(Username)
                .document(listtitle)
                .set(list);
        */


        db.collection("Listings")
                .document(Uid)
                .collection(Username)
                .add(list)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        docId = documentReference.getId();

                        ListingID ListingID = new ListingID(docId);

                       // Toast.makeText(getApplicationContext(), docId, Toast.LENGTH_SHORT).show();

                        db.collection("Users")
                                .document(UID)
                                .collection("Listings")
                                .document("Current Listings")
                                .set(ListingID);


                        String listtitle = title.getText().toString().trim();
                        String costStr = price.getText().toString().trim();
                        double cost;
                        cost = Double.parseDouble(costStr);
                        String descriptions = description.getText().toString().trim();

                        Listings list = new Listings(listtitle,cost,descriptions);
                        db.collection("All_Listings").document(docId).set(list);
                    }
                });



    }




    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }




    //Bottom Navigation View code
    private BottomNavigationView.OnNavigationItemSelectedListener Listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    //Activity selectedActivity = null;

                    switch(item.getItemId()) {
                        case R.id.home:
                            Intent home = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(home);
                            break;
                        case R.id.sell:
                            break;
                        case R.id.account:
                            Intent account = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(account);
                            break;
                    }

                    return true;
                }
            };

}