package com.blaqbox.smartbocx.ui.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.blaqbox.smartbocx.R;

public class NotesListViewHolder extends RecyclerView.ViewHolder {

    public TextView note_link;
    public TextView note_description;

    View note_summary;
    public NotesListViewHolder(View main_viewholder)
    {
        super(main_viewholder);
        note_link = main_viewholder.findViewById(R.id.note_title);
        note_description = main_viewholder.findViewById(R.id.note_hint);
        note_summary = main_viewholder;
    }
}
