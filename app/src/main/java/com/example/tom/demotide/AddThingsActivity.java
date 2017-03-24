package com.example.tom.demotide;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
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


public class AddThingsActivity extends AppCompatActivity {
    String cUserName,lackNoAdd,lackNameAdd,tblTable4,listname;
    SQLiteDatabase db4;
    int index;
    String LackNameTo,LackNo2,LackNo3;
    List<String> checked;
    ArrayList<String> trans,json2;
    //儲位API
    String url="http://demo.shinda.com.tw/ModernWebApi/LackAPI.aspx";
    //把JSON 類別化
    public class ProductInfo {
        private String mLackNo;
        private String mLackName;
        //建構子
        ProductInfo(final String LackNo, final String LackName) {
            this.mLackNo = LackNo;
            this.mLackName = LackName;
        }
        //方法
        @Override
        public String toString() {
            return this.mLackNo + "-" + this.mLackName;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_things);

        PassList passList = new PassList();
        passList.start();

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
                Intent intent = new Intent(AddThingsActivity.this, SystemActivity.class);
                Bundle bag = new Bundle();
                bag.putString("cUserName", cUserName);
                intent.putExtras(bag);
                startActivity(intent);
                AddThingsActivity.this.finish();
            }
        });
    }
    public void onAdd (View v){
        //對話框
        final View item = LayoutInflater.from(AddThingsActivity.this).inflate(R.layout.activity_alertdialog2, null);
        new AlertDialog.Builder(AddThingsActivity.this)
                .setTitle("")
                .setView(item)
                .setNegativeButton("取消", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //取得輸入的字串 注意item
                        //String lackNoAdd,lackNameAdd;

                        EditText lackNo =(EditText)item.findViewById(R.id.editText6);
                        EditText lackName = (EditText)item.findViewById(R.id.editText4);
                        lackNoAdd = lackNo.getText().toString();
                        lackNameAdd = lackName.getText().toString();

                        Log.e("LACKNOADD", lackNoAdd);
                        Log.e("LACKNOADD", lackNameAdd);
                        //把字串放入SQL
                        //putSQL();
                        //POST 字串
                        Pass pass = new Pass();
                        pass.start();



                    }

                }).show();
    }
    //把字串放入SQL
    private void putSQL(){

        MyDBhelper4 myDB4 = new MyDBhelper4(AddThingsActivity.this,"tblTable4",null,1);
        db4=myDB4.getWritableDatabase();
        ContentValues addbase = new ContentValues();
        addbase.put("LackNo","ackNoAdd");
        addbase.put("LackName","lackNameAdd");
        db4.insert("tblTable4",null,addbase);
    }
    //顯示SQL
    private void cursor3(){
        MyDBhelper4 myDB4 = new MyDBhelper4(AddThingsActivity.this,"tblTable4",null,1);
        db4=myDB4.getWritableDatabase();
        Cursor c=db4.rawQuery("SELECT * FROM "+"tblTable4", null);
        ListView lv = (ListView)findViewById(R.id.list);
        SimpleCursorAdapter adapter;
        adapter = new SimpleCursorAdapter(this,
                //android.R.layout.simple_expandable_list_item_2,
                android.R.layout.simple_list_item_multiple_choice,
                c,
                //new String[] {"info","amount"},
                new String[] {"LackNo", "LackName"},
                //new int[] {android.R.id.text1,android.R.id.text2},
                new int[] {R.id.textView20,R.id.textView21},
                0);
        lv.setAdapter(adapter);
    }
    //POST 新增Lack JSON的方法
    class Pass extends Thread {
        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\": \"xxxxxxxxxxxxxxxxxxxxxxxx\", \"Action\": \"add\", \"UserID\": \"test\", \"LackInfo\": {\"LackNo\": \""+lackNoAdd+"\", \"LackName\": \""+lackNameAdd+"\"}}";
            Log.e("JSON",json);
            RequestBody body = RequestBody.create(JSON,json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            //使用OkHttp的newCall方法建立一個呼叫物件(尚未連線至主機)
            Call call = client.newCall(request);
            //呼叫call類別的enqueue進行排程連線(連線至主機)
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }
                //
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    Log.e("OkHttp", response.toString());
                    Log.e("OkHttp2", json);
                }
            });
        }
    }
    //POST 取得Lack列表
    class PassList extends Thread {
        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"list\"}";
            Log.e("JSON",json);
            RequestBody body = RequestBody.create(JSON,json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Log.e("UP", body.toString());
            //使用OkHttp的newCall方法建立一個呼叫物件(尚未連線至主機)
            Call call = client.newCall(request);
            //呼叫call類別的enqueue進行排程連線(連線至主機)
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    Log.e("OkHttp3", response.toString());
                    Log.e("OkHttp4", json);
                    json2 = new ArrayList<String>();
                    try {
                        JSONObject j = new JSONObject(json);
                        for (int i =0; i<j.getJSONArray("LackList").length();i++){
                            String json0 = j.getJSONArray("LackList").getString(i);
                            Log.e("json0",json0);
                            json2.add(json0);
                        }
                        Log.e("JSON2222", String.valueOf(json2));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    parseJson2(String.valueOf(json2));
                }
            });

        }
    }

    private void parseJson2(String json2) {
        try {
            final ArrayList<String> trans = new ArrayList<String>();
            final JSONArray array = new JSONArray(json2);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                listname = obj.getString("LackNo")+"-"+obj.getString("LackName");
                Log.e("okHTTP8", listname);
                //ArrayList新增listname項目
                trans.add(listname);
                Log.e("trans", String.valueOf(trans));
            }

            final ListView listView = (ListView)findViewById(R.id.list);
            // 設定 ListView 選擇的方式 :
            // 單選 : ListView.CHOICE_MODE_SINGLE
            // 多選 : ListView.CHOICE_MODE_MULTIPLE
            listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            // 陣列接收器
            // RadioButton Layout 樣式 : android.R.layout.simple_list_item_single_choice
            // CheckBox Layout 樣式    : android.R.layout.simple_list_item_multiple_choice
            // trans 是陣列
            final   ArrayAdapter<String> list = new ArrayAdapter<>(
                    AddThingsActivity.this,
                    android.R.layout.simple_list_item_multiple_choice,
                    trans);
                    //顯示出listView
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    listView.setVisibility(View.VISIBLE);
                    //設定 ListView 的接收器, 做為選項的來源
                    listView.setAdapter(list);
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

            //第二種點擊方式 (長按)
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
                    // 利用索引值取得點擊的項目內容。
                    String text = trans.get(index);
                    Log.e("TEXT",text);
                    // 因為只要取LackNO LackNO在-之前 所以先找出-的位置
                    int i = text.indexOf('-');
                    //取出0到-之間的值即LackNo
                    LackNameTo = text.substring(0, i);
                    Log.e("LackNameTo",LackNameTo);
                    // 整理要顯示的文字。
                    String result = "索引值: " + index + "\n" + "內容: " + LackNameTo;
                    // 顯示。
                    //Toast.makeText(AddThingsActivity.this, result, Toast.LENGTH_SHORT).show();
                    PassList2 passList2 = new PassList2();
                    passList2.start();
                    // 回傳 false，長按後該項目被按下的狀態會保持。
                    return false;
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("CHECKED", String.valueOf(checked));
    }
    //長按後 到下一頁
    class PassList2 extends Thread {

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"detail\",\"LackNo\":\"" + LackNameTo + "\"}";
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
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    Log.e("OkHttp5", response.toString());
                    Log.e("OkHttp6", json);
                    int i = json.length();
                    String json2 = json.substring(26, i - 4);
                    Log.e("JSON2", json2);
                    parseJson3(json);
                }
            });

        }

        private void parseJson3(String json) {
            try {

                JSONObject jsonObject = new JSONObject(json);
                String LackNo = jsonObject.getJSONArray("LackList").getString(0);
                Log.e("LACKNO", String.valueOf(LackNo));
                JSONObject jsonObject1 = new JSONObject(LackNo);
                LackNo2 = (String) jsonObject1.get("LackNo");
                LackNo3 = (String) jsonObject1.get("LackName");
                Log.e("LACKNO2", LackNo2);
                Log.e("LACKNO3", LackNo3);
                trans = new ArrayList<String>();
                for(int i =0; i<jsonObject1.getJSONArray("LackProduct").length();i++){
                    String LackNo4 = jsonObject1.getJSONArray("LackProduct").getString(i);
                    Log.e("LACKNO4", LackNo4);
                    trans.add(LackNo4);
                }

                Log.e("TRANS", String.valueOf(trans));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(AddThingsActivity.this, ThingsActivity.class);
            Bundle bag = new Bundle();
            bag.putString("LackNo2",LackNo2);
            bag.putString("LackNo3",LackNo3);
            bag.putString("trans", String.valueOf(trans));
            intent.putExtras(bag);
            startActivity(intent);

        }

    }
}
