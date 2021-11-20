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

public class Login extends AppCompatActivity implements View.OnClickListener{
    private TextView user_email;
    private TextView user_password;
    private TextView signup;
    private Button login;

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user_email = findViewById(R.id.login_email);
        user_password = findViewById(R.id.login_password);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login_button);
        login.setOnClickListener(this);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    @Override
    public void onClick(View v) {
        if (v==login){
            signIn(user_email.getText().toString(), user_password.getText().toString());
        }else if ((v==signup)){
            Intent Signup = new Intent(this, Signup.class);
            startActivity(Signup);
        }
    }


    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
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