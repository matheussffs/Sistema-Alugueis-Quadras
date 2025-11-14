package com.matheus.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Matheus Fassicollo
 */
@Entity
@Table(name = "reservas")
public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "res_id")
    private Integer resId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "res_user_id", nullable = false)
    private Usuarios usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "res_qua_id", nullable = false)
    private Quadra quadra;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "res_dt_inicio", nullable = false)
    private Date resDtInicio;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "res_dt_fim", nullable = false)
    private Date resDtFim;

    @Column(name = "res_valor_total", precision = 10, scale = 2, nullable = false)
    private BigDecimal resValorTotal;

    @Column(name = "res_status", length = 50, nullable = false)
    private String resStatus; 

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "res_dt_criacao", insertable = false, updatable = false)
    private Date resDtCriacao;
    
    @Column(name = "res_lembrete_enviado", nullable = false)
    private boolean resLembreteEnviado = false;

    public Reserva() {
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public Quadra getQuadra() {
        return quadra;
    }

    public void setQuadra(Quadra quadra) {
        this.quadra = quadra;
    }

    public Date getResDtInicio() {
        return resDtInicio;
    }

    public void setResDtInicio(Date resDtInicio) {
        this.resDtInicio = resDtInicio;
    }

    public Date getResDtFim() {
        return resDtFim;
    }

    public void setResDtFim(Date resDtFim) {
        this.resDtFim = resDtFim;
    }

    public BigDecimal getResValorTotal() {
        return resValorTotal;
    }

    public void setResValorTotal(BigDecimal resValorTotal) {
        this.resValorTotal = resValorTotal;
    }

    public String getResStatus() {
        return resStatus;
    }

    public void setResStatus(String resStatus) {
        this.resStatus = resStatus;
    }

    public Date getResDtCriacao() {
        return resDtCriacao;
    }

    public void setResDtCriacao(Date resDtCriacao) {
        this.resDtCriacao = resDtCriacao;
    }

    public boolean isResLembreteEnviado() {
		return resLembreteEnviado;
	}

	public void setResLembreteEnviado(boolean resLembreteEnviado) {
		this.resLembreteEnviado = resLembreteEnviado;
	}

	@Override
    public int hashCode() {
        return Objects.hash(resId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Reserva other = (Reserva) obj;
        return Objects.equals(resId, other.resId);
    }
}