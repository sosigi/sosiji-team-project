package com.sausage.voca;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class wordAdd extends AppCompatActivity {

    private TextView textView;
    private EditText editText1, editText2;
    private ImageButton word_add_back;
    private TextView json_test_text;

    //[은소]test - edit text에 입력된 내용을 realtime database에 업로드해봄.
    //데이터베이스에서 데이터를 읽고 쓰려면 DataReference의 인스턴스가 필요하다.
//    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    //child()는 데이터가 있을 위치의 이름을 정해준다.
//    DatabaseReference conditionRef = mRootRef.child("test");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_add);

        String[] data = getIntent().getStringArrayExtra("data");

        textView = findViewById(R.id.test_textview);
        editText1 = findViewById(R.id.english);
        editText2 = findViewById(R.id.korean);

        if (data!=null){
            editText1.setText(data[0]);
            editText2.setText(data[1]);
        }

        word_add_back = findViewById(R.id.word_add_back);
        word_add_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), wordbook.class);
                startActivity(intent);
            }
        });

    }

}