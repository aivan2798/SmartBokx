package com.blaqbox.smartbocx.ui.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.blaqbox.smartbocx.R;

public class NoteQAListViewHolder extends RecyclerView.ViewHolder {

    public TextView note_question;
    public TextView note_answer;

    View note_qa;
    public NoteQAListViewHolder(View main_viewholder)
    {
        super(main_viewholder);
        note_question = main_viewholder.findViewById(R.id.note_question);
        note_answer = main_viewholder.findViewById(R.id.note_answer);
        note_qa = main_viewholder;
    }
}
