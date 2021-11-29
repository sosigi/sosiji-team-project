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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SnapshotMetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CategoryFragment extends Fragment {

    TextView wordbook1, wordbook2, wordbook3;
    LinearLayout linearLayout;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        //TODO : fragment에 wordbooktitle 연속 출력
        //textView들의 부모로 들어갈 linearlayout
        linearLayout = v.findViewById(R.id.fragment_category_list_xml);
        if (user != null) {
            db.collection("users").document(user.getUid()).collection("wordbooks")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //여기 logcat 출력해보면 문서 id인 document.getId()에 따라서 data들이 출력되는 걸 알수있어.
                                    Log.i("mytag", document.getId() + " => " + document.getData());
                                    //TODO : 여기서 titleText를 생성해서 setText하고 linearLayout에 추가해주면 될것같아.
                                    /*
                                    TextView titleText = new TextView(this);
                                    titleText.setText(document.get("wordbooktitle").toString());
                                    linearLayout.addView(titleText);
                                    */
                                }
                            } else {
                                Log.i("mytag", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        } else {
            Log.i("mytag", "user is null");
        }

        return v;
    }


}