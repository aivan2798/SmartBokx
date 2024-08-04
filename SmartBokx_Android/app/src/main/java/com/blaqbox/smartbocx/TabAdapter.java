package com.blaqbox.smartbocx;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.blaqbox.smartbocx.db.DBHandler;
import com.blaqbox.smartbocx.ui.BokxBot;
import com.blaqbox.smartbocx.ui.FindNotes;
import com.blaqbox.smartbocx.ui.NotesToday;
import com.blaqbox.smartbocx.ui.TestModel;

public class TabAdapter extends FragmentStateAdapter{

    //DBHandler db_handler;
    public TabAdapter(FragmentActivity activity)//, DBHandler active_dbHandler)
    {
        super(activity);
        //db_handler = active_dbHandler;
    }
    @Override
    public Fragment createFragment(int position)
    {

        switch (position)
        {
            case 0:
                return new NotesToday();
            //break;

            case 1:
                return new FindNotes();
            //break;

            case 2:
                return new BokxBot();
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
}
