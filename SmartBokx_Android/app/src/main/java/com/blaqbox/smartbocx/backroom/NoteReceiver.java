package com.blaqbox.smartbocx.backroom;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.db.DBHandler;
import com.blaqbox.smartbocx.utils.Notifier;
import com.blaqbox.smartbocx.utils.UrlTools;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NoteReceiver extends BroadcastReceiver {
    NotificationManager notes_notification_manager = null;
    Notifier broadcast_notifier;
    String global_note = "";
    Intent global_intent;
    Context global_context;
    @Override
    public void onReceive(Context context, Intent intent) {
        global_context = context;
        broadcast_notifier =  new Notifier(context);
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        String note_data = remoteInput.getString("note_description");
        String note_link = intent.getStringExtra("MAIN_NOTE");
        intent.putExtra("note_description",note_data);
        global_note = note_link;
        global_intent = intent;
        //intent.removeExtra("MAIN_NOTE");
        Log.i("NOTE RAW STATUS",note_link+" "+note_data);
        if(UrlTools.isValidUrl(note_link)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UrlTools.check(note_link,http_callback);
                }
            }).start();

        }
        else{
           // intent.putExtra("MAIN_NOTE",note_link);
            Log.i("NOTE STATUS",note_link+" "+note_data);

            if (notes_notification_manager == null)
            {
                notes_notification_manager = context.getSystemService(NotificationManager.class);
            }

            broadcast_notifier.showSavedNoteNotification(context,notes_notification_manager,intent);
        }

    }

    public boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true; // If no exception is thrown, it's a valid URL
        } catch (MalformedURLException e) {
            return false; // If an exception is thrown, it's not a valid URL
        }
    }

    Callback http_callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.i("NOTE STATUS",global_note);

            if (notes_notification_manager == null)
            {
                notes_notification_manager = global_context.getSystemService(NotificationManager.class);
            }

            broadcast_notifier.showSavedNoteNotification(global_context,notes_notification_manager,global_intent);
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            if (UrlTools.isRedirect(response)) {
                // Return the value of the 'Location' header if the response is a redirect
                String location = response.header("Location");
                Log.i("New Location",location);

                global_intent.removeExtra("MAIN_NOTE");
                global_intent.putExtra("MAIN_NOTE",location);

                if (notes_notification_manager == null)
                {
                    notes_notification_manager = global_context.getSystemService(NotificationManager.class);
                }

                broadcast_notifier.showSavedNoteNotification(global_context,notes_notification_manager,global_intent);

            } else {
                Log.i("New Location: ","not redirect");
                // Return the original URL if no redirect
                String url_location = response.request().url().toString();
                Log.i("URL LOCATION: ", url_location);
                if (notes_notification_manager == null)
                {
                    notes_notification_manager = global_context.getSystemService(NotificationManager.class);
                }

                broadcast_notifier.showSavedNoteNotification(global_context,notes_notification_manager,global_intent);
            }
        }
    };

}