package com.matheus.beans;

import com.matheus.entidades.Modalidade;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * @author Matheus Fassicollo
 */
@Named
@RequestScoped
public class ModalidadeBean implements Serializable {

    @PersistenceContext
    private EntityManager em;
    
    private List<Modalidade> modalidades;
    
    @PostConstruct
    public void init() {
        modalidades = em.createQuery("SELECT m FROM Modalidade m ORDER BY m.nome", Modalidade.class).getResultList();
    }
    
    public List<Modalidade> getModalidades() {
        return modalidades;
    }
}