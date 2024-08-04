package com.blaqbox.smartbocx.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.db.Note;
import com.blaqbox.smartbocx.db.NoteQA;
import com.blaqbox.smartbocx.ui.viewholders.NoteQAListViewHolder;

import java.util.List;

public class NoteQAAdapter extends RecyclerView.Adapter<NoteQAListViewHolder>{
    List<NoteQA> all_notes;
    public NoteQAAdapter(List<NoteQA> notes_list)
    {
        all_notes = notes_list;
    }
    @Override
    public NoteQAListViewHolder onCreateViewHolder(ViewGroup viewParent, int viewType)
    {
        Context context = viewParent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View note_summary_view = layoutInflater.inflate(R.layout.note_qa_view,viewParent,false);

        return new NoteQAListViewHolder(note_summary_view);
    }

    @Override
    public void onBindViewHolder(NoteQAListViewHolder viewHolder, int view_position)
    {
        NoteQA note = all_notes.get(view_position);
        viewHolder.note_question.setText(note.note_question);
        viewHolder.note_answer.setText(note.note_answer);
    }

    @Override
    public int getItemCount()
    {
        return all_notes.size();
    }

    //@Override
    public NoteQA getItem(int position)
    {
        return all_notes.get(position);
    }
}
