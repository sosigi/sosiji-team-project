package com.sausage.voca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressWarnings("deprecation") //이건 왜 넣어둔건지 본인[시하]도 모르겠습니다.
public class Main extends AppCompatActivity implements View.OnClickListener {

    private TextView logout, dicsearch, category, setting;
    private long first_time, second_time;
    private final int Fragment_1 = 1, Fragment_2 = 2;
    FragmentManager fragmentManager = getSupportFragmentManager(); //fragment 관리
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); //fragment 관련 작업 수행

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
        if (v==logout){
            FirebaseAuth.getInstance().signOut();
            checkCurrentUser();
        }
        else if (v==dicsearch){
            FragmentView(Fragment_1);
        }
        else if (v==category){
            FragmentView(Fragment_2);
        }
        else if (v==setting){
            //내 정보로 intent.
            //TODO 은소가 위에서 따로 onClick 만들었길래 여기로 이전했어욤
            Intent intent = new Intent(getApplicationContext(), Mypage.class);
            startActivity(intent);
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

    private void FragmentView(int fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (fragment){
            case 1:
                category.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                dicsearch.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                transaction.replace(R.id.contents, new DictionaryFragment());
                transaction.commit();
                break;
            case 2:
                dicsearch.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                category.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                Fragment fragment2 = new Fragment();
                transaction.replace(R.id.contents, new CategoryFragment());
                transaction.commit();
                break;
        }

    }

    //TODO [시하] 이건 안 쓰는 것 같은데?
    public void goToWordBook(View view){
        Intent intent = new Intent(getApplicationContext(), wordbook.class);
        startActivity(intent);
    }

    //두 번 누르면 앱 종료
    @Override
    public void onBackPressed() {
        second_time = System.currentTimeMillis();
        Toast.makeText(this.getApplicationContext(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        if(second_time - first_time < 2000){
            super.onBackPressed();
            finishAffinity();
        }
        first_time = System.currentTimeMillis();
    }

}