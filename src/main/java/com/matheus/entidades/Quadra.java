package com.matheus.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Matheus Fassicollo
 */
@Entity
@Table(name = "quadras")
public class Quadra implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "qua_id")
	private Integer quaId;
	@Column(name = "qua_nome", length = 100, nullable = false)
	private String quaNome;
	@Column(name = "qua_descricao")
	private String quaDescricao;
	@Column(name = "qua_valor_hora", precision = 10, scale = 2, nullable = false)
	private BigDecimal quaValorHora;
	@Column(name = "qua_ativa", nullable = false)
	private boolean quaAtiva = true;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "qua_modalidade_id", nullable = false)
	private Modalidade modalidade;
	
	@Lob
    @Basic(fetch=FetchType.LAZY)
    @Column(name="qua_imagem_dados")
    private byte[] quaImagemDados;
    
    @Column(name="qua_imagem_tipo", length = 50)
    private String quaImagemTipo;

	public Quadra() {
	}

	public Integer getQuaId() {
		return quaId;
	}

	public void setQuaId(Integer quaId) {
		this.quaId = quaId;
	}

	public String getQuaNome() {
		return quaNome;
	}

	public void setQuaNome(String quaNome) {
		this.quaNome = quaNome;
	}

	public String getQuaDescricao() {
		return quaDescricao;
	}

	public void setQuaDescricao(String quaDescricao) {
		this.quaDescricao = quaDescricao;
	}

	public BigDecimal getQuaValorHora() {
		return quaValorHora;
	}

	public void setQuaValorHora(BigDecimal quaValorHora) {
		this.quaValorHora = quaValorHora;
	}

	public boolean isQuaAtiva() {
		return quaAtiva;
	}

	public void setQuaAtiva(boolean quaAtiva) {
		this.quaAtiva = quaAtiva;
	}

	public Modalidade getModalidade() {
		return modalidade;
	}

	public void setModalidade(Modalidade modalidade) {
		this.modalidade = modalidade;
	}
	
	public byte[] getQuaImagemDados() {
		return quaImagemDados;
	}

	public void setQuaImagemDados(byte[] quaImagemDados) {
		this.quaImagemDados = quaImagemDados;
	}

	public String getQuaImagemTipo() {
		return quaImagemTipo;
	}

	public void setQuaImagemTipo(String quaImagemTipo) {
		this.quaImagemTipo = quaImagemTipo;
	}

	@Override
	public int hashCode() {
		return Objects.hash(quaId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Quadra other = (Quadra) obj;
		return Objects.equals(quaId, other.quaId);
	}
}