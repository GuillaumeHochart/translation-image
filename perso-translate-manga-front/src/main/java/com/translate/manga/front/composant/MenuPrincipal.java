package com.translate.manga.front.composant;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;


public class MenuPrincipal extends HorizontalLayout {

    public MenuPrincipal (VerticalLayoutPerso verticalLayoutPerso){

        addClassName("menubar-content");

        Image image=new Image("./frontend/declasin-logo.png", "HybridMenu Logo");
        image.addClassName("logo");

        add(image);

        Button buttonUtilisateur=new Button("Utilisateur Lambda");
        buttonUtilisateur.addClassName("menuRight");


        add(buttonUtilisateur);

    }
}

