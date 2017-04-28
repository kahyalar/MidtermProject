package com.kahyalar.midtermproject.question1;

import android.content.Intent;
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
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button buttonLogin;
    public static final String API_END_POINT = "http://tux.reeder.com.tr/wishlist/mobile.php";

    public void initViews(){
        username = (EditText)findViewById(R.id.usernameInput_Login);
        password = (EditText)findViewById(R.id.passwordInput_Login);
        buttonLogin = (Button)findViewById(R.id.buttonLogin_Login);
    }

    public void initHelpers(){
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameText = username.getText().toString();
                final String passwordText = password.getText().toString();
                new AsyncTask<String, String, String>() {
                    public JSONObject responseObject, innerResponseObject;
                    public String response;
                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            Document doc = Jsoup.connect(API_END_POINT)
                                    .ignoreContentType(true)
                                    .timeout(15000)
                                    .data("op", "login")
                                    .data("un", usernameText)
                                    .data("pw", passwordText)
                                    .userAgent("Mozilla")
                                    .post();
                            response = doc.text();
                            responseObject = new JSONObject(response);
                            innerResponseObject = responseObject.getJSONObject("user_data");
                            String fullname = innerResponseObject.getString("person");
                            int id = innerResponseObject.getInt("user_id");
                            Intent loginResponse = new Intent(LoginActivity.this, WishMainActivity.class);
                            loginResponse.putExtra("fullname", fullname);
                            loginResponse.putExtra("id", id);
                            if(response.contains("ok")){
                                startActivity(loginResponse);
                                finish();
                            }
                        } catch (Exception e){
                            Log.e("x", "Error = "+e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        if(response.contains("ok")){
                            Toast.makeText(LoginActivity.this, "Hoşgeldiniz !", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Bilgilerinizi kontrol edip, tekrar deneyin !", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initHelpers();
    }
}
