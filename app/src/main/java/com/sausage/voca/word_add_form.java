package com.sausage.voca;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class word_add_form {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String wordbookID = "0";
    CollectionReference wordbooksRef = db.collection("users").document(user.getUid()).collection("wordbooks");
    DocumentReference docRef = wordbooksRef.document(wordbookID);
    int result = 999;

    public void add(String word, String meaning) {
        Map<String, String> newWord = new HashMap<>();
        newWord.put("word", word);
        newWord.put("mean1", meaning);

        numberOfWords();
        Map<String, Object> numbering = new HashMap<>();
        numbering.put(String.valueOf(result), newWord);
        Log.i("mytag", "numbering : "+numbering + "\nresult는 :" + result);

        Map<String, Object> addThis = new HashMap<>();
        addThis.put("wordlist", numbering);
        Log.i("mytag", "addThis : "+addThis);

        docRef.update("wordlist", FieldValue.arrayUnion(newWord));
    }

    private void numberOfWords() {

        //docRef.get().getResult().getData().get("wordlist");

        wordbooksRef.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.i("mytag", "wordlist 쭉 돌면서 몇개인지 확인하기 : " + document.getData().get("wordlist"));
                    result++;
                }
            } else {
                Log.d("mytag", "Error getting documents: ", task.getException());
            }
        });
    }

    public void changeID(int id){
        wordbookID = String.valueOf(id);
    }
}
