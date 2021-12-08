package com.sausage.voca;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class wordbook extends AppCompatActivity implements View.OnClickListener {
    String TAG = "mytag";
    //단어 정렬
    final private String[] mSorting = {"전체", "암기", "미암기"};
    private TextView mWordSorting, logout;
    private AlertDialog mWordSortingSelectDialog;


    TextView wordHideBtn;
    TextView meanHideBtn;
    TextView wordQuiz;

    //word card data list
    ArrayList<Word> dataList = new ArrayList<>();
    WordAdapter wordAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    boolean dataChange = false;
    boolean dataDelete = false;

    TextView search, mypage;

    //단어장 상단바
    TextView categoryName;
    //단어장 상단부 - 소개 (wordbookTitle & explain)
    TextView wordbook_top_title;
    TextView wordbook_top_explain;

    //[database]
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference wordBooksDoc;

    //[단어장 정보]
    String wordbookID;
    //TODO : 입력받은 단어장의 문서 id(int number)를 마지막 document 인자에 넣어주면됨.
    int thisWordbookMemorizationType = 2;
    //default=2, 암기=1, 미암기=0;
    int thisWordbookHideType = 0;
    private View HamburgerBarButton;
    //default =0, 단어숨김=1, 뜻숨김=2;
    //단어장의 단어수
    int coundWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordbook);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태 바 없애기

        logout = findViewById(R.id.logout_txt);
        logout.setOnClickListener(this);
        //From. categoryFragment
        wordbookID = getIntent().getStringExtra("id");
        wordBooksDoc = db.collection("users").document(user.getUid()).collection("wordbooks").document(wordbookID);
        Log.i(TAG, "가져온 id값은 " + wordbookID);


        //quiz btn onclickListener & custom font 적용
        wordQuiz = findViewById(R.id.wordQuiz);
        wordQuiz.setOnClickListener(this);
        Typeface wordfont = Typeface.createFromAsset(getAssets(), "times_new_roman.ttf");
        wordQuiz.setTypeface(wordfont);

        //drawer onclickListener
        search = findViewById(R.id.dicSearch);
        mypage = findViewById(R.id.setting);

        //단어장 title, explain Textset
        //wordbooktitle, wordbookexplain 출력.

        categoryName = findViewById(R.id.categoryName);
        categoryName.setSelected(true);
        wordbook_top_title = findViewById(R.id.wordbook_top_title);
        wordbook_top_explain = findViewById(R.id.wordbook_top_explain);

        wordBooksDoc
                .get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    categoryName.setText(document.get("wordbooktitle").toString());
                    wordbook_top_title.setText(document.get("wordbooktitle").toString());
                    wordbook_top_explain.setText(document.get("wordbookexplain").toString());
                } else {
                    Log.i(TAG, "No such document");
                }
            } else {
                Log.i(TAG, "get failed with " + task.getException());
            }
        });

        updateWordcard(thisWordbookMemorizationType);

        //wordcard List 변경된 내용 있는지 확인.
        final DocumentReference docRef = db.collection("users").document(user.getUid()).collection("wordbooks").document(wordbookID);
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                //Log.d(TAG, "Current data: " + snapshot.getData());
                Map<String,Object> wordList = (Map<String, Object>) snapshot.getData().get("wordlist");
                if(wordList==null || wordList.size() > coundWord){
                    Log.i("mytag","current data updata");
                    updateWordcard(thisWordbookMemorizationType);
                }
            } else {
                Log.d(TAG, "Current data: null");
            }
        });

        //drawer기능
        onSidebarClick();

        //단어 정렬 선택 btn (전체/암기/미암기)
        mWordSorting = findViewById(R.id.select_wordSorting);
        mWordSorting.setOnClickListener(this);
        mWordSortingSelectDialog = new AlertDialog.Builder(wordbook.this)
                .setItems(mSorting, (dialogInterface, i) -> {
                    mWordSorting.setText(mSorting[i] + " ▼");
                    switch (mSorting[i]) {
                        case "전체":
                            thisWordbookMemorizationType = 2;
                            thisWordbookHideType = 0;
                            updateWordcard(thisWordbookMemorizationType);
                            break;
                        case "암기":
                            thisWordbookMemorizationType = 1;
                            thisWordbookHideType = 0;
                            updateWordcard(thisWordbookMemorizationType);
                            break;
                        case "미암기":
                            thisWordbookMemorizationType = 0;
                            thisWordbookHideType = 0;
                            updateWordcard(thisWordbookMemorizationType);
                            break;
                    }

                })
                .setTitle("정렬방식 선택")
                .setPositiveButton("확인", null)
                .setNegativeButton("취소", null)
                .create();

        //단어 숨김 btn 선택
        wordHideBtn = findViewById(R.id.hideWord);
        wordHideBtn.setOnClickListener(this);

        //뜻 숨김 btn 선택
        meanHideBtn = findViewById(R.id.hideMeaning);
        meanHideBtn.setOnClickListener(this);

        //Quiz Btn 선택
        wordQuiz = findViewById(R.id.wordQuiz);
        wordQuiz.setOnClickListener(this);


        //상단바의 햄버거 바 선택->main page로 navigate할 수 있는 drawer 등장
        ImageButton HamburgerBarButton = findViewById(R.id.hamburgerBarBtn);
        HamburgerBarButton.setOnClickListener(view -> {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (!drawer.isDrawerOpen(Gravity.LEFT)) drawer.openDrawer(Gravity.LEFT);
            else drawer.closeDrawer(Gravity.LEFT);
        });

        //상단바의 plus btn 선택->word add page로 전환
        ImageButton plusBtnButton = findViewById(R.id.plusBtn);
        plusBtnButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), wordAdd.class).putExtra("categoryID", wordbookID);
            startActivity(intent);
        });
    }

    private void onSidebarClick() {
        //drawer onclickListener
        search = findViewById(R.id.dicSearch);
        mypage = findViewById(R.id.setting);
        search.setOnClickListener(this);
        mypage.setOnClickListener(this);
    }


    //update wordbook wordcard
    //입력받는 memorizationType의 int값에 따라 암기 or 미암기 단어들만을 출력한다.
    //암기 1 미암기 0 전체 2
    private void updateWordcard(int memorizationType){
        wordBooksDoc.get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    dataList = new ArrayList<>();
                    Map<String, Object> wordList = (Map<String, Object>) document.getData().get("wordlist");
                    int countWordlist =0;
                    if(wordList == null){
                        countWordlist=0;
                    }else {
                        try {
                            for (String key : wordList.keySet()) {
                                HashMap map = (HashMap) wordList.get(key);
                                int memorization = Integer.parseInt(map.get("memorization").toString());
                                if (memorizationType == 2 || memorization == memorizationType) {
                                    countWordlist++;
                                    String word_english = map.get("word").toString();
                                    String word_meaning1 = map.get("mean1").toString();
                                    String word_meaning2 = "";
                                    String word_meaning3 = "";
                                    if (map.get("mean2") != null) {
                                        word_meaning2 = map.get("mean2").toString();
                                    }
                                    if (map.get("mean3") != null) {
                                        word_meaning3 = map.get("mean3").toString();
                                    }
                                    if (thisWordbookHideType == 1) {
                                        dataList.add(new Word("", word_meaning1, word_meaning2, word_meaning3, memorization));
                                    } else if (thisWordbookHideType == 2) {
                                        dataList.add(new Word(word_english, "", "", "", memorization));
                                    } else {
                                        dataList.add(new Word(word_english, word_meaning1, word_meaning2, word_meaning3, memorization));
                                    }
                                }
                            }
                            recyclerView = findViewById(R.id.word_card_recycleView);
                            manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(manager); // LayoutManager 등록
                            wordAdapter = new WordAdapter(dataList);
                            recyclerView.setAdapter(wordAdapter);  // Adapter 등록

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    TextView text = findViewById(R.id.recommend_word_add);
                    coundWord = countWordlist;
                    if(countWordlist==0){
                        text.setVisibility(View.VISIBLE);
                    }else{
                        text.setVisibility(View.GONE);
                    }
                } else {
                    Log.i(TAG, "No such document");
                }
            } else {
                Log.i(TAG, "get failed with " + task.getException());
            }
        });
    }

    //wordcard에서 단어 삭제 btn 클릭시
    public void deleteWordBtnClick(View view) {
        if(thisWordbookHideType>0){
            Toast.makeText(view.getContext(),"단어/뜻 숨김 처리 시에는 단어 삭제가 불가능합니다.",Toast.LENGTH_SHORT).show();
        } else {
            LinearLayout wordcardL = (LinearLayout) view.getParent().getParent().getParent().getParent().getParent();
            LinearLayout wordcardLL = (LinearLayout) view.getParent().getParent().getParent();
            TextView englishWord = wordcardLL.findViewById(R.id.list_english_word);
            String englishWordText = englishWord.getText().toString();

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("단어 삭제");
            alert.setMessage("정말 삭제 하시겠습니까?");

            alert.setPositiveButton("YES", (dialog, which) -> {
                Log.i(TAG, "YES");
                wordcardL.setVisibility(View.GONE);
                wordBooksDoc.get().addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, Object> wordList_find = (Map<String, Object>) document.getData().get("wordlist");
                        try {
                            Map<String, Object> newWordcardArray = new HashMap<>();
                            for (int i = 0; i < wordList_find.size(); i++) {
                                Map<String, Object> map_find = (HashMap) wordList_find.get(String.valueOf(i));
                                Map<String, Object> newWordCard = new HashMap<>();
                                String word_english = map_find.get("word").toString();
                                //int word_memorization = Integer.parseInt(map_find.get("memorization").toString());
                                newWordCard.put("word", word_english);
                                newWordCard.put("mean1", map_find.get("mean1"));
                                if (map_find.get("mean2") != "") {
                                    newWordCard.put("mean2", map_find.get("mean2"));
                                } else {
                                    newWordCard.put("mean2", "");
                                }
                                if (map_find.get("mean3") != "") {
                                    newWordCard.put("mean3", map_find.get("mean3"));
                                } else {
                                    newWordCard.put("mean3", "");
                                }
                                newWordCard.put("memorization", map_find.get("memorization"));
                                if (englishWordText.equals(word_english)) {
                                    dataDelete = true;
                                } else {
                                    int wordListIDNumber = dataDelete ? i - 1 : i;
                                    newWordcardArray.put(String.valueOf(wordListIDNumber), newWordCard);
                                }
                            }
                            if (dataDelete) {
                                wordBooksDoc.update("wordlist", newWordcardArray);
                                Log.i(TAG, "data delete complete");
                                //updateWordcard(thisWordbookMemorizationType);
                                Toast.makeText(view.getContext(), R.string.toast_delete_word, Toast.LENGTH_SHORT).show();
                                dataDelete = false;
                                coundWord = coundWord-1;
                                if(coundWord==0){
                                    TextView text = findViewById(R.id.recommend_word_add);
                                    text.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.i(TAG, "get failed with " + task.getException());
                    }
                });
            });

            alert.setNegativeButton("NO", (arg0, arg1) -> Log.i(TAG, "NO"));
            alert.show();
        }
    }
    //단어 암기<->미암기 체크
    public void wordMemoryBtnClick(View view) {
        if(thisWordbookHideType>0){
            Toast.makeText(view.getContext(), "단어/뜻 숨김 처리 시에는 암기/미암기 처리가 불가능합니다.",Toast.LENGTH_SHORT).show();
        }else {
            LinearLayout wordcardLL = (LinearLayout) view.getParent().getParent().getParent();
            TextView englishWord = wordcardLL.findViewById(R.id.list_english_word);
            String englishWordText = englishWord.getText().toString();
            TextView wordMean1 = wordcardLL.findViewById(R.id.list_word_mean1);
            TextView wordMean2 = wordcardLL.findViewById(R.id.list_word_mean2);
            TextView wordMean3 = wordcardLL.findViewById(R.id.list_word_mean3);
            ImageView memorizeBtn = (ImageView) view;

            wordBooksDoc.get().addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> wordList_find = (Map<String, Object>) document.getData().get("wordlist");
                    try {
                        Map<String, Object> newWordcardArray = new HashMap<>();
                        for (int i = 0; i < wordList_find.size(); i++) {
                            HashMap map_find = (HashMap) wordList_find.get(String.valueOf(i));
                            Map<String, Object> newWordCard = new HashMap<>();
                            String word_english = map_find.get("word").toString();
                            int word_memorization = Integer.parseInt(map_find.get("memorization").toString());
                            newWordCard.put("word", word_english);
                            newWordCard.put("mean1", map_find.get("mean1"));

                            if (map_find.get("mean2") != "") {
                                newWordCard.put("mean2", map_find.get("mean2"));
//                            } else {
//                                newWordCard.put("mean2", "");
                            }
                            if (map_find.get("mean3") != "") {
                                newWordCard.put("mean3", map_find.get("mean3"));
//                            } else {
//                                newWordCard.put("mean3", "");
                            }
                            if (englishWordText.equals(word_english)) {
                                dataChange = true;
                                if (word_memorization == 0) {
                                    //memorization 1로 전환 후
                                    newWordCard.put("memorization", 1);
                                    memorizeBtn.setImageResource(R.drawable.memorization_check);
                                    englishWord.setTextColor(Color.LTGRAY);
                                    wordMean1.setTextColor(Color.LTGRAY);
                                    wordMean2.setTextColor(Color.LTGRAY);
                                    wordMean3.setTextColor(Color.LTGRAY);
                                    Toast.makeText(this.getApplicationContext(), R.string.toast_change_to_memorization, Toast.LENGTH_SHORT).show();
                                } else {
                                    newWordCard.put("memorization", 0);
                                    memorizeBtn.setImageResource(R.drawable.memorization_uncheck);
                                    englishWord.setTextColor(Color.BLACK);
                                    wordMean1.setTextColor(Color.BLACK);
                                    wordMean2.setTextColor(Color.BLACK);
                                    wordMean3.setTextColor(Color.BLACK);
                                    Toast.makeText(this.getApplicationContext(), R.string.toast_change_to_notmemorization, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                newWordCard.put("memorization", map_find.get("memorization"));
                            }
                            newWordcardArray.put(String.valueOf(i), newWordCard);
                        }
                        wordBooksDoc.update("wordlist", newWordcardArray);
                        if (dataChange) {
                            Log.i("mytag", "data chage ");
                            //updateWordcard(thisWordbookMemorizationType);
                            dataChange = false;
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i("mytag", "get failed with " + task.getException());
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v == logout) {
            FirebaseAuth.getInstance().signOut();
            checkCurrentUser();
        }else if (v == wordQuiz) {
            Intent intent = new Intent(getApplicationContext(), QuizDialog.class).putExtra("id",wordbookID);
            startActivity(intent);
        }else if (v == search) {
            Intent intent = new Intent(getApplicationContext(), DicSearch.class);
            startActivity(intent);
        }else if (v == mypage) {
            Intent intent = new Intent(getApplicationContext(), Mypage.class);
            startActivity(intent);
        }else if (v == mWordSorting) {
            mWordSortingSelectDialog.show();
        }else if (v == wordHideBtn) {
            Log.i(TAG,"단어숨김 선택");
            thisWordbookHideType=1;
            updateWordcard(thisWordbookMemorizationType);
            Toast myToast = Toast.makeText(this,R.string.toast_hide_word ,Toast.LENGTH_SHORT);
            myToast.show();
        }else if (v == meanHideBtn) {
            Log.i(TAG,"단어숨김 선택");
            thisWordbookHideType=2;
            updateWordcard(thisWordbookMemorizationType);
            Toast myToast = Toast.makeText(this,R.string.toast_hide_mean ,Toast.LENGTH_SHORT);
            myToast.show();
        }
    }
    public void checkCurrentUser() {
        // [START check_current_user]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
        } else {
            updateUI(user);
            // No user is signed in
        }
        // [END check_current_user]
    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // User is signed in
        } else {
            // No user is signed in
            // 로그아웃 실행
            Intent main = new Intent(this, Index.class);
            startActivity(main);
            Toast.makeText(this.getApplicationContext(), "로그아웃되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}