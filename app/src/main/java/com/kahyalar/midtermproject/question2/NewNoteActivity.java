package com.kahyalar.midtermproject.question2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kahyalar.midtermproject.R;

public class NewNoteActivity extends AppCompatActivity {
    private Button buttonSave;
    private EditText noteTitle, noteText;

    public void initViews(){
        buttonSave = (Button)findViewById(R.id.buttonSave);
        noteTitle = (EditText)findViewById(R.id.note_title);
        noteText = (EditText)findViewById(R.id.note_text);
    }

    public void initHelpers(){
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toBase64_Text = noteText.getText().toString();
                byte[] tempArray_Text = toBase64_Text.getBytes();
                String toBase64_Title = noteTitle.getText().toString();
                byte[] tempArray_Title = toBase64_Title.getBytes();
                String Base64_Text = Base64.encodeToString(tempArray_Text, Base64.URL_SAFE);
                String Base64_Title = Base64.encodeToString(tempArray_Title, Base64.URL_SAFE);
                DBGateway gateway = new DBGateway(NewNoteActivity.this);
                gateway.addNewNote(Base64_Title, Base64_Text);
                Toast.makeText(NewNoteActivity.this, "Note Added!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        initViews();
        initHelpers();
    }
}
