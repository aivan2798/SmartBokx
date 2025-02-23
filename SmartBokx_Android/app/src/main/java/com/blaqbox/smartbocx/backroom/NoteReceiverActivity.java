package com.blaqbox.smartbocx.backroom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.db.DBHandler;
import com.blaqbox.smartbocx.utils.Notifier;

import io.github.jan.supabase.gotrue.user.UserSession;

public class NoteReceiverActivity extends AppCompatActivity {
    Notifier note_recv_activity_notifier;// = new Notifier();
    NotificationManager notification_manager;
    String bokx_url;
    Bokxman bokxman;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        note_recv_activity_notifier = new Notifier(getApplicationContext());
        if (notification_manager == null)
        {
            notification_manager = getSystemService(NotificationManager.class);
        }
        bokx_url =  getResources().getString(R.string.bokx_base_url);
        Intent intent = getIntent();
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        //UserSession user_session = .getUserSession();
        //if(user_session!=null){
            Log.i("NOTIFY SESSION", "USER SESSION YES");
        //}else{
            //DataConnector.getBokxmanInstance().reauth();
            Log.i("NOTIFY SESSION", "USER SESSION NO");
        //}
        note_recv_activity_notifier.showNewNoteNotification(getApplicationContext(),notification_manager,"NEW NOTE",sharedText);
        finish();
        //setContentView(R.layout.activity_note_receiver);
    }
}