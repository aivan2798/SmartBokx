package com.blaqbox.smartbocx.db;

public class Note {
    String note_name;
    String note_link;
    String note_description;

    public Note(String anote_name, String anote_link, String anote_description)
    {
        note_name = anote_name;
        note_description = anote_description;
        note_link = anote_link;
    }
}
