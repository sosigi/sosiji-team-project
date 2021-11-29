package com.sausage.voca;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SnapshotMetadata;

import java.util.ArrayList;


public class CategoryFragment extends Fragment {

    TextView wordbook1, wordbook2, wordbook3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_category, null);
        wordbook1 = v.findViewById(R.id.wordbook1);
        wordbook2 = v.findViewById(R.id.wordbook2);
        wordbook3 = v.findViewById(R.id.wordbook3);

        wordbook1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                Intent intent = new Intent(getActivity(), wordbook.class);
                //TODO v.getContext()를 하나 getActivity()를 하나 둘 다 멀정하게 나옴. 뭐가 다를까?
                startActivityForResult(intent, 1234);
                //Main activity에 fragment가 올라와 있었기 때문에,
                // wordbook에서 정보 받아오려면 여기서 정보가 거쳐가야 한다.
            }
        });


        //firestore에서 wordbooktitle끌어오기.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            CollectionReference wordbooksRef = db.collection("users").document(user.getUid()).collection("wordbooks");
//            Log.i("mytag",wordbooksRef.toString());
//            for(int i =0; wordbooksRef.document(Integer.toString(i)) != null; i++) {
//                Log.i("mytag",wordbooksRef.document(Integer.toString(i)).toString());
            wordbooksRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.i("mytag", task.getResult().toString());
//                            for(int i=0;task.getResult())
//                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()) {
//                                String wordbooktitle = document.getDate("wordbooktitle").toString();
//                                Log.i("mytag",wordbooktitle);
//                            } else {
//                                Log.i("mytag", "No such document");
//                            }
                    } else {
                        Log.i("mytag", "get failed with ", task.getException());
                    }
                }
            });

//            }
        }

        return v;
    }


}