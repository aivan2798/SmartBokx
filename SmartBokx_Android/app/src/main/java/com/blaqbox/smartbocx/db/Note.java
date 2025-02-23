package com.blaqbox.smartbocx.db;

import android.os.Build;
import android.util.Log;

import com.blaqbox.smartbocx.Models.NoteJson;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class Note {

    public long note_id;

    public int note_index;
    public String note_name;
    public String note_link;
    public String note_description;

    public Note(String anote_name, String anote_link, String anote_description)
    {
        if(Build.VERSION.SDK_INT>25) {
            note_id = Instant.now().getEpochSecond();
        }
        else{
            note_id = new Date().getTime();
        }

        Log.i("Note_ID",""+note_id);
        Log.i("Note_name",anote_name);
        Log.i("Note_link",anote_link);
        note_name = anote_name;
        note_description = anote_description;
        note_link = anote_link;
    }

    public void setNoteID(int anote_id)
    {
        note_id = anote_id;
    }

    public void setNoteIndex(int anote_index)
    {
        note_index = anote_index;
    }
    public Note(int anote_id,String anote_name, String anote_link, String anote_description)
    {
        note_id = anote_id;
        note_name = anote_name;
        note_description = anote_description;
        note_link = anote_link;
    }

    public Note(int anote_id,int anote_index,String anote_name, String anote_link, String anote_description)
    {
        note_id = anote_id;
        note_index = anote_index;
        note_name = anote_name;
        note_description = anote_description;
        note_link = anote_link;
    }

    public NoteJson toJson()
    {
        return new NoteJson(note_name,note_link,note_description);
    }
}
