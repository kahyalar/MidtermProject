package com.kahyalar.midtermproject.question2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kahyalar.midtermproject.R;

import java.util.ArrayList;

public class MainActivity_Q2 extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{
    public ListView listView;
    public LayoutInflater inflater;
    public BaseAdapter baseAdapter;
    public DBGateway db;
    public ArrayList<Note> notes = new ArrayList<>();
    public Note note;

    //Diğer aktivitelerden finish() ile çıktıktan sonra onResume fazında tekrardan ekleme/düzenleme işlemlerinin aktivasyonu için..
    //..refresh komutunu tekrardan çağırdım.
    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    //Datebase'i sorgusunu yollayıp, fresh listeyi ArrayList'in içinde döndüren ve ana konteynırı yenileyen mekanizma.
    public void refreshList()
    {
        notes = db.getNotesAsObject();
        baseAdapter.notifyDataSetChanged();
    }

    //MainActivity'miz sadece listView ile DB'deki notları göstereceği için doğrudan adaptörü..
    //..OnCreate fonksiyonunun içine implemente ettim. Ayrı bir sınıfa gerek duymadım.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_question2);
        db = new DBGateway(this);
        listView = (ListView) findViewById(R.id.lv);
        inflater = LayoutInflater.from(this);
        baseAdapter = new BaseAdapter() {
            public int getCount()
            {
                return notes.size();
            }
            public View getView(int i, View view, ViewGroup viewGroup)
            {
                if (view == null)
                {
                    view = inflater.inflate(R.layout.note_entry, null);
                }
                TextView noteTitle = (TextView) view.findViewById(R.id.noteTitle);
                note = notes.get(i);
                noteTitle.setText(note.title);
                return view;
            }
            @Override
            public Object getItem(int i) {
                return null;
            }
            @Override
            public long getItemId(int i) {
                return 0;
            }
        };
        listView.setAdapter(baseAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    //ActionBar'a "Yeni Not Ekle" Butonu eklendi.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.secret_notes_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.button_add_new_note){
            Intent toAddNote = new Intent(this, NewNoteActivity.class);
            startActivity(toAddNote);
            return true;
        }
        return false;
    }

    //Not detaylarına yönlendiriyor.
    //Intent'in içine o nota ait olan id, title ve text'i de katıp, karşı taraftan yakalayacağım.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Note selectedNote = notes.get(position);
        Intent toDetails = new Intent(MainActivity_Q2.this, NoteDetailsActivity.class);
        toDetails.putExtra("id", selectedNote.id);
        toDetails.putExtra("title", selectedNote.title);
        toDetails.putExtra("text", selectedNote.text);
        startActivity(toDetails);
    }

    //Uzun tıklama eventinde AlertDialog.Builder ile silme dialog penceresi oluşturuldu.
    //Vazgeç butonu için sadece sağlanan diagogInterface'in cancel fonksiyonu kullanıldı.
    //Sil butonu da DBGateway sınıfından silme fonksiyonunu seçilmiş notun id'si baz alınarak işlem bitiriliyor.
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Note selectedNote = notes.get(position);
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
        deleteDialog.setTitle("Silmek istediğinizden emin misiniz ?");
        deleteDialog.setMessage("Yaptığınız işlemin geri dönüşü olmayacaktır.");
        deleteDialog.setNeutralButton("Vazgeç", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        deleteDialog.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBGateway gateway = new DBGateway(MainActivity_Q2.this);
                gateway.deleteNote(selectedNote.id);
                dialog.dismiss();
                refreshList();
            }
        });
        deleteDialog.show();
        return true;
    }
}
