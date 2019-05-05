package com.example.qichaoqun.mobileplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.example.qichaoqun.mobileplayer.R;

public class SplashActivity extends Activity {

    Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toMainActivity();
            }
        },2000);
    }

    /**
     * 当启动两秒之后跳转到主页面
     */

    private void toMainActivity(){
        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
