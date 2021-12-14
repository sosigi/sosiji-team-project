package com.sausage.voca;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class Category extends AppCompatActivity {

    public static final int REQUEST_CODE = 100;

    private ArrayList<CategoryTitle> titlesDataList = new ArrayList<>();
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    CategoryTitleAdapter categoryTitleAdapter;

    //database
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    int titleCountUnchanged = 0;
    String init = "";

    String TAG = "mytag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태 바 없애기


        //wordbooktitle 정렬
        this.InitializeData("edit");

        ImageButton back_btn = findViewById(R.id.category_back);
        ImageButton plus_btn = findViewById(R.id.category_plus);

        //back btn 클릭시
        back_btn.setOnClickListener(view -> finish());

        //plus btn 클릭시 categoryAdd page 열기.
        plus_btn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CategoryAdd.class);
            startActivityForResult(intent, REQUEST_CODE);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Log.i("mytag", "ActivityResult 실행됨");
        if (requestCode == REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            init = intent.getStringExtra("init");
            Log.i("mytag", "init : " + init);
            InitializeData(init);
        }
    }

    public void InitializeData(String type) {
        titlesDataList = new ArrayList<>();
        db.collection("users").document(user.getUid()).collection("wordbooks")
                .get().addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                if (type.equals("delete")) {
                    titleCountUnchanged = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        titleCountUnchanged = titleCountUnchanged + 1;
                        //Log.i("mytag", document.getId() + " => " + Objects.requireNonNull(document.getData().get("wordbooktitle")).toString());
                        String title = Objects.requireNonNull(document.getData().get("wordbooktitle")).toString();
                        titlesDataList.add(new CategoryTitle(title));
                    }
                } else if (type.equals("edit")) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.i("mytag", document.getId() + " => " + Objects.requireNonNull(document.getData().get("wordbooktitle")).toString());
                        String title = Objects.requireNonNull(document.getData().get("wordbooktitle")).toString();
                        titlesDataList.add(new CategoryTitle(title));
                    }
                }
                recyclerView = findViewById(R.id.recycler_category_title_list);
                manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(manager); // LayoutManager 등록
                categoryTitleAdapter = new CategoryTitleAdapter(titlesDataList);
                recyclerView.setAdapter(categoryTitleAdapter);  // Adapter 등록
            } else {
                Log.i("mytag", "get failed with " + task.getException());
            }
        }));
    }

    public void editBtn(View view) {
        LinearLayout categoryLayout = (LinearLayout) view.getParent();
        TextView titleTextView = categoryLayout.findViewById(R.id.list_category_title_text);
        String title = titleTextView.getText().toString();
        Intent intent = new Intent(view.getContext(), CategoryRevise.class).putExtra("titleID", title);
        startActivityForResult(intent, REQUEST_CODE);
    }
}