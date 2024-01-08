package com.chtrembl.petstoreassistant.model;

import java.io.Serializable;

import com.chtrembl.petstoreassistant.service.AzureAIServices.Classification;

public class Prompt implements Serializable{
    private Classification classification = null;
    private String question = null;
    private String answer = null;

    public Prompt() {
        super();
    }
    public Prompt(Classification classification, String question, String answer) {
        super();
        this.classification = classification;
        this.question = question;
        this.answer = answer;
    }
    public Classification getClassification() {
        return classification;
    }
    public void setClassification(Classification classification) {
        this.classification = classification;
    }
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
