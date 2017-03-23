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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AddThingsActivity extends AppCompatActivity {
    String cUserName,lackNoAdd,lackNameAdd,tblTable4;
    SQLiteDatabase db4;
    String url="http://demo.shinda.com.tw/ModernWebApi/LackAPI.aspx";

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
    //POST JSON的方法
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
                    Log.e("OkHttp", response.toString());
                    Log.e("OkHttp2", json);
                }
            });
        }
    }




}