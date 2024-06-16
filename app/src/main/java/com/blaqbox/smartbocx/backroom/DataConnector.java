package com.blaqbox.smartbocx.backroom;

import android.content.Context;
import android.util.Log;

import com.blaqbox.smartbocx.db.DBHandler;
import com.blaqbox.smartbocx.db.Note;
import com.blaqbox.smartbocx.ui.adapters.NotesListAdapter;

import java.util.ArrayList;
import java.util.List;

public class DataConnector
{
    private static List<Note> all_notes;
    private static DBHandler dbHandler;

    private static NotesListAdapter all_notes_adapter;
    private static Context db_context;
    private static boolean context_set = false;
    private static final DataConnector data_connector  = new DataConnector();


    private DataConnector()
    {
        all_notes  = new ArrayList<Note>();

    }

    public static DataConnector getInstance()
    {
        if(context_set==true)
        {
            Log.i("context status: ", "context has been set");
        }
        else
        {
            Log.i("context status: ", "context isnot set");
        }
        return data_connector;
    }

    public static DataConnector getInstance(Context context)
    {
        db_context = context;
        dbHandler = new DBHandler(db_context,all_notes);
        all_notes = dbHandler.getNotes();
        all_notes_adapter = new NotesListAdapter(all_notes);
        context_set = true;
        return data_connector;
    }

    public NotesListAdapter getAllNotesAdapter()
    {
        return all_notes_adapter;
    }
    public int refresh()
    {
        all_notes_adapter.notifyDataSetChanged();

        return all_notes.size();
    }

    public void addNewNote(String note_name, String note_link, String note_description)
    {
        dbHandler.addNewNote(note_name,note_link,note_description);
        all_notes_adapter.notifyDataSetChanged();
    }

    public List<Note> getAllNotes()
    {
        return all_notes;
    }

}
