package com.sausage.voca;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class QuizPage extends AppCompatActivity {
    ImageButton back_btn;
    TextView quiz_word;


    //이전페이지에서 받아오는 string 값
    String sendData;
    //단어장 정보 - 단어장 id
    String wordbookID = "0";
    int quiz_option = 0;

    //quiz_option와 맞는 단어수
    int size = 0;

//    Random r;
//    int turn = 1;
//    int random[] = new int[5];

//    int wrong_count = 0, accuracy = 0;
//    String[] eng, kor;

    Button quiz_answer1, quiz_answer2, quiz_answer3, quiz_answer4;

    Map<String,Object> dataList;

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
//        Log.i("mytag",wordbookID);
//        Log.i("mytag",String.valueOf(quiz_option));

//        docRef = db.collection("users").document(user.getUid()).collection("wordbooks").document(wordbookID);

//        quiz_word = findViewById(R.id.quiz_word);

//        quiz_answer1 = findViewById(R.id.quiz_answer1);
//        quiz_answer2 = findViewById(R.id.quiz_answer2);
//        quiz_answer3 = findViewById(R.id.quiz_answer3);
//        quiz_answer4 = findViewById(R.id.quiz_answer4);


        back_btn = findViewById(R.id.quiz_back);
        back_btn.setOnClickListener(view -> finish());

        //db에서 quiz_option에 따라 단어 mapping해오기.
        db.collection("users").document(user.getUid()).collection("wordbooks").document(wordbookID)
                .get().addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            dataList = new HashMap<>();
                            Map<String, Object> wordList = (Map<String, Object>) document.getData().get("wordlist");
                            Log.i("mytag","wordList : "+wordList.toString());
                            int countWordlist =0;
                            boolean addWordArray;
                            try {
                                for (String key : wordList.keySet()) {
                                    HashMap map = (HashMap) wordList.get(key);
                                    int memorization = Integer.parseInt(map.get("memorization").toString());
                                    addWordArray = false;
                                    if(quiz_option==0){
                                        // quiz_option 0 전체, 1 암기, 2 미암기
                                        addWordArray=true;
                                    }else if(quiz_option==1 && memorization==1){
                                        //둘다 암기로 1일때
                                        addWordArray=true;
                                    } else if (quiz_option==2 && memorization==0) {
                                        //미암기
                                        addWordArray=true;
                                    }
                                    if (addWordArray) {
                                        // quiz_option 0 전체, 1 암기, 2 미암기
                                        countWordlist++;
                                        Map<String, Object> newMap = new HashMap<>();
                                        newMap.put("word",map.get("word").toString());
                                        newMap.put("mean1",map.get("mean1").toString());
                                        if (map.get("mean2") != null) {
                                            newMap.put("mean2",map.get("mean2").toString());
                                            if (map.get("mean3") != null) {
                                                newMap.put("mean3",map.get("mean3").toString());
                                            }
                                        }
                                        dataList.put(String.valueOf(countWordlist),newMap);
                                    }
                                }
                                size = countWordlist;
                                Log.i("mytag",String.valueOf(size));
                            }catch(NullPointerException e){
                                e.printStackTrace();
                            }
                            if(countWordlist<=5){
                                Toast.makeText(getApplicationContext(),"단어수 부족으로 퀴즈 실행이 불가능합니다.",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Log.i("mytag", "No such document");
                        }
                    } else {
                        Log.i("mytag", "get failed with " + task.getException());
                    }
                });



    }

    //quiz종료후 결과페이지 실행
    public void complete(){
        Intent intent = new Intent(getApplicationContext(), QuizResult.class);
        startActivity(intent);
        finish();
    }
}