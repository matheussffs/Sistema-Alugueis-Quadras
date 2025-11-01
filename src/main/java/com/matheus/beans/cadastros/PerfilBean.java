package com.matheus.beans.cadastros;

import com.matheus.beans.BuscaBean;
import com.matheus.entidades.Perfil;
import com.matheus.services.BaseCrud;
import com.matheus.services.PerfilService;
import com.matheus.utils.JsfUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * @author Matheus Fassicollo
 */
@Named
@ViewScoped
public class PerfilBean extends BaseCrud<Perfil> implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private PerfilService perfilService;

    private boolean alterando;
    private List<Perfil> perfis; 

    @PostConstruct
    public void montaRegistros() {
        perfis = perfilService.filtrar(new HashMap<>());
    }

    @Override
    public void criaObj() {
        crudObj = new Perfil();
        alterando = false;
    }

    @Override
    public void salvar() {
        if (alterando) {
            perfilService.salvar(crudObj);
            JsfUtil.info("Perfil atualizado com sucesso!");
        } else {
            Map<String, Object> filtros = new HashMap<>();
            filtros.put("perfDesc", crudObj.getPerfDesc());
            List<Perfil> perfisExistentes = perfilService.filtrar(filtros);

            if (perfisExistentes.isEmpty()) {
                perfilService.salvar(crudObj);
                JsfUtil.info("Perfil cadastrado com sucesso!");
            } else {
                JsfUtil.warn("Já existe um perfil cadastrado com esta descrição: " 
                            + perfisExistentes.get(0).getPerfDesc());
                return;
            }
        }

        perfis = perfilService.filtrar(new HashMap<>());
        criaObj();
    }

    @Override
    public void deletar() {
        if (crudObj != null) {
            perfilService.deletar(crudObj);
            JsfUtil.info("Perfil excluído com sucesso!");
            perfis = perfilService.filtrar(new HashMap<>());
            criaObj();
        }
    }

    @Override
    public void setObjetoCrudPesquisa() {
        Perfil perfil = BuscaBean.getResultadoPesquisa(Perfil.class);
        if (perfil != null) {
            crudObj = perfil;
            alterando = true;
        }
    }

    public void selecionarPerfil(Perfil perfil) {
        this.crudObj = perfil;
        this.alterando = true;
    }

    public void excluirPerfil(Perfil perfil) {
        this.crudObj = perfil; 
        perfilService.deletar(perfil);
        JsfUtil.info("Perfil excluído com sucesso!");
        perfis = perfilService.filtrar(new HashMap<>()); 
        criaObj(); 
    }

    public Perfil getCrudObj() {
        return crudObj;
    }

    public void setCrudObj(Perfil crudObj) {
        this.crudObj = crudObj;
    }

    public boolean isAlterando() {
        return alterando;
    }

    public void setAlterando(boolean alterando) {
        this.alterando = alterando;
    }

    public List<Perfil> getPerfis() {
        return perfis;
    }

    public void setPerfis(List<Perfil> perfis) {
        this.perfis = perfis;
    }
}