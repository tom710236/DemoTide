package com.example.tom.demotide;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

public class OrderThingActivity extends AppCompatActivity {
    String json5;
    String cUserName,name;
    int addNum = 0;
    ArrayList<String> json2;
    String url = "http://demo.shinda.com.tw/ModernWebApi/Purchase.aspx";

    LinearLayout linear;
    ArrayAdapter<ProductInfo> list;
    ArrayList<ProductInfo> trans;
    ArrayList<ProductInfo2> trans2;

    EditText editText;
    ListView listView;
    String jsonEnd;
    Object jsonEnd2;

    public class ProductInfo {
        private String mProductID;
        private int mQty = 0;
        private int mNowQty = 0;

        //建構子
        ProductInfo(final String ProductID, final int Qty, int NowQty) {
            this.mProductID = ProductID;
            this.mQty = Qty;
            this.mNowQty = NowQty;

        }

        //方法
        @Override
        public String toString() {
            return this.mProductID + "(" + this.mQty + ") " + this.mNowQty;
        }
    }

    public class ProductInfo2 {
        private String mProductID;
        private int mNowQty = 0;

        //建構子
        ProductInfo2(final String ProductID, int NowQty) {
            this.mProductID = ProductID;
            this.mNowQty = NowQty;

        }

        //方法
        @Override
        public String toString() {
            return "{\"ProductNo\":\"" + this.mProductID + "\",\"NowQty\":" + this.mNowQty + "}";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_thing);

        trans = new ArrayList<ProductInfo>();
        trans2 = new ArrayList<>();
        //上一頁傳過來的資料取得
        Intent intent = getIntent();
        //取得Bundle物件後 再一一取得資料
        Bundle bag = intent.getExtras();
        cUserName = bag.getString("cUserName", null);
        json5 = bag.getString("json5", json5);
        name = bag.getString("name", name);
        Log.e("json52", json5);
        Log.e("name",name);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");
        TextView textView1 = (TextView)findViewById(R.id.textView11);
        textView1.setText(name);

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
                Intent intent = new Intent(OrderThingActivity.this, SystemActivity.class);
                Bundle bag = new Bundle();
                bag.putString("cUserName", cUserName);
                intent.putExtras(bag);
                startActivity(intent);
                OrderThingActivity.this.finish();
            }
        });
        parseJson2(json5);


        //switch 設定
        Switch sw = (Switch) findViewById(R.id.switch2);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //switch 點擊
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //點擊後 lineat就會顯示並且addNum=1
                if (isChecked) {
                    linear = (LinearLayout) findViewById(R.id.linear);
                    linear.setVisibility(View.VISIBLE);
                    addNum = 1;
                }
                //沒有點擊 addNum=0
                else {
                    linear.setVisibility(View.INVISIBLE);
                    addNum = 0;
                }

            }
        });


    }


    public void add1(View v) {
        addNum = 1;
    }

    public void add5(View v) {
        addNum = 5;
    }

    public void add10(View v) {
        addNum = 10;
    }

    public void addAll(View v) {
        addNum = 99999;
    }

    private void parseJson2(String json5) {
        json2 = new ArrayList<String>();
        try {
            JSONObject j = new JSONObject(json5);
            for (int i = 0; i < j.getJSONArray("PurchaseProducts").length(); i++) {
                String json0 = j.getJSONArray("PurchaseProducts").getString(i);
                Log.e("json0", json0);
                json2.add(json0);
                Log.e("json2", String.valueOf(json2));
            }
            parseJson3(String.valueOf(json2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void parseJson3(String json2) {
        try {
            trans = new ArrayList<ProductInfo>();
            trans2 = new ArrayList<>();
            final JSONArray array = new JSONArray(json2);
            for (int i = 0; i < array.length(); i++)
            {
                JSONObject obj = array.getJSONObject(i);
                //用自訂類別 把JSONArray的值取出來

                trans.add(new ProductInfo(obj.optString("ProductNo"), obj.optInt("Qty"),obj.optInt("NowQty")));
                trans2.add(new ProductInfo2(obj.optString("ProductNo"),obj.optInt("NowQty")));
                Log.e("trans", String.valueOf(trans));
                Log.e("trans2", String.valueOf(trans2));
            }
            listView = (ListView) findViewById(R.id.list);
            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            list = new ArrayAdapter<>(OrderThingActivity.this, android.R.layout.simple_list_item_activated_1, trans);
            listView.setAdapter(list);
            Log.e("list", String.valueOf(list));

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }
    public void enter (View v) {
        // 輸入的商品條碼的值 並得到他
        EditText editText=(EditText)findViewById(R.id.editText99);
        final String UserEnterKey = editText.getText().toString();
        Log.e("editView", UserEnterKey);
        //類別
        final ProductInfo product = getProduct(UserEnterKey);
        final ProductInfo2 product2 = getProduct2(UserEnterKey);
        if (product != null && product.mNowQty>=0)
        {
            if(addNum==1){
                if(product.mNowQty>product.mQty||product.mNowQty+addNum>product.mQty){
                    product.mNowQty=product.mQty;
                    product2.mNowQty=product.mNowQty;
                    list.notifyDataSetChanged();
                    Toast.makeText(OrderThingActivity.this,"數量已滿", Toast.LENGTH_SHORT).show();
                }else{
                    product.mNowQty=product.mNowQty+addNum;
                    product2.mNowQty=product.mNowQty;
                    list.notifyDataSetChanged();
                }

            }
            else if(addNum==5){
                if(product.mNowQty>product.mQty||product.mNowQty+addNum>product.mQty){
                    product.mNowQty=product.mQty;
                    product2.mNowQty=product.mNowQty;
                    list.notifyDataSetChanged();
                    Toast.makeText(OrderThingActivity.this,"數量已滿", Toast.LENGTH_SHORT).show();
                }else{
                    product.mNowQty=product.mNowQty+addNum;
                    product2.mNowQty=product.mNowQty;
                    list.notifyDataSetChanged();
                }
            }
            else if(addNum==10){
                if(product.mNowQty>product.mQty||product.mNowQty+addNum>product.mQty){
                    product.mNowQty=product.mQty;
                    product2.mNowQty=product.mNowQty;
                    list.notifyDataSetChanged();
                    Toast.makeText(OrderThingActivity.this,"數量已滿", Toast.LENGTH_SHORT).show();
                }else{
                    product.mNowQty=product.mNowQty+addNum;
                    product2.mNowQty=product.mNowQty;
                    list.notifyDataSetChanged();
                }
            }
            else if(addNum==99999){
                if(product.mNowQty>product.mQty||product.mNowQty+addNum>product.mQty){
                    product.mNowQty=product.mQty;
                    product2.mNowQty=product.mNowQty;
                    list.notifyDataSetChanged();
                    Toast.makeText(OrderThingActivity.this,"數量已滿", Toast.LENGTH_SHORT).show();
                }else{
                    product.mNowQty=product.mNowQty+addNum;
                    product2.mNowQty=product.mNowQty;
                    list.notifyDataSetChanged();
                }
            }
            else if(addNum==0){
                //對話框
                final View item = LayoutInflater.from(OrderThingActivity.this).inflate(R.layout.activity_alertdialog, null);
                new AlertDialog.Builder(OrderThingActivity.this)
                        .setTitle("請輸入數量")
                        .setView(item)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) item.findViewById(R.id.editText2);
                                if(editText.length()!=0){
                                    int addnum = Integer.parseInt(editText.getText().toString());
                                    if(addnum >product.mQty||product.mNowQty+addnum>product.mQty||product.mNowQty>product.mQty){
                                        Log.e("addnum", String.valueOf(addnum));
                                        product.mNowQty=product.mQty;
                                        product2.mNowQty=product.mNowQty;
                                        list.notifyDataSetChanged();
                                        Toast.makeText(OrderThingActivity.this,"數量已滿", Toast.LENGTH_SHORT).show();

                                    }else{
                                        product.mNowQty=product.mNowQty+addnum;
                                        product2.mNowQty=product.mNowQty;
                                        list.notifyDataSetChanged();
                                    }
                                }else{
                                    Toast.makeText(OrderThingActivity.this,"請輸入數量", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }).show();
            }

        } else
        {
            Toast.makeText(OrderThingActivity.this,"請輸入正確商品條碼", Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    private ProductInfo getProduct(final String key)
    {
        Log.e("transsssss", String.valueOf(trans));
        if (TextUtils.isEmpty(key))
            return null;
        for (int index = 0; index < trans.size(); index++)
        {
            ProductInfo product = trans.get(index);
            if (product.mProductID.equals(key))
                return product;
        }
        return null;
    }

    @Nullable
    private ProductInfo2 getProduct2(final String key)
    {
        Log.e("trans2oooo", String.valueOf(trans2));
        if (TextUtils.isEmpty(key))
            return null;
        for (int index = 0; index < trans2.size(); index++)
        {
            ProductInfo2 product2 = trans2.get(index);
            if (product2.mProductID.equals(key))
                return product2;
        }
        return null;
    }
    public void onChange(View v){
        Log.e("TRANS222", String.valueOf(trans2));
        PassChange passChange = new PassChange();
        passChange.start();

    }
    class PassChange extends Thread {

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"save\",\"PurchaseID\" :\"PU000000001\",\"PurchaseProducts\":"+trans2+"}";
            Log.e("JSON", json);
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
                    Log.e("OkHttp5", response.toString());
                    Log.e("OkHttp6", json);
                }
            });

        }

    }

    public void onEnd(View v){
        Log.e("TRANS222", String.valueOf(trans2));
        PassEnd passEnd = new PassEnd();
        passEnd.start();


    }
    class PassEnd extends Thread {

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"finish\",\"PurchaseID\" :\""+name+"\"}";
            Log.e("JSON", json);
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
                    jsonEnd= response.body().string();
                    jsonEnd2 = null;
                    try {
                        JSONObject j = new JSONObject(jsonEnd);
                        jsonEnd2 = j.getString("msg");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e("OkHttp5", response.toString());
                    Log.e("OkHttp6", String.valueOf(jsonEnd2));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OrderThingActivity.this,String.valueOf(jsonEnd2), Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            });

        }

    }

}


