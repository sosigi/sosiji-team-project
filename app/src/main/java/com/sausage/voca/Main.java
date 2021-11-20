package com.sausage.voca;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressWarnings("deprecation")
public class Main extends AppCompatActivity implements View.OnClickListener {

    private TextView logout, dicsearch, category, setting;
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
        else if (v==setting){}

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

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }
        // [END get_user_profile]
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
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

}