package com.blaqbox.smartbocx.backroom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;

import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.db.DBHandler;
import com.blaqbox.smartbocx.utils.Notifier;

public class NoteReceiverActivity extends AppCompatActivity {
    Notifier note_recv_activity_notifier;// = new Notifier();
    NotificationManager notification_manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        note_recv_activity_notifier = new Notifier(getApplicationContext());
        if (notification_manager == null)
        {
            notification_manager = getSystemService(NotificationManager.class);
        }

        Intent intent = getIntent();
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

        note_recv_activity_notifier.showNewNoteNotification(getApplicationContext(),notification_manager,"NEW NOTE",sharedText);
        finish();
        //setContentView(R.layout.activity_note_receiver);
    }
}