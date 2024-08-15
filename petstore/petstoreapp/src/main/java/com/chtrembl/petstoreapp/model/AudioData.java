package com.chtrembl.petstoreapp.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
@Component
public class AudioData implements Serializable {
	@JsonProperty("id")
	private String id;

    @JsonProperty("audioAsText")
	private String audioAsText;

    @JsonProperty("actionItems")
	private List<String> actionItems;

    @JsonProperty("summary")
	private String summary;

    @JsonProperty("tone")
	private String tone;

    public AudioData() {  }

    public AudioData(String id, String audioAsText, List<String> actionItems, String summary, String tone) {
        this.setId(id);
        this.audioAsText = audioAsText; 
        this.actionItems = actionItems;
        this.summary = summary;
        this.tone = tone;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return this.id;
    }
    public void setAudioAsText(String audioAsText) {
        this.audioAsText = audioAsText;
    }
    public String getAudioAsText() {
        return this.audioAsText;
    }
    public void setActionItems(List<String> actionItems) {
        this.actionItems = actionItems;
    }
    public List<String> getActionItems() {
        return this.actionItems;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public String getSummary() {
        return this.summary;
    }
    public void setTone(String tone) {
        this.tone = tone;
    }
    public String getTone() {
        return this.tone;
    } 
}
