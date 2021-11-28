package com.sausage.voca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

//구글로 회원가입 하는 기능 여기에 있음
public class Index extends AppCompatActivity implements View.OnClickListener {
    private Button email_btn;
    private TextView signup_btn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태 바 없애기

        email_btn = findViewById(R.id.email_btn);
        signup_btn = findViewById(R.id.signup_btn);
        email_btn.setOnClickListener(this);
        signup_btn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        if(v==email_btn){
            onStart(); //앱 실행했을 당시에 이미 로그인 된 상태였다면 바로 main화면으로 이동 
            Intent login = new Intent(getApplicationContext(), Login.class);
            startActivity(login);
        }
        else if (v==signup_btn){
            Intent signup = new Intent(getApplicationContext(), Signup.class);
            startActivity(signup);
        }
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    //구글과 이메일 둘 다 해당되는 내용
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.i("mytag","로그인으로 바로 이동");
            Intent main = new Intent(this, Main.class);
            startActivity(main);
            Toast.makeText(this.getApplicationContext(), "로그인되었습니다", Toast.LENGTH_LONG).show();
            finish();
        }
    }

}