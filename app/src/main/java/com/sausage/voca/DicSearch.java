package com.sausage.voca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class DicSearch extends AppCompatActivity {

    private EditText search_result; //이게 회색이면 안 쓰인거니까 뭐가 문제인지 눈여겨볼것...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dic_search);

        search_result = findViewById(R.id.search_result);
        //나 대체 왜 그랬는지는 모르겠는데 search_result.findViewById(R.id.search_result); 라고 자꾸 써서 error냈다... 정신차려

        Intent intent = getIntent();
        CharSequence search = intent.getExtras().getCharSequence("search");
        search_result.setText(search);
    }
}