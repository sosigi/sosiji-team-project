package com.sausage.voca;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class QuizDialog extends AppCompatActivity {

    Button start;
    public int quiz_option;

    //선택된 checkbox개수
    int checkboxSelectCount =0;
    //단어장 정보
    String wordbookID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_dialog);
        wordbookID = getIntent().getStringExtra("id");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태 바 없애기

        start = findViewById(R.id.quiz_start);
        Toast toast = Toast.makeText(getApplicationContext() ,"한 가지 옵션을 선택하세요.", Toast.LENGTH_SHORT);

        CheckBox checkBox1 = findViewById(R.id.check1);
        CheckBox checkBox2 = findViewById(R.id.check2);
        CheckBox checkBox3 = findViewById(R.id.check3);

        checkBox1.setOnClickListener(view -> {
            if(checkBox1.isChecked()){
                checkboxSelectCount=checkboxSelectCount+1;
            }else{
                checkboxSelectCount=checkboxSelectCount-1;
            }
        });
        checkBox2.setOnClickListener(view -> {
            if(checkBox2.isChecked()){
                checkboxSelectCount=checkboxSelectCount+1;
            }else{
                checkboxSelectCount=checkboxSelectCount-1;
            }
        });
        checkBox3.setOnClickListener(view -> {
            if(checkBox3.isChecked()){
                checkboxSelectCount=checkboxSelectCount+1;
            }else{
                checkboxSelectCount=checkboxSelectCount-1;
            }
        });

        start.setOnClickListener(v -> {
            if(checkboxSelectCount==1){
                if(checkBox1.isChecked()) {
                    quiz_option = 0;
                    startQuiz();
                }
                else if(checkBox2.isChecked()) {
                    quiz_option = 1;
                    startQuiz();
                }
                else if(checkBox3.isChecked()) {
                    quiz_option = 2;
                    startQuiz();
                }
            }else{
                toast.show();
            }
        });
    }

    public void startQuiz(){
        String sendData = new StringBuilder().append(wordbookID).append("/").append(String.valueOf(quiz_option)).toString();
//        Log.i("mytag",sendData);
        Intent intent = new Intent(getApplicationContext(), QuizPage.class).putExtra("sendData", sendData);
        startActivity(intent);
        finish();
    }

}