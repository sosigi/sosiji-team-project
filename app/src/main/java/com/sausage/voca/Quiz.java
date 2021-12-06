package com.sausage.voca;

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

public class Quiz extends AppCompatActivity {
    ImageButton back_btn;
    TextView quiz_word;

    //단어장 정보
    String wordbookID = "0";

    Random r;
    int turn = 1;
    int random[] = new int[5];
    int size;
    int wrong_count = 0, accuracy = 0;
    String[] eng, kor;

    Button quiz_answer1, quiz_answer2, quiz_answer3, quiz_answer4;

    ArrayList<Word> dataList = new ArrayList<>();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

    Toast toast2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //toast2 = Toast.makeText(getApplicationContext(),"해당 옵션 단어가 5개 이상이어야 합니다", Toast.LENGTH_SHORT);

        wordbookID = getIntent().getStringExtra("id");
        docRef = db.collection("users").document(user.getUid()).collection("wordbooks").document(wordbookID);

//        r = new Random();

        quiz_word = findViewById(R.id.quiz_word);

        quiz_answer1 = findViewById(R.id.quiz_answer1);
        quiz_answer2 = findViewById(R.id.quiz_answer2);
        quiz_answer3 = findViewById(R.id.quiz_answer3);
        quiz_answer4 = findViewById(R.id.quiz_answer4);

//        docRef.get().addOnCompleteListener((task) -> {
//            if (task.isSuccessful()) {
//                dataList = new ArrayList<>();
//                DocumentSnapshot document = task.getResult();
//                Map<String, Object> wordList = (Map<String, Object>) document.getData().get("wordlist");
//                try {
//                    Iterator<String> keys = wordList.keySet().iterator();
//                    int i = 0;
//                    while (keys.hasNext()) {
//                        String key = keys.next();
//                        HashMap map = (HashMap) wordList.get(key);
//                        String word_english = map.get("word").toString();
//                        String word_meaning1 = map.get("mean1").toString();
//                        int memorization = Integer.parseInt(map.get("memorization").toString());
//
//                        switch(quiz_option) {
//                            case 0 : //전체
//                                eng[i] = word_english;
//                                kor[i] = word_meaning1;
//                                i++;
//                                break;
//                            case 1: //암기
//                                if (memorization == 1) {
//                                    eng[i] = word_english;
//                                    kor[i] = word_meaning1;
//                                    i++;
//                                }
//                                break;
//                            case 2: //미암기
//                                if (memorization == 0) {
//                                    eng[i] = word_english;
//                                    kor[i] = word_meaning1;
//                                    i++;
//                                }
//                                break;
//                        };
//                    }

//                    size = eng.length;
//
//                }catch(NullPointerException e){
//                    e.printStackTrace();
//                }
//            } else {
//                Log.i("mytag", "get failed with " + task.getException());
//            }
//        });

//        if (size < 5) {     //해당 옵션 단어가 5개 미만일 때
//            toast2.show();
//            finish();
//        }
//
//        for(int i = 0; i < 5; i++) { //랜덤숫자 5개 생성 0 ~ size-1
//            random[i] = r.nextInt(size);
//            for (int j = 0; j < i; j++) {
//                if (random[i] == random[j]) {
//                    i--;
//                    break;
//                }
//            }
//        }

//        //객관식 퀴즈 5문제
//        for(int i = 0; i < 5; i++) {
//
//            quiz_word.setText(eng[random[i]]);
//
//            int answer_num = r.nextInt(4) + 1; //정답으로 할 답 번호 1 2 3 4 랜덤
//            int first = random[i];
//            int second, third, forth;
//
//            switch (answer_num) {
//                case 1:
//                    quiz_answer1.setText(kor[random[i]]);
//                    do {
//                        second = r.nextInt(size);
//                    } while (second == first);
//                    do {
//                        third = r.nextInt(size);
//                    } while (third == first || third == second);
//                    do {
//                        forth = r.nextInt(size);
//                    } while (forth == first || forth == second || forth == third);
//
//                    quiz_answer2.setText(kor[random[second]]);
//                    quiz_answer3.setText(kor[random[third]]);
//                    quiz_answer4.setText(kor[random[forth]]);
//
//                    quiz_answer1.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            quiz_answer1.setBackgroundResource(R.drawable.correct_btn);
//                        }
//                    });
//
//                    quiz_answer2.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            quiz_answer1.setBackgroundResource(R.drawable.correct_btn);
//                            quiz_answer2.setBackgroundResource(R.drawable.wrong_btn);
//                            wrong_count++;
//                        }
//                    });
//
//                    quiz_answer3.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            quiz_answer1.setBackgroundResource(R.drawable.correct_btn);
//                            quiz_answer3.setBackgroundResource(R.drawable.wrong_btn);
//                            wrong_count++;
//                        }
//                    });
//
//                    quiz_answer4.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            quiz_answer1.setBackgroundResource(R.drawable.correct_btn);
//                            quiz_answer4.setBackgroundResource(R.drawable.wrong_btn);
//                            wrong_count++;
//                        }
//                    });
//
//                    break;
//                case 2:
//                    quiz_answer2.setText(kor[random[i]]);
//                    do {
//                        second = r.nextInt(size);
//                    } while (second == first);
//                    do {
//                        third = r.nextInt(size);
//                    } while (third == first || third == second);
//                    do {
//                        forth = r.nextInt(size);
//                    } while (forth == first || forth == second || forth == third);
//
//                    quiz_answer1.setText(kor[random[second]]);
//                    quiz_answer3.setText(kor[random[third]]);
//                    quiz_answer4.setText(kor[random[forth]]);
//
//                    quiz_answer2.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            quiz_answer2.setBackgroundResource(R.drawable.correct_btn);
//                        }
//                    });
//
//                    quiz_answer1.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            quiz_answer2.setBackgroundResource(R.drawable.correct_btn);
//                            quiz_answer1.setBackgroundResource(R.drawable.wrong_btn);
//                            wrong_count++;
//                        }
//                    });
//
//                    quiz_answer3.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            quiz_answer2.setBackgroundResource(R.drawable.correct_btn);
//                            wrong_count++;
//                            quiz_answer3.setBackgroundResource(R.drawable.wrong_btn);
//                        }
//                    });
//
//                    quiz_answer4.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            quiz_answer2.setBackgroundResource(R.drawable.correct_btn);
//                            quiz_answer4.setBackgroundResource(R.drawable.wrong_btn);
//                            wrong_count++;
//                        }
//                    });
//
//                    break;
//                case 3:
//                    quiz_answer3.setText(kor[random[i]]);
//                    do {
//                        second = r.nextInt(size);
//                    } while (second == first);
//                    do {
//                        third = r.nextInt(size);
//                    } while (third == first || third == second);
//                    do {
//                        forth = r.nextInt(size);
//                    } while (forth == first || forth == second || forth == third);
//
//                    quiz_answer1.setText(kor[random[second]]);
//                    quiz_answer2.setText(kor[random[third]]);
//                    quiz_answer4.setText(kor[random[forth]]);
//
//                    quiz_answer3.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            quiz_answer3.setBackgroundResource(R.drawable.correct_btn);
//                        }
//                    });
//
//                    quiz_answer2.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            quiz_answer3.setBackgroundResource(R.drawable.correct_btn);
//                            quiz_answer2.setBackgroundResource(R.drawable.wrong_btn);
//                            wrong_count++;
//                        }
//                    });
//
//                    quiz_answer1.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            quiz_answer3.setBackgroundResource(R.drawable.correct_btn);
//                            quiz_answer1.setBackgroundResource(R.drawable.wrong_btn);
//                            wrong_count++;
//                        }
//                    });
//
//                    quiz_answer4.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            quiz_answer3.setBackgroundResource(R.drawable.correct_btn);
//                            quiz_answer4.setBackgroundResource(R.drawable.wrong_btn);
//                            wrong_count++;
//                        }
//                    });
//
//                    break;
//                case 4:
//                    quiz_answer4.setText(kor[random[i]]);
//                    do {
//                        second = r.nextInt(size);
//                    } while (second == first);
//                    do {
//                        third = r.nextInt(size);
//                    } while (third == first || third == second);
//                    do {
//                        forth = r.nextInt(size);
//                    } while (forth == first || forth == second || forth == third);
//
//                    quiz_answer1.setText(kor[random[second]]);
//                    quiz_answer2.setText(kor[random[third]]);
//                    quiz_answer3.setText(kor[random[forth]]);
//
//                    quiz_answer4.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            quiz_answer4.setBackgroundResource(R.drawable.correct_btn);
//                        }
//                    });
//
//                    quiz_answer2.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            quiz_answer4.setBackgroundResource(R.drawable.correct_btn);
//                            quiz_answer2.setBackgroundResource(R.drawable.wrong_btn);
//                            wrong_count++;
//                        }
//                    });
//
//                    quiz_answer3.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            quiz_answer4.setBackgroundResource(R.drawable.correct_btn);
//                            quiz_answer3.setBackgroundResource(R.drawable.wrong_btn);
//                            wrong_count++;
//                        }
//                    });
//
//                    quiz_answer1.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            quiz_answer4.setBackgroundResource(R.drawable.correct_btn);
//                            quiz_answer1.setBackgroundResource(R.drawable.wrong_btn);
//                            wrong_count++;
//                        }
//                    });
//
//                    break;
//            }
//
//        }

        back_btn = findViewById(R.id.quiz_back);
        back_btn.setOnClickListener(view -> finish());

//        Intent intent = new Intent(getApplicationContext(), QuizResult.class);
//        startActivity(intent);
    }
}