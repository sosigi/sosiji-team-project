package com.sausage.voca;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class CategoryAdd extends AppCompatActivity {

    private ImageButton back_btn;
    private TextView complete_btn;
    EditText newWordBookTitle;
    EditText newWordBookExplain;

    //database
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //단어장 정보
    String wordbookID = "0";
    //db에 저장된 단어장 개수
    int wordBooksCount=0;

    //db에 단어장 추가 완료여부
    boolean wordBookAddComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_add);
        //wordBooksCount = Integer.parseInt(getIntent().getStringExtra("wordBooksCount"));

        back_btn = findViewById(R.id.category_add_back);
        complete_btn = findViewById(R.id.category_add_complete);
        newWordBookTitle = findViewById(R.id.category_new_title);
        newWordBookExplain = findViewById(R.id.category_new_explain);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Category.class);
                startActivity(intent);
                finish();
            }
        });

        complete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = newWordBookTitle.getText().toString();
                String explain = newWordBookExplain.getText().toString();
                if (title.length() != 0 && explain.length() != 0) {
                    Map<String, Object> newWordBook = new HashMap<>();
                    newWordBook.put("wordbooktitle", title);
                    newWordBook.put("wordbookexplain", explain);
                    newWordBook.put("wordbooklist", null);

                    //TODO : 시하 db update 참고
                    CollectionReference wordbooksCol = db.collection("users").document(user.getUid()).collection("wordbooks");
                    wordbooksCol.get().addOnCompleteListener((task -> {
                        if (task.isSuccessful()) {
//                            for(int i=0;i<task.getResult().size();i++){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.exists()) {
//                                DocumentSnapshot document = task.getResult().getDocuments().get(i);
                                    wordBookAddComplete = true;
                                    wordBooksCount = wordBooksCount + 1;
                                    if (document.getData().get("wordbooktitle").toString().equals(title)) {
                                        //db에 title과 중복되는 wordBookTitle이 있는지 확인.
                                        Toast myToast = Toast.makeText(view.getContext(), R.string.category_add_rewrite_title, Toast.LENGTH_SHORT);
                                        myToast.show();
                                        wordBookAddComplete = false;
                                        break;
                                    }
                                }
                            }
                            if (wordBookAddComplete) {
                                //중복없음을 확인 후 db에 새로운 단어장 추가.
                                wordbooksCol.document(String.valueOf(wordBooksCount))
                                        .set(newWordBook, SetOptions.merge());
//                                wordbooksCol.add(newWordBook);
                                Toast myToast = Toast.makeText(view.getContext(), R.string.category_add_complete, Toast.LENGTH_SHORT);
                                myToast.show();
                                wordBookAddComplete = false;

                                Intent intent = new Intent(getApplicationContext(), Category.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Log.i("mytag", "get failed with " + task.getException());
                        }
                    }));
                } else {
                    //editText가 모두 작성되지 않으면 toast문 띄움.
                    Toast myToast = Toast.makeText(view.getContext(), R.string.category_add_can_not_complete, Toast.LENGTH_SHORT);
                    myToast.show();
                }
            }
        });
    }
}