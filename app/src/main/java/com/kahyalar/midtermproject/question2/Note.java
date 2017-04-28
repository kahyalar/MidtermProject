package com.kahyalar.midtermproject.question2;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by kahyalar on 31/03/2017.
 */

public class Note {
    public String title, text;
    public int id;

    public Note(int id, String title, String text){
        this.id = id;
        this.title = title;
        this.text = text;
    }
}
