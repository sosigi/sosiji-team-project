package com.sausage.voca;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CategoryEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        ImageButton back_btn = findViewById(R.id.category_edit_back);
        TextView complete_btn = findViewById(R.id.category_edit_complete);
        TextView delete_btn = findViewById(R.id.category_delete);

        back_btn.setOnClickListener(view -> finish());

        complete_btn.setOnClickListener(view -> {
            //완료시 db접근.
            finish();
        });

        delete_btn.setOnClickListener(view -> {
            //db접근

            Toast.makeText(view.getContext(),"수능 영단어 이/가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}