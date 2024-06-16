package com.blaqbox.smartbocx.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.backroom.DataConnector;
import com.blaqbox.smartbocx.db.DBHandler;
import com.google.android.material.textfield.TextInputEditText;

/**
 * TODO: document your custom view class.
 */
public class ExDialog extends DialogFragment {
    TextInputEditText link_text;
    TextInputEditText note_desc;

    AppCompatButton save_note_btn;

    DataConnector db_handler;
    @Nullable
    @Override


    public View onCreateView(LayoutInflater layout_inflator, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        db_handler = DataConnector.getInstance();
                //new DBHandler(this.getContext());
        View dialog_view = layout_inflator.inflate(R.layout.fragment_add_note,container,false);
        link_text = dialog_view.findViewById(R.id.link_text);
        note_desc = dialog_view.findViewById(R.id.note_desc);
        save_note_btn = dialog_view.findViewById(R.id.save_note_btn);

        save_note_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote(v);
            }
        });
        getDialog().setTitle("Add Note");
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.curved_background);
        return dialog_view;
    }

    public void saveNote(View vw)
    {
        String link = link_text.getText().toString();

        String link_description = note_desc.getText().toString();
        Toast.makeText(this.getContext(), "Note Saved: "+link+"\n\n"+link_description, Toast.LENGTH_SHORT).show();
        db_handler.addNewNote("Link Note",link,link_description);

    }
}