package com.blaqbox.smartbocx.utils;

import android.content.res.Resources;
import android.util.Log;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.blaqbox.smartbocx.Models.BokxJob;
import com.blaqbox.smartbocx.R;
public class BokxAPIHelper {
    OkHttpClient bokxHttpClient;

    Callback bokx_callback;
    String bokx_url;
    String bokx_api_key;

    MediaType JSON = MediaType.get("application/json");

    public BokxAPIHelper(String xbokx_base_url){
        bokx_url = xbokx_base_url;
        bokxHttpClient = new OkHttpClient();
    }

    public BokxAPIHelper(String xbokx_base_url, String xbokx_key){
        bokx_url = xbokx_base_url;
        bokx_api_key = xbokx_key;
        bokxHttpClient = new OkHttpClient();
        
    }

    public void setBokxCallback(Callback bokx_callback) {
        this.bokx_callback = bokx_callback;
    }


    public void pushJob(String bokx_json)
    {

        try {
            RequestBody rb = RequestBody.create(bokx_json.getBytes(), JSON);
            Request request = new Request.Builder().url(bokx_url+"/model").header("x-bokx-key", bokx_api_key)
                    //.header("Host", "mango.bokxdata.com")
                    .header("Content-Type", "application/json")
                    .header("Content-Length", "" + rb.contentLength()).post(rb).build();
            bokxHttpClient.newCall(request).enqueue(bokx_callback);
        }
        catch(Exception ioe){
            Log.e("push error", ioe.getMessage());
        }
    }

    public void getJob(String job_id)
    {
        BokxJob bokx_job = new BokxJob(job_id);
        String bokx_job_json = bokx_job.makeJson();
        try {
            RequestBody rb = RequestBody.create(bokx_job_json.getBytes(), JSON);
            Request request = new Request.Builder().url(bokx_url+"/model/jobs").header("x-bokx-key", bokx_api_key)
                    //.header("Host", "mango.bokxdata.com")
                    .header("Content-Length", "" + rb.contentLength())
                    .header("Content-Type", "application/json")
                    .post(rb).build();

            bokxHttpClient.newCall(request).enqueue(bokx_callback);
            Log.i("Get Job","Job status sent");
        }
        catch(Exception ioe){
            Log.e("Get Job error",ioe.getMessage());
        }
    }

    public Response auth(){


        try {
            Log.i("Bokx_url",bokx_url+"/auth/signup");
            Request request = new Request.Builder().url(bokx_url+"/auth/signup").header("x-bokx-key", bokx_api_key).get().build();

            Response auth_response = bokxHttpClient.newCall(request).execute();
            int response_code = auth_response.code();
            //String message = auth_response.body().string();
            Log.i("Auth_STATUS","Auth Status Sent: "+response_code);
            //Log.i("Auth_Message",message);
            return auth_response;

        }
        catch(Exception ioe){
            ioe.printStackTrace();
            Log.i("Get Auth",""+ioe.getMessage());
            return null;

        }
    }
}
