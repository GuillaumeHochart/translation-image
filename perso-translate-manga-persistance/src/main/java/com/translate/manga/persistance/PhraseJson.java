package com.translate.manga.persistance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PhraseJson implements Serializable {


    private List<CoordonneesJson> coordonneesJsonList;

    private String phrase;

    private String phraseTraduite;


    public PhraseJson(){
        coordonneesJsonList=new ArrayList<CoordonneesJson>();
    }

    public PhraseJson(List<CoordonneesJson> coordonneesJsons, String phrase) {
        coordonneesJsonList=coordonneesJsons;
        this.phrase = phrase;
    }

    public List<CoordonneesJson> getCoordonneesJsonList() {
        return coordonneesJsonList;
    }

    public void setCoordonneesJsonList(List<CoordonneesJson> coordonneesJsonList) {
        this.coordonneesJsonList = coordonneesJsonList;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getPhraseTraduite() {
        return phraseTraduite;
    }

    public void setPhraseTraduite(String phraseTraduite) {
        this.phraseTraduite = phraseTraduite;
    }
}
