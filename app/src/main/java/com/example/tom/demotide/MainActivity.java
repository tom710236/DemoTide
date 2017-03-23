package com.example.tom.demotide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String cStatus, userName, passWord, cUserName;
    String url = "http://demo.shinda.com.tw/ModernWebApi/WebApiLogin.aspx";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences setting =
                getSharedPreferences("Login", MODE_PRIVATE);
        EditText uId = (EditText) findViewById(R.id.userName);
        uId.setText(setting.getString("userName", ""));
    }

    public void login(View v) {
        Pass pass = new Pass();
        pass.start();
    }

    class Pass extends Thread {
        @Override
        public void run() {
            //輸入帳號密碼
            final EditText uId = (EditText) findViewById(R.id.userName);
            EditText uPw = (EditText) findViewById(R.id.passWord);
            userName = uId.getText().toString();
            passWord = uPw.getText().toString();

            final OkHttpClient client = new OkHttpClient();
            //要上傳的內容(JSON)--帳號登入
            RequestBody body = new FormBody.Builder()
                    //.add("postdata", "{\"cAccount\":\"carlos\",\"cPassword\":\"123\"}")
                    .add("postdata", "{\"cAccount\":\"" + userName + "\",\"cPassword\":\"" + passWord + "\"}")
                    .build();
            //來設定一個連線必要的資訊
            final Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Log.e("UP", body.toString());
            //使用OkHttp的newCall方法建立一個呼叫物件(尚未連線至主機)
            Call call = client.newCall(request);
            //呼叫call類別的enqueue進行排程連線(連線至主機)
            call.enqueue(new Callback() {

                //post 失敗後執行
                @Override
                public void onFailure(Call call, IOException e) {

                }

                //post 成功後執行
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //取得回傳資料json 還是JSON檔
                    // {"cStatus":"1","cUserID":"S000000001","cUserName":"卡肉絲       ","cMsg":"成功"}
                    String json = response.body().string();
                    Log.e("OkHttp", response.toString());
                    Log.e("OkHttp2", json);
                    //所要執行的方法
                    parseJson(json);
                }

                //方法
                public void parseJson(String json) {


                    try {
                        //從回傳資料json 抓取cStatus項目裡的內容
                        cStatus = new JSONObject(json).getString("cStatus");
                        //從回傳資料json 抓取cUserName項目內的內容
                        cUserName = new JSONObject(json).getString("cUserName");
                        Log.e("JSOM", cStatus);
                        //回傳的cStatus為1時 登入成功
                        if (cStatus.equals("1")) {
                            //到另一頁 用Bundle把所需資料帶到另一頁
                            Intent intent = new Intent(MainActivity.this, SystemActivity.class);
                            Bundle bag = new Bundle();
                            bag.putString("cUserName", cUserName);
                            intent.putExtras(bag);
                            startActivity(intent);
                            MainActivity.this.finish();
                            //記住帳號
                            SharedPreferences setting =
                                    getSharedPreferences("Login", MODE_PRIVATE);
                            setting.edit()
                                    .putString("userName", userName)
                                    .commit();


                        } else {
                            //非主執行緒顯示UI(Toast)
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "登入失敗 請重新輸入", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }


                    } catch (JSONException e) {

                        e.printStackTrace();
                    }


                }


            });

        }
    }
}
