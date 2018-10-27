package com.translate.manga.front.Enum;

public enum  EnumVaadinSession {

    AUTHORIZATION("Authorization");

    private final String nom;

    EnumVaadinSession(String nom){
        this.nom=nom;
    }

    public String getNom() {
        return nom;
    }
}

