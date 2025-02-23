package com.blaqbox.smartbocx.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
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

    int view_index = 0;
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
          AppCompatButton speak_btn = note_summary_view.findViewById(R.id.note_speak);
          speak_btn.setOnClickListener(this::speakNote);
          speak_btn.setTag(view_index);

          AppCompatButton del_btn = note_summary_view.findViewById(R.id.note_delete);
          del_btn.setOnClickListener(this::deleteNote);
          del_btn.setTag(view_index);

          active_note = note_summary_view.findViewById(R.id.note_hint);
          Log.i("VIEW_INDEX", "using index "+view_index);
          view_index = view_index+1;

        return new NotesListViewHolder(note_summary_view);
    }


    public void deleteNote(View vw){

        int tag_index = (Integer)vw.getTag();
        Log.i("TAG_INDEX","deleting tag with: "+tag_index);
        String note_name = all_notes.get(tag_index).note_name;
        Log.i("deleting_note",note_name);
        if(note_name!=null){

            if(note_name.length()>0)
            {
                DataConnector.getInstance().deleteNote(tag_index,note_name);
            }
            else{
                //DataConnector.speakManStatic("No note now");
            }
        }
        else{
            //DataConnector.speakManStatic("No note answer now");
        }

    }

    public void speakNote(View vw){

        int tag_index = (Integer)vw.getTag();
        Log.i("TAG_INDEX","speaking tag with: "+tag_index);
        String note_answer = all_notes.get(tag_index).note_description;
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
        //viewHolder.itemView
        viewHolder.note_description.setText(note.note_description);
        Log.i("BIND STATUS","Binding view");
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
