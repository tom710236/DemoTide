package com.example.tom.demotide;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ThingsActivity extends AppCompatActivity {
    String trans,LackNo2,LackNo3;
    MyDBhelper helper;
    SQLiteDatabase db;
    String cProductName,cUserName;
    int Count,key;
    ArrayList<ProductInfo> transAdd;
    ArrayList<ProductInfo2> transAdd2;
    ArrayList<ProductInfo> checked;
    ArrayList<ProductInfo2> checked2;
    ArrayAdapter<ProductInfo> list;
    String newjson;
    String url="http://demo.shinda.com.tw/ModernWebApi/LackAPI.aspx";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_things);

        helper = new MyDBhelper(this, "tblTable", null, 1);
        db = helper.getWritableDatabase();

        Intent intent = getIntent();
        Bundle bag = intent.getExtras();
        trans = bag.getString("trans", null);
        LackNo2 = bag.getString("LackNo2",null);
        LackNo3 = bag.getString("LackNo3",null);
        Log.e("TRANS", trans);
        Log.e("LackNo2", LackNo2);
        Log.e("LackNo3", LackNo3);
        parseJson();

        TextView textview = (TextView)findViewById(R.id.textView24);
        TextView textview2 = (TextView)findViewById(R.id.textView7);
        textview.setText(LackNo2);
        textview2.setText(LackNo3);


        Intent intent3 = getIntent();
        //取得Bundle物件後 再一一取得資料
        Bundle bag3 = intent3.getExtras();
        cUserName = bag3.getString("cUserName", null);

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
                Intent intent = new Intent(ThingsActivity.this, AddThingsActivity.class);
                Bundle bag = new Bundle();
                bag.putString("cUserName", cUserName);
                intent.putExtras(bag);
                startActivity(intent);
                ThingsActivity.this.finish();
            }
        });
    }

    private void parseJson() {
        //取值
        try {
            //建立一個ArrayList
            transAdd = new ArrayList<>();
            transAdd2 = new ArrayList<>();
            //建立一個JSONArray 並把POST回傳資料json(JSOM檔)帶入
            JSONArray array = new JSONArray(trans);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String ProductID = obj.getString("ProductID");
                Count = obj.getInt("Count");
                Log.e("ProductID", ProductID);
                Log.e("Count", String.valueOf(Count));
                //SQL 比對cProductID 取出相同cProductID的cProductName
                Cursor c = db.query("tblTable",                            // 資料表名字
                        null,                                              // 要取出的欄位資料
                        "cProductID=?",                                    // 查詢條件式(WHERE)
                        new String[]{ProductID},                           // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                        null,                                              // Group By字串語法
                        null,                                              // Having字串法
                        null);                                             // Order By字串語法(排序)

                while(c.moveToNext()) {
                    cProductName = c.getString(c.getColumnIndex("cProductName"));
                    Log.e("cProductName",cProductName);
                }
                transAdd.add(new ProductInfo(cProductName , ProductID ,Count));
                Log.e("TRANSADD", String.valueOf(transAdd));
                transAdd2.add(new ProductInfo2(ProductID,Count));


            }



        } catch (JSONException e) {
            e.printStackTrace();
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
        list = new ArrayAdapter<>(
                ThingsActivity.this,
                android.R.layout.simple_list_item_multiple_choice,
                transAdd);
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
                checked = new ArrayList<ProductInfo>(list.getCheckedItemCount());
                //checked2 = new ArrayList<ProductInfo2>(list.getCheckedItemCount());
                for (int i = 0; i < array.size(); i++) {
                    key= array.keyAt(i);
                    Log.e("KET", String.valueOf(key));
                    if (array.get(key)) {
                        Log.e("KET2", String.valueOf(key));
                        checked.add((ProductInfo) adapter.getItem(key));
                        //checked2.add((ProductInfo2) adapter.getItem(key));
                        Log.e("CHECK", String.valueOf(checked));
                        //Log.e("CHECK2", String.valueOf(checked2));
                    }
                }
            }
        });
    }


    public class ProductInfo {
        private String mProductName;
        private String mProductID;
        private int mProductCount=0;

        //建構子
        ProductInfo(final String productName, final String productID, int productCount) {
            this.mProductName = productName;
            this.mProductID = productID;
            this.mProductCount = productCount;

        }

        //方法
        @Override
        public String toString() {
            return this.mProductName + "("+this.mProductCount+")" +"\n"+ "(" + this.mProductID + ")";
        }
    }
    public class ProductInfo2 {
        private String mProductID;
        private int mProductCount=0;

        //建構子
        ProductInfo2(final String productID, int productCount) {
            this.mProductID = productID;
            this.mProductCount = productCount;

        }

        //方法
        @Override
        public String toString() {
            return "{\"ProductID\": \""+this.mProductID+"\",\"Count\": "+this.mProductCount+"}";
        }
    }

    //型別(String 之纇的),方法名稱
    @Nullable
    private ProductInfo getProduct(final String key)
    {
        if (TextUtils.isEmpty(key))
            return null;
        for (int index = 0; index < transAdd.size(); index++)
        {
            ProductInfo product = transAdd.get(index);
            if (product.mProductID.equals(key))
                return product;
        }
        return null;
    }
    public void onAdd (View v){
        Log.e("CHECK1111", String.valueOf(transAdd));
        EditText text = (EditText)findViewById(R.id.editText3);
        EditText num = (EditText)findViewById(R.id.editText8);
        final String UserEnterKey = text.getText().toString();
        //final String UserNum2 =num.getText().toString();
        final String UserNum = num.getText().toString();
        final ProductInfo product = getProduct(UserEnterKey);
        final ProductInfo2 product2 = getProduct2(UserEnterKey);
        if(UserNum.length()!=0){
            if(product != null){
                product.mProductCount= product.mProductCount+Integer.parseInt(UserNum);
                product2.mProductCount = product.mProductCount;
                Log.e("product.mProductCount", String.valueOf(product.mProductCount));
                list.notifyDataSetChanged();
                Log.e("CHECK222", String.valueOf(transAdd));
                Log.e("transAdd2", String.valueOf(transAdd2));
            }else {
                Toast.makeText(ThingsActivity.this,"請輸入正確商品條碼或數量", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(ThingsActivity.this,"請輸入正確商品條碼或數量", Toast.LENGTH_SHORT).show();
        }


    }
    class PassList extends Thread {
        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = newjson;
            Log.e("JSON",json);
            RequestBody body = RequestBody.create(JSON,json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Log.e("UP", body.toString());
            //使用OkHttp的newCall方法建立一個呼叫物件(尚未連線至主機)
            okhttp3.Call call = client.newCall(request);
            //呼叫call類別的enqueue進行排程連線(連線至主機)
            call.enqueue(new Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {

                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    String json = response.body().string();
                    Log.e("OkHttp3", response.toString());
                    Log.e("OkHttp4", json);
                }

            });

        }
    }
    public void onClick (View v){
        Log.e("TRANS1111", String.valueOf(transAdd));
        Log.e("transAdd22", String.valueOf(transAdd2));

        newjson = "{\n" +
                "  \"Token\": \"\",\n" +
                "  \"Action\": \"update\",\n" +
                "  \"UserID\": \"test\",\n" +
                "  \"LackInfo\": {\n" +
                "    \"LackNo\": \"L0001\",\n" +
                "    \"LackName\": \"儲位1\",\n" +
                "    \"LackProduct\": "+transAdd2+"\n" +
                "  }\n" +
                "}";
        PassList passList = new PassList();
        passList.start();
        list.notifyDataSetChanged();
    }
    public void onDel(View v){
        checked.remove(key);
        Log.e("CHECKEDYY", String.valueOf(checked));
        list.notifyDataSetChanged();
    }
    //型別(String 之纇的),方法名稱
    @Nullable
    private ProductInfo2 getProduct2(final String key)
    {
        if (TextUtils.isEmpty(key))
            return null;
        for (int index = 0; index < transAdd2.size(); index++)
        {
            ProductInfo2 product2 = transAdd2.get(index);
            if (product2.mProductID.equals(key))
                return product2;
        }
        return null;
    }
}