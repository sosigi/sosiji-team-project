package com.sausage.voca;

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

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener{
    private EditText email;
    private EditText password;
    private TextView signup;
    private Button login;

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private View mainLayout;
    //google로 로그인 안되는 문제.

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
        signup.setOnClickListener(this);
        login.setOnClickListener(this);

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
        }else if ((v==signup)){
            Intent Signup = new Intent(this, Signup.class);
            startActivity(Signup);
            finish();
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

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent main = new Intent(this, Main.class);
            startActivity(main);
            Toast.makeText(this.getApplicationContext(), "로그인되었습니다", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}