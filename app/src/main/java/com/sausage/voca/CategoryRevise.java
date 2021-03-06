package com.sausage.voca;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CategoryRevise extends AppCompatActivity {
    private EditText title_editText;
    private EditText explain_editText;

    //단어장 정보
    String wordbookID;
    int wordbookIDInt = 0;
    String wordbooktitle;
    String wordbookexplain;
    int wordBooksCountInt = 0;

    int deleteNum;

    //[database]
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference wordbooksCol;
    DocumentReference wordBookDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_revise);
        wordbooktitle = getIntent().getStringExtra("titleID");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태 바 없애기

        ImageButton back_btn = findViewById(R.id.category_edit_back);
        TextView complete_btn = findViewById(R.id.category_edit_complete);
        TextView delete_btn = findViewById(R.id.category_delete);
        title_editText = findViewById(R.id.category_new_title);
        explain_editText = findViewById(R.id.category_new_explain);

        //db에서 title의 explain찾아서 editText의 text로 출력.
        wordbooksCol = db.collection("users").document(user.getUid()).collection("wordbooks");
        wordbooksCol
                .get().addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String title = Objects.requireNonNull(document.getData().get("wordbooktitle")).toString();
                    wordBooksCountInt = wordBooksCountInt + 1;
                    if (title.equals(wordbooktitle)) {
                        wordbookID = document.getId();
                        wordbookIDInt = Integer.parseInt(wordbookID);
                        wordbookexplain = Objects.requireNonNull(document.getData().get("wordbookexplain")).toString();
                        title_editText.setText(wordbooktitle);
                        explain_editText.setText(wordbookexplain);
                        wordBookDoc = db.collection("users").document(user.getUid()).collection("wordbooks").document(wordbookID);
                    }
                }
            } else {
                Log.i("mytag", "get failed with " + task.getException());
            }
        }));

        //back btn 클릭시
        back_btn.setOnClickListener(view -> {
            finish();
        });

        //category 수정 완료
        complete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeCategory();

            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategory();

            }
        });
    }

    public void deleteCategory() {
        deleteNum = wordbookIDInt;
        wordbooksCol
                .get().addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                Map<String, Object> removeDoc;
                for (; deleteNum < wordBooksCountInt - 1; deleteNum = deleteNum + 1) {
                    removeDoc = task.getResult().getDocuments().get(deleteNum + 1).getData();
                    Map<String, Object> newData = new HashMap<>();
                    if (removeDoc != null) {
                        newData.put("wordbooktitle", Objects.requireNonNull(removeDoc.get("wordbooktitle")).toString());
                        newData.put("wordbookexplain", Objects.requireNonNull(removeDoc.get("wordbookexplain")).toString());
                        newData.put("wordlist", removeDoc.get("wordlist"));
                        //Log.i("mytag", "이동할데이터" + newData.toString() + "->to:" + deleteNum);
                        wordbooksCol.document(String.valueOf(deleteNum)).set(newData);
                    }
                }
                wordbooksCol.document(String.valueOf(wordBooksCountInt - 1))
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d("mytag", "Last DocumentSnapshot successfully deleted!");
                            Toast.makeText(this, wordbooktitle + "이/가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            //여기에 intent
                            Intent intent = new Intent().putExtra("init", "delete");
                            setResult(RESULT_OK, intent);
                            Log.i("mytag", "보낼 data는 delete");
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Log.w("mytag", "Error deleting document", e);
                            Toast.makeText(this, "다시 시도하시오.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Log.i("mytag", "get failed with " + task.getException());
            }
        }));
    }

    public void completeCategory() {
        String newTitle = title_editText.getText().toString();
        String newExplain = explain_editText.getText().toString();
        //완료시 db접근.
        wordbooksCol.get().addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                boolean complete = true;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getId().equals(wordbookID)) {
                        continue;
                    }
                    String title = Objects.requireNonNull(document.getData().get("wordbooktitle")).toString();
                    if (title.equals(newTitle)) {
                        complete = false;
                        Toast.makeText(this, "중복되는 이름이 존재합니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if (complete) {
                    //db의 wordbooktitle들과 중복 여부가 확인됨.
                    wordBookDoc.update("wordbooktitle", newTitle);
                    wordBookDoc.update("wordbookexplain", newExplain);
                    Toast.makeText(this, newTitle + "이/가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    //여기에 intent
                    Intent intent = new Intent().putExtra("init", "edit");
                    setResult(RESULT_OK, intent);
                    Log.i("mytag", "보낼 data는 edit");
                    finish();
                }
            } else {
                Log.i("mytag", "get failed with " + task.getException());
            }
        }));
    }
}