package com.sausage.voca;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class wordAdd extends AppCompatActivity {

    private TextView textView;
    private EditText editText1, editText2;
    private ImageButton word_add_back;
    private TextView word_add_add;
    private TextView json_test_text;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String wordbookID = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_add);

        String[] data = getIntent().getStringArrayExtra("data");

        textView = findViewById(R.id.test_textview);
        editText1 = findViewById(R.id.english);
        editText2 = findViewById(R.id.korean);

        if (data != null) {
            editText1.setText(data[0]);
            editText2.setText(data[1]);
        }

        word_add_back = findViewById(R.id.word_add_back);
        word_add_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        word_add_add = findViewById(R.id.wordAddCompelete);
        word_add_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> newWord = new HashMap<String, String>();
                newWord.put("word", data[0]);
                newWord.put("mean1", data[1]);

                db.collection("users").document(user.getUid()).collection("wordbooks").document(wordbookID)
                        .update("wordlist", FieldValue.arrayUnion(newWord));
                //TODO 2안 해보고 안되면 3안으로 ㄱㄱ. 3안은 CategoryAdd.java 참고
                finish();
            }
        });

    }

}