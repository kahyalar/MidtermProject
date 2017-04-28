package com.kahyalar.midtermproject.question1;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kahyalar.midtermproject.R;

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SignupActivity extends AppCompatActivity {
    private EditText username, password, name, surname;
    private Button completeSignup;
    public static final String API_END_POINT = "http://tux.reeder.com.tr/wishlist/mobile.php";

    public void initViews(){
        username = (EditText)findViewById(R.id.usernameInput);
        password = (EditText)findViewById(R.id.passwordInput);
        name = (EditText)findViewById(R.id.nameInput);
        surname = (EditText)findViewById(R.id.surnameInput);
        completeSignup = (Button)findViewById(R.id.buttonCompleteSignup);
    }

    public void initHelpers(){
        completeSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameText = username.getText().toString();
                final String passwordText = password.getText().toString();
                final String nameText = name.getText().toString();
                final String surnameText = surname.getText().toString();
                new AsyncTask<String, String, String>() {
                    String JSONResponse;
                    @Override
                    protected String doInBackground(String... params) {
                        try
                        {
                            Document doc = Jsoup.connect(API_END_POINT)
                                    .ignoreContentType(true)
                                    .timeout(15000)
                                    .data("op", "register")
                                    .data("un", usernameText)
                                    .data("pw", passwordText)
                                    .data("fn", nameText)
                                    .data("ln", surnameText)
                                    .userAgent("Mozilla")
                                    .post();
                            JSONResponse = doc.text();
                            Log.e("x", JSONResponse);

                        }catch (Exception e) {
                            Log.e("x", "CONNECTION ERR : " + e);
                            Toast.makeText(SignupActivity.this, "Bağlantı Hatası!", Toast.LENGTH_SHORT).show();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        if(JSONResponse.contains("ok")){
                            Toast.makeText(SignupActivity.this, "Kayıt Başarılı!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Toast.makeText(SignupActivity.this, "Kullanıcı Adı Kullanımda..", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initViews();
        initHelpers();
    }
}
