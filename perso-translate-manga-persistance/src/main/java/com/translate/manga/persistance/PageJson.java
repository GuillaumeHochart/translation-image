package com.translate.manga.persistance;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "PageJson")
public class PageJson {

    @Id
    private String key; //name-project + name page

    private String filename;

    private String formatName;

    private byte[] contentNotModified;

    private byte[] contentTraducted;

    private List<PhraseJson> phraseJsonList;

    public PageJson(){}

    public PageJson(String key,String filename, String formatName, byte[] contentNotModified) {
        this.filename = filename;
        this.key=key;
        this.formatName=formatName;
        this.contentNotModified = contentNotModified;
    }

    public String getFormatName() {
        return formatName;
    }

    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public byte[] getContentTraducted() {
        return contentTraducted;
    }

    public void setContentTraducted(byte[] contentTraducted) {
        this.contentTraducted = contentTraducted;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getContentNotModified() {
        return contentNotModified;
    }

    public void setContentNotModified(byte[] contentNotModified) {
        this.contentNotModified = contentNotModified;
    }

    public List<PhraseJson> getPhraseJsonList() {
        return phraseJsonList;
    }

    public void setPhraseJsonList(List<PhraseJson> phraseJsonList) {
        this.phraseJsonList = phraseJsonList;
    }
}
