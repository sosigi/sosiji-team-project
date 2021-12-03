package com.sausage.voca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CategoryEdit extends AppCompatActivity {

    private TextView back_btn;
    private TextView complete_btn;
    private TextView delete_btn;
    private Toast toast = Toast.makeText(this,"수능 영단어 이/가 삭제되었습니다.", Toast.LENGTH_SHORT);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        back_btn = findViewById(R.id.category_edit_back);
        complete_btn = findViewById(R.id.category_edit_complete);
        delete_btn = findViewById(R.id.category_delete);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Category.class);

                startActivity(intent);
            }
        });

        complete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Category.class);

                startActivity(intent);
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toast.show();
                Intent intent = new Intent(getApplicationContext(), Category.class);

                startActivity(intent);
            }
        });
    }
}