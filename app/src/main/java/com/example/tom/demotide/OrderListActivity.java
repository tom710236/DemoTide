package com.example.tom.demotide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderListActivity extends AppCompatActivity {
    // 宣告
    String url = "http://demo.shinda.com.tw/ModernWebApi/Purchase.aspx";
    //String url2 = "http://demo.shinda.com.tw/ModernWebApi/GetShippersByCustomerID.aspx";
    String cUserName,json5;
    List<String> checked;
    String door1 = null;
    int index;
    String name;
    String listname;
    ArrayList<String> json2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

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
                Intent intent = new Intent(OrderListActivity.this, SystemActivity.class);
                Bundle bag = new Bundle();
                bag.putString("cUserName", cUserName);
                intent.putExtras(bag);
                startActivity(intent);
                OrderListActivity.this.finish();
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
        OkHttpClient client = new OkHttpClient();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"Token\":\"\" ,\"Action\":\"purchases\",\"UserID\" :\"S000000001\"}";
        Log.e("JSON", json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Log.e("UP2", body.toString());
        //使用OkHttp的newCall方法建立一個呼叫物件(尚未連線至主機)
        Call call = client.newCall(request);
        //呼叫call類別的enqueue進行排程連線(連線至主機)
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
                json2 = new ArrayList<String>();
                try {
                    JSONObject j = new JSONObject(json);
                    for(int i=0;i<j.getJSONArray("UserPurchases").length();i++){
                        String json0 = j.getJSONArray("UserPurchases").getString(i);
                        Log.e("json0",json0);
                        json2.add(json0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                parseJson(String.valueOf(json2));
                Log.e("JSON222", String.valueOf(json2));
            }
        });
    }


    //POST成功後回傳的值(陣列)取出來 用spinner顯示
    private void parseJson(final String json2) {
        //取值
        try {

            //建立一個ArrayList
            final ArrayList<String> trans = new ArrayList<String>();
            //建立一個JSONArray 並把POST回傳資料json(JSOM檔)帶入
            JSONArray array = new JSONArray(json2);
            //ArrayList 新增"請選擇"這一單項
            trans.add("請選擇");
            //用迴圈取出JSONArray內的JSONObject標題為"cCustomerName"的值
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                //String id = obj.getString("cCustomerID");
                String listname = obj.getString("PurchaseNo");
                Log.e("okHTTP5", listname);
                //ArrayList 新增JSONObject標題為"cCustomerName"的值
                trans.add(listname);

            }

            //宣告並取得Spinner
            final Spinner spinner = (Spinner) findViewById(R.id.spinner);
            //設定Spinner
            final ArrayAdapter<String> list = new ArrayAdapter<>(
                    OrderListActivity.this,
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
                    postjson2(json2, index);

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
    private void postjson2(String json2, int index) {

        try {
            //把點到的索引值-1(多了請選擇) 就能連結到所點到的json的客戶ID
            door1 = new JSONArray(json2).getJSONObject(index - 1).getString("PurchaseNo");
            Log.e("ARRAY", door1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //把連接到的客戶IP帶入JSON並POST上去
        OkHttpClient client = new OkHttpClient();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        String json3 = "{\"Token\":\"\" ,\"Action\":\"dopurchase\",\"UserID\" :\"S000000001\" ,\"PurchaseID\" : \""+door1+"\"}";
        Log.e("JSON", json3);
        RequestBody body = RequestBody.create(JSON, json3);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Log.e("UP2", body.toString());
        //使用OkHttp的newCall方法建立一個呼叫物件(尚未連線至主機)
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                //取得POST上去後所得到的JSON檔
                //[{"cShippersID":"S20160000011"}] 依所點的上傳 所以回傳不同
                json5 = response.body().string();
                Log.e("OkHttp6", response.toString());
                Log.e("OkHttp7", json5);

            }
        });
    }

    public void enter (View v){
            Intent intent = new Intent(OrderListActivity.this, OrderThingActivity.class);
            Bundle bag = new Bundle();
            bag.putString("name",name);
            bag.putString("cUserName",cUserName);
            bag.putString("json5",json5);
            intent.putExtras(bag);
            startActivity(intent);
            OrderListActivity.this.finish();
        }




}
