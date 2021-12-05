package com.sausage.voca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sausage.voca.ui.word_add_form;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class wordAdd extends AppCompatActivity {

    private TextView textView;
    private EditText editText1, editText2;
    private ImageButton word_add_back;
    private TextView word_add_add;
    private TextView json_test_text;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String wordbookID = "0";

    //[은소]test - edit text에 입력된 내용을 realtime database에 업로드해봄.
    //데이터베이스에서 데이터를 읽고 쓰려면 DataReference의 인스턴스가 필요하다.
//    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    //child()는 데이터가 있을 위치의 이름을 정해준다.
//    DatabaseReference conditionRef = mRootRef.child("test");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_add);

        String[] data = getIntent().getStringArrayExtra("data");

        textView = findViewById(R.id.test_textview);
        editText1 = findViewById(R.id.english);
        editText2 = findViewById(R.id.korean);

        if (data != null) {
            editText1.setText(data[0]);
            editText2.setText(data[1]);
        }

        word_add_back = findViewById(R.id.word_add_back);
        word_add_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        word_add_add = findViewById(R.id.wordAddCompelete);
        word_add_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> newWord = new HashMap<String, String>();
                newWord.put("word", data[0]);
                newWord.put("mean1", data[1]);

                db.collection("users").document(user.getUid()).collection("wordbooks").document(wordbookID)
                        .update("wordlist", FieldValue.arrayUnion(newWord));
                //TODO 2안 해보고 안되면 3안으로 ㄱㄱ. 3안은 CategoryAdd.java 참고
                finish();
            }
        });

    }

}