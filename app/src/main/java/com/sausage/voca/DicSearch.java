package com.sausage.voca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class DicSearch extends AppCompatActivity {

    private EditText searching_word; //이게 회색이면 안 쓰인거니까 뭐가 문제인지 눈여겨볼것...
    private TextView searched, meaning;
    private String letsSearch, searchResult;
    ImageButton dicSearch_add, dicSearch_back, search_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dic_search);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태 바 없애기

        searching_word = findViewById(R.id.search_result);
        searched = findViewById(R.id.result_searched);
        meaning = findViewById(R.id.result_meaning);
        dicSearch_add = findViewById(R.id.dicSearch_add);
        dicSearch_back = findViewById(R.id.dicSearch_back);


        invisible();

        //TODO 여기서 뭔 문제가 있는지, 자꾸 검색결과 화면이 두 개가 겹친다
        CharSequence search = getIntent().getCharSequenceExtra("search");
        if (search!=null) {
            searching_word.setText(search);
            RunThread();
            visible();
        }

        searching_word.setOnKeyListener((v, keyCode, event) ->  {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    RunThread();
                    visible();

                    return true;
                }
                return false;
        });

        search_btn = findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunThread();
                visible();
            }
        });


        dicSearch_back.setOnClickListener(view -> finish());
        dicSearch_add.setOnClickListener(v -> {
            String[] data = {letsSearch, searchResult};
            Intent intent = new Intent(getApplicationContext(), DicSearchWordAdd.class).putExtra("data", data);
            startActivity(intent);
        });

    }

    public void RunThread() { //TODO:스레드 공부하자...
        Thread thread = new Thread(() -> {
            try {
                letsSearch = searching_word.getText().toString();
                searchResult = papagoTranslate(letsSearch);

                runOnUiThread(() -> {
                    searched.setText(letsSearch);
                    meaning.setText(searchResult);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void visible(){
        searched.setVisibility(View.VISIBLE);
        meaning.setVisibility(View.VISIBLE);
        dicSearch_add.setVisibility(View.VISIBLE);
    }

    public void invisible(){
        searched.setVisibility(View.INVISIBLE);
        meaning.setVisibility(View.INVISIBLE);
        dicSearch_add.setVisibility(View.INVISIBLE);
    }

    private static String papagoTranslate(String args) {
        String clientId = "9nlapeOCtCRa3WR16aMM";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "ddORBhc9I9";//애플리케이션 클라이언트 시크릿값";

        String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
        String text;
        try {
            text = URLEncoder.encode(args, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("인코딩 실패", e);
        }

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);

        String responseBody = post(apiURL, requestHeaders, text);

        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            String message = jsonObject.getString("message");
            JSONObject subJsonObjects = new JSONObject(message);
            String result = subJsonObjects.getString("result");
            JSONObject subJsonObjects2 = new JSONObject(result);
            result = subJsonObjects2.getString("translatedText");
            return result; //파싱 결과
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return responseBody; //위의 try가 제대로 안 돼면 파싱 안 한 날것의 정보 return. TODO:코드 정리
    }

    private static String post(String apiUrl, Map<String, String> requestHeaders, String text) {
        HttpURLConnection con = connect(apiUrl);
        String postParams = "source=en&target=ko&text=" + text; //원본언어: 목적언어: 영어 (en) -> 한국어 (ko)
        try {
            con.setRequestMethod("POST");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postParams.getBytes());
                wr.flush();
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 응답
                return readBody(con.getInputStream());
            } else {  // 에러 응답
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

}