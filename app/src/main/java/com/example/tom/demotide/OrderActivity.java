package com.example.tom.demotide;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderActivity extends AppCompatActivity {
    String check, door1, cUserName;
    String url = "http://demo.shinda.com.tw/ModernWebApi/WebApi.aspx";
    String check2 = null;
    String check3 = null;
    String json;
    int addNum=0;

    LinearLayout linear;
    ArrayAdapter<ProductInfo> list;
    ArrayList<ProductInfo> trans;

    OkHttpClient client = new OkHttpClient();

    EditText editText;
    ListView listView;
    //把JSON 類別化
    public class ProductInfo {
        private String mProductName;
        private String mProductID;
        private int mProductCount=0;
        private int mStocks = 0;
        //建構子
        ProductInfo(final String productName, final String productID, int productCount, int cShippersCountEd) {
            this.mProductName = productName;
            this.mProductID = productID;
            this.mProductCount = productCount;

        }
        //方法
        @Override
        public String toString() {
            return this.mProductName + "(" + this.mProductID + ") " + this.mProductCount + " (" + this.mStocks + ")";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //接收上一頁的資料
        Intent intent = getIntent();
        Bundle bag = intent.getExtras();
        check = bag.getString("checked", null);
        cUserName = bag.getString("cUserName", null);
        door1 = bag.getString("order", null);
        Log.e("check", check);
        //把check的JSON去中框號和中間空白處(變成check3) 才能POST
        //先取得字串的長度
        int i = check.length();
        //再取字串範圍 (0和最後是[])
        //取代
        check2 = check.substring(1, i - 1);
        //check3 = check2.replaceAll(", ", ",");
        check3 = check2.replaceAll(", ", ",");
        Log.e("check3", "check3: " + check3);

        TextView orderName = (TextView) findViewById(R.id.textView11);
        //把上一頁傳過來的door用TextView顯示
        orderName.setText(door1);
        Log.e("CHECK11", check2);
        Log.e("DOOR", door1);
        //設定Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //設定回到上一頁
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, OutActivity.class);
                Bundle bag = new Bundle();
                bag.putString("cUserName", cUserName);
                intent.putExtras(bag);
                startActivity(intent);
                OrderActivity.this.finish();
            }
        });

        //執行緒
        new Thread(new Runnable() {
            @Override
            public void run() {
                postjson();
            }
        }).start();
        editText = (EditText) findViewById(R.id.editText);
        trans = new ArrayList<ProductInfo>();
        listView = (ListView) findViewById(R.id.list);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        list = new ArrayAdapter<>(OrderActivity.this, android.R.layout.simple_list_item_activated_1, trans);
        //switch 設定
        Switch sw = (Switch)findViewById(R.id.switch2);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //switch 點擊
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //點擊後 lineat就會顯示並且addNum=1
                if(isChecked){
                    linear = (LinearLayout)findViewById(R.id.linear);
                    linear.setVisibility(View.VISIBLE);
                    addNum=1;
                }
                //沒有點擊 addNum=0
                else{
                    linear.setVisibility(View.INVISIBLE);
                    addNum=0;
                }

            }
        });


    }


    public void add1 (View v){
        addNum=1;
    }
    public void add5 (View v){
        addNum=5;
    }
    public void add10 (View v){
        addNum=10;
    }
    public void addAll (View v) {
        addNum=99999;
    }


    //執行執行緒的方法
    private void postjson() {
        //post
        RequestBody body = new FormBody.Builder()
                .add("postdata", "{ ApiName: \"GetShippersD\", ApiID: \"" + check3 + "\"}")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //POST後 回傳的JSON檔
                json = response.body().string();
                Log.e("json",json);
                Log.e("OkHttp10", response.toString());
                Log.e("OkHttp11", json);
                parseJson2(json);

            }
        });
    }

    //取出回傳後JSON的值
    private void parseJson2(String json) {
        try {
            final JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++)
            {
                JSONObject obj = array.getJSONObject(i);
                //用自訂類別 把JSONArray的值取出來
                trans.add(new ProductInfo(obj.optString("cProductName"), obj.optString("cProductID"),obj.optInt("cShippersCount"),obj.optInt("cShippersCountEd")));
                Log.e("trans", String.valueOf(trans));
            }
            //顯示listView(JSONArray的值)
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    listView.setAdapter(list);
                    Log.e("list", String.valueOf(list));
                }
            });

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }

    public void enter(View v)
    {   // 輸入的商品條碼的值 並得到他
        final String UserEnterKey = editText.getText().toString();
        //類別
        final ProductInfo product = getProduct(UserEnterKey);

        if (product != null && product.mStocks>=0)
        {
            if(addNum==1){
                if(product.mStocks>product.mProductCount||product.mStocks+addNum>product.mProductCount){
                    product.mStocks=product.mProductCount;
                    list.notifyDataSetChanged();
                    Toast.makeText(OrderActivity.this,"數量已滿", Toast.LENGTH_SHORT).show();
                }else{
                    product.mStocks=product.mStocks+addNum;
                    list.notifyDataSetChanged();
                }

            }
            else if(addNum==5){
                if(product.mStocks>product.mProductCount||product.mStocks+addNum>product.mProductCount){
                    product.mStocks=product.mProductCount;
                    list.notifyDataSetChanged();
                    Toast.makeText(OrderActivity.this,"數量已滿", Toast.LENGTH_SHORT).show();
                }else{
                    product.mStocks=product.mStocks+addNum;
                    list.notifyDataSetChanged();
                }
            }
            else if(addNum==10){
                if(product.mStocks>product.mProductCount||product.mStocks+addNum>product.mProductCount){
                    product.mStocks=product.mProductCount;
                    list.notifyDataSetChanged();
                    Toast.makeText(OrderActivity.this,"數量已滿", Toast.LENGTH_SHORT).show();
                }else{
                    product.mStocks=product.mStocks+addNum;
                    list.notifyDataSetChanged();
                }
            }
            else if(addNum==99999){
                if(product.mStocks>product.mProductCount||product.mStocks+addNum>product.mProductCount){
                    product.mStocks=product.mProductCount;
                    list.notifyDataSetChanged();
                    Toast.makeText(OrderActivity.this,"數量已滿", Toast.LENGTH_SHORT).show();
                }else{
                    product.mStocks=product.mStocks+addNum;
                    list.notifyDataSetChanged();
                }
            }
            else if(addNum==0){

                final View item = LayoutInflater.from(OrderActivity.this).inflate(R.layout.activity_alertdialog, null);
                new AlertDialog.Builder(OrderActivity.this)
                        .setTitle("請輸入數量")
                        .setView(item)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) item.findViewById(R.id.editText2);
                                if(editText.length()!=0){
                                    int addnum = Integer.parseInt(editText.getText().toString());
                                    if(addnum >product.mProductCount||product.mStocks+addnum>product.mProductCount||product.mStocks>product.mProductCount){
                                        Log.e("addnum", String.valueOf(addnum));
                                        product.mStocks=product.mProductCount;
                                        list.notifyDataSetChanged();
                                        Toast.makeText(OrderActivity.this,"數量已滿", Toast.LENGTH_SHORT).show();

                                    }else{
                                        product.mStocks=product.mStocks+addnum;
                                        list.notifyDataSetChanged();
                                    }
                                }else{
                                    Toast.makeText(OrderActivity.this,"請輸入數量", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }).show();
            }

        } else
        {
            Toast.makeText(OrderActivity.this,"請輸入正確商品條碼", Toast.LENGTH_SHORT).show();
        }
    }

    private ProductInfo getProduct(final String key)
    {
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

    }

