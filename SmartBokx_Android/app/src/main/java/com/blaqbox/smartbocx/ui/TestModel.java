package com.blaqbox.smartbocx.ui;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.blaqbox.smartbocx.BertOps;
import com.blaqbox.smartbocx.R;
import com.blaqbox.smartbocx.backroom.*;
import com.blaqbox.smartbocx.backroom.DataConnector;
import com.google.android.material.textfield.TextInputEditText;

import org.tensorflow.lite.task.text.qa.QaAnswer;

import java.util.List;

import io.github.jan.supabase.gotrue.user.UserSession;


public class TestModel extends Fragment {
    Bokxman bokxman;
    TextView query_answer;
    EditText query_txt;

    TextInputEditText context_text;

    BertOps bert_man;
    public TestModel() {
        // Required empty public constructor

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bert_man = new BertOps(this.getContext(),"");
        bokxman = DataConnector.getBokxmanInstance();
        /*UserSession userSession = bokxman.getUserSession();
        if(userSession==null)
        {
            Log.i("Test Model","no user session");
            //bokxman.createUser("aalg2798007@gmail.com","bokxman4altalgo");
        }
        else{
            String user_data = userSession.getUser().toString();
            Log.i("Test Model","user session: "+user_data);
        }

         */
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View model_test_view = inflater.inflate(R.layout.fragment_test_model, container, false);

        context_text = model_test_view.findViewById(R.id.model_context);
        query_answer = model_test_view.findViewById(R.id.answer_text);
        query_txt = model_test_view.findViewById(R.id.query_text);


        AppCompatButton ask_button =  model_test_view.findViewById(R.id.ask_model_btn);
        ask_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryAnswer(v);
            }
        });
        return model_test_view;
    }

    public void queryAnswer(View vw)
    {
        String context = context_text.getText().toString();
        String query = query_txt.getText().toString();

        List<QaAnswer> bert_answers = bert_man.getAnswers(context,query);
        int ans_count = 0;
        query_answer.setText("Answer:\n\n");
        for(QaAnswer bert_answer: bert_answers)
        {
            query_answer.append("\n\n"+ans_count+" : "+bert_answer.text);
            ans_count = ans_count+1;
        }

    }
}