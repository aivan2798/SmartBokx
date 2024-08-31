package com.blaqbox.smartbocx.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteAction;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteActionCompat;
import androidx.core.app.RemoteInput;

import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.backroom.AutoNoteReceiver;
import com.blaqbox.smartbocx.backroom.DataConnector;
import com.blaqbox.smartbocx.backroom.NoteReceiver;
import com.blaqbox.smartbocx.db.DBHandler;

public class Notifier
{
    DataConnector db_connector;
    public Notifier(Context context)
    {
        Log.i("loading notfier","notifier loaded");
        db_connector = DataConnector.getInstance();;
    }
    public void showNewNoteNotification(Context context, NotificationManager notes_notification_manager,String note_title, String content)
    {
        if (notes_notification_manager == null)
        {
            notes_notification_manager = context.getSystemService(NotificationManager.class);
        }

        String receive_action = content;

        RemoteInput note_text = new RemoteInput.Builder("note_description").setLabel("ADD NOTE").build();


        Intent my_intent = new Intent(context, NoteReceiver.class);
        my_intent.setAction("com.blaqbokx.smartbocx.ADD_NOTE");
        my_intent.putExtra("MAIN_NOTE",content);

        PendingIntent note_reply_intent = PendingIntent.getBroadcast(context,1998,my_intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action note_description_action  = new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_send,"add note",note_reply_intent).addRemoteInput(note_text).build();

        String choices[] = {"AUTO GEN"};
        RemoteInput.Builder auto_note_builder = new RemoteInput.Builder("auto_note_description");
        auto_note_builder.setChoices(choices);
        auto_note_builder.setLabel("");

        auto_note_builder.setEditChoicesBeforeSending(RemoteInput.EDIT_CHOICES_BEFORE_SENDING_DISABLED);
        RemoteInput auto_note = auto_note_builder.build();




        Intent auto_intent = new Intent(context, AutoNoteReceiver.class);
        auto_intent.setAction("com.blaqbokx.smartbocx.AUTO_NOTE");
        auto_intent.putExtra("MAIN_NOTE",content);

        PendingIntent auto_note_reply_intent = PendingIntent.getBroadcast(context,1998,auto_intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action auto_note_description_action  = new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_send,"auto gen note",auto_note_reply_intent).build();


        String channel_id = context.getPackageName()+"note_bundle";
        NotificationCompat.Builder notification_builder = new NotificationCompat.Builder(context,channel_id)
                .setSmallIcon(R.drawable.smartbocx_txt_foreground)
                .setContentTitle(note_title)
                .setContentText(receive_action)
                .addAction(note_description_action)
                .addAction(auto_note_description_action)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification note_notification = notification_builder.build();

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
                notes_notification_manager.getNotificationChannel(channel_id)==null) {
            notes_notification_manager.createNotificationChannel(new NotificationChannel(channel_id,
                    "Whatever", NotificationManager.IMPORTANCE_DEFAULT));
        }
        //NotificationChannel notes_channel = new NotificationChannel(channel_id,"Smart Bocx",NotificationManager.IMPORTANCE_DEFAULT);
        //NotificationManagerCompat notification_compat = NotificationManagerCompat.from(context);
        notes_notification_manager.notify(1998,note_notification);
        /*
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
        )
        {
            Toast.makeText(this, "PERMISSIONS OKAY", Toast.LENGTH_LONG).show();
            notification_compat.notify(1998, note_notification);
        }
        else
        {
            Toast.makeText(this, "PERMISSIONS NOT OKAY", Toast.LENGTH_LONG).show();
            notification_compat.notify(1998, note_notification);
        }
        */
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
    }


    public void showSavedNoteNotification(Context context, NotificationManager notes_notification_manager,Intent intent)
    {
        if (notes_notification_manager == null)
        {
            notes_notification_manager = context.getSystemService(NotificationManager.class);
        }
        int note_status = intent.getIntExtra("NOTE_STATUS",200);
        String note_title = intent.getStringExtra("MAIN_NOTE");

        String note_data = intent.getStringExtra("note_description");
                //remoteInput.getString("note_description");
        String receive_action = intent.getAction();
        String channel_id = context.getPackageName()+"note_bundle";
        NotificationCompat.Builder notification_builder = new NotificationCompat.Builder(context,channel_id)
                .setSmallIcon(R.drawable.smartbocx_txt_foreground)
                .setContentTitle(note_title+" : Note Saved")
                .setContentText(note_data)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification note_notification = notification_builder.build();

        /*NotificationChannel notes_channel = new NotificationChannel(channel_id,"Smart Bocx",NotificationManager.IMPORTANCE_DEFAULT);*/
        NotificationManagerCompat notification_compat = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
                notes_notification_manager.getNotificationChannel(channel_id)==null) {
            notes_notification_manager.createNotificationChannel(new NotificationChannel(channel_id,
                    "Whatever", NotificationManager.IMPORTANCE_DEFAULT));
        }

        if(note_status==200) {
            db_connector.addNewNote("Link Note", note_title, note_data);
        }
        notes_notification_manager.notify(1998,note_notification);
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
