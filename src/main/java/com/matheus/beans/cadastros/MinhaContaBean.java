package com.matheus.beans.cadastros;

import com.matheus.beans.UsuarioLogadoBean;
import com.matheus.entidades.Usuarios;
import com.matheus.services.UsuariosService;
import com.matheus.utils.JsfUtil;
import com.matheus.utils.StringUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Matheus Fassicollo
 * 
 */
@Named
@ViewScoped
public class MinhaContaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UsuariosService usuariosService;

    @Inject
    private UsuarioLogadoBean usuarioLogadoBean; 

    private Usuarios usuario; 
    private String senhaAtual;
    private String novaSenha;
    private String confirmacaoNovaSenha;

    @PostConstruct
    public void init() {
        Usuarios usuarioNaSessao = usuarioLogadoBean.getUsuarioLogado();

        if (usuarioNaSessao == null) {
            JsfUtil.redirect("/SistemaAlugueis/login.xhtml");
            return;
        }

        try {
            Map<String, Object> filtro = new HashMap<>();
            filtro.put("userId", usuarioNaSessao.getUserId());
            List<Usuarios> usuarios = usuariosService.filtrar(filtro);
            if (!usuarios.isEmpty()) {
                this.usuario = usuarios.get(0);
            } else {
                JsfUtil.redirect("/SistemaAlugueis/login.xhtml");
            }
        } catch (Exception e) {
            JsfUtil.error("Erro ao carregar dados do usuário.");
        }
    }

    public void salvarDados() {
        try {
            usuario.setUserTelefone(StringUtil.getOnlyNumbers(usuario.getUserTelefone()));
            
            usuario = usuariosService.salvar(usuario);
            
            usuarioLogadoBean.setUsuarioLogado(usuario);
            
            JsfUtil.info("Dados atualizados com sucesso!");

        } catch (Exception e) {
            JsfUtil.error("Erro ao salvar dados: " + e.getMessage());
        }
    }

    public void alterarSenha() {
        try {
            if (StringUtil.isNullOrEmpty(senhaAtual) || StringUtil.isNullOrEmpty(novaSenha)) {
                JsfUtil.warn("Preencha todos os campos de senha.");
                return;
            }
            if (novaSenha.length() < 8) {
                JsfUtil.error("A nova senha deve ter no mínimo 8 caracteres.");
                return;
            }
            if (!novaSenha.equals(confirmacaoNovaSenha)) {
                JsfUtil.error("A 'Nova Senha' e a 'Confirmação' não conferem.");
                return;
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!encoder.matches(senhaAtual, usuario.getUserSenha())) {
                JsfUtil.warn("A 'Senha Atual' está incorreta!");
                return;
            }

            String novoHash = encoder.encode(novaSenha);
            usuario.setUserSenha(novoHash);
            
            usuariosService.salvar(usuario);
            
            JsfUtil.info("Senha alterada com sucesso!");

            senhaAtual = null;
            novaSenha = null;
            confirmacaoNovaSenha = null;

        } catch (Exception e) {
            JsfUtil.error("Erro ao alterar a senha: " + e.getMessage());
        }
    }
    
    public Usuarios getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }
    public String getSenhaAtual() {
        return senhaAtual;
    }
    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
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
}