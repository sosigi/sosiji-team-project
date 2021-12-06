package com.sausage.voca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

//[은소] 내정보 page이다.
public class Mypage extends AppCompatActivity {
    private TextView textView_name;
    private TextView textView_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        textView_name = findViewById(R.id.user_name);
        textView_email = findViewById(R.id.user_email);

        //firestore의 user컬렉션에서 user의 문서에접근하여 name과 email을 끌어옴.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Name, email address, and profile photo Url
                        String name = Objects.requireNonNull(document.get("name")).toString();
                        textView_name.setText(name);
                        String email = Objects.requireNonNull(document.get("email")).toString();
                        textView_email.setText(email);
                        //Uri photoUrl = user.getPhotoUrl();
                    } else {
                        Log.i("mytag", "No such document");
                    }
                } else {
                    Log.i("mytag", "get failed with ", task.getException());
                }
            });
        }
        ImageButton mypage_back = findViewById(R.id.mypage_back);

        mypage_back.setOnClickListener(view -> finish());
    }
}