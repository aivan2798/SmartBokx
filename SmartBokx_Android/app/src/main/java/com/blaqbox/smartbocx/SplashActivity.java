package com.blaqbox.smartbocx;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.blaqbox.smartbocx.backroom.Bokxman;
import com.blaqbox.smartbocx.backroom.DataConnector;
import com.blaqbox.smartbocx.utils.Constants;

import io.github.jan.supabase.gotrue.user.UserSession;

public class SplashActivity extends AppCompatActivity {
    ImageView image_view;
    DataConnector data_connector;
    Bokxman bokxman;

    Intent intent;
    SharedPreferences.OnSharedPreferenceChangeListener sp_listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = new Intent(getApplicationContext(), MainActivity.class);
        SharedPreferences.Editor editor =  getSharedPreferences("AUTH",Context.MODE_PRIVATE).edit();

        editor.putBoolean("AUTH_STATUS",false);
        editor.putBoolean(Constants.AUTH_STATUS,false);
        editor.putBoolean(Constants.EXTERNAL_SESSION,false);
        editor.putBoolean(Constants.REFRESH_SESSION,false);
        editor.putBoolean(Constants.STORAGE_SESSION,false);
        editor.putBoolean(Constants.SIGNED_OUT,false);
        editor.putBoolean(Constants.NETWORK_ERROR,false);
        editor.apply();
        //boolean asession_stat;
        sp_listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
                boolean session_stat;
                switch (key) {
                    case "AUTH_STATUS":
                        Log.i("SESSION_STAT", "GOTTEN AUTH STATUS");
                        break;

                    case "STORAGE_SESSION":
                        Log.i("SESSION_STAT", "GOTTEN STORAGE SESSION");

                        session_stat = sharedPreferences.getBoolean(key, false);

                        if (session_stat == true) {
                            image_view.clearAnimation();
                            startActivity(intent);
                        }


                        //Toast.makeText(getApplicationContext(),"SESSION READY",Toast.LENGTH_SHORT).show();


                        finish();

                        break;

                    case "REFRESH_SESSION":
                        Log.i("SESSION_STAT", "NETWORK REFRESHED");
                        session_stat = sharedPreferences.getBoolean(key, false);

                        if (session_stat == true) {
                            image_view.clearAnimation();
                            startActivity(intent);
                        }


                        //Toast.makeText(getApplicationContext(),"SESSION READY",Toast.LENGTH_SHORT).show();


                        finish();
                        break;

                    case "SIGNED_OUT":
                        Log.i("SESSION_STAT", "SIGNED OUT");
                        session_stat = sharedPreferences.getBoolean(key, false);

                        if (session_stat == true) {
                            image_view.clearAnimation();
                            startActivity(intent);
                        }


                        //Toast.makeText(getApplicationContext(),"SESSION READY",Toast.LENGTH_SHORT).show();


                        finish();
                    break;
                    case "NETWORK_ERROR":
                        Log.i("SESSION_STAT", "GOTTEN NETWORK ERROR");
                        session_stat = sharedPreferences.getBoolean(key, false);

                        if (session_stat == true) {
                            image_view.clearAnimation();
                            startActivity(intent);
                        }


                        //Toast.makeText(getApplicationContext(),"SESSION READY",Toast.LENGTH_SHORT).show();


                        finish();
                        break;

                    default:
                        Log.i("SESSION_STAT", "session okay");
                }
            }
        };
        getApplicationContext().getSharedPreferences("AUTH", Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(sp_listener);
        data_connector = DataConnector.getInstance(getApplicationContext());
        bokxman = data_connector.getABokxmanInstance();
        Log.i("SA_STAT","SPLASH CREATED");
        UserSession user_session = bokxman.getUserSession();
        if(user_session==null) {


            setContentView(R.layout.activity_splash);

            image_view = (ImageView) findViewById(R.id.splash_img);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.faded);
            image_view.startAnimation(animation);








            ///data_connector = DataConnector.getInstance(getApplicationContext());


                Log.i("USER_SESSION_STAT", "User session error");

        }
        else{

                Log.i("USER_SESSION_STAT", user_session.getAccessToken());

            startActivity(intent);
            finish();
        }

    }
}