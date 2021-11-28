package com.sausage.voca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) { //public으로 바꿈. 꼭 이런 방식이어야 할까?
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        new_name = findViewById(R.id.signup_name);
        new_email = findViewById(R.id.signup_email);
        new_password = findViewById(R.id.signup_password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup_button);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    @Override
    public void onClick(View v) {
        if (v==signup){
            createAccount(new_name.getText().toString(), new_email.getText().toString(), new_password.getText().toString());
        }
        else if ((v==login)){
            Intent Login = new Intent(this, Login.class);
            startActivity(Login);
            finish();
        }
    }

    private void createAccount(String name, String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Log.i("mytag","여기까지는 성공");


                            // Create a new user with a first and last name
                            Map<String, Object> thisuser = new HashMap<>();
                            thisuser.put("name", name);
                            thisuser.put("email", email);

                            //[은소] asset 폴더에 있는 json 파일을 json object로 만들어서 thisuser에 put하려는 나의 노력들..
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

                            // Add a new document with a generated ID
                            db.collection("users")
                                    .add(thisuser)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Signup.this, "다시 시도해주세요",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

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