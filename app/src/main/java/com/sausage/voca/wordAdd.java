package com.sausage.voca;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class wordAdd extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private Button button;

    //[은소]test - edit text에 입력된 내용을 realtime database에 업로드해봄.
    //데이터베이스에서 데이터를 읽고 쓰려면 DataReference의 인스턴스가 필요하다.
    //DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    //child()는 데이터가 있을 위치의 이름을 정해준다.
    //DatabaseReference conditionRef = mRootRef.child("test");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_add);

        textView = (TextView) findViewById(R.id.test_textview);
        editText = (EditText) findViewById(R.id.test_edit_text);
//        button = (Button) findViewById(R.id.test_send_btn);

    }

    //[은소]test - edit text에 입력된 내용을 realtime database에 업로드해보는 test용.
    //데이터의 변화를 알기 위한 onStart()메소드 사용
//    @Override
//    protected void onStart() {
//        super.onStart();
//        //child 로 설정해준 경로안의 데이터가 변화됐을 때 작동.
//        conditionRef.addValueEventListener(new ValueEventListener() {
//
//            //데이터의 값이 변했을때마다 작동
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                //text에 받아온 데이터 문자열을 넣어준다.
//                String text = dataSnapshot.getValue(String.class);
//                //이전에 선언해둔 textView의 텍스트를 이 문자열로 설정한다.
//                textView.setText(text);
//            }
//            //에러가 날 때 작동
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                conditionRef.setValue(editText.getText().toString());
//            }
//        });
//    }
}