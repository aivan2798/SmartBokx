package com.blaqbox.smartbocx.utils;

import android.util.Log;

import com.blaqbox.smartbocx.Models.SieveModel;

import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SieveDataHelper {

    OkHttpClient sieveHttpClient;

    Callback sieve_callback;
    String sieve_url = "https://mango.sievedata.com/v2";
    String sieve_api_key;

    MediaType JSON = MediaType.get("application/json");

    public SieveDataHelper(String xsieve_api_key){
        sieve_api_key = xsieve_api_key;
        sieveHttpClient = new OkHttpClient();
    }

    public void setSieveCallback(Callback sieve_callback) {
        this.sieve_callback = sieve_callback;
    }

    public void pushJob(String sieve_json)
    {

        try {
            RequestBody rb = RequestBody.create(sieve_json.getBytes(), JSON);
            Request request = new Request.Builder().url(sieve_url+"/push").header("X-API-Key", sieve_api_key)
                    .header("Host", "mango.sievedata.com")
                    .header("Content-Type", "application/json")
                    .header("Content-Length", "" + rb.contentLength()).post(rb).build();
            sieveHttpClient.newCall(request).enqueue(sieve_callback);
        }
        catch(Exception ioe){
            Log.i("sieve error",ioe.getMessage());
        }
    }

    public void getJob(String job_id)
    {

        try {
            //RequestBody rb = RequestBody.create(sieve_json.getBytes(), JSON);
            Request request = new Request.Builder().url(sieve_url+"/jobs"+"/"+job_id).header("X-API-Key", sieve_api_key)
                    .header("Host", "mango.sievedata.com")
                    .header("Content-Type", "application/json")
                    .get().build();
            sieveHttpClient.newCall(request).enqueue(sieve_callback);
            Log.i("Get Job","Job status sent");
        }
        catch(Exception ioe){
            Log.e("Get Job error",ioe.getMessage());
        }
    }
}
