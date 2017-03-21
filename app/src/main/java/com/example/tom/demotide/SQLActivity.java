package com.example.tom.demotide;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SQLActivity extends AppCompatActivity {
    String cUserName;
    String url = "http://demo.shinda.com.tw/ModernWebApi/getProduct.aspx";
    ArrayList<ProductInfo> trans;
    private MyDBhelper helper;
    SQLiteDatabase db;
    final String DB_NAME = "tblTable";
    ListView listView;
    ContentValues addbase;
    String ID,name,NO,DT;
    int index;


    //建立一個類別存JSON
    public class ProductInfo {
        private String cProductID;
        private String cProductName;
        private String cGoodsNo;
        private String cUpdateDT;

        //建構子
        ProductInfo(final String ProductID, final String ProductName, final String GoodsNo,final String UpdateDT) {
            this.cProductID = ProductID;
            this.cProductName = ProductName;
            this.cGoodsNo = GoodsNo;
            this.cUpdateDT = UpdateDT;

        }
        //方法
        @Override
        public String toString() {
            return this.cProductID +  this.cProductName  + this.cGoodsNo + this.cUpdateDT;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql);

        //上一頁傳過來的資料取得
        Intent intent = getIntent();
        //取得Bundle物件後 再一一取得資料
        Bundle bag = intent.getExtras();
        cUserName = bag.getString("cUserName", null);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");

        //設定Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //回到上一頁圖示
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        //回到上一頁按鍵設定
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //因為cUserName從上一頁傳過來了 所以要回到上一頁 要把cUserName再傳回去
                Intent intent = new Intent(SQLActivity.this, SystemActivity.class);
                Bundle bag = new Bundle();
                bag.putString("cUserName", cUserName);
                intent.putExtras(bag);
                startActivity(intent);
                SQLActivity.this.finish();
            }
        });
        ArrayAdapter<String> upTime = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line);
        //upTime.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        upTime.add("請選擇");
        upTime.add("08:00");
        upTime.add("12:00");
        upTime.add("18:00");
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(upTime);
        spinner.setOnItemSelectedListener
                (new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //所點擊的索引值
                        index = spinner.getSelectedItemPosition();
                        Log.e("SPINNER", String.valueOf(index));
                        if (index==1){
                            Intent serviceIntent = new Intent(SQLActivity.this,Delay.class);
                            //Activity to Service
                            serviceIntent.putExtra("timeUp", "08:00:00");
                            SQLActivity.this.startService(serviceIntent);
                        }
                        else if(index==2){
                            Intent serviceIntent = new Intent(SQLActivity.this,Delay.class);
                            serviceIntent.putExtra("timeUp", "12:00:00");
                            SQLActivity.this.startService(serviceIntent);
                        }
                        else if(index==3){
                            Intent serviceIntent = new Intent(SQLActivity.this,Delay.class);
                            serviceIntent.putExtra("timeUp", "18:00:00");
                            SQLActivity.this.startService(serviceIntent);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        helper = new MyDBhelper(this, DB_NAME, null, 1);
        //實做 db(繼承SQLiteDatabase)類別 getWritableDatabase用來更新 新增修改刪除
        db = helper.getWritableDatabase();
    }



    class Get extends Thread {
        @Override
        public void run() {
            okHttpGet();
        }
    }
    // Get
    private void okHttpGet(){
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }
            //把get到的資料(JSON)轉為字串 並執行parseJson方法
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.e("json",json);
                parseJson(json);
            }
        });

    }
    //把json的資料解析出來 並放入SQL裡
    private void parseJson(String json) {

        try {
            //解析JSON資料
            trans = new ArrayList<ProductInfo>();
            final JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                trans.add(new ProductInfo(obj.optString("cProductID"), obj.optString("cProductName"),obj.optString("cGoodsNo"),obj.optString("cUpdateDT")));
                ID = obj.optString("cProductID");
                name = obj.optString("cProductName");
                NO = obj.optString("cGoodsNo");
                DT = obj.optString("cUpdateDT");
                //放入SQL
                addbase = new ContentValues();
                addbase.put("cProductID", ID);
                addbase.put("cProductName", name);
                addbase.put("cGoodsNo", NO);
                addbase.put("cUpdateDT", DT);
                db.insert(DB_NAME, null, addbase);
            }



        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }
    public void upthing(View v){
        //先刪除就有資料表格
        db.delete(DB_NAME, null, null);
        //放入新增表格
        Get get = new Get();
        get.start();

    }
    public void delThing(View v){
        db.delete(DB_NAME,null,null);

    }
    public void back (View v){
        cursor3();
    }
    public void out (View v){
        Intent intent = new Intent(SQLActivity.this, MainActivity.class);
        startActivity(intent);

    }
    private void cursor3(){

        Cursor c=db.rawQuery("SELECT * FROM "+DB_NAME, null);
        ListView lv = (ListView)findViewById(R.id.listView);
        SimpleCursorAdapter adapter;
        adapter = new SimpleCursorAdapter(this,
                //android.R.layout.simple_expandable_list_item_2,
                R.layout.lview2,
                c,
                //new String[] {"info","amount"},
                new String[] {"_id", "cProductID", "cProductName", "cGoodsNo", "cUpdateDT"},
                //new int[] {android.R.id.text1,android.R.id.text2},
                new int[] {R.id.textView19,R.id.textView18,R.id.textView17,R.id.textView16,R.id.textView15},
                0);
        lv.setAdapter(adapter);
    }



}
