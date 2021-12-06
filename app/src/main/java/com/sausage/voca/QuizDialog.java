package com.sausage.voca;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.sausage.voca.R;

public class QuizDialog extends AppCompatActivity {

    Button start;
    public int quiz_option;

    //private Toast toast = Toast.makeText(QuizDialog.this ,"한 가지 옵션을 선택하세요.", Toast.LENGTH_SHORT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_dialog);

        start = findViewById(R.id.quiz_start);

        CheckBox checkBox1 = findViewById(R.id.check1) ;
        CheckBox checkBox2 = findViewById(R.id.check2) ;
        CheckBox checkBox3 = findViewById(R.id.check3) ;

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                while(true) { //중복 선택 방지
                    if (checkBox1.isChecked()&&checkBox2.isChecked()) {
                        //toast.show();
                    }
                    else if(checkBox2.isChecked()&&checkBox3.isChecked()) {
                        //toast.show();
                    }
                    else if(checkBox3.isChecked()&&checkBox1.isChecked()) {
                        //toast.show();
                    }
                    else if(checkBox1.isChecked()&&checkBox2.isChecked()&&checkBox3.isChecked()) {
                        //toast.show();
                    }
                    else {
                        if(checkBox1.isChecked()) {
                            quiz_option = 0;
                            break;
                        }
                        else if(checkBox2.isChecked()) {
                            quiz_option = 1;
                            break;
                        }
                        else if(checkBox3.isChecked()) {
                            quiz_option = 2;
                            break;
                        }
                        else { //아무것도 선택 안 했을 때
                            //toast.show();
                        }
                    }
                }

                Intent intent = new Intent(getApplicationContext(), Quiz.class);
                startActivity(intent);
            }
        });
    }

}