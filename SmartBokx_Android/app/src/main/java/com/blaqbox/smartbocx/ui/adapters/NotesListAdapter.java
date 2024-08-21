package com.blaqbox.smartbocx.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.backroom.DataConnector;
import com.blaqbox.smartbocx.db.Note;
import com.blaqbox.smartbocx.ui.viewholders.NoteQAListViewHolder;
import com.blaqbox.smartbocx.ui.viewholders.NotesListViewHolder;

import java.util.List;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListViewHolder>
{

    List<Note> all_notes;
    TextView active_note;
    View note_summary_view;
    public NotesListAdapter(List<Note> notes_list)
    {
        all_notes = notes_list;
    }
    @Override
    public NotesListViewHolder onCreateViewHolder(ViewGroup viewParent, int viewType)
    {
        Context context = viewParent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

         note_summary_view = layoutInflater.inflate(R.layout.note_summary_view,viewParent,false);
        note_summary_view.findViewById(R.id.note_speak).setOnClickListener(this::speakNote);
        active_note = note_summary_view.findViewById(R.id.note_hint);


        return new NotesListViewHolder(note_summary_view);
    }

    public void speakNote(View vw){


        String note_answer = active_note.getText().toString();
        Log.i("using note",note_answer);
        if(note_answer!=null){

            if(note_answer.length()>0)
            {
                DataConnector.speakManStatic(note_answer);
            }
            else{
                DataConnector.speakManStatic("No note now");
            }
        }
        else{
            DataConnector.speakManStatic("No note answer now");
        }

    }

    @Override
    public void onBindViewHolder(NotesListViewHolder viewHolder, int view_position)
    {
        Note note = all_notes.get(view_position);
        viewHolder.note_link.setText(note.note_link);
        viewHolder.note_description.setText(note.note_description);
    }

    @Override
    public int getItemCount()
    {
        return all_notes.size();
    }

    //@Override
    public Note getItem(int position)
    {
        return all_notes.get(position);
    }

}
