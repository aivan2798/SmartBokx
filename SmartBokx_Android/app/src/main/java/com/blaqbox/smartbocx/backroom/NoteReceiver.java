package com.blaqbox.smartbocx.backroom;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.db.DBHandler;
import com.blaqbox.smartbocx.utils.Notifier;

public class NoteReceiver extends BroadcastReceiver {
    NotificationManager notes_notification_manager = null;
    Notifier broadcast_notifier = new Notifier();


    @Override
    public void onReceive(Context context, Intent intent) {

        if (notes_notification_manager == null)
        {
            notes_notification_manager = context.getSystemService(NotificationManager.class);
        }

        broadcast_notifier.showSavedNoteNotification(context,notes_notification_manager,intent);
    }
}