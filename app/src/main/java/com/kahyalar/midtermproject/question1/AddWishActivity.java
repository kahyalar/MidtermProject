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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class AddWishActivity extends AppCompatActivity {
    public int userId;
    private EditText wishTitle, wishText;
    private Button buttonSave;
    public static final String API_END_POINT = "http://tux.reeder.com.tr/wishlist/mobile.php";

    public void initViews(){
        wishText = (EditText)findViewById(R.id.wishText);
        wishTitle = (EditText)findViewById(R.id.wishTitle);
        buttonSave = (Button)findViewById(R.id.buttonSaveWish);
    }

    public void initHelpers(){
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String wishTitle_Text = wishTitle.getText().toString();
                final String wishText_Text = wishText.getText().toString();
                new AsyncTask<String, String, String>() {
                    String response;
                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            Document doc = Jsoup.connect(API_END_POINT)
                                    .ignoreContentType(true)
                                    .timeout(15000)
                                    .data("op", "make_a_wish")
                                    .data("user_id", String.valueOf(userId))
                                    .data("title", wishTitle_Text)
                                    .data("text", wishText_Text)
                                    .post();
                            response = doc.text();
                            Log.e("x", response);
                            if(response.contains("ok")){
                                finish();
                            }
                        }catch (Exception e){
                            Log.e("x", "Hata: " +e);
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(String s) {
                        if(response.contains("ok")){
                            Toast.makeText(AddWishActivity.this, "Dileğiniz kaydedildi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wish);
        Bundle fromWishMain = getIntent().getExtras();
        userId = fromWishMain.getInt("id");
        initViews();
        initHelpers();
    }
}
