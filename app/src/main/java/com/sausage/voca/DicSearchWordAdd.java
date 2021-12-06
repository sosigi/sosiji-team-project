package com.sausage.voca;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DicSearchWordAdd extends AppCompatActivity {

    private EditText editText1, editText2,editText3,editText4;
    private ImageButton word_add_back;
    private TextView word_add_add, category_view;
    private RelativeLayout drawer;

    TextView titleText2, titleText3;
    LinearLayout linearLayout2, linearLayout3;
    ImageButton imageButton2, imageButton3;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String wordbookID = "0";
    ArrayList<String> titles = new ArrayList<>();

    //입력할 korean2, korean3 존재여부
    boolean korean2Add = false;
    boolean korean3Add = false;
    //입력된 의미의 개수
    int koreanCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dic_search_word_add);
        getTitles();

        drawer = findViewById(R.id.drawer);
        category_view = findViewById(R.id.category_view);

        editText3 = findViewById(R.id.korean2);
        editText4 = findViewById(R.id.korean3);

        //의미2 끌어오기
        titleText2 = findViewById(R.id.korean2_title);
        linearLayout2 = findViewById(R.id.korean2_layout);
        imageButton2 = findViewById(R.id.korean2_deleteBtn);
        //의미3 끌어오기
        titleText3 = findViewById(R.id.korean3_title);
        linearLayout3 = findViewById(R.id.korean3_layout);
        imageButton3 = findViewById(R.id.korean3_deleteBtn);

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
        }else{
            Toast.makeText(category_view.getContext(),"다시 시도해주세요." ,Toast.LENGTH_SHORT).show();
            finish();
        }

        word_add_back = findViewById(R.id.word_add_back);
        word_add_back.setOnClickListener(view -> finish());

        word_add_add = findViewById(R.id.wordAddCompelete);
        word_add_add.setOnClickListener(v -> {
            Map<String, Object> wordcardData = new HashMap<>();
            String english = editText1.getText().toString();
            String korean1 = editText2.getText().toString();
            String korean2 = editText3.getText().toString();
            String korean3 = editText4.getText().toString();

            if (english.equals("") || korean1.equals("")) {
                Toast.makeText(category_view.getContext(), "한 단어당 최소 단어 1개와 의미1개가 필요합니다.", Toast.LENGTH_SHORT).show();
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
                    //Log.i("mytag", "여기까지는 들어옴");
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
                                    Toast.makeText(category_view.getContext(),"중복되는 단어가 존재합니다." ,Toast.LENGTH_SHORT).show();
                                }
                            }catch(NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Log.i("mytag", "No such document");
                    }
                } else {
                    Log.i("mytag", "get failed with " + task.getException());
                }
            });
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

    public void addKoreanMean(View view) {
        if (koreanCount==1) {
            titleText2.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.VISIBLE);
            imageButton2.setVisibility(View.VISIBLE);
            editText3.setVisibility(View.VISIBLE);
            koreanCount = koreanCount+1;
            korean2Add = true;
        } else if (koreanCount==2) {
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