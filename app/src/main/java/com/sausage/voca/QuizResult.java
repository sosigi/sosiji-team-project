package com.sausage.voca;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class QuizResult extends AppCompatActivity {

    Button back_btn;
    CheckBox checkbox = findViewById(R.id.quiz_result_check);
    TextView result = findViewById(R.id.result_accuracy);
    TextView correct = findViewById(R.id.result_correct);
    TextView wrong = findViewById(R.id.result_wrong);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

//        accuracy = (5 - wrong_count)*20;
//        result.setText(accuracy +"%\n정답률");
//        correct.setText(5-wrong_count);
//        wrong.setText(wrong_count);

        back_btn = findViewById(R.id.quiz_result_back);

        back_btn.setOnClickListener(view -> {

                if(checkbox.isChecked()) {
                    //ToDo 오답을 미암기 단어로 표기
                }

            Intent intent = new Intent(getApplicationContext(), wordbook.class);
            startActivity(intent);

        });
    }
}