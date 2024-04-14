package com.blaqbox.smartbocx.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "Notes_db";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "mycourses";

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
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
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
        long row_id = db.insert(TABLE_NAME, null, values);
        Log.i("added row_id","Row: "+row_id);
        // at last we are closing our
        // database after adding database.
        db.close();
    }

    public List<Note> getNotes()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        List<Note> notes_list = new ArrayList<Note>();
        if (cursor.moveToFirst()) {
            do {
                String link_txt = cursor.getString(2);
                String link_decription = cursor.getString(3);
                notes_list.add(new Note(link_txt,link_txt,link_decription));

                Log.i("link text", link_txt);
                // get  the  data into array,or class variable
            } while (cursor.moveToNext());
        }
        db.close();
        return notes_list;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);


    }
}