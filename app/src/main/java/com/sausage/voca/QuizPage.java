package com.sausage.voca;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QuizPage extends AppCompatActivity {
    ImageButton back_btn;
    TextView quiz_word;

    //시간지연
    Handler handler = new Handler();


    //이전페이지에서 받아오는 string 값
    String sendData;
    //단어장 정보 - 단어장 id
    String wordbookID = "0";
    int quiz_option = 0;
    //db에서 끌어오는 단어 정보를 저장.
    ArrayList<Map<String,Object>> data;

    //quiz_option와 맞는 단어수
    int size = 0;
    int random[] = new int[5];
//    Random rd = new Random();

    int wrong_count = 0, accuracy = 0;

    Button quiz_answer1, quiz_answer2, quiz_answer3, quiz_answer4;

    //정오답 toast
    Toast correctToast, wrongToast;


    //firebase 연동
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

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

        correctToast = Toast.makeText(getApplicationContext(),"정답입니다.🎉",Toast.LENGTH_SHORT);
        correctToast.setGravity(Gravity.CENTER,0,0);
        wrongToast = Toast.makeText(getApplicationContext(),"오답입니다!❗",Toast.LENGTH_SHORT);
        wrongToast.setGravity(Gravity.CENTER,0,0);


        back_btn = findViewById(R.id.quiz_back);
        back_btn.setOnClickListener(view -> finish());

        //db에서 quiz_option에 따라 단어 mapping해오기.
        docRef.get().addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            data = new ArrayList<>();
                            Map<String, Object> wordList = (Map<String, Object>) document.getData().get("wordlist");
                            Log.i("mytag","wordList : "+wordList.toString());
                            int countWordlist =0;
                            boolean addWordArray;
                            try {
                                for (String key : wordList.keySet()) {
                                    Map<String,Object> map = (HashMap) wordList.get(key);
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
                                        Map<String, Object> newMap = new HashMap<>();
                                        newMap.put("word",map.get("word").toString());
                                        newMap.put("mean1",map.get("mean1").toString());
                                        if (map.get("mean2") != null) {
                                            newMap.put("mean2",map.get("mean2").toString());
                                            if (map.get("mean3") != null) {
                                                newMap.put("mean3",map.get("mean3").toString());
                                            }
                                        }
                                        data.add(countWordlist,newMap);
                                        countWordlist++;
                                    }
                                }
                                size = countWordlist;
                                //Log.i("mytag",String.valueOf(size));
//
                            }catch(NullPointerException e){
                                e.printStackTrace();
                            }
                            if(countWordlist<5){
                                Toast.makeText(getApplicationContext(),"단어수 부족으로 퀴즈 실행이 불가능합니다.",Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                quizStart(data);
                            }
                        } else {
                            Log.i("mytag", "No such document");
                        }
                    } else {
                        Log.i("mytag", "get failed with " + task.getException());
                    }
                });

    }

    //퀴즈 실행하는 함수.
    public void quizStart(ArrayList<Map<String, Object>> myData){
        Random rd = new Random();
        rd.setSeed(System.currentTimeMillis());

        for(int i = 0; i < 5; i++) { //랜덤숫자 0 ~ size-1까지
            random[i] = rd.nextInt(size); //random[i] = (int) (Math.random()* 5) + 1;
            for (int j = 0; j < i; j++) {
                if (random[i] == random[j]) {
                    i--;
                    break;
                }
            }
        }
        for(int i=0;i<5;i++){
            Log.i("mytag",String.valueOf(random[i]));
        }
        //Log.i("mytag","함수안 "+myData.toString());

        //객관식 퀴즈 5문제
        quiz(0,myData);

    }

    //quiz종료후 결과페이지 실행
    public void complete(){
        Toast.makeText(getApplicationContext(),"Quiz가 모두 완료되었습니다.",Toast.LENGTH_SHORT).show();
        Log.i("mytag",sendData.toString());
        Intent intent = new Intent(getApplicationContext(), QuizDone.class).putExtra("sendData", sendData);
        startActivity(intent);
        finish();
    }

    public void quiz(int i,ArrayList<Map<String, Object>> myData){
        if(i==random.length){
            Log.i("mytag","quiz종료 : " + String.valueOf(i));
            complete();
//            return;
        }else {
            quiz_word.setText(myData.get(random[i]).get("word").toString());
            Random rd = new Random();
            int answer_num = rd.nextInt(4) + 1; //정답으로 할 답 번호 1 2 3 4 랜덤

            int first = random[i];
            int second, third, forth;
            do {
                second = rd.nextInt(size);
            } while (second == first);
            do {
                third = rd.nextInt(size);
            } while (third == first || third == second);
            do {
                forth = rd.nextInt(size);
            } while (forth == first || forth == second || forth == third);

            switch (answer_num) {
                case 1:
                    quiz_answer1.setText(myData.get(first).get("mean1").toString());

                    quiz_answer2.setText(myData.get(second).get("mean1").toString());
                    quiz_answer3.setText(myData.get(third).get("mean1").toString());
                    quiz_answer4.setText(myData.get(forth).get("mean1").toString());


                    quiz_answer1.setOnClickListener(v -> {
                        correctToast.show();
                        handler.postDelayed(() -> {
                            quiz(i + 1, myData);
                            return;
                        },1500);
                    });

                    quiz_answer2.setOnClickListener(v -> {
                        wrongToast.show();
                        sendData = new StringBuilder().append(sendData).append("/").append(quiz_word.getText().toString()).toString();
                        wrong_count++;
                        handler.postDelayed(() -> {
                            quiz(i + 1, myData);
                            return;
                        },1500);
                    });

                    quiz_answer3.setOnClickListener(v -> {
                        wrongToast.show();
                        sendData = new StringBuilder().append(sendData).append("/").append(quiz_word.getText().toString()).toString();
                        wrong_count++;
                        handler.postDelayed(() -> {
                            quiz(i + 1, myData);
                            return;
                        },1500);
                    });

                    quiz_answer4.setOnClickListener(v -> {
                        wrongToast.show();
                        sendData = new StringBuilder().append(sendData).append("/").append(quiz_word.getText().toString()).toString();
                        wrong_count++;
                        handler.postDelayed(() -> {
                            quiz(i + 1, myData);
                            return;
                        },1500);
                    });

                break;
                case 2:
                    quiz_answer2.setText(myData.get(first).get("mean1").toString());

                    quiz_answer1.setText(myData.get(second).get("mean1").toString());
                    quiz_answer3.setText(myData.get(third).get("mean1").toString());
                    quiz_answer4.setText(myData.get(forth).get("mean1").toString());

                    quiz_answer1.setBackgroundResource(R.drawable.correct_btn);

                    quiz_answer2.setOnClickListener(v -> {
                        correctToast.show();
                        handler.postDelayed(() -> {
                            quiz(i + 1, myData);
                            return;
                        },1500);
                    });

                    quiz_answer1.setOnClickListener(v -> {
                        wrongToast.show();
                        sendData = new StringBuilder().append(sendData).append("/").append(quiz_word.getText().toString()).toString();
                        wrong_count++;
                        handler.postDelayed(() -> {
                            quiz(i + 1, myData);
                            return;
                        },1500);
                    });

                    quiz_answer3.setOnClickListener(v -> {
                        wrongToast.show();
                        sendData = new StringBuilder().append(sendData).append("/").append(quiz_word.getText().toString()).toString();
                        wrong_count++;
                        handler.postDelayed(() -> {
                            quiz(i + 1, myData);
                            return;
                        },1500);
                    });

                    quiz_answer4.setOnClickListener(v -> {
                        wrongToast.show();
                        sendData = new StringBuilder().append(sendData).append("/").append(quiz_word.getText().toString()).toString();
                        wrong_count++;
                        handler.postDelayed(() -> {
                            quiz(i + 1, myData);
                            return;
                        },1500);
                    });
                break;
                case 3:
                    quiz_answer3.setText(myData.get(first).get("mean1").toString());

                    quiz_answer1.setText(myData.get(second).get("mean1").toString());
                    quiz_answer2.setText(myData.get(third).get("mean1").toString());
                    quiz_answer4.setText(myData.get(forth).get("mean1").toString());

                    quiz_answer3.setOnClickListener(v -> {
                        correctToast.show();
                        handler.postDelayed(() -> {
                            quiz(i + 1, myData);
                            return;
                        },1500);
                    });

                    quiz_answer2.setOnClickListener(v -> {
                        wrongToast.show();
                        sendData = new StringBuilder().append(sendData).append("/").append(quiz_word.getText().toString()).toString();
                        wrong_count++;
                        handler.postDelayed(() -> {
                            quiz(i + 1, myData);
                            return;
                        },1500);
                    });

                    quiz_answer1.setOnClickListener(v -> {
                        wrongToast.show();
                        sendData = new StringBuilder().append(sendData).append("/").append(quiz_word.getText().toString()).toString();
                        wrong_count++;
                        handler.postDelayed(() -> {
                            quiz(i + 1, myData);
                            return;
                        },1500);
                    });

                    quiz_answer4.setOnClickListener(v -> {
                        wrongToast.show();
                        sendData = new StringBuilder().append(sendData).append("/").append(quiz_word.getText().toString()).toString();
                        wrong_count++;
                        handler.postDelayed(() -> {
                            quiz(i + 1, myData);
                            return;
                        },1500);
                    });
                break;
                case 4:
                    quiz_answer4.setText(myData.get(first).get("mean1").toString());

                    quiz_answer1.setText(myData.get(second).get("mean1").toString());
                    quiz_answer2.setText(myData.get(third).get("mean1").toString());
                    quiz_answer3.setText(myData.get(forth).get("mean1").toString());

                    quiz_answer4.setOnClickListener(v -> {
                        correctToast.show();
                        handler.postDelayed(() -> {
                            quiz(i + 1, myData);
                            return;
                        },1500);
                    });

                    quiz_answer2.setOnClickListener(v -> {
                        wrongToast.show();
                        sendData = new StringBuilder().append(sendData).append("/").append(quiz_word.getText().toString()).toString();
                        wrong_count++;
                        handler.postDelayed(() -> {
                            quiz(i + 1, myData);
                            return;
                        },1500);
                    });

                    quiz_answer3.setOnClickListener(v -> {
                        wrongToast.show();
                        sendData = new StringBuilder().append(sendData).append("/").append(quiz_word.getText().toString()).toString();
                        wrong_count++;
                        handler.postDelayed(() -> {
                            quiz(i + 1, myData);
                            return;
                        },1500);
                    });

                    quiz_answer1.setOnClickListener(v -> {
                        wrongToast.show();
                        sendData = new StringBuilder().append(sendData).append("/").append(quiz_word.getText().toString()).toString();
                        wrong_count++;
                        handler.postDelayed(() -> {
                            quiz(i + 1, myData);
                            return;
                        },1500);
                    });
                break;
            }
        }
    }
}