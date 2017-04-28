package com.kahyalar.midtermproject.question1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kahyalar.midtermproject.R;
import com.kahyalar.midtermproject.question2.MainActivity_Q2;

public class MainActivity_Q1 extends AppCompatActivity {

    private Button buttonSignup, buttonLogin;

    public void initViews(){
        buttonSignup = (Button)findViewById(R.id.buttonSignup);
        buttonLogin = (Button)findViewById(R.id.buttonLogin);
    }

    public void initHelpers(){
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSignup = new Intent(MainActivity_Q1.this, SignupActivity.class);
                startActivity(toSignup);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLogin = new Intent(MainActivity_Q1.this, LoginActivity.class);
                startActivity(toLogin);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_question1);
        initViews();
        initHelpers();
    }
}
