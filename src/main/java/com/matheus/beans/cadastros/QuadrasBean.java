package com.matheus.beans.cadastros;

import com.matheus.beans.BuscaBean;
import com.matheus.entidades.Modalidade;
import com.matheus.entidades.Quadra;
import com.matheus.services.BaseCrud;
import com.matheus.services.ModalidadeService;
import com.matheus.services.QuadraService;
import com.matheus.utils.JsfUtil;

import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.DefaultStreamedContent;
import java.io.ByteArrayInputStream;

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

	@PostConstruct
	public void montaRegistros() {
		quadras = quadraService.filtrar(new HashMap<>());
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
				JsfUtil.error(
						"Erro crítico: A modalidade '" + modalidadeSelecionada + "' não foi encontrada no banco.");
				return;
			}
			crudObj.setModalidade(modLista.get(0));
		} catch (Exception e) {
			JsfUtil.error("Erro ao buscar modalidade: " + e.getMessage());
			return;
		}

		if (arquivoImagem != null && arquivoImagem.getSize() > 0) {
			try {
				crudObj.setQuaImagemDados(arquivoImagem.getContent());
				crudObj.setQuaImagemTipo(arquivoImagem.getContentType());
			} catch (Exception e) {
				JsfUtil.error("Erro ao ler a imagem: " + e.getMessage());
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
				JsfUtil.warn("Já existe uma quadra cadastrada com o nome: " + quadrasExistentes.get(0).getQuaNome());
				return;
			}
		}
		quadras = quadraService.filtrar(new HashMap<>());
		criaObj();
	}

	@Override
	public void deletar() {
		try {
			quadraService.deletar(crudObj);
			JsfUtil.info("Quadra excluída com sucesso!");
			quadras = quadraService.filtrar(new HashMap<>());
			criaObj();
		} catch (Exception e) {
			JsfUtil.error(
					"Não é possível excluir esta quadra pois ela já possui histórico de reservas. Para não perder os dados financeiros, edite a quadra e mude a situação para 'Inativa'.");
		}
	}

	@Override
	public void setObjetoCrudPesquisa() {
		Quadra quadra = BuscaBean.getResultadoPesquisa(Quadra.class);
		if (quadra != null) {
			selecionarQuadra(quadra);
		}
	}

	public void selecionarQuadra(Quadra quadra) {

		try {
			Map<String, Object> filtro = new HashMap<>();
			filtro.put("quaId", quadra.getQuaId());

			List<Quadra> resultado = quadraService.filtrar(filtro);

			if (resultado == null || resultado.isEmpty()) {
				JsfUtil.error("Erro ao carregar os dados da quadra.");
				return;
			}

			this.crudObj = resultado.get(0);

		} catch (Exception e) {
			JsfUtil.error("Erro ao carregar dados da quadra: " + e.getMessage());
			return;
		}

		this.alterando = true;
		if (this.crudObj.getModalidade() != null) {
			this.modalidadeSelecionada = this.crudObj.getModalidade().getModNome();
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
			e.printStackTrace();

			JsfUtil.error(
					"Não é possível excluir esta quadra pois ela já possui histórico de reservas. Para não perder os dados financeiros, edite a quadra e mude a situação para 'Inativa'.");
		}
	}

	public StreamedContent getImagemParaVisualizar() {
		if (crudObj != null && crudObj.getQuaImagemDados() != null) {
			return DefaultStreamedContent.builder().contentType(crudObj.getQuaImagemTipo())
					.stream(() -> new ByteArrayInputStream(crudObj.getQuaImagemDados())).build();
		}
		return null;
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