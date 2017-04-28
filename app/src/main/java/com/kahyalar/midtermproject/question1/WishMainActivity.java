package com.kahyalar.midtermproject.question1;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Toast;

import com.kahyalar.midtermproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class WishMainActivity extends AppCompatActivity{
    public int userId, wishId;
    public static final String API_END_POINT = "http://tux.reeder.com.tr/wishlist/mobile.php";
    public BaseAdapter adapter;
    private SwipeRefreshLayout panel;
    private ListView listView;
    public JSONObject jsonObject, wishObject;
    public JSONArray array;
    public LayoutInflater inflater;
    public ArrayList<JSONObject> wishes = new ArrayList<>();
    public TextView wishTitle;

    public void initViews(){
        listView = (ListView)findViewById(R.id.lv_wish);
        panel = (SwipeRefreshLayout)findViewById(R.id.pnlRefresh);
        inflater = LayoutInflater.from(this);
    }

    public void initHelpers(){
        panel.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWishList();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent toWishDetails = new Intent(WishMainActivity.this, WishDetailsActivity.class);
                wishId = getWishId(position);
                toWishDetails.putExtra("id", wishId);
                toWishDetails.putExtra("user_id", userId);
                startActivity(toWishDetails);
                Toast.makeText(WishMainActivity.this, "Wish ID: "+wishId+", User ID: "+userId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWishList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_main);
        initViews();
        initHelpers();
        Bundle infoFromLogin = getIntent().getExtras();
        userId = infoFromLogin.getInt("id");
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return wishes.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                inflater = LayoutInflater.from(WishMainActivity.this);
                View temp = convertView;
                if(convertView == null){
                    temp = inflater.inflate(R.layout.wish_entry, null);
                }
                wishTitle = (TextView)temp.findViewById(R.id.wishTitle_ListView);
                String textToWrite = getWishTitle(position);
                wishTitle.setText(textToWrite);
                return temp;
            }
        };
        listView.setAdapter(adapter);
        panel.setRefreshing(true);
        getWishList();
    }

    public void getWishList(){
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try{
                    Document doc = Jsoup.connect(API_END_POINT)
                            .ignoreContentType(true)
                            .timeout(15000)
                            .data("op", "get_wish_list")
                            .post();
                    String response  = doc.text();
                    jsonObject = new JSONObject(response);
                    array = jsonObject.getJSONArray("wish_list");
                    Log.e("x", "Array: "+array.length());
                }catch (Exception e){
                    Log.e("x", "Error: "+e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                refreshContent();
                adapter.notifyDataSetChanged();
                panel.setRefreshing(false);
            }
        }.execute();
    }

    public String getWishTitle(int i){
        try {
            return getWishId(i)+"-) "+wishes.get(i).getString("wish_title");
        } catch (JSONException e) {
            Log.e("x", "Error: "+e);
            return null;
        }
    }

    public void fillList(){
        try{
            for(int i = 0; i<array.length(); i++){
                wishObject = array.getJSONObject(i);
                wishes.add(i, wishObject);
            }
        }catch (Exception e){
            Log.e("x", "Error: "+e);
        }
    }

    public void emptyList(){
        wishes.clear();
    }

    public void refreshContent(){
        emptyList();
        fillList();
    }

    public int getWishId(int i){
        try {
            return wishes.get(i).getInt("id");
        } catch (JSONException e) {
            Log.e("x", "Error: "+e);
            return 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.wish_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.button_add_new_wish){
            Intent toAddWish = new Intent(this, AddWishActivity.class);
            toAddWish.putExtra("id", userId);
            startActivity(toAddWish);
            return true;
        }
        return false;
    }
}
