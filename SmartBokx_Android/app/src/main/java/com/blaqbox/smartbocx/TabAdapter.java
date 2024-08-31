package com.blaqbox.smartbocx;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.blaqbox.smartbocx.db.DBHandler;
import com.blaqbox.smartbocx.ui.BokxBot;
import com.blaqbox.smartbocx.ui.FindNotes;
import com.blaqbox.smartbocx.ui.NotesToday;
import com.blaqbox.smartbocx.ui.TestModel;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.internal.markers.KMutableList;

public class TabAdapter extends FragmentStateAdapter{
    NotesToday notesToday;
    FindNotes findNotes;

    BokxBot bokxBot;

    List<Fragment> fragmentList;
    FragmentActivity fragmentActivity;
    //DBHandler db_handler;
    public TabAdapter(FragmentActivity activity)//, DBHandler active_dbHandler)
    {
        super(activity);
        fragmentActivity = activity;
        notesToday = new NotesToday();
        findNotes = new FindNotes();
        bokxBot = new BokxBot();
        //db_handler = active_dbHandler;
    }

    public TabAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        fragmentList = new ArrayList<>();
        // Initialize with default fragments or data
    }

    @Override
    public long getItemId(int position) {
        long item_id = super.getItemId(position);
        return item_id;
    }


    @Override
    public void onBindViewHolder(@NonNull FragmentViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        Log.i("REFRESH STAT","Fragment refreshed "+position);
        /*
        switch (position)
        {
            case 0:
                notesToday.refreshFragment();
                break;

            case 1:
                findNotes.refreshFragment();
                break;

            case 2:
                bokxBot.refreshFragment();
                break;

            default:
                throw new IllegalStateException("Unexpected position " + position);
        }
        */

    }

    @Override
    public Fragment createFragment(int position)
    {

        switch (position)
        {
            case 0:
                return new NotesToday();
                //return notesToday;
            //break;

            case 1:
                return new FindNotes();
                //return findNotes;
            //break;

            case 2:
                return new BokxBot();
                //return bokxBot;
            //break;

            default:
                throw new IllegalStateException("Unexpected position " + position);
        }
        //return new FindNotes();
    }

    @Override
    public int getItemCount() {
        return 3;
    }




    public void refreshItem(int position){
        long item_id = getItemId(0);
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        Log.i("FRAG_COUNT",""+fragments.size());
        Log.i("frag item id: ",""+item_id);
        for(Fragment fragment: fragments){
            String frag_id = fragment.getTag();
            Log.i("FRAG_ID","This frag tag: "+frag_id);
            FragmentTransaction at = fragmentManager.beginTransaction();
            FragmentTransaction dt = fragmentManager.beginTransaction();
            //t.setAllowOptimization(false);
            dt.detach(fragment);
            at.attach(fragment);
            dt.commit();
            at.commit();
        }
        /*
        switch (position)
        {
            case 0:

                notesToday.refreshFragment();
            break;

            case 1:
                findNotes.refreshFragment();
            break;

            case 2:
                bokxBot.refreshFragment();
            break;

            default:
                throw new IllegalStateException("Unexpected position " + position);
        }
        */
    }





}
