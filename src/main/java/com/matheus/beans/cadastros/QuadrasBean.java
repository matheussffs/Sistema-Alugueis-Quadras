package com.matheus.beans.cadastros;

import com.matheus.beans.BuscaBean;
import com.matheus.entidades.Modalidade;
import com.matheus.entidades.Quadra;
import com.matheus.services.BaseCrud;
import com.matheus.services.ModalidadeService;
import com.matheus.services.QuadraService;
import com.matheus.utils.JsfUtil;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Matheus Fassicollo
 */
@Named
@ViewScoped
public class QuadrasBean extends BaseCrud<Quadra> implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private QuadraService quadraService;

    @EJB
    private ModalidadeService modalidadeService;

    private boolean alterando;
    private List<Quadra> quadras;
    private List<Modalidade> modalidades;

    @PostConstruct
    public void montaRegistros() {
        quadras = quadraService.filtrar(new HashMap<>());
        modalidades = modalidadeService.filtrar(new HashMap<>());
    }

    @Override
    public void criaObj() {
        crudObj = new Quadra();
        crudObj.setQuaAtiva(true);
        alterando = false;
    }

    @Override
    public void salvar() {
        if (alterando) {
            quadraService.salvar(crudObj);
            JsfUtil.info("Quadra atualizada com sucesso!");
        } else {
            Map<String, Object> filtros = new HashMap<>();
            filtros.put("quaNome", crudObj.getQuaNome());
            List<Quadra> quadrasExistentes = quadraService.filtrar(filtros);

            if (quadrasExistentes.isEmpty()) {
                quadraService.salvar(crudObj);
                JsfUtil.info("Quadra cadastrada com sucesso!");
            } else {
                JsfUtil.warn("Já existe uma quadra cadastrada com o nome: " 
                              + quadrasExistentes.get(0).getQuaNome());
                return;
            }
        }

        quadras = quadraService.filtrar(new HashMap<>());
        criaObj();
    }

    @Override
    public void deletar() {
        quadraService.deletar(crudObj);
        JsfUtil.info("Quadra excluída com sucesso!");
        quadras = quadraService.filtrar(new HashMap<>());
        criaObj();
    }

    @Override
    public void setObjetoCrudPesquisa() {
        Quadra quadra = BuscaBean.getResultadoPesquisa(Quadra.class);
        if (quadra != null) {
            crudObj = quadra;
            alterando = true;
        }
    }

    public void selecionarQuadra(Quadra quadra) {
        this.crudObj = quadra;
        this.alterando = true;
    }

    public void excluirQuadra(Quadra quadraParaExcluir) {
        try {
            quadraService.deletar(quadraParaExcluir); 
            
            this.quadras.remove(quadraParaExcluir); 
            
            JsfUtil.info("Quadra excluída com sucesso!");
            criaObj(); 
            
        } catch (Exception e) {
            JsfUtil.error("Erro ao excluir quadra: " + e.getMessage());
        }
    }

    public Quadra getCrudObj() {
        return crudObj;
    }

    public void setCrudObj(Quadra crudObj) {
        this.crudObj = crudObj;
    }

    public boolean isAlterando() {
        return alterando;
    }

    public void setAlterando(boolean alterando) {
        this.alterando = alterando;
    }

    public List<Quadra> getQuadras() {
        return quadras;
    }

    public void setQuadras(List<Quadra> quadras) {
        this.quadras = quadras;
    }

    public List<Modalidade> getModalidades() {
        return modalidades;
    }

    public void setModalidades(List<Modalidade> modalidades) {
        this.modalidades = modalidades;
    }
}
