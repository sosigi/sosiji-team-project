package com.sausage.voca;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class QuizDone extends AppCompatActivity {
    Button back_btn;
    CheckBox checkbox;
    TextView result;
    TextView correct;
    TextView wrong;

    int wrong_count;
    int accuracy;

    //db에 저장하는 미암기 단어
    int num;

    //이전페이지에서 받아오는 string 값
    String sendData;
    //단어장 정보 - 단어장 id
    String wordbookID = "0";
    String[] sendDataArr;

    //firebase 연동
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_done);

        //이전에서 넘겨받은 data저장
        sendData = getIntent().getStringExtra("sendData");
        sendDataArr = sendData.split("/");
        wrong_count = sendDataArr.length - 2;
        wordbookID = sendDataArr[0];

        checkbox = findViewById(R.id.quiz_result_check);
        result = findViewById(R.id.result_accuracy);
        correct = findViewById(R.id.result_correct);
        wrong = findViewById(R.id.result_wrong);

        accuracy = (5 - wrong_count) * 20;
        result.setText(Integer.toString(accuracy) + "%\n정답률");
        correct.setText(Integer.toString(5 - wrong_count));
        wrong.setText(Integer.toString(wrong_count));


        back_btn = findViewById(R.id.quiz_result_back);
        back_btn.setOnClickListener(view -> {
            if (checkbox.isChecked()) {
                Log.i("mytag","체크함");
                //ToDo 오답을 미암기 단어로 표기
                for (num =0; num<wrong_count;num++) {
                    //Log.i("mytag",String.valueOf(num)+"::"+sendDataArr[num+2]);
                    dbCheck();
                }
                Toast.makeText(getApplicationContext(),"오답을 미암기 단어로 표기하였습니다.",Toast.LENGTH_SHORT).show();
            }
            finish();
        });


    }
    public void dbCheck(){
        db.collection("users").document(user.getUid()).collection("wordbooks").document(wordbookID)
                .get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Map<String, Object> wordList_find = (Map<String, Object>) document.getData().get("wordlist");
                try {
                    Map<String, Object> newWordcardArray = new HashMap<>();
                    for (int i = 0; i < wordList_find.size(); i++) {
                        HashMap map_find = (HashMap) wordList_find.get(String.valueOf(i));
                        Map<String, Object> newWordCard = new HashMap<>();
                        String word_english = map_find.get("word").toString();
                        newWordCard.put("word", word_english);
                        newWordCard.put("mean1", map_find.get("mean1"));
                        if (map_find.get("mean2") != "") {
                            newWordCard.put("mean2", map_find.get("mean2"));
                        }
                        if (map_find.get("mean3") != "") {
                            newWordCard.put("mean3", map_find.get("mean3"));
                        }
                        if (sendDataArr[num].equals(word_english)) {
                            //memorization 1로 전환 후
                            newWordCard.put("memorization", 0);
                        } else {
                            newWordCard.put("memorization", map_find.get("memorization"));
                        }
                        newWordcardArray.put(String.valueOf(i), newWordCard);
                    }
                    db.collection("users").document(user.getUid()).collection("wordbooks").document(wordbookID)
                            .update("wordlist", newWordcardArray);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i("mytag", "get failed with " + task.getException());
            }
            return;
        });
    }
}