package com.example.tom.demotide;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by TOM on 2017/3/21.
 */

public class Delay extends Service {
    //宣告
    Runnable runnable;
    Handler handler;
    String cUserName;
    String url = "http://demo.shinda.com.tw/ModernWebApi/getProduct.aspx";
    ArrayList<ProductInfo> trans;
    private MyDBhelper helper;
    SQLiteDatabase db,db2;
    final String DB_NAME = "tblTable";
    ListView listView;
    ContentValues addbase;
    String ID,name,NO,DT;
    String today,today2;
    //建構子
    public Delay(){

    }
    // startServicec後
    // 每五秒就執行Log.e("delay","timeDelay");和makeNotification();
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        handler = new Handler();
        final String timeUp = intent.getStringExtra("timeUp");
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                timeUp();
                Log.e("today",today);
                Log.e("timeUp",timeUp);
                if(today.equals(timeUp)){
                    helper = new MyDBhelper(Delay.this, DB_NAME, null, 1);
                    //實做 db(繼承SQLiteDatabase)類別 getWritableDatabase用來更新 新增修改刪除
                    db = helper.getWritableDatabase();
                    db.delete(DB_NAME, null, null);
                    //放入新增表格
                    Get get = new Get();
                    get.start();
                    upDateTimes();


                }
                handler.postAtTime(this,android.os.SystemClock.uptimeMillis()+1000);
            }
        };
        handler.postAtTime(runnable,android.os.SystemClock.uptimeMillis()+1000);
        //return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }
    //stopService後
    //執行Log.e("STOP","STOP") ,handler.removeCallbacks(runnable);
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("STOP","STOP");
        handler.removeCallbacks(runnable);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public class ProductInfo {
        private String cProductID;
        private String cProductName;
        private String cGoodsNo;
        private String cUpdateDT;

        //建構子
        ProductInfo(final String ProductID, final String ProductName, final String GoodsNo,final String UpdateDT) {
            this.cProductID = ProductID;
            this.cProductName = ProductName;
            this.cGoodsNo = GoodsNo;
            this.cUpdateDT = UpdateDT;

        }
        //方法
        @Override
        public String toString() {
            return this.cProductID +  this.cProductName  + this.cGoodsNo + this.cUpdateDT;
        }
    }
    class Get extends Thread {
        @Override
        public void run() {
            okHttpGet();
        }
    }
    // Get
    private void okHttpGet(){
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }
            //把get到的資料(JSON)轉為字串 並執行parseJson方法
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.e("json",json);
                parseJson(json);
            }
        });

    }
    //把json的資料解析出來 並放入SQL裡
    private void parseJson(String json) {

        try {
            //解析JSON資料
            trans = new ArrayList<ProductInfo>();
            final JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                trans.add(new ProductInfo(obj.optString("cProductID"), obj.optString("cProductName"),obj.optString("cGoodsNo"),obj.optString("cUpdateDT")));
                ID = obj.optString("cProductID");
                name = obj.optString("cProductName");
                NO = obj.optString("cGoodsNo");
                DT = obj.optString("cUpdateDT");
                //放入SQL
                addbase = new ContentValues();
                addbase.put("cProductID", ID);
                addbase.put("cProductName", name);
                addbase.put("cGoodsNo", NO);
                addbase.put("cUpdateDT", DT);
                db.insert(DB_NAME, null, addbase);
            }



        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }
    private void timeUp(){
        Calendar mCal = Calendar.getInstance();
        String dateformat = "HH:mm:ss";
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        today = df.format(mCal.getTime());
    }
    private void upDateTimes(){
        MyDBhelper2 MyDB2 = new MyDBhelper2(this,"tblOrder2",null,1);
        db2=MyDB2.getWritableDatabase();
        ContentValues addbase = new ContentValues();
        time();
        addbase.put("cUpdateDT",today2);
        db2.insert("tblTable2",null,addbase);
    }
    private void time(){
        Calendar mCal = Calendar.getInstance();
        String dateformat = "yyyy/MM/dd/ HH:mm:ss";
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        today2 = df.format(mCal.getTime());
    }

}
