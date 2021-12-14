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

import java.util.Objects;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private EditText email;
    private EditText password;
    private TextView signup;
    private Button login;
    private Button google;
    private static final String TAG = "EmailPassword";
    private static final String GoogleTAG = "GoogleActivity";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private View mainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태 바 없애기

        mainLayout = findViewById(R.id.main_layout);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        email.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if (!email.getText().equals("") && !password.getText().equals("")){
                    login.callOnClick();
                    return true;
                }
                Snackbar.make(mainLayout, "이메일 혹은 패스워드를 입력해주세요.", Snackbar.LENGTH_SHORT).show();
            }
            return false;
        });
        password.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if (!email.getText().equals("") && !password.getText().equals("")){
                    login.callOnClick();
                    return true;
                }
                Snackbar.make(mainLayout, "이메일 혹은 패스워드를 입력해주세요.", Snackbar.LENGTH_SHORT).show();
            }
            return false;
        });

        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login_button);
        google = findViewById(R.id.google_btn);

        signup.setOnClickListener(this);
        login.setOnClickListener(this);
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
        if (v==login){
            if(email.getText().toString().equals("")||password.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(),"입력칸을 작성해주세요.",Toast.LENGTH_SHORT).show();
            }else{
                signIn(email.getText().toString(), password.getText().toString());
            }
        }else if (v==signup){
            Intent Signup = new Intent(this, Signup.class);
            startActivity(Signup);
            finish();
        }else if (v==google){
            GoogleSignIn();
        }
    }


    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Snackbar.make(mainLayout, "로그인에 실패했습니다. 다시 시도해주세요.", Snackbar.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
        // [END sign_in_with_email]
    }

    private void GoogleSignIn() {
        //이대로 firebase에는 나와있으면서 제대로 안됨
        //Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //startActivityForResult(signInIntent, RC_SIGN_IN);
        resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
    }
    // [END signin]

    //https://www.youtube.com/watch?v=gCrVwjh4LiY 참고
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
                        updateUI(mAuth.getCurrentUser());
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(GoogleTAG, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }
    // [END auth_with_google]

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent main = new Intent(this, Main.class);
            startActivity(main);
            Toast.makeText(this.getApplicationContext(), "로그인되었습니다", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}