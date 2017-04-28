package com.kahyalar.midtermproject.question2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by kahyalar on 31/03/2017.
 */

public class DBGateway extends SQLiteOpenHelper {
    private SQLiteDatabase db;

    public DBGateway(Context context) {
        super(context, context.getDatabasePath("db.db").getAbsolutePath(), null, 1);
        db = getWritableDatabase();
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create Table notes(id INTEGER PRIMARY KEY AUTOINCREMENT, note_title TEXT, note_text TEXT);");
    }

    public void addNewNote(String note_title, String note_text)
    {
        db = getWritableDatabase();
        String values[] = {note_title, note_text};
        db.execSQL("insert into notes (note_title, note_text) values (?, ?)", values);
        db.close();
    }

    public void deleteNote(int id)
    {
        db = getWritableDatabase();
        db.execSQL("delete from notes where id = "+id);
        db.close();
    }

    //Midterm makalemizde ID'yi int olarak tutmamızı söylemişsiniz, array yapısını da String'den Object'e yükselttim
    //Dinamik cast yaparken sorunsuz işliyor.
    //Temel SQLite Update sorgusu yapan fonskiyon.
    public void updateNote(int id, String note_title, String note_text)
    {
        db = getWritableDatabase();
        Object values[] = { note_title, note_text, id };
        db.execSQL("update notes set note_title = ?, note_text = ? where id = ? ", values);
    }

    //Database'yi dump edip Cursor ile bütün listeyi gezerek bütün notları oluşturuyorum burada.
    //Aynı zamanda SQLite'dan aldığım Base64'ün decrption işlemini de burada gerçekleştiriyorum.
    public ArrayList<Note> getNotesAsObject()
    {
        ArrayList<Note> resultList = new ArrayList<>();
        db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from notes", null);
        while (cursor.moveToNext())
        {
            int noteId = cursor.getInt(0);
            byte[] Base64_Title = Base64.decode(cursor.getString(1), Base64.URL_SAFE);
            byte[] Base64_Text = Base64.decode(cursor.getString(2), Base64.URL_SAFE);
            String decrpytedTitle = new String(Base64_Title, StandardCharsets.UTF_8);
            String decrpytedText = new String(Base64_Text, StandardCharsets.UTF_8);
            Note note = new Note(noteId, decrpytedTitle, decrpytedText);
            resultList.add(note);
        }
        cursor.close();
        db.close();
        return resultList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
