package com.blaqbox.smartbocx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.jan.supabase.gotrue.user.UserSession;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class MainActivity extends AppCompatActivity {
    Notifier notifier;

    boolean auth_status;
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

    AlertDialog alertDialog;
    AdView adview;
    TabLayout main_tablayout;

    LinearLayout banner_holder;
    NotificationManager notes_notification_manager = null;

    AppCompatButton sync_btn;
    Bokxman bokxman;
    String bokx_url;
    ImageView splash_screen_img;
    UserSession user_session;
    String session_token;
    SyncBokxCallBack syncBokxCallBack;

    AppCompatButton login_btn;
    AppCompatButton logout_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(()->{
            MobileAds.initialize(this.getApplicationContext());

        }).start();
        new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("FF441097AB540DAA01157414C5946665"));
        //all_notes = new ArrayList<Note>();
        adview = new AdView(this);
        adview.setAdSize(AdSize.BANNER);
        sync_animation = AnimationUtils.loadAnimation(this,R.anim.rotator);
        alertDialog = new AlertDialog.Builder(MainActivity.this,R.style.BokxDialogTheme).create();
        adview.setAdUnitId(getResources().getString(R.string.main_banner_app_id));

        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);

        master_dbHandler = DataConnector.getInstance(getApplicationContext());
        bokxman = DataConnector.getBokxmanInstance();

        notifier = new Notifier(getApplicationContext());
        setContentView(R.layout.activity_main);
        splash_screen_img = (ImageView) findViewById(R.id.splashscreen_view);
        splash_screen_img.startAnimation(AnimationUtils.loadAnimation(this, R.anim.faded));
        auth_status = bokxman.getLoginStatus();
        login_btn = ((AppCompatButton)findViewById(R.id.login_btn));
        logout_btn = ((AppCompatButton)findViewById(R.id.logout_btn));
        Log.i("TEMP_AUTH_STATUS",""+auth_status);
        if(auth_status==true){

            login_btn.setVisibility(View.GONE);
            logout_btn.setVisibility(View.VISIBLE);
        }
        else{
            logout_btn.setVisibility(View.GONE);
            login_btn.setVisibility(View.VISIBLE);
        }

        splash_screen_img.clearAnimation();
        ((LinearLayout)findViewById(R.id.splashscreen_holder)).setVisibility(View.GONE);
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
            Log.i("MAIN_USER_SESSION","NOT NULL");
            DataConnector.setUserSession(user_session);
            String user_token = user_session.getAccessToken();
            bokxAPIHelper = new BokxAPIHelper(bokx_url, user_token);
            bokxAPIHelper.setBokxCallback(syncBokxCallBack);
        }
        else{
            Log.i("MAIN_USER_SESSION","IS NULL");
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
        List<Fragment> active_frags = fragment_manager.getFragments();
        Log.i("NO OF FRAGS",""+active_frags.size());
        for (Fragment active_frag : active_frags){

            if(active_frag!=null){

                Log.i("ACTIVE_FRAGS","NOT NULL "+active_frag.getId());
            }
            else{
                Log.i("ACTIVE_FRAGS","FRAGS NULL");
            }

        }
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

        bokxman.getAuth_status().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                auth_status = bokxman.getLoginStatus();
                if(auth_status==true){
                    user_session = bokxman.getUserSession();

                    if(user_session!=null){
                        Log.i("INTERNAL_MAIN_USER_SESSION","NOT NULL");
                        DataConnector.setUserSession(user_session);
                        String user_token = user_session.getAccessToken();
                        bokxAPIHelper = new BokxAPIHelper(bokx_url, user_token);
                        bokxAPIHelper.setBokxCallback(syncBokxCallBack);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                        Log.i("BOKX_AUTH","STARTED BOKX_AUTH");


                                        Response credict = bokxAPIHelper.auth();

                                        if(credict==null){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    alertDialog.setTitle("CONNECTION ERROR");
                                                    alertDialog.setMessage("Internal error");
                                                    alertDialog.show();
                                                }
                                            });
                                            //return;
                                        } else if (credict.code()>200) {
                                            showAlertDialog("CONNECTION ERROR","Problem with Auth");
                                        } else {
                                            String credits_body = credict.body().string();
                                            Log.i("THREAD DATA: ", credits_body);

                                            JSONObject json_cedits = new JSONObject(credits_body);

                                            int status = json_cedits.getInt("status");

                                            if (status == 200) {
                                                String available_qn = "" + json_cedits.getInt("available_questions");
                                                String available_ans = "" + json_cedits.getInt("available_answers");

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        showAlertDialog("Auth Ok", "User Authorized");
                                                    }
                                                });


                                            } else {
                                               // String available_qn = "" + json_cedits.getString("message");

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        showAlertDialog("Auth Not Ok", "oops");
                                                    }
                                                });

                                            }
                                        }


                                }
                                catch (IOException ioe){
                                    ioe.printStackTrace();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showAlertDialog("UNKOWN ERROR","Couldn't login properly");

                                        }
                                    });
                                }

                                catch(JSONException joe){

                                    joe.printStackTrace();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showAlertDialog("DATA ERROR","Couldn't parse data");

                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                    login_btn.setVisibility(View.GONE);
                    logout_btn.setVisibility(View.VISIBLE);
                }
                else{
                    logout_btn.setVisibility(View.GONE);
                    login_btn.setVisibility(View.VISIBLE);
                }
                //refreshAllTabs();
                List<Fragment> active_frags = fragment_manager.getFragments();
                Log.i("NO OF FRAGS",""+active_frags.size());
                for (Fragment active_frag : active_frags){

                    if(active_frag!=null){

                        Log.i("ACTIVE_FRAGS","NOT NULL "+active_frag.getId());
                    }
                    else{
                        Log.i("ACTIVE_FRAGS","FRAGS NULL");
                    }

                }

                refreshTab(2);
                viewpager.setCurrentItem(0,true);

            }
        });

    }

    public void showAlertDialog(String xdialog_title,String xdialog_msg){

        String dialog_msg = "<font color='#111111'>"+xdialog_msg+"</font>";
        String dialog_title = "<font color='#111111'>"+xdialog_title+"</font>";
        alertDialog.setTitle(Html.fromHtml(dialog_title));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"OK",(dialog, which) -> {
            dialog.dismiss();
        });
        alertDialog.setMessage(Html.fromHtml(dialog_msg));
        alertDialog.show();
    }

    public void refreshAllTabs(){

        int tabs_ct = tab_adapter.getItemCount();
        for (int t = 0; t < tabs_ct; t++) {

            tab_adapter.notifyItemChanged(t);

        }
    }

    public void refreshTab(int tab){
        tab_adapter.refreshItem(tab);
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

        UserSession xuser_session = bokxman.getUserSession();
        List<NoteJson> notejson_list = new ArrayList<NoteJson>();
        Log.i("LOGIN STATUS",""+auth_status);
        if((user_session==null)&&(xuser_session!=null)){
            auth_status = bokxman.getLoginStatus();
            user_session = xuser_session;
            bokxAPIHelper = new BokxAPIHelper(bokx_url,user_session.getAccessToken());
        }

        if(user_session!=null) {
            sync_btn.startAnimation(sync_animation);
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

            alertDialog.setTitle("SESSION ALERT");
            alertDialog.setMessage("PLEASE LOGIN/SIGN UP 1ST");
            alertDialog.setIcon(R.drawable.smartbocx_main_foreground);
            //Toast.makeText(this,,Toast.LENGTH_LONG).show();
            viewpager.setCurrentItem(2,true);
        }


    }

    public void autoBokx(View view){

    }

    public void showAddNoteView(View vw)
    {
        exDialog.show(fragment_manager," test");
        viewpager.invalidate();
    }

    public void login(View view){
        auth_status = bokxman.getLoginStatus();
        if(auth_status==true){

            alertDialog.setTitle("Login Message");
            alertDialog.setMessage("user Already loged in");
            alertDialog.show();
        }
        else{
            viewpager.setCurrentItem(2,true);
        }
    }

    public void logout(View view){
        auth_status = bokxman.getLoginStatus();
        if(auth_status==true){
            bokxman.logoutUser();
            logout_btn.setVisibility(View.GONE);
            login_btn.setVisibility(View.VISIBLE);


            refreshTab(2);
            //refreshAllTabs();

            //alertDialog.setTitle("Login Message");
            //alertDialog.setMessage("user Already loged in");
            //alertDialog.show();
        }
        else{
            alertDialog.setTitle("Login Message");

            alertDialog.setMessage("user loged out alread");
            alertDialog.show();
        }
    }


    public class SyncBokxCallBack implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sync_btn.clearAnimation();

                    alertDialog.setTitle("CONNECTION ERROR");
                    alertDialog.setMessage("Oops, Connect to the internet and please try again");
                    alertDialog.show();
                }
            });
            Log.i("BOKX HTTP", "CONNECTION FAILED");
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
                int json_status = job_json.getInt("status");

                if(json_status<=200) {
                    String job_status = job_json.getString("job_status");
                    String job_id = job_json.getString("job_id");
                    if (job_status.equals("finished")) {
                        //JSONArray json_outputs = job_json.getJSONArray("outputs");
                        //JSONObject query_output = json_outputs.getJSONObject(0);
                        //String query_answer = query_output.getJSONObject("data").getString("answer");
                        //search_results.clear();
                        //search_results.add(new NoteQA(recent_query,query_answer));
                        Log.i("Job Status", " Job has been finished");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sync_btn.clearAnimation();
                                Toast.makeText(getApplicationContext(), "Data Finished to Synchronise", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else if (job_status.equals("queued")) {
                        //recent_job = job_id;
                        //model_free = false;
                        //search_results.clear();
                        //search_results.add(new NoteQA(recent_query,"Please wait ...."));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(getApplicationContext(), "Data Synchronc Started", Toast.LENGTH_LONG).show();
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
                    } else {
                        Log.i("Job Status", job_status);
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
                else{
                    String job_message = job_json.getString("message");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sync_btn.clearAnimation();

                            alertDialog.setTitle("BOKX ERROR");
                            alertDialog.setMessage(job_message);
                            alertDialog.show();
                        }
                    });
                }

            }
            catch(Exception except){
                Log.e("call back error",except.getMessage());
            }

        }
    }

    @Override
    protected void onDestroy() {
        master_dbHandler.killTTS();
        super.onDestroy();

    }
}