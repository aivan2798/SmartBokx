package com.blaqbox.smartbocx.ui;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.backroom.DataConnector;
import com.blaqbox.smartbocx.db.Note;
import com.blaqbox.smartbocx.ui.adapters.NotesListAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.bokx_lucene.Searcher;

import java.util.ArrayList;
import java.util.List;


public class FindNotes extends Fragment {
    AppCompatButton find_notes_btn;
    View find_notes_view;

    DataConnector find_notes_connector;
    NotesListAdapter note_results_adapter;
    TextInputEditText note_query_input;

    List<Note> search_results;

    RecyclerView notes_results;
    public FindNotes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        search_results = new ArrayList<>();
        find_notes_view =  inflater.inflate(R.layout.fragment_find_notes, container, false);
        note_results_adapter = new NotesListAdapter(search_results);

        note_query_input = find_notes_view.findViewById(R.id.find_notes_context);
        find_notes_btn = find_notes_view.findViewById(R.id.find_note_btn);
        notes_results = find_notes_view.findViewById(R.id.notes_results_view);
        notes_results.setLayoutManager(new LinearLayoutManager(FindNotes.this.getContext()));

        notes_results.setAdapter(note_results_adapter);
        find_notes_btn.setOnClickListener(this::findNote);

        find_notes_connector = DataConnector.getInstance();
        return find_notes_view;
    }

    public void findNote(View vw)
    {
        search_results.clear();
        String query_string = note_query_input.getText().toString();
        String notes_index_path = find_notes_connector.getIndexDirectory();
        Searcher note_searcher = null;
        Log.i("Notes directory: ",notes_index_path);
        int all_hits = find_notes_connector.searchNotes(search_results,query_string);
        /*
        TopDocs topDocs;
        int all_hits = 0;
        try {
            note_searcher = new Searcher(notes_index_path);

            topDocs = note_searcher.search(query_string);
            all_hits  = topDocs.totalHits;
            Log.i("Number of hits: ",Integer.toString(all_hits));
            ScoreDoc scoreDoc[] = topDocs.scoreDocs;
            Log.i("Number of scores: ",Integer.toString(scoreDoc.length));
            for(ScoreDoc score: scoreDoc)
            {
                Document this_doc = note_searcher.getDocument(score);
                String file_data = this_doc.get(LuceneConstants.CONTENTS);
                String file_link = this_doc.get(LuceneConstants.FILE_NAME);

                Log.i("gotten file: ", file_link+"\n\n"+file_data);
            }

            note_searcher.close();
        }
        catch(IOException ioe)
        {
            Log.e("searcher error: ", ioe.getMessage());

        }
        catch(ParseException ioe)
        {
            Log.e("searcher error: ", ioe.getMessage());
        }
        */
        note_results_adapter.notifyDataSetChanged();
        Toast.makeText(getContext(),"Looking for Note: "+query_string+"__with hits: ",Toast.LENGTH_LONG).show();

    }

    public void refreshFragment()
    {
        if((getActivity()!=null)&&isAdded()){
            FragmentManager fm = getFragmentManager();
            Log.i("FM STATUS FindNotes","FM NOT NULL");
            fm.beginTransaction().detach(this).attach(this).commit();
        }
        else{
            Log.i("FM STATUS","FM IS NULL");
        }
    }
}