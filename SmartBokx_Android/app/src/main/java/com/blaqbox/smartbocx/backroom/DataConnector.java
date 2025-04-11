package com.blaqbox.smartbocx.backroom;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.MutableBoolean;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.blaqbox.smartbocx.Models.BokxCredits;
import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.db.DBHandler;
import com.blaqbox.smartbocx.db.Note;
import com.blaqbox.smartbocx.db.NoteQA;
import com.blaqbox.smartbocx.ui.adapters.NoteQAAdapter;
import com.blaqbox.smartbocx.ui.adapters.NotesListAdapter;
import com.bokx_lucene.Indexer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.jan.supabase.gotrue.user.UserSession;

public class DataConnector
{
    private static List<Note> all_notes;

    private static MutableLiveData<Boolean> sync_fin;
    private static NoteQAAdapter note_results_adapter;
    private static List<NoteQA> ai_search_results;
    private static String project_url = "";
    private static String project_key = "";
    private static Bokxman bokxman;// = new Bokxman(project_key,project_url);
    private static DBHandler dbHandler;
    private static String index_directory;
    private static NotesListAdapter all_notes_adapter;
    private static Context db_context;
    private static boolean context_set = false;

    private static Indexer note_index;
    private static final DataConnector data_connector  = new DataConnector();

    private static BokxCredits user_credits;

    private static LiveData<BokxCredits> bokx_credits;
    private static MutableLiveData<Boolean> auth_status;

    private static UserSession current_session;
    private static TextToSpeech tts_module;
    private static String session_string;
    private DataConnector()
    {
        all_notes  = new ArrayList<Note>();

    }

    public Context getDbContext() {
        return db_context;
    }

    public static UserSession getUserSession(){
        return  current_session;
    }

    public void setCredits(BokxCredits credits){
        user_credits = credits;
    }
    public static Bokxman getBokxmanInstance()
    {


        return bokxman;
    }

    public Bokxman getABokxmanInstance()
    {


        return bokxman;
    }

    public static DataConnector getInstance()
    {
        if(context_set==true)
        {
            Log.i("context status: ", "context has been set");
        }
        else
        {
            Log.i("context status: ", "context is not set");

        }
        /*
        if (note_index == null) {
            try {
                String data_path = db_context.getFilesDir().getPath();

                Log.i("Applicatiion datapath: ", data_path);
                note_index = new Indexer(data_path);

            }
            catch(IOException ioe)
            {

            }
        }
        else
        {
            Log.i("Indexer status: ","Indexer already instantiated");
        }

         */
        return data_connector;
    }

    public static MutableLiveData<Boolean> getAuthStatus() {
        return auth_status;
    }

    public MutableLiveData<Boolean> getTheAuthStatus() {
        return auth_status;
    }

    public MutableLiveData<Boolean> getSyncStatus() {
        return sync_fin;
    }
    public static void setSession_string(String xsession_string) {
       session_string = xsession_string;
    }

    public static void setUserSession(UserSession userSession){
        current_session = userSession;
    }

    public static DataConnector getInstance(Context context)
    {
        if ((db_context!=null)){
            return data_connector;
        }
        db_context = context.getApplicationContext();
        tts_module = new TextToSpeech(db_context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.i("TTS Status: ", "initialised tts status "+status);
            }
        });
        project_key = db_context.getResources().getString(R.string.supabase_apikey);
        project_url = context.getResources().getString(R.string.supabase_url);
        auth_status = new MutableLiveData<Boolean>(false);
        bokxman = new Bokxman(context,project_key,project_url,auth_status);
        UserSession xcurrent_session = bokxman.getUserSession();

        if(current_session!=null){
            Log.i("static session",current_session.getAccessToken());
        }
        else{
            Log.i("static session","static session null");
        }

        dbHandler = new DBHandler(db_context,all_notes);
        boolean is_okay = dbHandler.checkTable();
        if(is_okay==false)
        {
            dbHandler.migrateToFTS4Table();
        }
        all_notes = dbHandler.getNotes();
        all_notes_adapter = new NotesListAdapter(all_notes);

        ai_search_results = new ArrayList<NoteQA>();
        note_results_adapter =  new NoteQAAdapter(ai_search_results);
        sync_fin = new MutableLiveData<Boolean>(false);
        context_set = true;
        /*
        if (note_index == null) {
            try {
                String data_path = db_context.getExternalFilesDir(null).getPath();
                index_directory = data_path;
                File records = new File(data_path);
                if(records.isDirectory()&&records.exists()) {
                    records.delete();
                    Log.i("records status: ", "records cleared");
                }
                Log.i("2nd Application datapath: ", data_path);
                note_index = new Indexer(data_path);
                int created_notes = note_index.createNoteIndex(all_notes);
                note_index.close();
                Log.i("total number of index records: ", Integer.toString(created_notes));
            }
            catch(IOException ioe)
            {
                Log.e("Files Dir Error: ",ioe.getMessage());
            }
        }
        else
        {
            Log.i("Indexer status: ","Indexer already instantiated");
        }
        */
        return data_connector;
    }

    public String getIndexDirectory()
    {
        return index_directory;
    }
    public NotesListAdapter getAllNotesAdapter()
    {
        return all_notes_adapter;
    }



    public int searchNotes(List<Note> note_results,String query) {
        return dbHandler.searchNotes(note_results,query);
    }

    public void speakMan(String text_to_say){
        tts_module.speak(text_to_say,TextToSpeech.QUEUE_FLUSH,null,"");
    }

    public static void speakManStatic(String text_to_say){
        tts_module.speak(text_to_say,TextToSpeech.QUEUE_FLUSH,null,"");
    }

    public void killTTS() {
        if (tts_module != null) {
            tts_module.stop();
            tts_module.shutdown();
        }
    }


    public int refresh()
    {
        all_notes_adapter.notifyDataSetChanged();
        return all_notes.size();
    }

    public void migrateFTS4()
    {
        dbHandler.migrateToFTS4Table();
    }


    public void addNewNote(String note_name, String note_link, String note_description)
    {

        //long epoch_sec = new Date().getTime();

        //String note_name = anote_name+"_"+epoch_sec;
        Log.i("NEW NOTE","adding note: "+note_name);
        dbHandler.addNewNote(note_name,note_link,note_description);

        if(all_notes_adapter!=null) {
            all_notes_adapter.notifyDataSetChanged();
        }
    }

    public void addNewNotePlain(String note_name, String note_link, String note_description)
    {

        dbHandler.addNewNote(note_name,note_link,note_description);

    }

    public void deleteNote(int index,String note_name){
        dbHandler.deleteNote(index,note_name);
        if(all_notes_adapter!=null) {
            all_notes_adapter.notifyDataSetChanged();
        }
    }

    public List<Note> getAllNotes()
    {
        return all_notes;
    }

    public static NoteQAAdapter getNoteQAAdapter(){
        return note_results_adapter;
    }
    public static List<NoteQA> getAllAIResults()
    {
        return ai_search_results;
    }

}
