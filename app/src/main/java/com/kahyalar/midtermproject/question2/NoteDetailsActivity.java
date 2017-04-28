package com.kahyalar.midtermproject.question2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kahyalar.midtermproject.R;

public class NoteDetailsActivity extends AppCompatActivity {
    private EditText noteTitleDetails, noteTextDetails;
    private Button buttonEdit, buttonSave, buttonCancel;
    public String noteTitle, noteText;
    public int noteId;

    public void initViews(){
        noteTextDetails = (EditText)findViewById(R.id.note_text_field_details);
        noteTitleDetails = (EditText)findViewById(R.id.note_title_field_details);
        buttonEdit =(Button)findViewById(R.id.buttonEdit);
        buttonSave = (Button)findViewById(R.id.buttonSave_Details);
        buttonCancel = (Button)findViewById(R.id.buttonCancel_Details);
    }

    public void initHelpers(){
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonEdit.setVisibility(View.GONE);
                buttonCancel.setVisibility(View.VISIBLE);
                buttonSave.setVisibility(View.VISIBLE);
                noteTitleDetails.setEnabled(true);
                noteTextDetails.setEnabled(true);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBGateway dbGateway = new DBGateway(NoteDetailsActivity.this);
                byte[] toEncrypted_Title = noteTitleDetails.getText().toString().getBytes();
                byte[] toEncrypted_Text = noteTextDetails.getText().toString().getBytes();
                String encryptedTitle = Base64.encodeToString(toEncrypted_Title, Base64.URL_SAFE);
                String encryptedText = Base64.encodeToString(toEncrypted_Text, Base64.URL_SAFE);
                dbGateway.updateNote(noteId, encryptedTitle, encryptedText);
                finish();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSave.setVisibility(View.GONE);
                buttonCancel.setVisibility(View.GONE);
                buttonEdit.setVisibility(View.VISIBLE);
                noteTextDetails.setText(noteText);
                noteTitleDetails.setText(noteTitle);
                noteTextDetails.setEnabled(false);
                noteTitleDetails.setEnabled(false);
            }
        });
    }

    public void prepareFields(){
        buttonSave.setVisibility(View.GONE);
        buttonCancel.setVisibility(View.GONE);
        noteTitleDetails.setText(noteTitle, TextView.BufferType.EDITABLE);
        noteTextDetails.setText(noteText, TextView.BufferType.EDITABLE);
        noteTitleDetails.setEnabled(false);
        noteTextDetails.setEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        initViews();
        Bundle fromMainActivity = getIntent().getExtras();
        noteId = fromMainActivity.getInt("id");
        noteTitle = fromMainActivity.getString("title");
        noteText = fromMainActivity.getString("text");
        prepareFields();
        initHelpers();
    }
}
