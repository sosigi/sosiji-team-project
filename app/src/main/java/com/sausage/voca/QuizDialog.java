package com.sausage.voca;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.sausage.voca.R;

public class QuizDialog extends AppCompatActivity {

    Button start;
    public int quiz_option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_dialog);

        start = (Button)findViewById(R.id.quiz_start);

        CheckBox checkBox1 = (CheckBox) findViewById(R.id.check1) ;
        CheckBox checkBox2 = (CheckBox) findViewById(R.id.check2) ;
        CheckBox checkBox3 = (CheckBox) findViewById(R.id.check3) ;

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox1.isChecked()) {
                    quiz_option = 0;
                }
                else if(checkBox2.isChecked()) {
                    quiz_option = 1;
                }
                else if(checkBox3.isChecked()) {
                    quiz_option = 2;
                }

                Intent intent = new Intent(getApplicationContext(), Quiz.class);
                startActivity(intent);
            }
        });
    }
}