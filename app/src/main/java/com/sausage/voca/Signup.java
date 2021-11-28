package com.sausage.voca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup<mDatabase> extends AppCompatActivity implements View.OnClickListener {
    private TextView new_name;
    private TextView new_email;
    private TextView new_password;
    private TextView login;
    private Button signup;

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) { //public으로 바꿈. 꼭 이런 방식이어야 할까?
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        new_name = findViewById(R.id.signup_name);
        new_email = findViewById(R.id.signup_email);
        new_password = findViewById(R.id.signup_password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup_button);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    @Override
    public void onClick(View v) {
        if (v == signup) {
            createAccount(new_name.getText().toString(), new_email.getText().toString(), new_password.getText().toString());
        } else if ((v == login)) {
            Intent Login = new Intent(this, Login.class);
            startActivity(Login);
            finish();
        }
    }

    private void createAccount(String name, String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            mRootRef.child("wordbook").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data", task.getException());
                                    } else {
                                        Map<String, Object> wordbookArrayData = new HashMap<>();
                                        for (int i = 0; task.getResult().child(Integer.toString(i)).exists(); i++) {
                                            DataSnapshot wordbooktask = task.getResult().child(Integer.toString(i));
                                            Map<String, Object> wordbookData = new HashMap<>();

                                            //wordcard 배열 만듦.
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
                                            wordbookArrayData.put(Integer.toString(i), wordbookData);
                                        }

                                        Log.d("firebase", String.valueOf(task.getResult().getValue()));

                                        FirebaseUser user = mAuth.getCurrentUser();
                                        // Create a new user with a first and last name
                                        Map<String, Object> thisuser = new HashMap<>();
                                        thisuser.put("name", name);
                                        thisuser.put("email", email);
                                        thisuser.put("wordbooks", wordbookArrayData);

                                        // Add a new document with a generated ID
                                        db.collection("users").document(user.getUid())
                                                .set(thisuser)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error writing document", e);
                                                    }
                                                });

                                        updateUI(user);
                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Signup.this, "다시 시도해주세요",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.i("mytag", "로그인으로 바로 이동");
            Intent main = new Intent(this, Main.class);
            startActivity(main);
            Toast.makeText(this.getApplicationContext(), "로그인되었습니다", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}