package com.example.tom.demotide;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
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

public class ThingsActivity extends AppCompatActivity {
    String cUserName, mLackNo, mLackName;
    String url = "http://demo.shinda.com.tw/ModernWebApi/LackAPI.aspx";
    ArrayList<ProductInfo> getlist;
    ArrayList<ProductInfo3> getlist2;
    //ArrayList<ProductInfo> getlist3;
    ArrayAdapter<ProductInfo> list;
    ArrayList<String> checked;
    ProductInfo2 checkNo;
    MyDBhelper helper;
    SQLiteDatabase db;
    int key;
    public class ProductInfo {
        private String mProductName;
        private String mProductID;
        private int mProductCount = 0;

        //建構子
        ProductInfo(final String productName,final String productID,final int productCount) {
            this.mProductName = productName;
            this.mProductID = productID;
            this.mProductCount = productCount;

        }


        //方法
        @Override
        public String toString() {
            return this.mProductName + "(" + this.mProductCount + ")" + "\n" + "(" + this.mProductID + ")";
        }
    }
    public class ProductInfo2 {
        private String mProductID;
        private int mProductCount = 0;

        //建構子
        ProductInfo2(final String productID, int productCount) {
            this.mProductID = productID;
            this.mProductCount = productCount;

        }

        //方法
        @Override
        public String toString() {
            //return "{\"ProductID\": \"" + this.mProductID + "\",\"Count\": " + this.mProductCount + "}";
            return this.mProductID + "(" + this.mProductCount + ")" ;
        }

    }
    public class ProductInfo3 {
        private String mProductID;
        private int mProductCount = 0;

        //建構子
        ProductInfo3(final String productID, int productCount) {
            this.mProductID = productID;
            this.mProductCount = productCount;

        }

        //方法
        @Override
        public String toString() {
            return "{\"ProductID\": \"" + this.mProductID + "\",\"Count\": " + this.mProductCount + "}";
            //return this.mProductID + "(" + this.mProductCount + ")" ;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_things);



        helper = new MyDBhelper(this, "tblTable", null, 1);
        db = helper.getWritableDatabase();

        Intent intent = getIntent();
        //取得Bundle物件後 再一一取得資料
        Bundle bag = intent.getExtras();
        cUserName = bag.getString("cUserName", null);
        mLackNo = bag.getString("mLackNo", null);
        mLackName = bag.getString("mLackName", null);

        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");

        TextView textView1 = (TextView) findViewById(R.id.textView24);
        textView1.setText(mLackNo);

        TextView textView2 = (TextView) findViewById(R.id.textView7);
        textView2.setText(mLackName);

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
                Intent intent = new Intent(ThingsActivity.this, AddThingsActivity.class);
                Bundle bag = new Bundle();
                bag.putString("cUserName", cUserName);
                intent.putExtras(bag);
                startActivity(intent);
                ThingsActivity.this.finish();
            }

        });
        PassList passList = new PassList();
        passList.start();
    }

    class PassList extends Thread {
        String cProductName;
        public void run() {
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"detail\",\"LackNo\":\"" + mLackNo + "\"}";
            Log.e("JSON", json);
            RequestBody body = RequestBody.create(JSON, json);
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
                    Log.e("取得list的網址", response.toString());
                    Log.e("取得的list", json);
                    
                    //解析 JSON
                    //建立一個ArrayListList
                    getlist = new ArrayList<>();
                    getlist2 = new ArrayList<>();
                    //getlist3 = new ArrayList<>();
                    try {
                        //取出LackList的陣列
                        JSONObject j = new JSONObject(json);
                        JSONArray array = j.getJSONArray("LackList");
                        Log.e("ARRAY", String.valueOf(array));
                        JSONArray array1 = array.getJSONObject(0).getJSONArray("LackProduct");
                            //把取出的物件 放進ARRAYLIS

                        for (int i = 0; i < array1.length(); i++)
                        {
                            JSONObject obj = array1.getJSONObject(i);

                            //把取出的物件 放進ARRAYLIS
                            //getlist.add(new ProductInfo2 (obj.optString("ProductID"),obj.optInt("Count")));
                            getlist2.add(new ProductInfo3 (obj.optString("ProductID"),obj.optInt("Count")));


                            Cursor c = db.query("tblTable",                            // 資料表名字
                                    null,                                              // 要取出的欄位資料
                                    "cProductID=?",                                    // 查詢條件式(WHERE)
                                    new String[]{obj.optString("ProductID")},                           // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                                    null,                                              // Group By字串語法
                                    null,                                              // Having字串法
                                    null);                                             // Order By字串語法(排序)

                            while (c.moveToNext()) {
                                cProductName = c.getString(c.getColumnIndex("cProductName"));
                                Log.e("cProductName", cProductName);
                            }
                            getlist.add(new ProductInfo(cProductName,obj.optString("ProductID"),obj.optInt("Count")));
                            getListView(getlist);
                            Log.e("getlist3", String.valueOf(getlist));

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
                ThingsActivity.this,
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
                        //checkNo =(ProductInfo2)adapter.getItem(key);
                        //checked.add(checkNo.mProductID);
                        Log.e("點擊", String.valueOf(key));

                    }
                }
            }
        });

    }

    public void onDel(View v){
        listDel();
    }
    private void listDel(){
        Log.e("key", String.valueOf(key));
        getlist.remove(key);
        getlist2.remove(key);
        final ListView listView = (ListView)findViewById(R.id.list);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        list = new ArrayAdapter<>(
                ThingsActivity.this,
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

    }
    public void onSave (View v){
        ListSave listSave = new ListSave();
        listSave.start();

    }
    class ListSave extends Thread {
        @Override
        public void run() {
            Log.e("LackNo21", mLackNo);
            Log.e("LackNo31", mLackName);

            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\n" +
                    "  \"Token\": \"\",\n" +
                    "  \"Action\": \"update\",\n" +
                    "  \"UserID\": \"test\",\n" +
                    "  \"LackInfo\": {\n" +
                    "    \"LackNo\": \"" + mLackNo + "\",\n" +
                    "    \"LackName\": \"" + mLackName + "\",\n" +
                    "    \"LackProduct\": " + getlist2 + "\n" +
                    "  }\n" +
                    "}";
            Log.e("JSONIIII", json);
            RequestBody body = RequestBody.create(JSON, json);
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

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    Log.e("OkHttp", response.toString());
                    Log.e("OkHttp2", json);
                }
            });
        }
    }
    public void onAdd (View v){
        listAdd();
    }
    private void listAdd() {
        String cProductName2 = null;
        EditText edit = (EditText) findViewById(R.id.editText3);
        String editList = edit.getText().toString();
        final EditText edit2 = (EditText) findViewById(R.id.editText8);

        int editCount = 0;
        if (edit2.length() != 0) {
            editCount = Integer.parseInt(edit2.getText().toString());
        }



        Cursor c = db.query("tblTable",                            // 資料表名字
                null,                                              // 要取出的欄位資料
                "cProductID=?",                                    // 查詢條件式(WHERE)
                new String[]{editList},                           // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                null,                                              // Group By字串語法
                null,                                              // Having字串法
                null);                                             // Order By字串語法(排序)

        while (c.moveToNext()) {
            cProductName2 = c.getString(c.getColumnIndex("cProductName"));
            Log.e("cProductName", cProductName2);
        }
        final ProductInfo product = getProduct(editList);
        final ProductInfo3 product2 = getProduct2(editList);
        if (cProductName2 != null ) {
            Log.e("TRANSADD", String.valueOf(getlist));
            Log.e ("getlist", "YES");
            //getlist.add(new ProductInfo(cProductName2, editList, editCount));
            //getlist2.add(new ProductInfo3(editList, editCount));
            if (product != null) {
                product.mProductCount=product.mProductCount+editCount;
                product2.mProductCount=product.mProductCount;
                list.notifyDataSetChanged();
            }else {
                getlist.add(new ProductInfo(cProductName2, editList, editCount));
                getlist2.add(new ProductInfo3(editList, editCount));
            }

            final ListView listView = (ListView)findViewById(R.id.list);
            listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            list = new ArrayAdapter<>(
                    ThingsActivity.this,
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

        }

    }
    private ProductInfo getProduct(final String key)
    {
        if (TextUtils.isEmpty(key))
            return null;
        for (int index = 0; index < getlist.size(); index++)
        {
            ProductInfo product = getlist.get(index);
            if (product.mProductID.equals(key))
                return product;
        }
        return null;
    }
    private ProductInfo3 getProduct2(final String key)
    {
        if (TextUtils.isEmpty(key))
            return null;
        for (int index = 0; index < getlist2.size(); index++)
        {
            ProductInfo3 product = getlist2.get(index);
            if (product.mProductID.equals(key))
                return product;
        }
        return null;
    }
}