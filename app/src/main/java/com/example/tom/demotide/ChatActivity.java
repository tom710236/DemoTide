package com.example.tom.demotide;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //點擊推播的Activity後執行
        //初始化Notification Manager
        NotificationManager gNotMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //清除所有的通知內容
        gNotMgr.cancelAll();
    }
    public void onClick (View v){
        Intent intent = new Intent(ChatActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
