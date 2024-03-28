package com.example.remart;

import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;


import androidx.appcompat.app.AppCompatActivity; //alt entered
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private CollectionReference ListingRef;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ListingAdapter adapter;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(Listener);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);


        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        recyclerView = findViewById(R.id.task_recycler_view);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }

            }
        };

        //Get Firebase firestore reference
        db = FirebaseFirestore.getInstance();

        ListingRef = db.collection("All_Listings");
        Log.e(TAG,"Setting up recycle view");
        setUpRecyclerView();
        Log.e(TAG,"Already set up recycle view");
    }


    private void setUpRecyclerView() {

        Query query = ListingRef.orderBy("listingTitle");

        FirestoreRecyclerOptions<Listings> options = new FirestoreRecyclerOptions.Builder<Listings>()
                .setQuery(query, Listings.class)
                .build();


        Log.e(TAG,"BEFORE");
        adapter = new ListingAdapter(options);
        Log.e(TAG,"AFTER");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ListingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

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
                            break;
                        case R.id.sell:
                            Intent sell = new Intent(getApplicationContext(), AddListingActivity.class);
                            startActivity(sell);
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