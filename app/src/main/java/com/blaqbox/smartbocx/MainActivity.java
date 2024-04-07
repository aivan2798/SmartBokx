package com.blaqbox.smartbocx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.blaqbox.smartbocx.ui.ExDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    public boolean clipboard_service_state = false;
    ViewPager2 viewpager;
    View notes_list;
    ExDialog exDialog;
    FragmentManager fragment_manager;

    FloatingActionButton addnote_fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewpager = findViewById(R.id.tab_view_space);
        addnote_fab = findViewById(R.id.add_note_fab);
        addnote_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNoteView(v);
            }
        });
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fragment_manager = getSupportFragmentManager();
        //notes_list = layoutInflater.inflate(R.layout.activity_main,null);
        //viewpager.addView(notes_list);
        exDialog = new ExDialog();



    }

    public void setClipboard_service_state(View parent_view)
    {

        AppCompatButton parent_btn = (AppCompatButton) parent_view;

        if(clipboard_service_state == false) {
            Toast.makeText(this, "Clipboard Service Started", Toast.LENGTH_LONG).show();

            parent_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.clipboard_btn_off_state)));

        }
        else
        {
            parent_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.clipboard_btn_on_state)));
            Toast.makeText(this, "Clipboard Service Stoped", Toast.LENGTH_LONG).show();
        }

        clipboard_service_state = !clipboard_service_state;
    }

    public void showAddNoteView(View vw)
    {
        exDialog.show(fragment_manager," test");
    }
}