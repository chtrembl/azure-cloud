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

    @JsonProperty("aiActionItems")
	private String aiActionItems;

	private List<String> aiActionItemsList;

    public AudioData() {  }

    public AudioData(String id, String audioAsText, String aiActionItems) {
        this.setId(id);
        this.audioAsText = audioAsText; 
        this.aiActionItems = aiActionItems;

        this.setAiActionItemsList(this.aiActionItems);
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
    public void setAiActionItems(String aiActionItems) {
        this.aiActionItems = aiActionItems;
        this.setAiActionItemsList(this.aiActionItems);
    }
    public String getAiActionItems() {
        return this.aiActionItems;
    }
    public List<String> getAiActionItemsList() {
        return this.aiActionItemsList;
    }
    public void setAiActionItemsList(String aiActionItems) {
        this.aiActionItemsList = List.of(aiActionItems.split("- "));
        this.aiActionItemsList = this.aiActionItemsList.subList(1, this.aiActionItemsList.size());
    }
}
