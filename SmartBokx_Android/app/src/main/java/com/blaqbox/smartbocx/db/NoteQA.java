package com.blaqbox.smartbocx.db;

public class NoteQA {
    public String note_answer;
    public String note_question;

    boolean final_answer = false;
    public NoteQA(String question, String answer){
        note_answer = answer;
        note_question = question;
    }

    public NoteQA(String question, String answer,boolean xfinal_answer){
        note_answer = answer;
        note_question = question;
        final_answer = xfinal_answer;
    }

    public boolean isFinalAnswer() {
        return final_answer;
    }
}
