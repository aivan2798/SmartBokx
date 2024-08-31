package com.blaqbox.smartbocx.backroom;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blaqbox.smartbocx.Models.BokxCredits;
import com.blaqbox.smartbocx.Models.QABuilder;
import com.blaqbox.smartbocx.Models.QAModel;
import com.blaqbox.smartbocx.Models.SieveBaseModel;
import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.db.NoteQA;
import com.blaqbox.smartbocx.ui.BokxBot;
import com.blaqbox.smartbocx.utils.BokxAPIHelper;
import com.blaqbox.smartbocx.utils.Notifier;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import io.github.jan.supabase.gotrue.user.UserSession;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.Buffer;

public class AutoNoteReceiver extends BroadcastReceiver {
    NotificationManager notes_notification_manager = null;
    Notifier broadcast_notifier;

    Bokxman bokxman;
    UserSession user_session;
    String bokx_url;

    String recent_job;
    boolean model_free = true;
    BokxAPIHelper bokxAPIHelper;
    Context context;
    BokxAPICallBack bokxAPICallBack;

    boolean job_finished = false;
    String query_string;
    @Override
    public void onReceive(Context xcontext, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.i("Gottten autogen", "auto gen message");


        context = xcontext;
        bokx_url = context.getResources().getString(R.string.bokx_base_url);
        broadcast_notifier =  new Notifier(context);
        if (notes_notification_manager == null)
        {
            notes_notification_manager = context.getSystemService(NotificationManager.class);
        }

        Intent auto_intent = new Intent(context, NoteReceiver.class);
        auto_intent.setAction("com.blaqbokx.smartbocx.AUTO_NOTE");
        auto_intent.putExtra("MAIN_NOTE","Auto Generating");
        auto_intent.putExtra("note_description","Please wait ....");
        auto_intent.putExtra("NOTE_STATUS",500);

        broadcast_notifier.showSavedNoteNotification(context,notes_notification_manager,auto_intent);
        query_string = intent.getStringExtra("MAIN_NOTE");
        Log.i("using url",query_string);

        bokxman = DataConnector.getBokxmanInstance();

        bokxAPICallBack = new BokxAPICallBack();
        user_session = DataConnector.getUserSession();
        if (user_session != null) {
            String user_id = user_session.getUser().getId();
            QABuilder qa_made_json = new QABuilder(query_string,"auto_gen",user_id);
            QAModel qaModel = qa_made_json.getQAModel();
            SieveBaseModel sieveBaseModel = new SieveBaseModel(qaModel);
            String made_json = qa_made_json.makeJson();
            Log.i("Generated JSON",made_json);
            bokxAPIHelper = new BokxAPIHelper(bokx_url,user_session.getAccessToken());
            bokxAPIHelper.setBokxCallback(bokxAPICallBack);
            bokxAPIHelper.pushJob(made_json);
        }
        else{
            Log.i("user__stat__session","user sesssion null");
        }

        /*while(job_finished==false){
            Log.i("WAIT RESPONSE","waiting response");
        }*/
    }

    public class BokxAPICallBack implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {
            Intent auto_intent = new Intent(context, NoteReceiver.class);
            auto_intent.setAction("com.blaqbokx.smartbocx.AUTO_NOTE");
            auto_intent.putExtra("MAIN_NOTE","CONNECTION ERROR");
            auto_intent.putExtra("note_description","CONNECTION ERROR");
            auto_intent.putExtra("NOTE_STATUS",500);

            broadcast_notifier.showSavedNoteNotification(context,notes_notification_manager,auto_intent);
            job_finished = true;
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
                int request_status = job_json.getInt("status");

                if(request_status>200){

                    String error_message = job_json.getString("message");

                    Log.i("Job Status"," Job has been finished");
                    Intent auto_intent = new Intent(context, NoteReceiver.class);
                    auto_intent.setAction("com.blaqbokx.smartbocx.AUTO_NOTE");
                    auto_intent.putExtra("MAIN_NOTE","error_message");
                    auto_intent.putExtra("note_description",error_message);
                    auto_intent.putExtra("NOTE_STATUS",500);

                    broadcast_notifier.showSavedNoteNotification(context,notes_notification_manager,auto_intent);
                    job_finished = true;
                    return;
                }
                String job_status = job_json.getString("job_status");
                String job_id = job_json.getString("job_id");
                if(job_status.equals("finished")){
                    JSONArray json_outputs = job_json.getJSONArray("outputs");
                    JSONObject query_output = json_outputs.getJSONObject(0);
                    JSONObject json_credits = job_json.getJSONObject("credits");
                    JSONObject json_bokx_credits = json_credits.getJSONObject("bokx_details");

                    int bokx_responses_limit = json_bokx_credits.getInt("bokx_responses_limit");
                    int bokx_responses = json_bokx_credits.getInt("bokx_responses");
                    int bokx_request_limit = json_bokx_credits.getInt("bokx_request_limit");
                    int bokx_requests = json_bokx_credits.getInt("bokx_requests");


                    JSONObject query_json_answer = query_output;
                    int status_code = query_json_answer.getInt("status_code");
                    String query_answer = query_json_answer.getString("message");
                    Log.i("Job Status"," Job has been finished");
                    Intent auto_intent = new Intent(context, NoteReceiver.class);
                    auto_intent.setAction("com.blaqbokx.smartbocx.AUTO_NOTE");
                    auto_intent.putExtra("MAIN_NOTE",query_string);
                    auto_intent.putExtra("note_description",query_answer);
                    auto_intent.putExtra("NOTE_STATUS",status_code);

                    broadcast_notifier.showSavedNoteNotification(context,notes_notification_manager,auto_intent);

                }
                else if(job_status.equals("queued")){
                    recent_job = job_id;
                    model_free = false;



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