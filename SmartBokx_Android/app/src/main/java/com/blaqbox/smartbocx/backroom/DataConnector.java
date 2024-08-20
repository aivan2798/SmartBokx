package com.blaqbox.smartbocx.backroom;

import android.content.Context;
import android.util.Log;

import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.db.DBHandler;
import com.blaqbox.smartbocx.db.Note;
import com.blaqbox.smartbocx.ui.adapters.NotesListAdapter;
import com.bokx_lucene.Indexer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataConnector
{
    private static List<Note> all_notes;
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


    private DataConnector()
    {
        all_notes  = new ArrayList<Note>();

    }


    public static Bokxman getBokxmanInstance()
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

    public static DataConnector getInstance(Context context)
    {
        if ((db_context!=null)){
            return data_connector;
        }
        db_context = context.getApplicationContext();
        project_key = context.getResources().getString(R.string.supabase_apikey);
        project_url = context.getResources().getString(R.string.supabase_url);
        bokxman = new Bokxman(project_key,project_url);
        dbHandler = new DBHandler(db_context,all_notes);
        boolean is_okay = dbHandler.checkTable();
        if(is_okay==false)
        {
            dbHandler.migrateToFTS4Table();
        }
        all_notes = dbHandler.getNotes();
        all_notes_adapter = new NotesListAdapter(all_notes);
        context_set = true;

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
        dbHandler.addNewNote(note_name,note_link,note_description);

        if(all_notes_adapter!=null) {
            all_notes_adapter.notifyDataSetChanged();
        }
    }

    public List<Note> getAllNotes()
    {
        return all_notes;
    }

}
