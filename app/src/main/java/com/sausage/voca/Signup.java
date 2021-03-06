package com.sausage.voca;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Signup extends AppCompatActivity implements View.OnClickListener {

    private EditText new_name;
    private EditText new_email;
    private EditText new_password;
    private TextView login;
    private Button signup;
    private Button google;
    private static final String EmailTAG = "EmailPassword";
    private static final String GoogleTAG = "GoogleActivity";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private View mainLayout;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) { //public?????? ??????. ??? ?????? ??????????????? ???????
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //?????? ??? ?????????

        mainLayout = findViewById(R.id.main_layout);
        new_name = findViewById(R.id.signup_name);
        new_email = findViewById(R.id.signup_email);
        new_password = findViewById(R.id.signup_password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup_button);
        google = findViewById(R.id.google_btn);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        google.setOnClickListener(this);

        new_name.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if (!new_name.getText().equals("") && !new_email.getText().equals("") && !new_password.getText().equals("")) {
                    signup.callOnClick();
                    return true;
                }
                Toast.makeText(view.getContext(), "??? ?????? ?????? ???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
        new_email.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if (!new_name.getText().equals("") && !new_email.getText().equals("") && !new_password.getText().equals("")) {
                    signup.callOnClick();
                    return true;
                }
                Toast.makeText(view.getContext(), "??? ?????? ?????? ???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
        new_password.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if (!new_name.getText().equals("") && !new_email.getText().equals("") && !new_password.getText().equals("")) {
                    signup.callOnClick();
                    return true;
                }
                Toast.makeText(view.getContext(), "??? ?????? ?????? ???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            }
            return false;
        });

        // [START config_google_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_google_signin]

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    @Override
    public void onClick(View v) {
        if (v == signup) { //????????????
            //???????????? ?????????????????? ?????? new_name ?????? ????????? ?????? ???????????? ??????.
            String name = new_name.getText().toString();
            String email = new_email.getText().toString();
            String password = new_password.getText().toString();
            if (name.equals("") || email.equals("") || password.equals(""))
                Toast.makeText(getApplicationContext(), "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            else createEmailAccount(name, email, password);
        } else if (v == login) {
            Intent Login = new Intent(this, Login.class);
            startActivity(Login);
            finish();
        } else if (v == google) { //?????? ????????????
            //????????? ?????????????????? ?????? auth ???????????? ???????????? ????????? ???????????? ???????????? ??? ???.
            createGoogleAccount();
        }
    }

    //????????? ?????? ?????? ??????//
    private void createEmailAccount(String name, String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password) //?????? ????????? ????????? ?????? ?????????
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("mytag", "createUserWithEmail:success");

                        //[??????] firestore??? user data ???????????? ?????? ?????? ?????????~
                        storeUserdata(name, email);

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(EmailTAG, "createUserWithEmail:failure", task.getException());
                        Snackbar.make(mainLayout, "??????????????? ??????????????????. ?????? ??????????????????,", Snackbar.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
        // [END create_user_with_email]
    }

    //?????? ?????? ?????? ??????//
    // [START signin]
    private void createGoogleAccount() {
        //????????? firebase?????? ?????????????????? ????????? ??????
        //Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //startActivityForResult(signInIntent, RC_SIGN_IN);
        resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
    }
    // [END signin]

    //https://www.youtube.com/watch?v=gCrVwjh4LiY ??????
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(GoogleTAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(GoogleTAG, "Google sign in failed", e);
            }
        }
    });

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(GoogleTAG, "signInWithCredential:success");
                        //[??????] firestore??? user data ???????????? ?????? ?????? ?????????~
                        storeUserdata(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName(), mAuth.getCurrentUser().getEmail());
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(GoogleTAG, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }
    // [END auth_with_google]


    //????????? ????????? ??? ??? ???????????? ??????//
    private void storeUserdata(String name, String email) {
        FirebaseUser user = mAuth.getCurrentUser();
        // Create a new user with a first and last name
        Map<String, Object> thisuser = new HashMap<>(); //????????? ??? ?????? ??????
        thisuser.put("name", name);
        thisuser.put("email", email);
        //thisuser.put("wordbooks", wordbookArrayData);
        Log.d("mytag", "createUserWithEmail:success");

        //?????? ?????? ????????? ?????? ??????????????? ??????????????? ?????? ??????. ????????? ???????????? ?????? TODO ?????? ?????? ??????
        DocumentReference docRef = db.collection("users").document(user.getUid());
        if (docRef != null && user != null) {
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("mytag", "?????? ????????? ?????? ????????? ?????? ??????????????? : " + document.getData());
                    } else {
                        Log.d("mytag", "No such document");

                        // ?????? ????????? ?????? ?????? ?????? ??????
                        // Add a new document with a generated ID
                        db.collection("users").document(user.getUid())
                                .set(thisuser)
                                .addOnSuccessListener(aVoid -> Log.d("mytag", "DocumentSnapshot successfully written!"))
                                .addOnFailureListener(e -> Log.w("mytag", "Error writing document", e));

                        mRootRef.child("wordbooks").get().addOnCompleteListener(DBtask -> {
                            if (!DBtask.isSuccessful()) {
                                Log.e("firebase", "Error getting data", DBtask.getException());
                            } else {
//                                        Map<String, Object> wordbookArrayData = new HashMap<>();
                                for (int i = 0; DBtask.getResult().child(Integer.toString(i)).exists(); i++) {
                                    DataSnapshot wordbooktask = DBtask.getResult().child(Integer.toString(i));
                                    Map<String, Object> wordbookData = new HashMap<>();

                                    //wordcard ?????? ??????.
                                    Map<String, Object> wordcardArrayData = new HashMap<>();
                                    for (int j = 0; wordbooktask.child("wordlist").child("wordcard").child(Integer.toString(j)).exists(); j++) {
                                        DataSnapshot wordcardtask = wordbooktask.child("wordlist").child("wordcard").child(Integer.toString(j));

                                        Map<String, Object> wordcardData = new HashMap<>();
                                        wordcardData.put("word", String.valueOf(wordcardtask.child("word").getValue()));
                                        wordcardData.put("mean1", String.valueOf(wordcardtask.child("mean1").getValue()));

                                        if (wordcardtask.child("mean2").exists()) {
                                            wordcardData.put("mean2", String.valueOf(wordcardtask.child("mean2").getValue()));
                                        }
                                        if (wordcardtask.child("mean3").exists()) {
                                            wordcardData.put("mean3", String.valueOf(wordcardtask.child("mean3").getValue()));
                                        }
                                        wordcardData.put("memorization", 0);

                                        wordcardArrayData.put(Integer.toString(j), wordcardData);

                                    }
                                    wordbookData.put("wordbooktitle", String.valueOf(wordbooktask.child("wordbooktitle").getValue()));
                                    wordbookData.put("wordbookexplain", String.valueOf(wordbooktask.child("wordbookexplain").getValue()));
                                    wordbookData.put("wordlist", wordcardArrayData);
                                    Log.i("mytag", wordbookData.toString());
                                    Log.i("mytag", Objects.requireNonNull(wordbookData.get("wordbooktitle")).toString());

//                                            wordbookArrayData.put(Integer.toString(i), wordbookData);

                                    if (user != null) {
                                        db.collection("users").document(user.getUid()).collection("wordbooks").document(Integer.toString(i))
                                                .set(wordbookData)
                                                .addOnSuccessListener(aVoid -> Log.d("mytag", "DocumentSnapshot successfully written!"))
                                                .addOnFailureListener(e -> Log.w("mytag", "Error writing document", e));
                                    }
//                        db.collection("users").document(user.getUid()).collection("wordbooks").add(wordbookData);
                                }
                                Log.d("firebase", String.valueOf(DBtask.getResult().getValue()));

                            }
                        });
                    }
                    updateUI(user);
                } else {
                    Log.d(GoogleTAG, "get failed with ", task.getException());
                }
            });
        }


    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.i("mytag", "??????????????? ?????? ??????");
            Intent main = new Intent(this, Main.class);
            startActivity(main);
            Toast.makeText(this.getApplicationContext(), "????????????????????????", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}