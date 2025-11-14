package com.matheus.entidades;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Matheus Fassicollo
 */

@Entity
@Table(name = "usuarios")
public class Usuarios implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer userId;
	@Column(name = "user_nome", length = 100, nullable = false)
	private String userNome;
	@Column(name = "user_email", length = 500, nullable = false, unique = true)
	private String userEmail;
	@Column(name = "user_senha", length = 255, nullable = false)
	private String userSenha;
	@Column(name = "user_cpf", length = 11, nullable = false, unique = true)
	private String userCpf;
	@Column(name = "user_telefone", length = 20, nullable = false)
	private String userTelefone;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "user_data_cadastro", length = 255, nullable = false)
	private Date userDataCadastro;
	@Column(name = "user_ativo")
	private boolean userAtivo = true;
	@Column(name = "user_notifica_whatsapp", nullable = false)
    private boolean userNotificaWhatsapp = false;
    @Column(name = "user_notifica_antecedencia_min", nullable = false)
    private Integer userNotificaAntecedenciaMin = 60;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_perf_id", nullable = false)
	private Perfil perfil;

	public Usuarios() {
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserNome() {
		return userNome;
	}

	public void setUserNome(String userNome) {
		this.userNome = userNome;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserSenha() {
		return userSenha;
	}

	public void setUserSenha(String userSenha) {
		this.userSenha = userSenha;
	}

	public String getUserCpf() {
		return userCpf;
	}

	public void setUserCpf(String userCpf) {
		this.userCpf = userCpf;
	}

	public String getUserTelefone() {
		return userTelefone;
	}

	public void setUserTelefone(String userTelefone) {
		this.userTelefone = userTelefone;
	}

	public Date getUserDataCadastro() {
		return userDataCadastro;
	}

	public void setUserDataCadastro(Date userDataCadastro) {
		this.userDataCadastro = userDataCadastro;
	}

	public boolean isUserAtivo() {
		return userAtivo;
	}

	public void setUserAtivo(boolean userAtivo) {
		this.userAtivo = userAtivo;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public boolean isUserNotificaWhatsapp() {
		return userNotificaWhatsapp;
	}

	public void setUserNotificaWhatsapp(boolean userNotificaWhatsapp) {
		this.userNotificaWhatsapp = userNotificaWhatsapp;
	}

	public Integer getUserNotificaAntecedenciaMin() {
		return userNotificaAntecedenciaMin;
	}

	public void setUserNotificaAntecedenciaMin(Integer userNotificaAntecedenciaMin) {
		this.userNotificaAntecedenciaMin = userNotificaAntecedenciaMin;
	}

	@Override
	public String toString() {
		return userNome;
	}

}
