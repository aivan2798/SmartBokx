package com.blaqbox.smartbocx;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer;
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer.BertQuestionAnswererOptions;
import org.tensorflow.lite.task.text.qa.QaAnswer;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class BertOps {
    BertQuestionAnswererOptions options;
    BertQuestionAnswerer answerer;


    public BertOps(Context app_context,String model_path) {

        try {
            File asset_file = new File(app_context.getObbDir().getPath(),"bert.tflite");

            if(asset_file.exists())
            {
                Log.i("asset_status: ", "found");
            }
            else
            {
                Log.i("asset status: ","not found");
            }
            options = BertQuestionAnswererOptions.builder()
                    .setBaseOptions(BaseOptions.builder().setNumThreads(4).build())
                    .build();
            answerer = BertQuestionAnswerer.createFromFileAndOptions(asset_file, options);


        } catch (IOException ioe) {
                Log.e("IO exception", ioe.getMessage());
                System.out.println(ioe.getMessage());
        }
    }

    public List<QaAnswer> getAnswers(String contextOfTheQuestion, String questionToAsk)
    {
// Run inference
        List<QaAnswer> answers = answerer.answer(contextOfTheQuestion, questionToAsk);

        return answers;
    }
}
