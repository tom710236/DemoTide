package com.example.tom.demotide;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AddThingsActivity extends AppCompatActivity {
    String cUserName,lackNoAdd,lackNameAdd;
    int key;
    String url="http://demo.shinda.com.tw/ModernWebApi/LackAPI.aspx";
    ArrayList<ProductInfo> getlist;
    ArrayList<String> checked;
    ArrayAdapter<ProductInfo> list;
    ProductInfo checkNo;
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
        PassList passList = new PassList();
        passList.start();

    }
    //POST 取得list清單
    class PassList extends Thread {
        public void run(){
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
                    Log.e("取得list清單的網址", response.toString());
                    Log.e("取得的list清單", json);
                    //解析 JSON
                    //建立一個ArrayListList
                    getlist = new ArrayList<>();
                    try {
                        //取出LackList的陣列
                        JSONObject j = new JSONObject(json);
                        JSONArray array = j.getJSONArray("LackList");
                        Log.e("ARRAY", String.valueOf(array));

                        //用FOR取出陣列裡的物件
                        for (int i = 0; i < array.length(); i++)
                        {
                            JSONObject obj = array.getJSONObject(i);
                            //把取出的物件 放進ARRAYLIS
                            getlist.add(new ProductInfo(obj.optString("LackNo"),obj.optString("LackName")));
                            Log.e("getlist", String.valueOf(getlist));
                            getListView(getlist);

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        }
    }
    //顯示 點擊listView
    private void getListView(final ArrayList<ProductInfo> getlist) {
        final ListView listView = (ListView)findViewById(R.id.list);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                list = new ArrayAdapter<>(
                AddThingsActivity.this,
                android.R.layout.simple_list_item_multiple_choice,
                getlist);
        //顯示出listView
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                listView.setVisibility(View.VISIBLE);
                //設定 ListView 的接收器, 做為選項的來源
                listView.setAdapter(list);
                list.notifyDataSetChanged();

            }
        });
        //第一種點擊方式 勾選
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                AbsListView list = (AbsListView)adapterView;
                Adapter adapter = list.getAdapter();
                SparseBooleanArray array = list.getCheckedItemPositions();
                checked = new ArrayList<>(list.getCheckedItemCount());

                for (int i = 0; i < array.size(); i++) {
                    key = array.keyAt(i);
                    if (array.get(key)) {
                        checkNo =(ProductInfo)adapter.getItem(key);
                        checked.add(checkNo.mLackNo);
                        Log.e("點擊", String.valueOf(checked));

                    }
                }
            }
        });

        //第二種點擊方式 (長按)
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
                // 利用索引值取得點擊的項目內容。
                ProductInfo text = getlist.get(index);
                Log.e("text.mLackNo", text.mLackNo);
                Log.e("text.mLackName", text.mLackName);
                Intent intent = new Intent(AddThingsActivity.this, ThingsActivity.class);
                Bundle bag = new Bundle();
                bag.putString("mLackNo",text.mLackNo);
                bag.putString("mLackName",text.mLackName);
                bag.putString("cUserName",cUserName);
                intent.putExtras(bag);
                startActivity(intent);
                // 回傳 false，長按後該項目被按下的狀態會保持。
                return false;
            }
        });

    }
    //新增按鈕
    public void onAdd (View v){
        //對話框
        final View item = LayoutInflater.from(AddThingsActivity.this).inflate(R.layout.activity_alertdialog2, null);
        new AlertDialog.Builder(AddThingsActivity.this)
                .setTitle("")
                .setView(item)
                .setNegativeButton("取消", null)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText lackNo =(EditText)item.findViewById(R.id.editText6);
                        EditText lackName = (EditText)item.findViewById(R.id.editText4);
                        lackNoAdd = lackNo.getText().toString();
                        lackNameAdd = lackName.getText().toString();
                        Pass pass = new Pass();
                        pass.start();




                    }
                }).show();

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
                    PassList passList = new PassList();
                    passList.start();
                }
            });
        }
    }
    //刪除按鈕
    public void onDel (View v){
        PassDel passDel = new PassDel();
        passDel.start();

    }
    class PassDel extends Thread {

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String checked3 = String.valueOf(checked).replace("[", "");
            String checked4 = checked3.replace("]", "");
            String checked5 = checked4.replace(", ", ",");
            String json = "{\"Token\":\"\" ,\"Action\":\"delete\",\"LackNo\":\"" + checked5 + "\"}";
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
                    PassList passList = new PassList();
                    passList.start();

                }
            });

        }

    }

}
