package com.matheus.beans.cadastros;

import com.matheus.beans.BuscaBean;
import com.matheus.entidades.Modalidade;
import com.matheus.entidades.Quadra;
import com.matheus.services.BaseCrud;
import com.matheus.services.ModalidadeService;
import com.matheus.services.QuadraService;
import com.matheus.utils.JsfUtil;

import org.primefaces.model.file.UploadedFile;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

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
    
    private String modalidadeSelecionada;
    
    private UploadedFile arquivoImagem; 
    
    private static final String PATH_UPLOADS = "C:/quadras_uploads/";

    @PostConstruct
    public void montaRegistros() {
        quadras = quadraService.filtrar(new HashMap<>());
        
        new File(PATH_UPLOADS).mkdirs();
    }

    @Override
    public void criaObj() {
        
        crudObj = new Quadra();
        crudObj.setQuaAtiva(true);
        alterando = false;
        
        modalidadeSelecionada = null; 
        arquivoImagem = null;
    }

    @Override
    public void salvar() {
    
        try {
            if (modalidadeSelecionada == null || modalidadeSelecionada.isEmpty()) {
                JsfUtil.warn("Selecione uma modalidade.");
                return;
            }
            List<Modalidade> modLista = modalidadeService.buscarPorNome(modalidadeSelecionada);
            
            if (modLista.isEmpty()) {
                JsfUtil.error("Erro crítico: A modalidade '" + modalidadeSelecionada + "' não foi encontrada no banco.");
                JsfUtil.warn("Verifique se as modalidades 'Futebol', 'Beach Tenis' e 'Padel' estão cadastradas no banco de dados.");
                return;
            }
            crudObj.setModalidade(modLista.get(0));

        } catch (Exception e) {
            JsfUtil.error("Erro ao buscar modalidade: " + e.getMessage());
            return;
        }
        
        if (arquivoImagem != null && arquivoImagem.getSize() > 0) {
            try {
                String nomeOriginal = arquivoImagem.getFileName();
                String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
                String nomeUnico = UUID.randomUUID().toString() + extensao;

                InputStream input = arquivoImagem.getInputStream();
                Path destino = new File(PATH_UPLOADS + nomeUnico).toPath();
                Files.copy(input, destino, StandardCopyOption.REPLACE_EXISTING);

                crudObj.setQuaCaminhoImagem(nomeUnico);
                
            } catch (Exception e) {
                JsfUtil.error("Erro ao salvar a imagem: " + e.getMessage());
                return;
            }
        }
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
            selecionarQuadra(quadra); 
        }
    }

    public void selecionarQuadra(Quadra quadra) {
        this.crudObj = quadra;
        this.alterando = true;
        
        if (quadra.getModalidade() != null) {
            this.modalidadeSelecionada = quadra.getModalidade().getModNome();
        } else {
            this.modalidadeSelecionada = null;
        }
        this.arquivoImagem = null; 
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

    public String getModalidadeSelecionada() {
        return modalidadeSelecionada;
    }
    public void setModalidadeSelecionada(String modalidadeSelecionada) {
        this.modalidadeSelecionada = modalidadeSelecionada;
    }
    
    public UploadedFile getArquivoImagem() {
        return arquivoImagem;
    }
    public void setArquivoImagem(UploadedFile arquivoImagem) {
        this.arquivoImagem = arquivoImagem;
    }
    
    public List<Modalidade> getModalidades() {
        return null;
    }
}