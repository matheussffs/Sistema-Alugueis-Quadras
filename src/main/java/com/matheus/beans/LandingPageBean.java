package com.matheus.beans;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@RequestScoped 
public class LandingPageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> imagensQuadras;

    @PostConstruct
    public void init() {
        imagensQuadras = new ArrayList<>();
        
        imagensQuadras.add("Society.jpg");
        imagensQuadras.add("Volei.jpg");
        imagensQuadras.add("Padel.jpg");
        imagensQuadras.add("BeachTennis.jpg");
        imagensQuadras.add("Tenis.jpg");
    }

    public List<String> getImagensQuadras() {
        return imagensQuadras;
    }
}