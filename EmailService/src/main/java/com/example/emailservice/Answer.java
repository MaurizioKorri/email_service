package com.example.emailservice;

import java.io.Serializable;

public class Answer implements Serializable {

    private int code;
    private String text;

    public Answer(int code, String text) {
        this.code = code;
        this.text = text;
    }
    public Answer(){};

    public int getAnswerCode(){
        return this.code;
    }

    public String getAnswerText(){
        return this.text;
    }

    @Override
    public String toString(){
        return "Answer: { errorCode = " + code + "errorText= " + text + '\'' + " }";
    }
}
