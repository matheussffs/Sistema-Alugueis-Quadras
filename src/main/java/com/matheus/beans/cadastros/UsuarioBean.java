package com.matheus.beans.cadastros;

import com.matheus.beans.BuscaBean;
import com.matheus.entidades.Usuarios;
import com.matheus.entidades.Perfil;
import com.matheus.services.BaseCrud;
import com.matheus.services.UsuariosService;
import com.matheus.services.PerfilService;
import com.matheus.utils.JsfUtil;
import com.matheus.utils.StringUtil;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
public class UsuarioBean extends BaseCrud<Usuarios> implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private UsuariosService usuarioService;

	@EJB
	private PerfilService perfilService;

	private boolean alterando;
	private List<Usuarios> usuarios;
	private List<Perfil> perfis;

	private String senhaParaSalvar;

	@PostConstruct
	public void montaRegistros() {
		usuarios = usuarioService.filtrar(new HashMap<>());
		perfis = perfilService.filtrar(new HashMap<>());
	}

	@Override
	public void criaObj() {
		crudObj = new Usuarios();
		crudObj.setUserAtivo(true);
		crudObj.setPerfil(null);
		crudObj.setUserNome(null);
		crudObj.setUserEmail(null);
		crudObj.setUserCpf(null);
		crudObj.setUserTelefone(null);

		alterando = false;
		senhaParaSalvar = null;

	}

	@Override
	public void salvar() {
		if (!StringUtil.isCPFValido(crudObj.getUserCpf())) {
			JsfUtil.error("CPF informado não é válido.");
			return;
		}

		crudObj.setUserCpf(StringUtil.getOnlyNumbers(crudObj.getUserCpf()));
		if (crudObj.getUserTelefone() != null && !crudObj.getUserTelefone().isEmpty()) {
			crudObj.setUserTelefone(StringUtil.getOnlyNumbers(crudObj.getUserTelefone()));
		} else {
			crudObj.setUserTelefone(null);
		}

		if (senhaParaSalvar != null && !senhaParaSalvar.isEmpty()) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String hashDaSenha = encoder.encode(senhaParaSalvar);
			crudObj.setUserSenha(hashDaSenha);
		} else if (!alterando) {
			JsfUtil.warn("Para um novo usuário, a senha é obrigatória.");
			return;
		}
		if (alterando) {
			usuarioService.salvar(crudObj);
			JsfUtil.info("Usuário atualizado com sucesso!");
		} else {
			Map<String, Object> filtrosCpf = new HashMap<>();
			filtrosCpf.put("userCpf", crudObj.getUserCpf());
			List<Usuarios> usuariosCpf = usuarioService.filtrar(filtrosCpf);
			if (!usuariosCpf.isEmpty()) {
				JsfUtil.warn("Já existe um usuário cadastrado com o CPF: " + usuariosCpf.get(0).getUserNome());
				return;
			}

			Map<String, Object> filtrosEmail = new HashMap<>();
			filtrosEmail.put("userEmail", crudObj.getUserEmail());
			List<Usuarios> usuariosEmail = usuarioService.filtrar(filtrosEmail);
			if (!usuariosEmail.isEmpty()) {
				JsfUtil.warn("Já existe um usuário cadastrado com o E-mail: " + usuariosEmail.get(0).getUserNome());
				return;
			}

			usuarioService.salvar(crudObj);
			JsfUtil.info("Usuário cadastrado com sucesso!");
		}

		usuarios = usuarioService.filtrar(new HashMap<>());
		criaObj();
	}

	@Override
	public void deletar() {
		if (crudObj != null) {
			excluirUsuario(crudObj);
		}
	}

	@Override
	public void setObjetoCrudPesquisa() {
		Usuarios usuario = BuscaBean.getResultadoPesquisa(Usuarios.class);
		if (usuario != null) {
			crudObj = usuario;
			alterando = true;
			senhaParaSalvar = null;
		}
	}

	public void selecionarUsuario(Usuarios usuario) {
		this.crudObj = usuario;
		this.alterando = true;
		this.senhaParaSalvar = null;
	}

	public void excluirUsuario(Usuarios usuarioParaExcluir) {
		try {
			usuarioService.deletar(usuarioParaExcluir);
			this.usuarios.remove(usuarioParaExcluir);
			JsfUtil.info("Usuário excluído com sucesso!");
			criaObj();
		} catch (Exception e) {
			e.printStackTrace();

			JsfUtil.error(
					"Não é possível excluir este usuário pois ele possui histórico de reservas. Para manter os dados, edite o usuário e desmarque a opção 'Ativo' para inativá-lo.");
		}
	}

	public Usuarios getCrudObj() {
		return crudObj;
	}

	public void setCrudObj(Usuarios crudObj) {
		this.crudObj = crudObj;
	}

	public boolean isAlterando() {
		return alterando;
	}

	public void setAlterando(boolean alterando) {
		this.alterando = alterando;
	}

	public List<Usuarios> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuarios> usuarios) {
		this.usuarios = usuarios;
	}

	public List<Perfil> getPerfis() {
		return perfis;
	}

	public void setPerfis(List<Perfil> perfis) {
		this.perfis = perfis;
	}

	public String getSenhaParaSalvar() {
		return senhaParaSalvar;
	}

	public void setSenhaParaSalvar(String senhaParaSalvar) {
		this.senhaParaSalvar = senhaParaSalvar;
	}
}