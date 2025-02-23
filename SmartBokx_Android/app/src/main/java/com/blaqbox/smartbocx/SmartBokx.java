package com.blaqbox.smartbocx;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.blaqbox.smartbocx.backroom.DataConnector;
import com.blaqbox.smartbocx.utils.Constants;
import com.google.android.gms.ads.MobileAds;

import io.github.jan.supabase.gotrue.user.UserSession;

public class SmartBokx extends Application {
    DataConnector data_connector;
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("APPLICATION_CREATED","Application oncreate");
        new Thread(()->{
            MobileAds.initialize(this.getApplicationContext());

        }).start();

    }


}
