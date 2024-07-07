package com.blaqbox.smartbocx;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddNote#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNote extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    LinearLayout add_note_banner_holder;
    private AdSize banner_adsize;
    AdView banner_adview;
    AdRequest banner_adrequest;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddNote() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddNote.
     */
    // TODO: Rename and change types and number of parameters
    public static AddNote newInstance(String param1, String param2) {
        AddNote fragment = new AddNote();
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
        new Thread(()->{
            MobileAds.initialize(getContext());
        }).start();
        // Inflate the layout for this fragment
        banner_adsize = AdSize.BANNER;
        banner_adrequest = new AdRequest.Builder().build();
        banner_adview = new AdView(getContext());
        banner_adview.setAdSize(banner_adsize);
        banner_adview.setAdUnitId(getResources().getString(R.string.main_banner_app_id));
        banner_adview.loadAd(banner_adrequest);

        View add_note_view = inflater.inflate(R.layout.fragment_add_note, container, false);
        add_note_banner_holder =  add_note_view.findViewById(R.id.add_note_banner);
        add_note_banner_holder.addView(banner_adview);
        return add_note_view;
    }
}