package com.blaqbox.smartbocx.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.service.controls.actions.BooleanAction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blaqbox.smartbocx.Models.QABuilder;
import com.blaqbox.smartbocx.Models.QAModel;
import com.blaqbox.smartbocx.Models.QAModel.*;
import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.backroom.Bokxman;
import com.blaqbox.smartbocx.backroom.DataConnector;
import com.blaqbox.smartbocx.db.Note;
import com.blaqbox.smartbocx.db.NoteQA;
import com.blaqbox.smartbocx.ui.adapters.NoteQAAdapter;
import com.blaqbox.smartbocx.ui.adapters.NotesListAdapter;
import com.blaqbox.smartbocx.utils.SieveDataHelper;
import com.google.android.material.textfield.TextInputEditText;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import com.tutorialspoint.lucene.Searcher;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.github.jan.supabase.gotrue.user.UserSession;


import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import com.blaqbox.smartbocx.Models.SieveModel;
import com.blaqbox.smartbocx.Models.SieveBaseModel;

import org.json.JSONArray;
import org.json.JSONObject;


public class BokxBot extends Fragment {
    AppCompatButton find_notes_btn;
    public View find_notes_view;
    String recent_query;
    String recent_job;
    Bokxman bokxman;
    public NoteQAAdapter note_results_adapter;
    TextInputEditText note_query_input;
    String function_name = ""
    List<NoteQA> search_results;
    SieveDataHelper sieveDataHelper;
    String bokxman_sieveman_apikey = "";
    RecyclerView notes_results;
    UserSession user_session;
    BokxSieveCallBack bokxSieveCallBack;
    //OkHttpClient okhttp;
    MutableLiveData<String> auth_status = new MutableLiveData<String>();

    boolean model_free = true;
    MutableLiveData<Boolean> has_auth = new MutableLiveData<Boolean>();
    public BokxBot() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bokxman = DataConnector.getBokxmanInstance();
        sieveDataHelper = new SieveDataHelper(bokxman_sieveman_apikey);
        //okhttp = new OkHttpClient();
        user_session = bokxman.getUserSession();
        bokxSieveCallBack = new BokxSieveCallBack();
        sieveDataHelper.setSieveCallback(bokxSieveCallBack);
        auth_status.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                ((TextView)find_notes_view.findViewById(R.id.auth_status)).setText(s);
            }
        });

        has_auth.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                Log.i("auth bool",s.toString());

                if(s==true){
                    refreshFragment();
                }

            }
        });

        if(user_session!=null) {
            // Inflate the layout for this fragment
            search_results = new ArrayList<NoteQA>();
            find_notes_view = inflater.inflate(R.layout.fragment_bokxbot, container, false);
            note_results_adapter = new NoteQAAdapter(search_results);

            note_query_input = find_notes_view.findViewById(R.id.find_notes_context);
            find_notes_btn = find_notes_view.findViewById(R.id.find_note_btn);
            notes_results = find_notes_view.findViewById(R.id.notes_results_view);
            notes_results.setLayoutManager(new LinearLayoutManager(BokxBot.this.getContext()));

            notes_results.setAdapter(note_results_adapter);
            find_notes_btn.setOnClickListener(this::runModel);


            return find_notes_view;
        }
        find_notes_view = inflater.inflate(R.layout.fragment_auth_view, container, false);
        find_notes_view.findViewById(R.id.create_user_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewUser(v);
            }
        });

        find_notes_view.findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(v);
            }
        });
        return find_notes_view;
    }

    public void refreshFragment()
    {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
    public void createNewUser(View vw){
        String user_email = ((TextInputEditText)(find_notes_view.findViewById(R.id.email_text))).getText().toString();
        String user_password = ((TextInputEditText)(find_notes_view.findViewById(R.id.user_password))).getText().toString();
        
        bokxman.createUser(getContext(),user_email,user_password,auth_status);
    }

    public void loginUser(View vw){
        String user_email = ((TextInputEditText)(find_notes_view.findViewById(R.id.email_text))).getText().toString();
        String user_password = ((TextInputEditText)(find_notes_view.findViewById(R.id.user_password))).getText().toString();

        bokxman.loginUser(getContext(),user_email,user_password,auth_status,has_auth);
    }

    public void signupCallback()
    {

    }

    public void updateAuthStatus(String auth_status){
        ((TextView)find_notes_view.findViewById(R.id.auth_status)).setText(auth_status);
    }
    public void findNote(View vw)
    {
        search_results.clear();
        String query_string = note_query_input.getText().toString();
        String notes_index_path = "find_notes_connector.getIndexDirectory();";
        Searcher note_searcher = null;
        Log.i("Notes directory: ",notes_index_path);
        search_results.add(new NoteQA(query_string,""));
        //int all_hits = find_notes_connector.searchNotes(search_results,query_string);
        /*
        TopDocs topDocs;
        int all_hits = 0;
        try {
            note_searcher = new Searcher(notes_index_path);

            topDocs = note_searcher.search(query_string);
            all_hits  = topDocs.totalHits;
            Log.i("Number of hits: ",Integer.toString(all_hits));
            ScoreDoc scoreDoc[] = topDocs.scoreDocs;
            Log.i("Number of scores: ",Integer.toString(scoreDoc.length));
            for(ScoreDoc score: scoreDoc)
            {
                Document this_doc = note_searcher.getDocument(score);
                String file_data = this_doc.get(LuceneConstants.CONTENTS);
                String file_link = this_doc.get(LuceneConstants.FILE_NAME);

                Log.i("gotten file: ", file_link+"\n\n"+file_data);
            }

            note_searcher.close();
        }
        catch(IOException ioe)
        {
            Log.e("searcher error: ", ioe.getMessage());

        }
        catch(ParseException ioe)
        {
            Log.e("searcher error: ", ioe.getMessage());
        }
        */
        note_results_adapter.notifyDataSetChanged();
        Toast.makeText(getContext(),"Looking for Note: "+query_string+"__with hits: ",Toast.LENGTH_LONG).show();

    }

    public void runModel(View vw){
        search_results.clear();
        String query_string = note_query_input.getText().toString();
        recent_query = query_string;
        String notes_index_path = "find_notes_connector.getIndexDirectory();";
        Searcher note_searcher = null;
        Log.i("Notes directory: ",notes_index_path);

        search_results.add(new NoteQA(query_string,"Thinking my friend"));
        note_results_adapter.notifyDataSetChanged();
        String user_id = user_session.getUser().getEmail();
        QABuilder qa_made_json = new QABuilder(query_string,"query",user_id);
        QAModel qaModel = qa_made_json.getQAModel();
        SieveBaseModel sieveBaseModel = new SieveBaseModel(qaModel);
        String made_json = qa_made_json.makeJson();
        Log.i("Json query",made_json);
        SieveModel sieveModel = new SieveModel(function_name,sieveBaseModel);
        String sieve_json = sieveModel.makeJson();
        Log.i("Json query",sieve_json);



        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                                if(model_free)
                                {
                                    sieveDataHelper.pushJob(sieve_json);
                                }

                            //String sieve_url = "https://mango.sievedata.com/v2/push";
                            //String sieve_api_key = "";

                            //String request_post = "{\n  \"workflow_name\": \"<string>\",\n  \"model_id\": \"<string>\",\n  \"inputs\": {}\n}";
                            /*MediaType JSON
                                    = MediaType.get("application/json");

                            RequestBody rb = RequestBody.create(sieve_json.getBytes(),JSON);
                            Log.i("json request body",rb.contentType().toString());
                            Request request = new Request.Builder().url(sieve_url).header("X-API-Key", sieve_api_key)
                                    .header("Host","mango.sievedata.com")
                                    .header("Content-Type", "application/json").header("Content-Length",""+rb.contentLength()).post(rb).build();
                            Log.i("unirest headers",request.headers().toString());
                            */
                            /*
                            okhttp.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call,IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response sieve_response) throws IOException
                                {
                                    String request_headers = call.request().headers().toString();
                                    Buffer kk = new Buffer();
                                    call.request().body().writeTo(kk);
                                    String request_str = kk.readUtf8();
                                    String request_body = request_str+"___"+call.request().body().contentLength();
                                    Log.i("request summary",request_headers+"\n"+request_body);
                                    String json_str = sieve_response.body().string();

                                    try {
                                        String job_status = new JSONObject(sieve_json).getString("status");


                                    }
                                    catch(Exception except){

                                    }
                                    int response_code = sieve_response.code();
                                    String response_headers = sieve_response.headers().toString();
                                    String response_msg = sieve_response.message();
                                    Log.i("unirest response json", json_str+"\n"+response_code+":\t"+response_headers+"\n"+response_msg);
                                }
                            });
                            */



                        }
                        catch(Exception unioe){
                            unioe.printStackTrace();
                            //Log.i("unirest exception",unioe.getMessage());
                        }
                    }
                }
        ).run();
    }



    public class BokxSieveCallBack implements Callback{
        @Override
        public void onFailure(Call call,IOException e) {
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
                String job_status = job_json.getString("status");
                String job_id = job_json.getString("id");
                if(job_status.equals("finished")){
                    JSONArray json_outputs = job_json.getJSONArray("outputs");
                    JSONObject query_output = json_outputs.getJSONObject(0);
                    String query_answer = query_output.getJSONObject("data").getString("answer");
                    search_results.clear();
                    search_results.add(new NoteQA(recent_query,query_answer));
                    Log.i("Job Status"," Job has been finished");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            note_results_adapter.notifyDataSetChanged();
                            model_free = true;
                        }
                    });
                }
                else if(job_status.equals("queued")){
                    recent_job = job_id;
                    model_free = false;
                    search_results.clear();
                    search_results.add(new NoteQA(recent_query,"Please wait ...."));

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            note_results_adapter.notifyDataSetChanged();
                        }
                    });


                    new Thread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    sieveDataHelper.getJob(job_id);
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
                                    sieveDataHelper.getJob(job_id);
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
