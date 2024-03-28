package com.example.remart;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    TextView VerifyOldPass;
    //private static final int CHOOSE_IMAGE = 101;
    CircleImageView circleImageView, editImage;
    EditText editName, editEmail, editPhoneNumber;
    TextView PName, Email, PNum;
    TextView NewPass, CfmNewPass, OldPass;
    EditText NewPassword, ConfirmNewPassword, OldPassword;
    Button updateProfile, editpassword, logout, deactivateaccount, backbutton;
    Uri uriProfileImage;
    ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    private StorageReference profileImageRef, storageRef;
    private static final String TAG = "ProfileActivity";
    private FirebaseAuth.AuthStateListener authListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        progressBar = findViewById(R.id.progressbar);


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(Listener);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        //Get Firebase current user
        user = auth.getCurrentUser();

        //Get Firestore Database reference
        db = FirebaseFirestore.getInstance();

        progressBar.setVisibility(View.VISIBLE);

        displayUserInfo();
        displayImage();

        progressBar.setVisibility(View.GONE);


        PName = findViewById(R.id.PName);
        Email = findViewById(R.id.Email);
        PNum = findViewById(R.id.PNum);

        editImage = findViewById(R.id.edit_image);
        circleImageView = findViewById(R.id.profile_image);
        editName = findViewById(R.id.profileName);
        editEmail = findViewById(R.id.email);
        editPhoneNumber = findViewById(R.id.phoneNumber);


        NewPass = findViewById(R.id.New_Pass);
        CfmNewPass = findViewById(R.id.CfmNewPass);
        OldPass = findViewById(R.id.OldPass);

        NewPassword = findViewById(R.id.New_Password);
        ConfirmNewPassword = findViewById(R.id.Confirm_New_Password);
        OldPassword = findViewById(R.id.Old_Password);

        updateProfile = findViewById(R.id.button_updateProfile);
        editpassword = findViewById(R.id.button_Change_Password);
        deactivateaccount = findViewById(R.id.button_Deactivate);
        logout = findViewById(R.id.button_logout);

        VerifyOldPass = findViewById(R.id.padding);
        backbutton = findViewById(R.id.button_back_profile);

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChoose();
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this, R.style.AlertDialogCustom);

                // set title
                alertDialogBuilder.setTitle("Confirmation");
                alertDialogBuilder.setIcon(R.drawable.mart);

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to update your particulars?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, change user details in firestore
                                changeEmailOnAuth();
                                uploadImageToFireBaseStorage();
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

        editpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PName.setVisibility(View.GONE);
                Email.setVisibility(View.GONE);
                PNum.setVisibility(View.GONE);

                editName.setVisibility(View.GONE);
                editEmail.setVisibility(View.GONE);
                editPhoneNumber.setVisibility(View.GONE);

                NewPass.setVisibility(View.VISIBLE);
                CfmNewPass.setVisibility(View.VISIBLE);
                OldPass.setVisibility(View.VISIBLE);

                NewPassword.setVisibility(View.VISIBLE);
                ConfirmNewPassword.setVisibility(View.VISIBLE);
                OldPassword.setVisibility(View.VISIBLE);

                updateProfile.setVisibility(View.GONE);
                deactivateaccount.setVisibility(View.GONE);
                logout.setVisibility(View.GONE);
                backbutton.setVisibility(View.VISIBLE);



                editpassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangePasswordOnAuth();
                    }
                });

                backbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent back = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(back);
                    }
                });

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this, R.style.AlertDialogCustom);

                // set title
                alertDialogBuilder.setTitle("Confirmation");
                alertDialogBuilder.setIcon(R.drawable.mart);

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to sign out?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, log out
                                auth.signOut();
                                Intent profile = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(profile);
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

        deactivateaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this, R.style.AlertDialogCustom);

                // set title
                alertDialogBuilder.setTitle("Confirmation");
                alertDialogBuilder.setIcon(R.drawable.mart);

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want deactivate your account PERMANENTLY?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, DELETE account
                                DeleteAccountOnAuth();
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


    private void changeEmailOnAuth() {
        String uid = auth.getUid();
        final String email_updated = editEmail.getText().toString().trim();

        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String email = task.getResult().getString("email");
                    String password = task.getResult().getString("password");
                    // Get auth credentials from the user for re-authentication
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password); // Current Login Credentials \\
                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), "User details updated", Toast.LENGTH_SHORT).show();
                                    //Now change your email address \\
                                    //----------------Code for Changing Email Address----------\\
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    user.updateEmail(email_updated)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "Email has been updated", Toast.LENGTH_SHORT).show();
                                                        saveUserInformation();
                                                    }
                                                }
                                            });
                                }
                            });
                }
            }
        });
    }

    private void saveUserInformation() {
        String email = editEmail.getText().toString().trim();
        String profileName = editName.getText().toString().trim();
        String phoneNumber = editPhoneNumber.getText().toString().trim();
        String Uid = auth.getUid();
        Map<String, Object> User = new HashMap<>();
        User.put("name", profileName);
        User.put("email", email);
        User.put("phoneNumber", phoneNumber);

        db.collection("Users").document(Uid).set(User, SetOptions.merge());
        //db.collection("Users").document(Uid).update("Name"); //TAKE NOTE UPDATE FOR SINGLE FIELD


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null && data.getData() !=null ) {
            uriProfileImage = data.getData();

            circleImageView.setImageURI(uriProfileImage);
        }
    }

    private void uploadImageToFireBaseStorage() {
        storageRef = FirebaseStorage.getInstance().getReference("ProfileImage/");
        final String uid = auth.getUid();


        if (uriProfileImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef = storageRef.child(uid + "." + getFileExtension(uriProfileImage));

            profileImageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    profileImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String downloadUrl = task.getResult().toString();
                            Map<String, Object> User = new HashMap<>();
                            User.put("profileimage", downloadUrl);
                            db.collection("Users").document(uid).set(User, SetOptions.merge());
                            Toast.makeText(getApplicationContext(), "Profile Picture has been updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void displayUserInfo(){
        String uid = auth.getUid();

        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    editName.setText(task.getResult().getString("name"));
                    editEmail.setText(task.getResult().getString("email"));
                    editPhoneNumber.setText(task.getResult().getString("phoneNumber"));
                }
            }
        });
    }

    private void showImageChoose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void displayImage() {
        String uid = auth.getUid();

        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String url = task.getResult().getString("profileimage");
                    if(url!=null) {
                        Picasso.get().load(url).into(circleImageView);
                    }
                    else {
                        circleImageView.setImageResource(R.drawable.no_image);
                    }

                }
            }
        });
    }

    private void ChangePasswordOnAuth() {
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
                            NewPassword();
                        }
                    });
                }
            }
        });
    }



    private void NewPassword(){
        final String newPass = NewPassword.getText().toString().trim();
        String cfmnewPass = ConfirmNewPassword.getText().toString().trim();
        String OldPass = OldPassword.getText().toString().trim();

        String uid = auth.getUid();

        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    VerifyOldPass.setText(task.getResult().getString("password"));
                }
            }
        });


        String Verify = VerifyOldPass.getText().toString().trim();
        //if new password edittext is empty or size is less than 6
        if (user != null  && newPass.length() > 6) {
            //checks if new password is the same as the confirm new password entry
            if (cfmnewPass.equals(newPass)) {
                //checks if the old password entered is the same as the current password in the system
                if(OldPass.equals(Verify)){
                    //counter checks so that the old password is not the same as the new password
                    if(!OldPass.equals(newPass)) {
                        //add listener to check if the change was completed successfully
                        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this, R.style.AlertDialogCustom);

                                    // set title
                                    alertDialogBuilder.setTitle("Confirmation");
                                    alertDialogBuilder.setIcon(R.drawable.mart);

                                    // set dialog message
                                    alertDialogBuilder
                                            .setMessage("Are you sure you want to change your password?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    // if this button is clicked, update password in both authentication and firestore database
                                                    Toast.makeText(getApplicationContext(), "Your Password has been updated. Please sign in again", Toast.LENGTH_LONG).show();

                                                    String Uid = auth.getUid();
                                                    Map<String, Object> User = new HashMap<>();
                                                    User.put("password", newPass);
                                                    db.collection("Users").document(Uid).set(User, SetOptions.merge());

                                                    Intent profile = new Intent(getApplicationContext(), LoginActivity.class);
                                                    startActivity(profile);
                                                    auth.signOut();
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


                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to update your password!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Your new password cannot be the same as your old password.", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please re-verify your current password.", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Please make sure the new password confirmation is the same as the new password.", Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(getApplicationContext(), "Your new password must be at least 6 characters long.", Toast.LENGTH_LONG).show();
        }
        /*editpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordOnAuth();
            }
        });*/
    }

    private void DeleteAccountOnAuth(){
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
                            DeleteAccount();
                        }
                    });
                }
                else
                {
                    Log.e(TAG, "Auth at Account deletion issue");
                }
            }
        });

    }

    private void DeleteAccount() {
        progressBar.setVisibility(View.VISIBLE);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.getUid().equals(user.getUid())) {
            final String Uid = auth.getUid();
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Your account has been deleted...", Toast.LENGTH_SHORT).show();
                                //Inform that the user data stored in cloud firestore is now no longer valid
                                Map<String, Object> User = new HashMap<>();
                                db.collection("Users").document(Uid).delete();

                                //delete Profile Picture in Storage
                                deleteProfilePic(Uid);

                                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to delete your account", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "You cannot delete this account as you are not logged into this account.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProfilePic(String UID) {
        storageRef = FirebaseStorage.getInstance().getReference("ProfileImage/");
        final String uid = UID;

        circleImageView.setImageURI(uriProfileImage);

        if (uriProfileImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            //profileImageRef = storageRef.child(uid + "." + getFileExtension(uriProfileImage));

            // Create a reference to the file to delete
            StorageReference deleteRef = storageRef.child(uid + "." + getFileExtension(uriProfileImage));
            // Delete the file
            deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.e(TAG, "Picture Deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e(TAG, "Picture not Deleted");
                }
            });
        }
        else
        {
            Log.e(TAG, "No Profile Image");
        }
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
                            Intent account = new Intent(getApplicationContext(), AddListingActivity.class);
                            startActivity(account);
                            break;
                        case R.id.account:
                            break;
                    }

                    return true;
                }
            };

}

