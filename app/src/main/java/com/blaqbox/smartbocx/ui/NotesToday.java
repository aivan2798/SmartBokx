package com.blaqbox.smartbocx.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.db.DBHandler;
import com.blaqbox.smartbocx.db.Note;
import com.blaqbox.smartbocx.ui.adapters.NotesListAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotesToday#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesToday extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public List<Note> all_notes;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView notes_today_recycler_view;

    NotesListAdapter all_notes_adapter;
    public NotesToday() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotesToday.
     */
    // TODO: Rename and change types and number of parameters
    public static NotesToday newInstance(String param1, String param2) {
        NotesToday fragment = new NotesToday();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        all_notes = new DBHandler(this.getContext()).getNotes();
        // Inflate the layout for this fragment
        View notes_today_fragment = inflater.inflate(R.layout.fragment_notes_today, container, false);
        notes_today_recycler_view = notes_today_fragment.findViewById(R.id.notes_recycler_view);
        all_notes_adapter = new NotesListAdapter(all_notes);

        notes_today_recycler_view.setLayoutManager(new LinearLayoutManager(NotesToday.this.getContext()));
        notes_today_recycler_view.setAdapter(all_notes_adapter);
        return notes_today_fragment;
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    public void getNotes()
    {
        all_notes = new DBHandler(this.getContext()).getNotes();
    }


}