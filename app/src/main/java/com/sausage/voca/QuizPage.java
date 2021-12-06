package com.sausage.voca;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Random;

public class QuizPage extends AppCompatActivity {
    ImageButton back_btn;
    TextView quiz_word;


    //이전페이지에서 받아오는 string 값
    String sendData;
    //단어장 정보 - 단어장 id
    String wordbookID = "0";
    int quiz_option = 0;

    Random r;
    int turn = 1;
    int random[] = new int[5];
    int size;
    int wrong_count = 0, accuracy = 0;
    String[] eng, kor;

    Button quiz_answer1, quiz_answer2, quiz_answer3, quiz_answer4;

    ArrayList<Word> dataList = new ArrayList<>();

    //firebase 연동
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

    Toast toast2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);

        sendData = getIntent().getStringExtra("sendData");
        int sendDataIdx = sendData.indexOf("/");
        wordbookID = sendData.substring(0,sendDataIdx);
        quiz_option = Integer.parseInt(sendData.substring(sendDataIdx+1));

        docRef = db.collection("users").document(user.getUid()).collection("wordbooks").document(wordbookID);

        quiz_word = findViewById(R.id.quiz_word);

        quiz_answer1 = findViewById(R.id.quiz_answer1);
        quiz_answer2 = findViewById(R.id.quiz_answer2);
        quiz_answer3 = findViewById(R.id.quiz_answer3);
        quiz_answer4 = findViewById(R.id.quiz_answer4);


        back_btn = findViewById(R.id.quiz_back);
        back_btn.setOnClickListener(view -> finish());



    }
    public void complete(){
        Intent intent = new Intent(getApplicationContext(), QuizResult.class);
        startActivity(intent);
    }
}