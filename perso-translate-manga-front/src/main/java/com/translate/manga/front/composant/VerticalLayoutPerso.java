package com.translate.manga.front.composant;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class VerticalLayoutPerso extends VerticalLayout {

    private MenuPrincipal menuPrincipal;

    private HorizontalLayout contentGlobal;

    private VerticalLayout content;

    public VerticalLayoutPerso(){
        addClassName("page-perso");

        menuPrincipal=new MenuPrincipal(this);

        add(menuPrincipal);

        contentGlobal=new HorizontalLayout();
        contentGlobal.addClassName("content-global");

        add(contentGlobal);

        content=new VerticalLayout();
        content.addClassName("content-perso");

        contentGlobal.add(content);
    }

    public MenuPrincipal getMenuPrincipal() {
        return menuPrincipal;
    }

    public void setMenuPrincipal(MenuPrincipal menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
    }

    public HorizontalLayout getContentGlobal() {
        return contentGlobal;
    }

    public void setContentGlobal(HorizontalLayout contentGlobal) {
        this.contentGlobal = contentGlobal;
    }

    public VerticalLayout getContent() {
        return content;
    }

    public void setContent(VerticalLayout content) {
        this.content = content;
    }
}
