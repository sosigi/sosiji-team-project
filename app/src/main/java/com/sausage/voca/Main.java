package com.sausage.voca;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressWarnings("deprecation") //이건 왜 넣어둔건지 본인도 모르겠습니다.

public class Main extends AppCompatActivity implements View.OnClickListener {

    private TextView logout, dicsearch, category, setting, categoryedit;
    private final int Fragment_1 = 1, Fragment_2 = 2;
    private long first_time, second_time;
    FragmentManager fragmentManager = getSupportFragmentManager(); //fragment 관리
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); //fragment 관련 작업 수행
    private String nowImHere = "empty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentTransaction.add(R.id.contents, new DictionaryFragment()); //contents는 activity_main.xml에서 fragment 뜨게 할 부분의 id입력. 뒤에는 fragment입력
        fragmentTransaction.commit();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태 바 없애기

        logout = findViewById(R.id.logout_txt);
        dicsearch = findViewById(R.id.dicSearch);
        category = findViewById(R.id.category);
        setting = findViewById(R.id.setting);

        logout.setOnClickListener(this);
        dicsearch.setOnClickListener(this);
        category.setOnClickListener(this);
        setting.setOnClickListener(this);

        //내정보 선택-> mypage로 전환
        /* TODO [시하] 수정함
        TextView plusBtnButton = (TextView) findViewById(R.id.setting);
        plusBtnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
         */
    }

    @Override
    public void onClick(View v) {
        if (v == logout) {
            FirebaseAuth.getInstance().signOut();
            checkCurrentUser();

        } else if (v == dicsearch) {
            if (!nowImHere.equals("dicsearch")) {
                FragmentView(Fragment_1);
                nowImHere = "dicsearch";
            }

        } else if (v == category) {
            if (!nowImHere.equals("category")) {
                FragmentView(Fragment_2);
                nowImHere = "category";
            }

        } else if (v == setting) {
            Intent intent = new Intent(Main.this, Mypage.class);
            startActivityForResult(intent, 1234);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("mytag", "ActivityResult 실행됨");
        if (resultCode == RESULT_OK) {
            String Data = data.getStringExtra("inform");
            Log.i("mytag", "받은 Data는 " + Data);
            if (Data.equals("search")) { //Data == "search"라고 해서 해맸다...
                Log.i("mytag", Data + " 실행됨");
                dicsearch.callOnClick(); //TODO onClick(dicsearch) 中 뭐가 나을까?
            } else if (Data.equals("category")) {
                Log.i("mytag", Data + " 실행됨");
                category.callOnClick();
            } else if (Data.equals("mypage")) {
                Log.i("mytag", Data + " 실행됨");
                setting.callOnClick();
            }
        }
    }

    public void checkCurrentUser() {
        // [START check_current_user]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
        } else {
            updateUI(user);
            // No user is signed in
        }
        // [END check_current_user]
    }

    //사용자가 성공적으로 로그인되면 getCurrentUser 메서드를 사용하여 언제든지 사용자의 계정 데이터를 가져올 수 있습니다.
    public void getUserProfile() {
        // [START get_user_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();


            /* Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

             */

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }
        // [END get_user_profile]
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // User is signed in
        } else {
            // No user is signed in
            // 로그아웃 실행
            Intent main = new Intent(this, Index.class);
            startActivity(main);
            Toast.makeText(this.getApplicationContext(), "로그아웃되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void FragmentView(int fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (fragment) {
            case 1:
                category.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                dicsearch.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                transaction.replace(R.id.contents, new DictionaryFragment());
                transaction.commit();
                break;
            case 2:
                dicsearch.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                category.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                //Fragment fragment2 = new Fragment();
                transaction.replace(R.id.contents, new CategoryFragment());
                transaction.commit();
                break;
        }

    }

    //두 번 누르면 앱 종료
    @Override
    public void onBackPressed() {
        second_time = System.currentTimeMillis();
        Toast.makeText(this.getApplicationContext(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        if (second_time - first_time < 2000) {
            super.onBackPressed();
            finishAffinity();
        }
        first_time = System.currentTimeMillis();
    }

}