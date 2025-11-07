package com.matheus.beans;

import com.matheus.entidades.Modalidade;
import com.matheus.services.ModalidadeService;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
*
* @author Matheus Fassicollo
*/
@Named
@ViewScoped
public class ClienteHomeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ModalidadeService modalidadeService;

    private List<Modalidade> modalidades;

    @PostConstruct
    public void init() {
        // Carrega todas as modalidades cadastradas pelo Admin
        modalidades = modalidadeService.filtrar(new HashMap<>());
    }

    // Getter para a página XHTML ler a lista
    public List<Modalidade> getModalidades() {
        return modalidades;
    }
}