package com.blaqbox.smartbocx.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.backroom.DataConnector;
import com.blaqbox.smartbocx.db.Note;
import com.blaqbox.smartbocx.db.NoteQA;
import com.blaqbox.smartbocx.ui.viewholders.NoteQAListViewHolder;

import java.util.List;

public class NoteQAAdapter extends RecyclerView.Adapter<NoteQAListViewHolder>{
    List<NoteQA> all_notes;
    TextView bokx_loader_icon;
    Context context;
    Animation bokx_reply_animation;
    View note_summary_view;
    TextView active_note;
    public NoteQAAdapter(List<NoteQA> notes_list)
    {
        all_notes = notes_list;
    }
    @Override
    public NoteQAListViewHolder onCreateViewHolder(ViewGroup viewParent, int viewType)
    {
        context = viewParent.getContext();
        bokx_reply_animation = AnimationUtils.loadAnimation(context,R.anim.faded);
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        note_summary_view = layoutInflater.inflate(R.layout.note_qa_view,viewParent,false);
        bokx_loader_icon = note_summary_view.findViewById(R.id.bokx_reply_icon);
        note_summary_view.findViewById(R.id.note_speak).setOnClickListener(this::speakNote);
        active_note = note_summary_view.findViewById(R.id.note_answer);
        return new NoteQAListViewHolder(note_summary_view);
    }

    public void speakNote(View vw){


        String note_answer = active_note.getText().toString();
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
    public void onBindViewHolder(NoteQAListViewHolder viewHolder, int view_position)
    {

        NoteQA note = all_notes.get(view_position);
        viewHolder.note_question.setText(note.note_question);
        viewHolder.note_answer.setText(note.note_answer);
        if(!note.isFinalAnswer()){
            bokx_loader_icon.startAnimation(bokx_reply_animation);
        }

    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        Log.i("RECYCLER VIEW LOG", "detached from recycler view");
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

    public void stopAnimation(){
        bokx_loader_icon.clearAnimation();
    }

    public void startAnimation(){
        bokx_loader_icon.startAnimation(bokx_reply_animation);
    }
}
