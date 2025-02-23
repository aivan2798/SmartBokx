package com.blaqbox.smartbocx.ui;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blaqbox.smartbocx.MainActivity;
import com.blaqbox.smartbocx.Models.BokxCredits;
import com.blaqbox.smartbocx.Models.QABuilder;
import com.blaqbox.smartbocx.Models.QAModel;
import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.backroom.Bokxman;
import com.blaqbox.smartbocx.backroom.DataConnector;
import com.blaqbox.smartbocx.db.NoteQA;
import com.blaqbox.smartbocx.ui.adapters.NoteQAAdapter;
import com.blaqbox.smartbocx.utils.BokxAPIHelper;
import com.google.android.gms.ads.AbstractAdRequestBuilder;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewarded.ServerSideVerificationOptions;
import com.google.android.material.textfield.TextInputEditText;


import okhttp3.Call;
import okhttp3.Callback;

import com.bokx_lucene.Searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.jan.supabase.gotrue.user.UserSession;


import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

import com.blaqbox.smartbocx.Models.SieveBaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class BokxBot extends Fragment {
    AppCompatButton find_notes_btn;
    public View find_notes_view;
    String recent_query;
    String recent_job;
    Bokxman bokxman;
    public NoteQAAdapter note_results_adapter;
    TextInputEditText note_query_input;
    String function_name = "";
    List<NoteQA> search_results;
    BokxAPIHelper bokxAPIHelper;

    RecyclerView notes_results;
    UserSession user_session;
    BokxAPICallBack bokxAPICallBack;
    //OkHttpClient okhttp;
    MutableLiveData<String> auth_status = new MutableLiveData<String>();
    TextView auth_status_view;
    ServerSideVerificationOptions serverSideVerificationOptions;
    RewardedAd rewardedAd;
    boolean model_free = true;

    MutableLiveData<Boolean> has_auth = new MutableLiveData<Boolean>();

    MutableLiveData<BokxCredits> bokx_credits = new MutableLiveData<>();
    AlertDialog alertDialog;
    Animation faded_animation;
    public BokxBot() {
        // Required empty public constructor

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("FRAGMENT STARTED","Fragment started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadAd();
        Log.i("FRAG","FRAGMENT RELOADED");
        bokxman = DataConnector.getBokxmanInstance();
        String bokx_url = getResources().getString(R.string.bokx_base_url);
        user_session = bokxman.getUserSession();
        Boolean login_stats = DataConnector.getAuthStatus().getValue();
        faded_animation = AnimationUtils.loadAnimation(this.getContext(),R.anim.faded);
        alertDialog = new AlertDialog.Builder(this.getContext(),R.style.BokxDialogTheme).create();

        auth_status.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.i("AUTH_STATUS","auth status has changed");
                auth_status_view.clearAnimation();
                auth_status_view.setText(s);
                /*
                try {


                    if (user_session == null) {

                        auth_status_view.setText(s);
                        Response credict = bokxAPIHelper.auth();
                        ResponseBody credits_body = credict.body();
                        JSONObject json_cedits = new JSONObject(credits_body.string());
                        int status = json_cedits.getInt("available_questions");

                        if(status==200)
                        {
                            String available_qn = ""+json_cedits.getInt("available_questions");
                            String available_ans = ""+json_cedits.getInt("available_answers");
                            showDialog("Auth Ok","User Authorized");
                            bokxman.getUserCredits(bokx_credits);
                        }
                        else{
                            String available_qn = ""+json_cedits.getString("message");
                            showDialog("Auth Not Ok",available_qn);
                        }

                    }

                }
                catch(IOException ioe){
                    alertDialog.setTitle("CONNECTION ERROR");
                    alertDialog.setMessage("Internal error");
                    alertDialog.show();
                }
                catch(JSONException joe){
                    alertDialog.setTitle("DATA ERROR");
                    alertDialog.setMessage("Couldn't parse data");
                    alertDialog.show();
                }
                */
            }
        });

        bokx_credits.observe(getViewLifecycleOwner(), new Observer<BokxCredits>() {
            @Override
            public void onChanged(BokxCredits credits) {

                if (user_session != null) {
                    ((TextView) find_notes_view.findViewById(R.id.request_current_count)).setText(String.valueOf(credits.getBokx_requests()));
                    ((TextView) find_notes_view.findViewById(R.id.request_initial_count)).setText(String.valueOf(credits.getBokx_request_limit()));
                    ((TextView) find_notes_view.findViewById(R.id.response_current_count)).setText(String.valueOf(credits.getBokx_responses()));
                    ((TextView) find_notes_view.findViewById(R.id.response_initial_count)).setText(String.valueOf(credits.getBokx_responses_limit()));
                }
            }
        });

        has_auth.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                Log.i("auth bool",s.toString());

                if(s==true){
                    //refreshFragment();
                }

            }
        });

        if(user_session!=null) {
            String user_id = user_session.getUser().getId();
            Log.i("using user id: ",user_id);
            serverSideVerificationOptions = new ServerSideVerificationOptions.Builder().setUserId(user_id).build();
            bokxman.getUserCredits(bokx_credits);
            bokxAPIHelper = new BokxAPIHelper(bokx_url,user_session.getAccessToken());
            //okhttp = new OkHttpClient();

            bokxAPICallBack = new BokxAPICallBack();
            bokxAPIHelper.setBokxCallback(bokxAPICallBack);
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
            find_notes_view.findViewById(R.id.add_credits_btn).setOnClickListener(this::showRewardedAd);


            return find_notes_view;
        }
        Log.i("using user id: ","NULL");
        find_notes_view = inflater.inflate(R.layout.fragment_auth_view, container, false);
        find_notes_view.findViewById(R.id.create_user_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth_status_view.setText("Creating Session, Please wait ..... ");
                auth_status_view.startAnimation(faded_animation);
                createNewUser(v);
            }
        });
        auth_status_view = ((TextView) find_notes_view.findViewById(R.id.auth_status));

        find_notes_view.findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth_status_view.setText("Creating Session, Please wait ..... ");
                auth_status_view.startAnimation(faded_animation);
                loginUser(v);
            }
        });
        return find_notes_view;
    }

    public void showDialog(String dialog_title,String dialog_msg){
        alertDialog.setTitle(dialog_title);
        alertDialog.setMessage(dialog_msg);
        alertDialog.show();
    }
    public void refreshFragment()
    {

        if(isAdded()){


            Log.i("FM STATUS BokxBot","FM NOT NULL");
            //getFragmentManager().beginTransaction().detach(this);
            //getFragmentManager().beginTransaction().attach(this).commit();
            FragmentTransaction at = getFragmentManager().beginTransaction();
            FragmentTransaction dt = getFragmentManager().beginTransaction();
            //t.setAllowOptimization(false);
            dt.detach(this);
            at.attach(this);
            dt.commit();
            at.commit();
        }
        else{
            Log.i("FM STATUS BokxBot","FM IS NULL");
        }
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

    public void showRewardedAd(View vw){


        if (rewardedAd != null) {
            ServerSideVerificationOptions ssvo = new ServerSideVerificationOptions.Builder().build();

            //Activity activityContext = MainActivity.this;
            rewardedAd.show(this.getActivity(), new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                    // Handle the reward.
                    Log.d("REWARED STATUS", "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    Log.d("REWARED AMOUNT", "The user earned the reward amount of: "+rewardAmount);
                    Log.d("REWARED TYPE", "The user earned the reward type of: "+rewardType);
                    int bokx_requests = bokx_credits.getValue().getBokx_requests();
                    int bokx_request_limit = bokx_credits.getValue().getBokx_request_limit()+1;
                    int bokx_responses = bokx_credits.getValue().getBokx_responses();
                    int bokx_responses_limit = bokx_credits.getValue().getBokx_responses_limit()+1;
                    bokx_credits.postValue(new BokxCredits(bokx_requests,bokx_request_limit,bokx_responses,bokx_responses_limit));
                    //bokxman.getUserCredits(bokx_credits);

                }
            });
        } else {
            Log.d("REWARD STATUS", "The rewarded ad wasn't ready yet.");
        }
        loadAd();
    }

    public void signupCallback()
    {

    }

    public void loadAd(){
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();


        RewardedAd.load(this.getContext(), getString(R.string.bokx_rewarded_ad_id), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.i("Ad status","ad failed");
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd xrewardedAd) {
                super.onAdLoaded(xrewardedAd);

                Log.i("Ad status","adloaded");
                rewardedAd = xrewardedAd;

                rewardedAd.setServerSideVerificationOptions(serverSideVerificationOptions);

                rewardedAd.setOnPaidEventListener(new OnPaidEventListener() {
                    @Override
                    public void onPaidEvent(@NonNull AdValue adValue) {
                        Log.i("Paid Status: ",""+adValue.getValueMicros());

                    }
                });
            }
        });
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
        //note_results_adapter.startAnimation();
        String user_id = "";
        //String user_token = user_session.getAccessToken();
        QABuilder qa_made_json = new QABuilder(query_string,"query",user_id);
        QAModel qaModel = qa_made_json.getQAModel();
        SieveBaseModel sieveBaseModel = new SieveBaseModel(qaModel);
        String made_json = qa_made_json.makeJson();
        Log.i("Json query",made_json);
        //SieveModel sieveModel = new SieveModel(function_name,sieveBaseModel);
        //String sieve_json = sieveModel.makeJson();
        //Log.i("Json query",sieve_json);



        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                                if(model_free)
                                {
                                    bokxAPIHelper.pushJob(made_json);
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




    public class BokxAPICallBack implements Callback{
        @Override
        public void onFailure(Call call,IOException e) {
            search_results.clear();
            String error_message = "Connection error";
            search_results.add(new NoteQA("Bokx Error",error_message,true));
            Log.i("Job Status"," Job has been finished");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    note_results_adapter.notifyDataSetChanged();

                    model_free = true;
                }
            });
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
                    search_results.clear();
                    String error_message = job_json.getString("message");
                    search_results.add(new NoteQA("Bokx Error",error_message,true));
                    Log.i("Job Status"," Job has been finished");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            note_results_adapter.notifyDataSetChanged();

                            model_free = true;
                        }
                    });

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

                    bokx_credits.postValue(new BokxCredits(bokx_requests,bokx_request_limit,bokx_responses,bokx_responses_limit));
                    String query_answer = query_output.getString("answer");
                    search_results.clear();
                    search_results.add(new NoteQA(recent_query,query_answer,true));
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
                            note_results_adapter.startAnimation();
                            note_results_adapter.notifyDataSetChanged();
                        }
                    });


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
