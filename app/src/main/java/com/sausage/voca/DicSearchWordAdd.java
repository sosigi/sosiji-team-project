package com.sausage.voca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DicSearchWordAdd extends AppCompatActivity {

    private EditText editText1, editText2;
    private ImageButton word_add_back;
    private TextView word_add_add;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String wordbookID = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dic_search_word_add);

        String[] data = getIntent().getStringArrayExtra("data");

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
                Map<String, Object> wordcardData = new HashMap<>();
                wordcardData.put("word", data[0]);
                wordcardData.put("mean1", data[1]);
                wordcardData.put("mean2", "");
                wordcardData.put("mean3", "");
                wordcardData.put("memorization", 0);


                DocumentReference wordBooksDoc = db.collection("users").document(user.getUid()).collection("wordbooks").document(wordbookID);
                wordBooksDoc.get().addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> wordList = (Map<String, Object>) document.getData().get("wordlist");
                            int wordBookNum = wordList.size();
                            wordList.put(String.valueOf(wordBookNum),wordcardData);
                            wordBooksDoc.update("wordlist", wordList);
                        } else {
                            Log.i("mytag", "No such document");
                        }
                    } else {
                        Log.i("mytag", "get failed with " + task.getException());
                    }
                });
                //TODO 2안 해보고 안되면 3안으로 ㄱㄱ. 3안은 CategoryAdd.java 참고
                finish();
            }
        });
    }
}