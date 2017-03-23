package com.example.tom.demotide;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by TOM on 2017/3/16.
 * 執行APP後，當RegistrationIntentService啟動後，取得InstanceID的token值
 */

public class RegistrationIntentService extends IntentService {
    public RegistrationIntentService(){
        super("RegistrationIntentService");
    }


    String token,IMEI;
    String url="http://demo.shinda.com.tw/ModernWebApi/AddDevice.aspx";
    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            // res/values/strings.xml String resource，建立名稱叫gcm_sender_id的字串，將GCM申請的Sender id填進去
            token = instanceID.getToken(getString(R.string.gcm_sender_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d("MyInstanceID", token);

            //service 顯示toast
            /*
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), token, Toast.LENGTH_LONG).show();
                }});
            */
            //執行執行緒
            //pass pass = new pass();
            //pass.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //執行緒 POST IMEI 和 token
    class pass extends Thread {
        @Override
        public void run() {
            /**
             * . 首先要取得手機硬體資訊與通訊資訊必須在AndroidManifest.xml加入權限 :
             *<uses-permission android:name="android.permission.READ_PHONE_STATE" />
             */
            TelephonyManager mTelManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = mTelManager.getDeviceId();

            final OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("postdata", "{\"cIMEI\":\"" + IMEI + "\",\"cGCMCode\":\"" + token + "\"}")
                    .build();
            final Request request = new Request.Builder()
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
                    String json = response.body().string();
                    Log.e("OkHttp", response.toString());
                    Log.e("OkHttp2", json);

                }

            });

        }
    }

}

