package com.sausage.voca;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;



public class CategoryFragment extends ListFragment {


    TextView wordbook1, wordbook2, wordbook3;
    LinearLayout linearLayout;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ListView listView;
    TextView edit;
    ArrayList<String> titles = new ArrayList<>();
    ArrayAdapter<String> adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //firestore에서 wordbooktitle끌어오기.
        //컬렉션>문서>필드

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_category, null);
        listView = v.findViewById(android.R.id.list);
        edit = v.findViewById(R.id.category_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("mytag", "edit 클릭됨");
                Intent intent = new Intent(getActivity(), Category.class);
                startActivity(intent);
            }
        });

        adapter = new ArrayAdapter<String>(listView.getContext(), android.R.layout.simple_list_item_1, titles){
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(18);
                return view;
            }
        };


        getTitles(); //db에서 title 정보 땡겨오는 함수
        
        v.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
            }
        });


        //이게 화면 띄워주는 필수 기능 두 가지인데, 각자가 어떤 역할을 하는지는 잘 모른다.
        listView.setAdapter(adapter);
        listView.getLastVisiblePosition();
        return v;
    }

    //TODO 삭제시 발생할 문제 고려
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String strText = (String) l.getItemAtPosition(position);
        Log.d("Fragment: ", position + ": " + strText);
        Toast.makeText(this.getContext(), "클릭: " + position + " " + strText, Toast.LENGTH_SHORT).show();
        String ID = String.valueOf(position);
        Intent intent = new Intent(getActivity(), wordbook.class).putExtra("id",ID);
        startActivity(intent);
    }

    private void getTitles(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference wordbooksRef = db
                    .collection("users")
                    .document(user.getUid())
                    .collection("wordbooks");

            Log.i("mytag", "wordbooksRef 주소 : " + wordbooksRef.toString()); //일단 wordbook ref까지는 접근 완료.
            Log.i("mytag", "wordbooksRef 경로 : " + wordbooksRef.getPath());

            //이제 wordbook 안에 있는 두 문서(0,1)에 접근하고, 그 각각의 문서에서 wordbooktitle 필드를 빼내와야 한다
            wordbooksRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.i("mytag", "words : " + document.getData().get("wordbooktitle"));
                            String wordbooktitle = document.getData().get("wordbooktitle").toString();
                            titles.add(wordbooktitle);
                            adapter.notifyDataSetChanged(); //데이터 갱신됐다는 알림 전달 -> adapter가 화면에 띄워줌
                        }
                    } else {
                        Log.d("mytag", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }
}