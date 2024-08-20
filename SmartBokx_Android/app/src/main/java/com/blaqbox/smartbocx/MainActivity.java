package com.blaqbox.smartbocx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blaqbox.smartbocx.Models.NoteJson;
import com.blaqbox.smartbocx.Models.SieveBokx;
import com.blaqbox.smartbocx.Models.SieveBokxBaseModel;
import com.blaqbox.smartbocx.Models.SieveSyncModel;
import com.blaqbox.smartbocx.backroom.Bokxman;
import com.blaqbox.smartbocx.backroom.DataConnector;
import com.blaqbox.smartbocx.backroom.NoteReceiver;
import com.blaqbox.smartbocx.db.DBHandler;
import com.blaqbox.smartbocx.db.Note;
import com.blaqbox.smartbocx.db.NoteQA;
import com.blaqbox.smartbocx.ui.ExDialog;
import com.blaqbox.smartbocx.utils.Notifier;
import com.blaqbox.smartbocx.utils.BokxAPIHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.jan.supabase.gotrue.user.UserSession;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.Buffer;

public class MainActivity extends AppCompatActivity {
    Notifier notifier;
    BokxAPIHelper bokxAPIHelper;
    String function_name = "";
    String bokxman_sieveman_apikey = "";
    //List<Note> all_notes;
    DataConnector master_dbHandler;
    TabAdapter tab_adapter;
    public boolean clipboard_service_state = false;
    ViewPager2 viewpager;
    View notes_list;
    ExDialog exDialog;
    FragmentManager fragment_manager;
    Animation sync_animation;
    FloatingActionButton addnote_fab;

    AdView adview;
    TabLayout main_tablayout;

    LinearLayout banner_holder;
    NotificationManager notes_notification_manager = null;

    AppCompatButton sync_btn;
    Bokxman bokxman;
    String bokx_url;

    UserSession user_session;
    SyncBokxCallBack syncBokxCallBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(()->{
            MobileAds.initialize(this);
        }).start();
        //all_notes = new ArrayList<Note>();
        adview = new AdView(this);
        adview.setAdSize(AdSize.BANNER);
        sync_animation = AnimationUtils.loadAnimation(this,R.anim.rotator);

        adview.setAdUnitId(getResources().getString(R.string.main_banner_app_id));

        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);

        master_dbHandler = DataConnector.getInstance(getApplicationContext());
        bokxman = DataConnector.getBokxmanInstance();
        notifier = new Notifier(getApplicationContext());
        setContentView(R.layout.activity_main);
        sync_btn =(AppCompatButton) findViewById(R.id.toggle_clipboard_service_btn);
        //sync_btn.startAnimation(sync_animation);


        viewpager = findViewById(R.id.tab_view_space);
        addnote_fab = findViewById(R.id.add_note_fab);
        main_tablayout = findViewById(R.id.main_tablayout);
        banner_holder = findViewById(R.id.banner_holder);
        syncBokxCallBack = new SyncBokxCallBack();
        bokx_url = getString(R.string.bokx_base_url);
        user_session = bokxman.getUserSession();

        if(user_session!=null) {
            String user_token = user_session.getAccessToken();
            bokxAPIHelper = new BokxAPIHelper(bokx_url, user_token);
            bokxAPIHelper.setBokxCallback(syncBokxCallBack);
        }
        else{
            bokxAPIHelper = new BokxAPIHelper(bokx_url);
            bokxAPIHelper.setBokxCallback(syncBokxCallBack);
        }


        banner_holder.addView(adview);
        //adview.se
        addnote_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNoteView(v);
            }
        });


        main_tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Toast.makeText(getApplicationContext(),tab.getText(),Toast.LENGTH_LONG).show();
                viewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tab_adapter = new TabAdapter(this);//, master_dbHandler);
        viewpager.setAdapter(tab_adapter);
        viewpager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback(){
                    @Override
                    public void onPageSelected(int position)
                    {
                        main_tablayout.selectTab(main_tablayout.getTabAt(position));
                    }
                }
        );
        /*
        new TabLayoutMediator(main_tablayout,viewpager,((tab, position) -> {

            //main_tablayout.selectTab(tab,false);
        })).attach();
        */
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fragment_manager = getSupportFragmentManager();
        //notes_list = layoutInflater.inflate(R.layout.activity_main,null);
        //viewpager.addView(notes_list);
        exDialog = new ExDialog();

        try {
            File files_dir = getObbDir();
            //download the model from: https://www.kaggle.com/models/tensorflow/mobilebert/tfLite/metadata
            File asset_file = new File(files_dir.getPath(),"bert.tflite");

            if(asset_file.exists())
            {
                Log.i("asset file: ","exists");
            }
            else
            {

                InputStream asset_io = getAssets().open("bert.tflite");

                Log.i("asset file: ","not exists with size: "+asset_io.available());
                byte asset_bytes[] = new byte[1024];

                BufferedInputStream bis;
                FileOutputStream fos = new FileOutputStream(asset_file);
                ReadableByteChannel asset_channel = Channels.newChannel(asset_io);

                fos.getChannel().transferFrom(asset_channel,0,asset_io.available());

            }

            Log.e("files_dir: ",files_dir.getPath());

        }
        catch(IOException ie)
        {
                Log.e("asset error", ie.getMessage());
        }

    }





    public void startClipboardService(View parent_view)
    {

        AppCompatButton parent_btn = (AppCompatButton) parent_view;
        int new_size = master_dbHandler.refresh();
        //master_dbHandler.migrateFTS4();
        Log.i("New Size: ", "updated size is "+new_size);
        //main_tablayout.destroyDrawingCache();
        /*notifier.showNewNoteNotification(getApplicationContext(),notes_notification_manager,"New Note","New Notification");*/
        if(clipboard_service_state == false) {


            ///parent_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.clipboard_btn_off_state)));
            Toast.makeText(this, "Refreshed Data", Toast.LENGTH_LONG).show();
        }
        else
        {
            ///parent_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.clipboard_btn_on_state)));
            Toast.makeText(this, "Refreshed Data", Toast.LENGTH_LONG).show();
        }

        clipboard_service_state = !clipboard_service_state;

    }

    public void syncBokx(View parent_view)
    {
        sync_btn.startAnimation(sync_animation);
        UserSession xuser_session = bokxman.getUserSession();

        if((user_session==null)&&(xuser_session!=null)){
            user_session = xuser_session;
            bokxAPIHelper = new BokxAPIHelper(bokx_url,user_session.getAccessToken());
        }

        List<NoteJson> notejson_list = new ArrayList<NoteJson>();
        if(user_session!=null) {

            for (Note note: master_dbHandler.getAllNotes()){
                notejson_list.add(note.toJson());
            }
            String user_id = "";
            SieveBokx sieveBokx = new SieveBokx(notejson_list,"digest",user_id);
            //SieveBokxBaseModel sieveBokxBaseModel = new SieveBokxBaseModel(sieveBokx);
            String sievesync_json = sieveBokx.makeJson();
            //new SieveSyncModel(function_name,sieveBokxBaseModel).makeJson();


            bokxAPIHelper.pushJob(sievesync_json);
            //Log.i("sievebokx json", sieveBokxBaseModel.makeJson());
            Log.i("sievebokx json", sievesync_json);
        }
        else{
            Toast.makeText(this,"PLEASE LOGIN/SIGN UP 1ST",Toast.LENGTH_LONG).show();
            viewpager.setCurrentItem(2,true);
        }


    }

    public void showAddNoteView(View vw)
    {
        exDialog.show(fragment_manager," test");
        viewpager.invalidate();
    }


    public class SyncBokxCallBack implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {
            e.printStackTrace();
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response sieve_response) throws IOException
        {
            String request_headers = call.request().headers().toString();
            Buffer kk = new Buffer();
            //call.request().body().writeTo(kk);
            //String request_str = kk.readUtf8();
            //String request_body = request_str+"___"+call.request().body().contentLength();
            //Log.i("request summary",request_headers+"\n"+request_body);
            String json_str = sieve_response.body().string();

            int response_code = sieve_response.code();
            String response_headers = sieve_response.headers().toString();
            String response_msg = sieve_response.message();
            Log.i("unirest response json", json_str+"\n"+response_code+":\t"+response_headers+"\nresponse_msg"+response_msg);

            try {
                JSONObject job_json = new JSONObject(json_str);
                String job_status = job_json.getString("job_status");
                String job_id = job_json.getString("job_id");
                if(job_status.equals("finished")){
                    //JSONArray json_outputs = job_json.getJSONArray("outputs");
                    //JSONObject query_output = json_outputs.getJSONObject(0);
                    //String query_answer = query_output.getJSONObject("data").getString("answer");
                    //search_results.clear();
                    //search_results.add(new NoteQA(recent_query,query_answer));
                    Log.i("Job Status"," Job has been finished");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sync_btn.clearAnimation();
                            Toast.makeText(getApplicationContext(),"Data Finished to Synchronise",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else if(job_status.equals("queued")){
                    //recent_job = job_id;
                    //model_free = false;
                    //search_results.clear();
                    //search_results.add(new NoteQA(recent_query,"Please wait ...."));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(),"Data Synchronc Started",Toast.LENGTH_LONG).show();
                        }
                    });


                    new Thread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    bokxAPIHelper.getJob(job_id);
                                }
                            }

                    ).run();
                }
                else{
                    Log.i("Job Status",job_status);
                    new Thread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    bokxAPIHelper.getJob(job_id);
                                }
                            }

                    ).run();
                }

            }
            catch(Exception except){
                Log.e("call back error",except.getMessage());
            }

        }
    }
}