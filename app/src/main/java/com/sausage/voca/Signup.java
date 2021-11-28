package com.sausage.voca;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

//이메일로 회원가입 하기
public class Signup extends AppCompatActivity implements View.OnClickListener{
    private TextView new_name;
    private TextView new_email;
    private TextView new_password;
    private TextView login;
    private Button signup;
    private Button google;
    private static final String EmailTAG = "EmailPassword";
    //private static final String GoogleTAG = "GoogleActivity";

    private FirebaseAuth mAuth;
    //private GoogleSignInClient mGoogleSignInClient;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) { //public으로 바꿈. 꼭 이런 방식이어야 할까?
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        new_name = findViewById(R.id.signup_name); //이메일이냐, 구글이냐에 따라 다름
        new_email = findViewById(R.id.signup_email);
        new_password = findViewById(R.id.signup_password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup_button);
        //google = findViewById(R.id.google_btn);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        //google.setOnClickListener(this);

        /* 구글은 보류
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signin]
         */

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    @Override
    public void onClick(View v) {
        if (v==signup){ //회원가입
            //이메일로 회원가입하는 경우 new_name 칸에 입력한 이름 가져오면 된다.
            createEmailAccount(new_email.getText().toString(), new_password.getText().toString());
        }
        else if (v==login){ //로그인
            Intent Login = new Intent(this, Login.class);
            startActivity(Login);
            finish();
        }else if(v==google){ //구글 회원가입
            //구글로 회원가입하는 경우 auth 가져오는 과정에서 사용자 닉네임을 가져와야 할 듯.
            // createGoogleAccount(); 구글은 보류
        }
    }

    //이메일 계정 회원 생성//
    private void createEmailAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password) //얘는 여기서 유효성 검사 하나봄
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(EmailTAG, "createUserWithEmail:success");

                            //[시하] firestore에 user data 저장하는 함수 따로 팠어요~
                            storeUserdata(new_name.getText().toString(), email, "email");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(EmailTAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Signup.this, "이미 존재하는 계정입니다.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    /*
    //구글 계정 회원 생성//
    // [START signin]
    private void createGoogleAccount() {
        //이대로 firebase에는 나와있으면서 제대로 안됨
        //Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //startActivityForResult(signInIntent, RC_SIGN_IN);

        resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
    }
    // [END signin]


    //https://www.youtube.com/watch?v=gCrVwjh4LiY 참고
   ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
       @Override
       public void onActivityResult(ActivityResult result) {
           if (result.getResultCode() == Activity.RESULT_OK){
               Intent intent = result.getData();

               Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
               try {
                   // Google Sign In was successful, authenticate with Firebase
                   GoogleSignInAccount account = task.getResult(ApiException.class);
                   Log.d(GoogleTAG, "firebaseAuthWithGoogle:" + account.getId());
                   firebaseAuthWithGoogle(account.getIdToken());
               } catch (ApiException e) {
                   // Google Sign In failed, update UI appropriately
                   Log.w(GoogleTAG, "Google sign in failed", e);
               }
           }
       }
   });

    // 이것도 문제있음. 위의 코드로 수정해야 함
    //[START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(GoogleTAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(GoogleTAG, "Google sign in failed", e);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(GoogleTAG, "signInWithCredential:success");
                            //[시하] firestore에 user data 저장하는 함수 따로 팠어요~
                            storeUserdata(mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail(), "google");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(GoogleTAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_google]

     */

    private void storeUserdata(String name, String email, String from) {
        FirebaseUser user = mAuth.getCurrentUser();
        //Log.i("mytag","여기까지는 성공");

        // Create a new user with a first and last name
        Map<String, Object> thisuser = new HashMap<>();
        thisuser.put("name", name);
        thisuser.put("email", email);

        /*[은소] asset 폴더에 있는 json 파일을 json object로 만들어서 thisuser에 put하려는 나의 노력들..
        //문제 : 현재 JSONExeption발생.
        //TODO : JSON파일을 thisuser 에 put하면된다..젭알..
        AssetManager assetManager = getResources().getAssets();
        try{
            InputStream inputStream = assetManager.open("word_test.json");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String jsonData = reader.toString();
            JSONObject wordbook_json = new JSONObject(jsonData);
            thisuser.put("wordBooks",wordbook_json);
            Log.i("mytag","데이터 이동 성공");
        }catch (IOException e){
            Log.i("mytag","IOException error catch");
            e.printStackTrace();
        }catch(JSONException e){
            Log.i("mytag","json exeption error catch");
            e.printStackTrace();
        }

         */

        // Add a new document with a generated ID
        db.collection("users")
                .add(thisuser)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        if (from == "email") Log.d(EmailTAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        //else if (from == "google") Log.d(GoogleTAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (from == "email") Log.w(EmailTAG, "Error adding document", e);
                        //else if (from == "google") Log.w(GoogleTAG, "Error adding document", e);
                    }
                });
        updateUI(user);
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    //구글과 이메일 둘 다 해당되는 내용
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.i("mytag","로그인으로 바로 이동");
            Intent main = new Intent(this, Main.class);
            startActivity(main);
            Toast.makeText(this.getApplicationContext(), "로그인되었습니다", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}