package com.sausage.voca;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class CategoryFragment extends ListFragment {

    ListView listView;
    List<String> titles = new ArrayList<String>();

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
                        }
                    } else {
                        Log.d("mytag", "Error getting documents: ", task.getException());
                    }
                }
            });
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, titles);
        setListAdapter(adapter);


        /*
        wordbook1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                Intent intent = new Intent(getActivity(), wordbook.class); //TODO v.getContext()를 하나 getActivity()를 하나 둘 다 멀정하게 나옴. 뭐가 다를까?
                startActivityForResult(intent, 1234);
                //Main activity에 fragment가 올라와 있었기 때문에,
                // wordbook에서 정보 받아오려면 여기서 정보가 거쳐가야 한다.
            }
        });

         */


        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        String strText = (String) l.getItemAtPosition(position);
        Log.d("Fragment: ", position + ": " +strText);
        Toast.makeText(this.getContext(), "클릭: " + position +" " + strText, Toast.LENGTH_SHORT).show();
    }
}