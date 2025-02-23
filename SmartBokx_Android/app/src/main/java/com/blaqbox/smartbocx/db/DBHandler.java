package com.blaqbox.smartbocx.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.blaqbox.smartbocx.ui.NotesToday;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "Notes_db";

    public List<Note> notes_list;
    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "mycourses";
    private static final String FTS_TABLE_NAME = "notes_fts";
    // below variable is for our id column.
    private static final String NOTE_ID = "note_id";

    // below variable is for our course name column
    private static final String NAME_COL = "name";

    // below variable id for our course duration column.
    private static final String LINK_COL = "link";

    // below variable for our course description column.
    private static final String DESCRIPTION_COL = "description";

// below variable is for our course tracks column.
private static final String TRACKS_COL = "tracks";

    // creating a constructor for our database handler.
    public DBHandler(Context context, List<Note> current_notes)
    {
        super(context, DB_NAME, null, DB_VERSION);
        this.notes_list = current_notes;
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + LINK_COL + " TEXT,"
                + DESCRIPTION_COL + " TEXT )";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }

    public void migrateToFTS4Table() {
        // Create the FTS4 table
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE VIRTUAL TABLE " + FTS_TABLE_NAME + " USING fts4("
                + NOTE_ID + ", "
                + NAME_COL + ", "
                + LINK_COL + ", "
                + DESCRIPTION_COL + ")");

        // Copy data from the original table to the FTS4 table
        db.execSQL("INSERT INTO " + FTS_TABLE_NAME + " ("
                + NOTE_ID + ", "
                + NAME_COL + ", "
                + LINK_COL + ", "
                + DESCRIPTION_COL + ") "
                + "SELECT " + NOTE_ID + ", "
                + NAME_COL + ", "
                + LINK_COL + ", "
                + DESCRIPTION_COL + " FROM " + TABLE_NAME);

        // Drop the original table if it's no longer needed
        //db.execSQL("DROP TABLE " + TABLE_NAME);
    }

    public void deleteNote(int index,String note_name)//, String note_link, String note_description)
    {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a

        // after adding all values we are passing
        // content values to our table.
        String delete_query = NAME_COL+" = ?";
        String delete_args[] = {note_name};
        int del_rows = db.delete(FTS_TABLE_NAME,delete_query,delete_args);

        Log.i("deleted rows","Row: "+del_rows);
        // at last we are closing our
        // database after adding database.
        db.close();

        Note dead_note = notes_list.remove(index);
        Log.i("DEAD_NOTE_NAME",dead_note.note_name);
        Log.i("DEAD_NOTE_LINK",dead_note.note_link);
        Log.i("DEAD_NOTE_INDEX",dead_note.note_description);
        /*
        notes_list.removeIf(new Predicate<Note>() {
            @Override
            public boolean test(Note note) {
                if(note.note_name == note_name)
                {
                    return true;
                }
                return false;
            }
        });

         */
                //add(0,new Note(note_name,note_link,note_description));
    }

    // this method is use to add new course to our sqlite database.
    public void addNewNote(String note_name, String note_link, String note_description) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(NAME_COL, note_name);
        values.put(LINK_COL, note_link);
        values.put(DESCRIPTION_COL, note_description);

        // after adding all values we are passing
        // content values to our table.
        long row_id = db.insert(FTS_TABLE_NAME, null, values);
        Log.i("added row_id","Row: "+row_id);
        // at last we are closing our
        // database after adding database.
        db.close();

        notes_list.add(0,new Note(note_name,note_link,note_description));
    }
    public boolean checkTable()
    {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{FTS_TABLE_NAME});
            if (cursor != null && cursor.moveToFirst()) {
                return true;
            }
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public List<Note> getNotes()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+FTS_TABLE_NAME,null);
        //List<Note> notes_list = new ArrayList<Note>();
        if (cursor.moveToFirst()) {
            do {
                int link_id = cursor.getInt(0);
                String name_txt = cursor.getString(1);
                String link_txt = cursor.getString(2);
                String link_decription = cursor.getString(3);
                int note_index = notes_list.size();
                notes_list.add(new Note(link_id,name_txt,link_txt,link_decription));

                Log.i("link text", link_txt);
                Log.i("name text", name_txt);

                // get  the  data into array,or class variable
            } while (cursor.moveToNext());
        }

        db.close();
        Collections.reverse(notes_list);
        return notes_list;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + FTS_TABLE_NAME);
        onCreate(db);


    }

    public int searchNotes(List<Note> note_results,String query) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM notes_fts WHERE notes_fts MATCH ?", new String[]{query});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int noteId = cursor.getInt(0);
                String name = cursor.getString(1);
                String link = cursor.getString(2);
                String description = cursor.getString(3);

                Log.i("search result: ",name+"_"+link+"_"+noteId+" : "+description);
                note_results.add(new Note(noteId, name, link, description));
            }
            cursor.close();
        }
        return note_results.size();
    }
}