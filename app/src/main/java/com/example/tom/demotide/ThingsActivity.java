package com.example.tom.demotide;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;

public class ThingsActivity extends AppCompatActivity {
    String trans,LackNo2,LackNo3;
    MyDBhelper helper;
    SQLiteDatabase db;
    String cProductName;
    int Count;
    ArrayList<ProductInfo> transAdd;
    ArrayList<ProductInfo> checked;
    ArrayAdapter<ProductInfo> list;


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
    }

    private void parseJson() {
        //取值
        try {
            //建立一個ArrayList
            transAdd = new ArrayList<>();
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
        // 會當機
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                AbsListView list = (AbsListView)adapterView;
                Adapter adapter = list.getAdapter();
                SparseBooleanArray array = list.getCheckedItemPositions();
                checked = new ArrayList<ProductInfo>(list.getCheckedItemCount());
                for (int i = 0; i < array.size(); i++) {
                    int key = array.keyAt(i);
                    if (array.get(key)) {
                        checked.add((ProductInfo) adapter.getItem(key));
                        Log.e("CHECK", String.valueOf(checked));

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
    public void onClick (View v){

        EditText text = (EditText)findViewById(R.id.editText3);
        EditText num = (EditText)findViewById(R.id.editText8);
        final String UserEnterKey = text.getText().toString();
        //final String UserNum2 =num.getText().toString();
        final String UserNum = num.getText().toString();
        final ProductInfo product = getProduct(UserEnterKey);
        if(UserNum.length()!=0){
            if(product != null){
                product.mProductCount= product.mProductCount+Integer.parseInt(UserNum);
                Log.e("product.mProductCount", String.valueOf(product.mProductCount));
                list.notifyDataSetChanged();

            }else {
                Toast.makeText(ThingsActivity.this,"請輸入正確商品條碼或數量", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(ThingsActivity.this,"請輸入正確商品條碼或數量", Toast.LENGTH_SHORT).show();
        }

        Log.e("TRAINADDD", String.valueOf(transAdd));

//
    }

}