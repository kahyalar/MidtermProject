package com.kahyalar.midtermproject.question1;

import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class WishDetailsActivity extends AppCompatActivity {
    private TextView txtTitle, txtText;
    private Button buttonGrant, buttonPostGrant, buttonCancel;
    private SwipeRefreshLayout commentPanel;
    private EditText commentInput;
    private ListView listView;
    public BaseAdapter adapter;
    public LayoutInflater inflater;
    public int wishId, userId;
    public static final String API_END_POINT = "http://tux.reeder.com.tr/wishlist/mobile.php";
    public JSONObject jsonObject, wishObject;
    public JSONArray grantArray;
    public ArrayList<JSONObject> grantDetails = new ArrayList<>();
    public TextView tv_Text;
    public TextView tv_Time;

    public void initViews(){
        txtTitle = (TextView)findViewById(R.id.wishTitle_WishDetails);
        txtText = (TextView)findViewById(R.id.wishText_WishDetails);
        buttonGrant = (Button)findViewById(R.id.buttonGrantWish);
        buttonCancel = (Button)findViewById(R.id.btnCancel_WishDetails);
        buttonPostGrant = (Button)findViewById(R.id.btnGrantText_WishDetails);
        commentPanel = (SwipeRefreshLayout)findViewById(R.id.pnlRefresh_WishDetails);
        commentInput = (EditText)findViewById(R.id.addComment_WishDetails);
        listView = (ListView)findViewById(R.id.lv_WishDetails);
    }

    public void initHelpers(){
        buttonGrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonGrant.setVisibility(View.GONE);
                showViews();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonGrant.setVisibility(View.VISIBLE);
                commentInput.setText("");
                hideViews();
            }
        });

        buttonPostGrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String textAnswer = commentInput.getText().toString();
                new AsyncTask<String, String, String>() {
                    String response;
                    @Override
                    protected String doInBackground(String... params) {
                        try{
                            Document doc = Jsoup.connect(API_END_POINT)
                                    .ignoreContentType(true)
                                    .timeout(15000)
                                    .data("op", "make_wish_real")
                                    .data("wish_id", String.valueOf(wishId))
                                    .data("user_id", String.valueOf(userId))
                                    .data("answer", textAnswer)
                                    .post();
                            response  = doc.text();
                            Log.e("x", response);
                        }catch (Exception e){
                            Log.e("x", "Error: "+e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        if(response.contains("ok")){
                            Toast.makeText(WishDetailsActivity.this, "Granted!", Toast.LENGTH_SHORT).show();
                            hideViews();
                            getWishDetails();
                            refreshContent();
                        }
                    }
                }.execute();
            }
        });
    }

    public void hideViews(){
        buttonGrant.setVisibility(View.VISIBLE);
        buttonCancel.setVisibility(View.GONE);
        buttonPostGrant.setVisibility(View.GONE);
        commentInput.setVisibility(View.GONE);
    }

    public void showViews(){
        buttonCancel.setVisibility(View.VISIBLE);
        buttonPostGrant.setVisibility(View.VISIBLE);
        commentInput.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_details);
        Bundle fromWishMain = getIntent().getExtras();
        wishId = fromWishMain.getInt("id");
        userId = fromWishMain.getInt("user_id");
        getWishDetails();
        initViews();
        hideViews();
        initHelpers();
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return grantDetails.size();
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
                inflater = LayoutInflater.from(WishDetailsActivity.this);
                View temp = convertView;
                commentPanel = (SwipeRefreshLayout)parent;
                if(convertView == null){
                    temp = inflater.inflate(R.layout.wish_details_entry, commentPanel, true);
                }
                TextView tv_Text = (TextView)temp.findViewById(R.id.wishDetails_grantText);
                TextView tv_Time = (TextView)temp.findViewById(R.id.wishDetails_Time);
                tv_Text.setText(getGrantText(position));
                tv_Time.setText(getGrantTime(position));
                return temp;
            }
        };
        listView.setAdapter(adapter);
    }

    public void getWishDetails(){
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try{
                    Document doc = Jsoup.connect(API_END_POINT)
                            .ignoreContentType(true)
                            .timeout(15000)
                            .data("op", "get_wish_detail")
                            .data("wish_id", String.valueOf(wishId))
                            .post();
                    String response  = doc.text();
                    jsonObject = new JSONObject(response);
                    wishObject = jsonObject.getJSONObject("wish_data");
                    grantArray = jsonObject.getJSONArray("grant_data");
                    Log.e("x", "Wish Object: "+wishObject.toString());
                    Log.e("x", "Grant Array: "+grantArray.length());
                }catch (Exception e){
                    Log.e("x", "Error: "+e);
                }
                return null;
            }
            @Override
            protected void onPostExecute(String s) {
                fillFields();
                refreshContent();
            }
        }.execute();
    }

    public void fillFields(){
        try {
            fillList();
            txtTitle.setText(wishObject.getString("wish_title"));
            txtText.setText(wishObject.getString("wish_text"));
            for (int i = 0; i< grantDetails.size(); i++){
                TextView tv = new TextView(this);
                tv.setText(grantDetails.get(i).getString("answer"));
                commentPanel.addView(tv);
                Log.e("x", grantDetails.get(i).getString("answer"));
            }
        } catch (JSONException e) {
            Log.e("e", "Error: "+e);
        }
    }

    public String getGrantText(int i){
        try {
            return grantDetails.get(i).getString("answer");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getGrantTime(int i){
        try {
            return grantDetails.get(i).getString("grant_date");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void fillList(){
        try {
            for (int i = 0; i < grantArray.length(); i++){
                grantDetails.add(grantArray.getJSONObject(i));
            }
        } catch (Exception e){
            Log.e("x", "Error: "+e);
        }
    }

    public void clearList(){
        grantDetails.clear();
    }

    public void refreshContent(){
        clearList();
        fillList();
    }
}
