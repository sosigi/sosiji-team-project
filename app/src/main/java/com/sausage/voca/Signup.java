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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signup extends AppCompatActivity implements View.OnClickListener{
    private TextView new_email;
    private TextView new_password;
    private TextView login;
    private Button signup;

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //public으로 바꿈. 꼭 이런 방식이어야 할까?
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        new_email = findViewById(R.id.signup_email);
        new_password = findViewById(R.id.signup_password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup_button);
        signup.setOnClickListener(this);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    @Override
    public void onClick(View v) {
        if (v==signup){
            createAccount(new_email.getText().toString(), new_password.getText().toString());
        }else if ((v==login)){
            Intent Login = new Intent(this, Login.class);
            startActivity(Login);
        }
    }

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
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
            Intent main = new Intent(this, Main.class);
            startActivity(main);
            Toast.makeText(this.getApplicationContext(), "로그인되었습니다", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}