package com.matheus.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.matheus.entidades.Usuarios;
import com.matheus.services.UsuariosService;
import com.matheus.utils.JsfUtil;
import com.matheus.utils.StringUtil;

/**
 * @author Matheus Fassicollo
 */
@Named
@SessionScoped
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private UsuariosService usuariosService;
	@Inject
	private UsuarioLogadoBean usuarioLogadoBean;

	private String nome;
	private String senha;
	private String cpf;
	private String telefone;
	private String novaSenha;
	private String confirmacaoNovaSenha;
	private List<Usuarios> usuariosEncontrados = new ArrayList<>();

	@PostConstruct
	private void init() {
	}

	public void doLogin() {

		if (StringUtil.isNullOrEmpty(cpf)) {
			JsfUtil.warn("É necessário informar seu CPF");
			return;
		}

		if (!StringUtil.isCPFValido(cpf)) {
			JsfUtil.warn("CPF inválido");
			return;
		}

		if (StringUtil.isNullOrEmpty(senha)) {
			JsfUtil.warn("É necessário informar sua Senha");
			return;
		}

		String cpfOnlyNumbers = StringUtil.getOnlyNumbers(cpf);
		List<Usuarios> usuarioList = usuariosService.getPessoaPorCpf(cpfOnlyNumbers);

		if (usuarioList.isEmpty()) {
			JsfUtil.warn("Nenhum usuário encontrado com o CPF informado");
			return;
		}

		Usuarios usuario = usuarioList.get(0);

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		boolean mesmaSenha = encoder.matches(senha, usuario.getUserSenha());

		if (!mesmaSenha) {
			JsfUtil.warn("Senha inválida");
			return;
		}

		usuarioLogadoBean.setUsuarioLogado(usuario);
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		session.setAttribute("usuarioLogadoBean", usuarioLogadoBean);

		String paginaDestino = null;
		try {
			paginaDestino = (String) session.getAttribute("paginaDestino");
			if (paginaDestino != null) {
				session.removeAttribute("paginaDestino");
			}
		} catch (Exception e) {
		}

		String perfilNome = "";
		try {
			perfilNome = usuario.getPerfil().getPerfDesc().toUpperCase();
		} catch (Exception e) {
			JsfUtil.error("Erro crítico ao ler o perfil do usuário.");
			return;
		}

		if ("CLIENTE".equals(perfilNome)) {
			if (!StringUtil.isNullOrEmpty(paginaDestino)) {
				JsfUtil.redirect(paginaDestino);
			} else {
				JsfUtil.redirect("/SistemaAlugueis/restrito/cadastros/clienteReserva.xhtml");
			}
		} else if ("ADMIN".equals(perfilNome)) {
			JsfUtil.redirect("/SistemaAlugueis/index.xhtml");
		} else {

			JsfUtil.redirect("/SistemaAlugueis/index.xhtml");
		}
	}

	public void redirecionarParaLogin() {
		JsfUtil.redirect("/SistemaAlugueis/login.xhtml");
	}

	public void abrirDialogEsqueciMinhaSenha() {
		JsfUtil.pfShowDialog("wvDlgEsqueciMinhaSenha");
	}

	public void redirecionarParaCriarConta() {
		JsfUtil.redirect("/SistemaAlugueis/publico/cadastros/usuario.xhtml");
	}

	private void limparCamposDialog() {
		cpf = null;
		telefone = null;
		novaSenha = null;
		confirmacaoNovaSenha = null;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String getConfirmacaoNovaSenha() {
		return confirmacaoNovaSenha;
	}

	public void setConfirmacaoNovaSenha(String confirmacaoNovaSenha) {
		this.confirmacaoNovaSenha = confirmacaoNovaSenha;
	}

	public List<Usuarios> getUsuariosEncontrados() {
		return usuariosEncontrados;
	}

	public void setUsuariosEncontrados(List<Usuarios> usuariosEncontrados) {
		this.usuariosEncontrados = usuariosEncontrados;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}