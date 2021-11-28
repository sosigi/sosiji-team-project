package com.sausage.voca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//[은소] 내정보 page이다.
public class Mypage extends AppCompatActivity {
    private TextView textView_name;
    private TextView textView_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        textView_name = (TextView) findViewById(R.id.user_name);
        textView_email = (TextView) findViewById(R.id.user_email);

        //firebase에 접근함. 문제는 firebaseAuth에 접근해서 firestore user문서에 저장되어있는 name을 못가져옴.
        //TODO : firestore의 user에 접근하도록 수정해서 name과 email을 끌어와야함.

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            textView_name.setText(name);
            String email = user.getEmail();
            textView_email.setText(email);
            //Uri photoUrl = user.getPhotoUrl();
        }
    }
}