package com.sausage.voca;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.sausage.voca.R;

public class QuizDialog extends AppCompatActivity {

    Button start;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button)findViewById(R.id.quiz_start);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new Dialog(QuizDialog.this);

                dialog.setContentView(R.layout.activity_quiz_dialog);
            }
        });

        CheckBox checkBox1 = (CheckBox) findViewById(R.id.check1) ;
        checkBox1.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    // TODO : CheckBox is checked.
                } else {
                    // TODO : CheckBox is unchecked.
                }
            }
        }) ;

        CheckBox checkBox2 = (CheckBox) findViewById(R.id.check2) ;
        checkBox2.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    // TODO : CheckBox is checked.
                } else {
                    // TODO : CheckBox is unchecked.
                }
            }
        }) ;

        CheckBox checkBox3 = (CheckBox) findViewById(R.id.check3) ;
        checkBox3.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    // TODO : CheckBox is checked.
                } else {
                    // TODO : CheckBox is unchecked.
                }
            }
        }) ;
    }
}
