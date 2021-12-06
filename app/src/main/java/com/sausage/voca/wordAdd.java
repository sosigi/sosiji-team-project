package com.sausage.voca;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class wordAdd extends AppCompatActivity {

    TextView textView;
    ImageButton word_add_btn;
    EditText editText1, editText2, editText3, editText4;
    private ImageButton word_add_back;
    private TextView word_add_add;

    View view2, view3;
    TextView titleText2, titleText3;
    LinearLayout linearLayout2, linearLayout3;
    ImageButton imageButton2, imageButton3;


    //입력할 단어장 정보
    String wordbookID = "0";
    //입력할 korean2, korean3 존재여부
    boolean korean2Add = false;
    boolean korean3Add = false;
    //입력된 의미의 개수
    int koreanCount = 1;
    //최종적으로 db에 전송되는 영단어
    String english;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_add);
        wordbookID = getIntent().getStringExtra("categoryID");

        editText1 = findViewById(R.id.english);
        editText2 = findViewById(R.id.korean1);
        editText3 = findViewById(R.id.korean2);
        editText4 = findViewById(R.id.korean3);

        view2 = findViewById(R.id.wordAdd2_line);
        titleText2 = findViewById(R.id.wordAdd2_title);
        linearLayout2 = findViewById(R.id.wordAdd2_layout);
        imageButton2 = findViewById(R.id.wordAdd2_deleteBtn);

        view3 = findViewById(R.id.wordAdd3_line);
        titleText3 = findViewById(R.id.wordAdd3_title);
        linearLayout3 = findViewById(R.id.wordAdd3_layout);
        imageButton3 = findViewById(R.id.wordAdd3_deleteBtn);


        word_add_btn = findViewById(R.id.word_mean_add_btn);
        word_add_btn.setOnClickListener(view -> addKoreanMean(view));
        textView = findViewById(R.id.koreanAddBtn);
        textView.setOnClickListener(view -> addKoreanMean(view));

        word_add_back = findViewById(R.id.word_add_back);
        word_add_back.setOnClickListener(view -> finish());

        word_add_add = findViewById(R.id.wordAddCompelete);
        word_add_add.setOnClickListener(view -> end(view));
    }

    public void end(View view) {
        Map<String, Object> wordcardData = new HashMap<>();

        english = editText1.getText().toString();
        String korean1 = editText2.getText().toString();
        String korean2 = editText3.getText().toString();
        String korean3 = editText4.getText().toString();
        if (english.equals("") || korean1.equals("")) {
            Toast.makeText(view.getContext(), "한 단어당 최소 단어 1개와 의미1개가 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        wordcardData.put("word", english);
        wordcardData.put("mean1", korean1);
        wordcardData.put("memorization", 0);

        boolean korean2AddExist=false;
        if(korean2Add){
            if(!korean2.equals("")){
                wordcardData.put("mean2", korean2);
                korean2AddExist=true;
            }
        }
        if(korean3Add){
            if(korean2AddExist){
                if(!korean3.equals("")){
                    wordcardData.put("mean3", korean3);
                }
            }else{
                if(!korean3.equals("")){
                    wordcardData.put("mean2", korean3);
                }
            }
        }

        DocumentReference wordBooksDoc = db.collection("users").document(user.getUid()).collection("wordbooks").document(wordbookID);
        wordBooksDoc.get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> wordList = (Map<String, Object>) document.getData().get("wordlist");

                    //영단어 중복되는지 검사.
                    boolean alreadyWordExist = false;
                    try {
                        for(int i=0;i<wordList.size();i++){
                            Map<String, Object> map_find = (HashMap) wordList.get(String.valueOf(i));
                            Log.i("mtyag",map_find.get("word").toString()+"::"+english);
                            if(map_find.get("word").toString().equals(english)){
                                alreadyWordExist=true;
                            }
                        }
                        if(!alreadyWordExist){
                            //중복안됨을 확인하고 db로 데이터 전송.
                            int wordBookNum = wordList.size();
                            wordList.put(String.valueOf(wordBookNum), wordcardData);
                            wordBooksDoc.update("wordlist", wordList);
                            finish();
                        }else{
                            Toast.makeText(view.getContext(),"중복되는 단어가 존재합니다." ,Toast.LENGTH_SHORT).show();
                        }
                    }catch(NullPointerException e){
                        e.printStackTrace();
                    }
                } else {
                    Log.i("mytag", "No such document");
                }
            } else {
                Log.i("mytag", "get failed with " + task.getException());
            }
        });
    }

    public void addKoreanMean(View view) {
        if (koreanCount==1) {
            view2.setVisibility(View.VISIBLE);
            titleText2.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.VISIBLE);
            imageButton2.setVisibility(View.VISIBLE);
            editText3.setVisibility(View.VISIBLE);
            koreanCount = koreanCount+1;
            korean2Add = true;
        } else if (koreanCount==2) {
            view3.setVisibility(View.VISIBLE);
            titleText3.setVisibility(View.VISIBLE);
            linearLayout3.setVisibility(View.VISIBLE);
            editText4.setVisibility(View.VISIBLE);
            imageButton3.setVisibility(View.VISIBLE);
            koreanCount=koreanCount+1;
            korean3Add = true;
        } else {
            Toast.makeText(view.getContext(), "한 단어당 의미는 최대 3개까지 저장 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteKoreanMean2(View view) {
        if (korean2Add) {
            Log.i("mytag", "2 삭제 실행");

            view2.setVisibility(View.GONE);
            titleText2.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.GONE);
            imageButton2.setVisibility(View.GONE);
            editText3.setVisibility(View.GONE);
            koreanCount = koreanCount-1;
            korean2Add = false;
        } else {
            Log.i("mytag", "2삭제 실행되지 않음.");
        }
    }

    public void deleteKoreanMean3(View view) {
        Log.i("mytag", "지우는 함수실행");
        if (korean3Add) {
            Log.i("mytag", "3 삭제 실행");
            view3.setVisibility(View.GONE);
            titleText3.setVisibility(View.GONE);
            linearLayout3.setVisibility(View.GONE);
            editText4.setVisibility(View.GONE);
            imageButton3.setVisibility(View.GONE);
            koreanCount= koreanCount-1;
            korean3Add = false;
        } else {
            Log.i("mytag", "삭제 실행되지 않음.");
        }
    }

}