package com.sausage.voca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class wordAdd extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private Button button;
    private TextView json_test_text;

    //[은소]test - edit text에 입력된 내용을 realtime database에 업로드해봄.
    //데이터베이스에서 데이터를 읽고 쓰려면 DataReference의 인스턴스가 필요하다.
//    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    //child()는 데이터가 있을 위치의 이름을 정해준다.
//    DatabaseReference conditionRef = mRootRef.child("test");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_add);

        textView = (TextView) findViewById(R.id.test_textview);
        editText = (EditText) findViewById(R.id.test_edit_text);

    }

}