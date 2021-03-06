package com.sausage.voca;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;


public class CategoryFragment extends ListFragment {

    String TAG = "mytag";

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ListView listView;
    TextView edit, loading;
    ArrayList<String> titles = new ArrayList<>();
    ArrayAdapter<String> adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_category, null);
        listView = v.findViewById(android.R.id.list);
        loading = v.findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        edit = v.findViewById(R.id.category_edit);
        edit.setOnClickListener(v1 -> {
            Log.i("mytag", "edit 클릭됨");
            Intent intent = new Intent(getActivity(), Category.class);
            startActivity(intent);
        });

        adapter = new ArrayAdapter<String>(listView.getContext(), android.R.layout.simple_list_item_1, titles) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(18);
//                tv.setSelected(true);
                return view;
            }
        };

        final CollectionReference docRef = db.collection("users").document(user.getUid()).collection("wordbooks");
        docRef.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
            } else {
                Log.i(TAG, "[categoryFragment] data update - 카테고리 종류" + value);
                getTitles();
            }
        });

        listView.setAdapter(adapter);
        listView.getLastVisiblePosition();

        return v;
    }


    //TODO 삭제시 발생할 문제 고려
    @Override
    public void onListItemClick(ListView l, @NonNull View v, int position, long id) {
        String strText = (String) l.getItemAtPosition(position);
        String idPosition = String.valueOf(position);
        Intent intent = new Intent(getActivity(), wordbook.class);
        intent.putExtra("title", strText);
        intent.putExtra("id", idPosition);
        startActivity(intent);
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
            wordbooksRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    titles.clear(); //[시하] TODO 굳이..이렇게 해야하나?? 굳이?
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.i("mytag", "words : " + document.getData().get("wordbooktitle"));
                        String wordbooktitle = Objects.requireNonNull(document.getData().get("wordbooktitle")).toString();
                        titles.add(wordbooktitle);
                        adapter.notifyDataSetChanged(); //데이터 갱신됐다는 알림 전달 -> adapter가 화면에 띄워줌
                    }
                } else {
                    Log.d("mytag", "Error getting documents: ", task.getException());
                }
                loading.setVisibility(View.INVISIBLE);
            });
        }
    }
}