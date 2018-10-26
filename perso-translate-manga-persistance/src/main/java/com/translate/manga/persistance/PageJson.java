package com.translate.manga.persistance;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "PageJson")
public class PageJson {

    @Id
    private String filename;

    private byte[] content;

    private List<PhraseJson> phraseJsonList;

    public PageJson(){}

    public PageJson(String filename, byte[] content) {
        this.filename = filename;
        this.content = content;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public List<PhraseJson> getPhraseJsonList() {
        return phraseJsonList;
    }

    public void setPhraseJsonList(List<PhraseJson> phraseJsonList) {
        this.phraseJsonList = phraseJsonList;
    }
}
