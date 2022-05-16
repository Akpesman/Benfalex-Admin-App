package com.cleantec.benfalexadmin.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.cleantec.benfalexadmin.R;

public class Splash extends AppCompatActivity {

    SharedPreferences sharedPref;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        sharedPref=getSharedPreferences("admin",MODE_PRIVATE);
        userId=sharedPref.getString("adminId","");


        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    if(userId.length()<2){
                        Intent intent = new Intent(Splash.this,Login.class);
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(Splash.this,MainActivity.class);
                        startActivity(intent);
                    }

                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}