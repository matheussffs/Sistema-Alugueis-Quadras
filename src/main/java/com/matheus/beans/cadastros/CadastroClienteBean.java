package com.matheus.beans.cadastros;

import com.matheus.entidades.Usuarios;
import com.matheus.entidades.Perfil;
import com.matheus.services.UsuariosService;
import com.matheus.utils.JsfUtil;
import com.matheus.utils.StringUtil;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Matheus Fassicollo
 */
@Named 
@ViewScoped
public class CadastroClienteBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final Integer ID_PERFIL_CLIENTE = 2; 

    @EJB
    private UsuariosService usuarioService;
    
    @PersistenceContext
    private EntityManager em; 

    private Usuarios usuario;
    private String confirmacaoSenha;

    @PostConstruct
    private void init() {
        usuario = new Usuarios();
    }

    public void salvar() {
        if (!StringUtil.isCPFValido(usuario.getUserCpf())) {
            JsfUtil.error("CPF inválido");
            return;
        }
        
        if (usuario.getUserSenha() == null || usuario.getUserSenha().isEmpty()) {
             JsfUtil.error("A senha é obrigatória.");
             return;
        }

        if (usuario.getUserSenha().length() < 8) {
            JsfUtil.error("A senha deve conter ao menos 8 caracteres");
            return;
        }

        if (!usuario.getUserSenha().equals(confirmacaoSenha)) {
            JsfUtil.error("A senha e a confirmação da senha não conferem");
            return;
        }

        String telefone = StringUtil.getOnlyNumbers(usuario.getUserTelefone());
        usuario.setUserTelefone(telefone);

        String cpf = StringUtil.getOnlyNumbers(usuario.getUserCpf());
        usuario.setUserCpf(cpf);

        Map<String, Object> filtrosCpf = new HashMap<>();
        filtrosCpf.put("userCpf", usuario.getUserCpf()); 
        List<Usuarios> usuariosCpf = usuarioService.filtrar(filtrosCpf);

        if (!usuariosCpf.isEmpty()) {
            JsfUtil.warn("Este CPF já está cadastrado.");
            return; 
        }
        
        Map<String, Object> filtrosEmail = new HashMap<>();
        filtrosEmail.put("userEmail", usuario.getUserEmail()); 
        List<Usuarios> usuariosEmail = usuarioService.filtrar(filtrosEmail);

        if (!usuariosEmail.isEmpty()) {
            JsfUtil.warn("Este E-mail já está cadastrado.");
            return; 
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashDaSenha = encoder.encode(usuario.getUserSenha());
        usuario.setUserSenha(hashDaSenha);

        try {
            Perfil perfilCliente = em.find(Perfil.class, ID_PERFIL_CLIENTE); 
            
            if (perfilCliente == null) {
                JsfUtil.error("Erro crítico: Perfil de cliente (ID=2) não encontrado no banco.");
                return;
            }
            usuario.setPerfil(perfilCliente);
            
        } catch (Exception e) {
            JsfUtil.error("Erro ao buscar perfil de cliente: " + e.getMessage());
            return;
        }

        usuario.setUserAtivo(true);
        usuario.setUserDataCadastro(new Date());

        try {
            usuario = usuarioService.salvar(usuario);
            JsfUtil.info("Usuario cadastrado com sucesso.");
            JsfUtil.redirect("/SistemaAlugueis/login.xhtml");
        } catch (Exception e) {
            JsfUtil.error("Erro ao finalizar o cadastro: " + e.getMessage());
        }
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public String getConfirmacaoSenha() {
        return confirmacaoSenha;
    }

    public void setConfirmacaoSenha(String confirmacaoSenha) {
        this.confirmacaoSenha = confirmacaoSenha;
    }
}