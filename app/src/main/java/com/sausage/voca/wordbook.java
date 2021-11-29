package com.sausage.voca;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class wordbook extends AppCompatActivity {
    //단어 정렬
    private String[] mSorting = {"전체", "암기", "미암기"};
    private TextView mWordSorting;
    private AlertDialog mWordSortingSelectDialog;
    //word card data list
    ArrayList<Word> dataList = new ArrayList<>();

    //database
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //단어 암기 <->미암기 전환 체크 - word test용
    ImageView wordMemory_check;
    ImageView wordMemory_uncheck;
    TextView word1;
    TextView word1_m1;
    TextView word1_m2;
    TextView word1_m3;
    TextView search, category, mypage;

    //단어장 상단바
    TextView categoryName;
    //단어장 상단부 - 소개 (wordbookTitle & explain)
    TextView wordbook_top_title;
    TextView wordbook_top_explain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordbook);


        //custom font 적용 - quiz btn
        TextView wordQuiztextview = findViewById(R.id.wordQuiz);
        Typeface wordfont = Typeface.createFromAsset(getAssets(), "times_new_roman.ttf");
        wordQuiztextview.setTypeface(wordfont);

        //drawer onclickListener
        search = findViewById(R.id.dicSearch);
        category = findViewById(R.id.category);
        mypage = findViewById(R.id.setting);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent().putExtra("inform", "search");
                Log.i("mytag", "보낼 Data는 search");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent().putExtra("inform", "category");
                Log.i("mytag", "보낼 Data는 category");
                setResult(RESULT_OK, intent);

                finish();
            }
        });
        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("inform", "mypage");
                Log.i("mytag", "보낼 Data는 mypage");

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        //단어장 title, explain Textset
        categoryName = findViewById(R.id.categoryName);
        categoryName.setSelected(true);
        wordbook_top_title = findViewById(R.id.wordbook_top_title);
        wordbook_top_explain = findViewById(R.id.wordbook_top_explain);
        if (user != null) {
            //TODO : 입력받은 단어장의 문서 id(int number)를 마지막 document 인자에 넣어주면됨.
            DocumentReference docRef = db.collection("users").document(user.getUid()).collection("wordbooks").document("0");
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //Log.i("mytag", "DocumentSnapshot data: " + document.getData());
                            categoryName.setText(document.get("wordbooktitle").toString());
                            wordbook_top_title.setText(document.get("wordbooktitle").toString());
                            wordbook_top_explain.setText(document.get("wordbookexplain").toString());
                        } else {
                            Log.i("mytag", "No such document");
                        }
                    } else {
                        Log.i("mytag", "get failed with ", task.getException());
                    }
                }
            });
        } else {
            Log.i("mytag", "user is null");
        }

        //wordcard list 나열
        //TODO : 입력받은 단어장의 문서 id(int number)를 마지막 document 인자에 넣어주면됨.
        db.collection("users").document(user.getUid()).collection("wordbooks").document("0")
                .get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                dataList = new ArrayList<>();
                DocumentSnapshot document = task.getResult();
                Map<String, Object> wordList = (Map<String, Object>) document.getData().get("wordlist");
                try {
                    Iterator<String> keys = wordList.keySet().iterator();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        HashMap map = (HashMap) wordList.get(key);
                        String word_english = map.get("word").toString();
                        String word_meaning1 = map.get("mean1").toString();
                        String word_meaning2 = "";
                        String word_meaning3 = "";
                        int memorization = Integer.parseInt(map.get("memorization").toString());
                        if (map.get("mean2") != null) {
                            word_meaning2 = map.get("mean2").toString();
                        }
                        if (map.get("mean3") != null) {
                            word_meaning3 = map.get("mean3").toString();
                        }
                        dataList.add(new Word(word_english, word_meaning1, word_meaning2, word_meaning3,memorization));
                    }
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.word_card_recycleView);
                    LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(manager); // LayoutManager 등록
                    recyclerView.setAdapter(new WordAdapter(dataList));  // Adapter 등록

                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            } else {
                Log.i("mytag", "get failed with " + task.getException());
            }
        });


        //단어 정렬 선택 btn (전체/암기/미암기)
        mWordSorting = (TextView) findViewById(R.id.select_wordSorting);
        mWordSorting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWordSortingSelectDialog.show();
                //선택된 정렬방식에 따라 wordcard 정렬
            }
        });
        mWordSortingSelectDialog = new AlertDialog.Builder(wordbook.this)
                .setItems(mSorting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mWordSorting.setText(mSorting[i] + " ▼");
                    }
                })
                .setTitle("정렬방식")
                .setPositiveButton("확인", null)
                .setNegativeButton("취소", null)
                .create();

        //상단바의 햄버거 바 선택->main page로 navigate할 수 있는 drawer 등장
        ImageButton HamburgerBarButton = (ImageButton) findViewById(R.id.hamburgerBarBtn);
        HamburgerBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                if (!drawer.isDrawerOpen(Gravity.LEFT)) drawer.openDrawer(Gravity.LEFT);
                else drawer.closeDrawer(Gravity.LEFT);
            }
        });

        //상단바의 plus btn 선택->word add page로 전환
        ImageButton plusBtnButton = (ImageButton) findViewById(R.id.plusBtn);
        plusBtnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), wordAdd.class);
                //Log.i("mytag",getApplicationContext().toString());
                startActivity(intent);
            }
        });
    }

    //wordcard에서 단어 삭제 btn 클릭시
    public void deleteWordBtnClick(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("단어 삭제");
        alert.setMessage("정말 삭제 하시겠습니까?");

        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("mytag", "YES");

            }
        });

        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Log.i("mytag", "NO");
            }
        });
        alert.show();
    }

    //단어 암기<->미암기 체크
    /*public void wordMemoryBtnClick(View view) {
        //객체 획득
        wordMemory_uncheck = (ImageView) findViewById(R.id.memorization_uncheck);
        wordMemory_check = (ImageView) findViewById(R.id.memorization_check);
        //객체 획득 - 암기 여부에 따른 단어 카드 글색상 변경
        word1 = (TextView) findViewById(R.id.word1);
        word1_m1 = (TextView) findViewById(R.id.word1_m1);
        word1_m2 = (TextView) findViewById(R.id.word1_m2);
        word1_m3 = (TextView) findViewById(R.id.word1_m3);

        //미암기->암기 Button 이벤트 콜백함수
        if (view.getId() == R.id.memorization_uncheck) {
            wordMemory_uncheck.setVisibility(View.GONE);
            wordMemory_check.setVisibility(View.VISIBLE);
            int color = ContextCompat.getColor(getApplicationContext(), R.color.memorization);
            word1.setTextColor(color);
            word1_m1.setTextColor(color);
            word1_m2.setTextColor(color);
            word1_m3.setTextColor(color);
            Toast toastUncheck = Toast.makeText(this.getApplicationContext(), R.string.toast_change_to_memorization, Toast.LENGTH_SHORT);
            toastUncheck.show();
        }

        //암기->미암기 Button 이벤트 콜백함수
        else if (view.getId() == R.id.memorization_check) {
            wordMemory_check.setVisibility(View.GONE);
            wordMemory_uncheck.setVisibility(View.VISIBLE);
            int color = ContextCompat.getColor(getApplicationContext(), R.color.notMemorization);
            word1.setTextColor(color);
            word1_m1.setTextColor(color);
            word1_m2.setTextColor(color);
            word1_m3.setTextColor(color);
            Toast toastCheck = Toast.makeText(this.getApplicationContext(), R.string.toast_change_to_notmemorization, Toast.LENGTH_SHORT);
            toastCheck.show();
        }
    }*/
}