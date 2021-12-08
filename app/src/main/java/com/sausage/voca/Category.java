package com.sausage.voca;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class Category extends AppCompatActivity {

    private ArrayList<CategoryTitle> titlesDataList = new ArrayList<>();
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    CategoryTitleAdapter categoryTitleAdapter;

    //database
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String TAG = "mytag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태 바 없애기


        //wordbooktitle 정렬
        this.InitializeData();

        ImageButton back_btn = findViewById(R.id.category_back);
        ImageButton plus_btn = findViewById(R.id.category_plus);

        //back btn 클릭시
        back_btn.setOnClickListener(view -> finish());

        //plus btn 클릭시 categoryAdd page 열기.
        plus_btn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CategoryAdd.class);
            startActivity(intent);
        });

        final CollectionReference colRef = db.collection("users").document(user.getUid()).collection("wordbooks");
        colRef.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
            } else {
                //Log.i(TAG, "카테고리 종류 : " + value);
                InitializeData();
            }
        });

    }

    public void InitializeData() {
        titlesDataList = new ArrayList<>();

        CollectionReference wordbooksCol = db.collection("users").document(user.getUid()).collection("wordbooks");
        wordbooksCol
                .get().addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    //Log.i("mytag", document.getId() + " => " + Objects.requireNonNull(document.getData().get("wordbooktitle")).toString());
                    String title= Objects.requireNonNull(document.getData().get("wordbooktitle")).toString();
                    titlesDataList.add(new CategoryTitle(title));
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

    public void editBtn(View view){
        LinearLayout categoryLayout = (LinearLayout) view.getParent();
        TextView titleTextView = categoryLayout.findViewById(R.id.list_category_title_text);
        String title = titleTextView.getText().toString();
        Intent intent = new Intent(view.getContext(), CategoryRevise.class).putExtra("titleID",title);
        startActivity(intent);
    }

}