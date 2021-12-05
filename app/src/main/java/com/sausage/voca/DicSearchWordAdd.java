package com.sausage.voca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DicSearchWordAdd extends AppCompatActivity {

    private EditText editText1, editText2;
    private ImageButton word_add_back;
    private TextView word_add_add, category_view;
    private RelativeLayout drawer;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String wordbookID = "0";
    ArrayList<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dic_search_word_add);
        getTitles();


        drawer = findViewById(R.id.drawer);
        category_view = findViewById(R.id.category_view);

        drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("mytag", titles.toString());

                AlertDialog.Builder dlg = new AlertDialog.Builder(DicSearchWordAdd.this);
                dlg.setTitle("카테고리 선택"); //제목
                String[] versionArray = titles.toArray(new String[titles.size()]);
                //Log.i("mytag", titles.toString());

                dlg.setItems(versionArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        category_view.setText(versionArray[which]);
                        wordbookID = String.valueOf(which);
                    }
                });
//                버튼 클릭시 동작
                dlg.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        //토스트 메시지
                        Toast.makeText(DicSearchWordAdd.this,"변경 : ",Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });


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
                        Log.i("mytag", "여기까지는 들어옴");
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (document.getData().get("wordlist")==null){
                                Map<String, Object> newMap = new HashMap<>();
                                newMap.put("0", wordcardData);
                                Map<String, Object> newnewMap = new HashMap<>();
                                newnewMap.put("wordlist", newMap);
                                wordBooksDoc.set(newnewMap, SetOptions.merge());
                            }else{
                                Map<String, Object> wordList = (Map<String, Object>) document.getData().get("wordlist");
                                int wordBookNum = wordList.size();
                                wordList.put(String.valueOf(wordBookNum), wordcardData);
                                wordBooksDoc.update("wordlist", wordList);
                            }
                        } else {
                            Log.i("mytag", "No such document");
                        }
                    } else {
                        Log.i("mytag", "get failed with " + task.getException());
                    }
                });
                finish();
            }
        });
    }

    private void getTitles() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference wordbooksRef = db
                    .collection("users")
                    .document(user.getUid())
                    .collection("wordbooks");

            //Log.i("mytag", "wordbooksRef 주소 : " + wordbooksRef.toString()); //일단 wordbook ref까지는 접근 완료.
            //Log.i("mytag", "wordbooksRef 경로 : " + wordbooksRef.getPath());

            //이제 wordbook 안에 있는 두 문서(0,1)에 접근하고, 그 각각의 문서에서 wordbooktitle 필드를 빼내와야 한다
            wordbooksRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        titles.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Log.i("mytag", "words : " + document.getData().get("wordbooktitle"));
                            String wordbooktitle = document.getData().get("wordbooktitle").toString();
                            titles.add(wordbooktitle);
                            //Log.i("mytag", titles.toString());
                        }
                    } else {
                        Log.d("mytag", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }
}