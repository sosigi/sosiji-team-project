package com.sausage.voca;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
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


public class Signup<mDatabase> extends AppCompatActivity implements View.OnClickListener {

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
        google = findViewById(R.id.google_btn);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        google.setOnClickListener(this);

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
        if (v == signup) { //회원가입
            //이메일로 회원가입하는 경우 new_name 칸에 입력한 이름 가져오면 된다.
            createEmailAccount(new_name.getText().toString(), new_email.getText().toString(), new_password.getText().toString());
        } else if ((v == login)) {
            Intent Login = new Intent(this, Login.class);
            startActivity(Login);
            finish();
        } else if (v == google) { //구글 회원가입
            //구글로 회원가입하는 경우 auth 가져오는 과정에서 사용자 닉네임을 가져와야 할 듯.
            createGoogleAccount();
        }
    }

    //이메일 계정 회원 생성//
    private void createEmailAccount(String name, String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password) //얘는 여기서 유효성 검사 하나봄
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("mytag", "createUserWithEmail:success");

                            //[시하] firestore에 user data 저장하는 함수 따로 팠어요~
                            storeUserdata(name, email, "email");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(EmailTAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Signup.this, "다시 시도해주세요",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    //구글 계정 회원 생성//
    // [START signin]
    private void createGoogleAccount() {
        //이대로 firebase에는 나와있으면서 제대로 안됨
        //Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //startActivityForResult(signInIntent, RC_SIGN_IN);
        resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
    }
    // [END signin]

    //https://www.youtube.com/watch?v=gCrVwjh4LiY 참고
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
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
        }
    });

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(GoogleTAG, "signInWithCredential:success");
                            //[시하] firestore에 user data 저장하는 함수 따로 팠어요~
                            storeUserdata(mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail(), "google");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(GoogleTAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_google]


    //구글과 이메일 둘 다 해당되는 내용//
    private void storeUserdata(String name, String email, String from) {
        FirebaseUser user = mAuth.getCurrentUser();
        // Create a new user with a first and last name
        Map<String, Object> thisuser = new HashMap<>(); //저장할 틀 먼저 생성
        thisuser.put("name", name);
        thisuser.put("email", email);
        //thisuser.put("wordbooks", wordbookArrayData);
        Log.d("mytag", "createUserWithEmail:success");

        //근데 해당 회원을 위한 다큐먼트가 존재하는지 확인 필수. 있으면 저장하면 안됨 TODO 코드 정리 필요
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("mytag", "해당 회원에 대한 문서가 이미 존재합니다 : " + document.getData());
                        return;
                    } else {
                        Log.d("mytag", "No such document");
                    }
                } else {
                    Log.d(GoogleTAG, "get failed with ", task.getException());
                    return;
                }
            }
        });

        // 해당 계정을 위한 문서 생성 시작
        // Add a new document with a generated ID
        db.collection("users").document(user.getUid())
                .set(thisuser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("mytag", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("mytag", "Error writing document", e);
                    }
                });

        mRootRef.child("wordbooks").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
//                                        Map<String, Object> wordbookArrayData = new HashMap<>();
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
                        Log.i("mytag",wordbookData.toString());
                        Log.i("mytag",wordbookData.get("wordbooktitle").toString());

//                                            wordbookArrayData.put(Integer.toString(i), wordbookData);

                        db.collection("users").document(user.getUid()).collection("wordbooks").document(Integer.toString(i))
                                .set(wordbookData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("mytag", "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("mytag", "Error writing document", e);
                                    }
                                });
//                        db.collection("users").document(user.getUid()).collection("wordbooks").add(wordbookData);
                    }
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                    updateUI(user);
                }
            }
        });
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