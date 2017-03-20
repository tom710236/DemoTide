package com.example.tom.demotide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OutActivity extends AppCompatActivity {
    // 宣告
    String url = "http://demo.shinda.com.tw/ModernWebApi/WebApi.aspx";
    String url2 = "http://demo.shinda.com.tw/ModernWebApi/GetShippersByCustomerID.aspx";
    OkHttpClient client = new OkHttpClient();
    String cUserName;
    List<String> checked;
    String door1 = null;
    int index;
    String name;
    String listname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out);

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
                Intent intent = new Intent(OutActivity.this, SystemActivity.class);
                Bundle bag = new Bundle();
                bag.putString("cUserName", cUserName);
                intent.putExtras(bag);
                startActivity(intent);
                OutActivity.this.finish();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                postjson();
            }
        }).start();
    }



    private void postjson() {
        //post--客戶
        RequestBody body = new FormBody.Builder()
                .add("postdata", "{\"ApiName\":\"GetCustomer\",\"ApiID\":\"S000000001\"}")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            //post 失敗後
            @Override
            public void onFailure(Call call, IOException e) {

            }

            //POST 成功後
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //取得回傳資料json JSON檔陣列
                //[{"cCustomerID":"C000000001","cCustomerName":"大島屋企業"},{"cCustomerID":"C000000002","cCustomerName":"新達科技"},{"cCustomerID":"C000000003","cCustomerName":"磯法資訊"}]
                String json = response.body().string();
                Log.e("OkHttp3", response.toString());
                Log.e("OkHttp4", json);
                parseJson(json);
            }
        });
    }


    //POST成功後回傳的值(陣列)取出來 用spinner顯示
    private void parseJson(final String json) {
        //取值
        try {
            //建立一個ArrayList
            final ArrayList<String> trans = new ArrayList<String>();
            //建立一個JSONArray 並把POST回傳資料json(JSOM檔)帶入
            JSONArray array = new JSONArray(json);
            //ArrayList 新增 請選擇這一單項
            trans.add("請選擇");
            //用迴圈取出JSONArray內的JSONObject標題為"cCustomerName"的值
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                //String id = obj.getString("cCustomerID");
                String listname = obj.getString("cCustomerName");
                Log.e("okHTTP5", listname);
                //ArrayList 新增JSONObject標題為"cCustomerName"的值
                trans.add(listname);

            }

            //宣告並取得Spinner
            final Spinner spinner = (Spinner) findViewById(R.id.spinner);
            //設定Spinner
            final ArrayAdapter<String> list = new ArrayAdapter<>(
                    OutActivity.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    trans);

            //顯示Spinner 非主執行緒的UI 需用runOnUiThread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    spinner.setAdapter(list);
                }
            });

            //spinner 點擊事件
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //所點擊的索引值
                    index = spinner.getSelectedItemPosition();
                    //所點擊的內容文字
                    name = spinner.getSelectedItem().toString();
                    Log.e("index", String.valueOf(index));
                    Log.e("name",name);
                    //點擊後所要執行的方法 並把所回傳的json和索引值帶入
                    postjson2(json, index);

                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // sometimes you need nothing here
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //點擊 spinner項目後 所要執行的方法
    private void postjson2(String json, int index) {

        try {
            //把點到的索引值-1(多了請選擇) 就能連結到所點到的json的客戶ID
            door1 = new JSONArray(json).getJSONObject(index - 1).getString("cCustomerID");
            Log.e("ARRAY", door1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //把連接到的客戶IP帶入JSON並POST上去
        RequestBody body = new FormBody.Builder()
                .add("postdata", "{ cCustomerID:\"" + door1 + "\", cUserID: \"S000000001\" }")
                //.add("postdata", "{\"cAccount\":\"" + userName + "\",\"cPassword\":\"" + passWord + "\"}")
                .build();
        Request request = new Request.Builder()
                .url(url2)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                //取得POST上去後所得到的JSON檔
                //[{"cShippersID":"S20160000011"}] 依所點的上傳 所以回傳不同
                String json2 = response.body().string();
                Log.e("OkHttp6", response.toString());
                Log.e("OkHttp7", json2);
                parseJson2(json2);
            }
        });
    }

    //POST成功後把回傳的值(陣列)取出來 用listView顯示 把JSON2帶進來
    private void parseJson2(String json2) {
        try {
            final ArrayList<String> trans = new ArrayList<String>();
            final JSONArray array = new JSONArray(json2);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                //取得標題為"cShippersID"的內容
                listname = obj.getString("cShippersID");
                Log.e("okHTTP8", listname);
                //ArrayList新增listname項目
                trans.add(listname);
            }
            final ListView listView = (ListView) findViewById(R.id.listView);
            // 設定 ListView 選擇的方式 :
            // 單選 : ListView.CHOICE_MODE_SINGLE
            // 多選 : ListView.CHOICE_MODE_MULTIPLE
            listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            // 陣列接收器
            // RadioButton Layout 樣式 : android.R.layout.simple_list_item_single_choice
            // CheckBox Layout 樣式    : android.R.layout.simple_list_item_multiple_choice
            // trans 是陣列
            final ArrayAdapter<String> list = new ArrayAdapter<>(
                    OutActivity.this,
                    android.R.layout.simple_list_item_multiple_choice,
                    trans);
            //非主執行緒顯示UI
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //顯示出listView
                    listView.setVisibility(View.VISIBLE);
                    //設定 ListView 的接收器, 做為選項的來源
                    listView.setAdapter(list);
                    //假如選到請選擇 list將不會出現
                    if (index==0){
                        listView.setVisibility(View.GONE);
                    }

                }
            });
            //ListView的點擊方法
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                    AbsListView list = (AbsListView)adapterView;
                    Adapter adapter = list.getAdapter();
                    SparseBooleanArray array = list.getCheckedItemPositions();
                    checked = new ArrayList<>(list.getCheckedItemCount());
                    for (int i = 0; i < array.size(); i++) {
                        int key = array.keyAt(i);
                        if (array.get(key)) {
                            checked.add((String) adapter.getItem(key));
                            Log.e("CHECK", String.valueOf(checked));

                        }

                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("CHECKED", String.valueOf(checked));
    }
    public void enter (View v){
        //如果沒有點擊
        if(checked==null){

            Toast.makeText(OutActivity.this,"請選擇出貨單", Toast.LENGTH_SHORT).show();
        }
        else{
            //點擊後到下一頁和所要傳的資料
            Intent intent = new Intent(OutActivity.this, OrderActivity.class);
            Bundle bag = new Bundle();
            bag.putString("checked", String.valueOf(checked));
            bag.putString("order",name);
            bag.putString("cUserName",cUserName);
            intent.putExtras(bag);
            startActivity(intent);
            OutActivity.this.finish();
        }



    }
}
