package com.blaqbox.smartbocx.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;

import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.db.Note;
import com.blaqbox.smartbocx.ui.viewholders.NotesListViewHolder;

import java.util.List;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListViewHolder>
{

    List<Note> all_notes;
    public NotesListAdapter(List<Note> notes_list)
    {
        all_notes = notes_list;
    }
    @Override
    public NotesListViewHolder onCreateViewHolder(ViewGroup viewParent, int viewType)
    {
        Context context = viewParent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View note_summary_view = layoutInflater.inflate(R.layout.note_summary_view,viewParent,false);

        return new NotesListViewHolder(note_summary_view);
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

}
