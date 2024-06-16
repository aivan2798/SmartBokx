package com.blaqbox.smartbocx;

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
import android.widget.Toast;

import com.blaqbox.smartbocx.backroom.DataConnector;
import com.blaqbox.smartbocx.backroom.NoteReceiver;
import com.blaqbox.smartbocx.db.DBHandler;
import com.blaqbox.smartbocx.db.Note;
import com.blaqbox.smartbocx.ui.ExDialog;
import com.blaqbox.smartbocx.utils.Notifier;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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

public class MainActivity extends AppCompatActivity {
    Notifier notifier;

    //List<Note> all_notes;
    DataConnector master_dbHandler;
    TabAdapter tab_adapter;
    public boolean clipboard_service_state = false;
    ViewPager2 viewpager;
    View notes_list;
    ExDialog exDialog;
    FragmentManager fragment_manager;

    FloatingActionButton addnote_fab;

    TabLayout main_tablayout;

    NotificationManager notes_notification_manager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //all_notes = new ArrayList<Note>();
        master_dbHandler = DataConnector.getInstance(getApplicationContext());
        notifier = new Notifier();
        setContentView(R.layout.activity_main);


        viewpager = findViewById(R.id.tab_view_space);
        addnote_fab = findViewById(R.id.add_note_fab);
        main_tablayout = findViewById(R.id.main_tablayout);
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

    public void showAddNoteView(View vw)
    {
        exDialog.show(fragment_manager," test");
        viewpager.invalidate();
    }
}