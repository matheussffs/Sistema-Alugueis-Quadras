package com.matheus.beans;

import com.matheus.utils.JsfUtil;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * @author Matheus Fassicollo
 * 
 */
@Named
@SessionScoped
public class GerBean implements Serializable {

    private static final long serialVersionUID = 1L;
	@Inject
    private UsuarioLogadoBean usuarioLogadoBean;

    public String getStyleMenu() {
        return usuarioLogadoBean.getUsuarioLogado() != null ? "" : "display: none;";
    }

    public void logout() {
        JsfUtil.getCurrentInstance().getExternalContext().invalidateSession();
    	JsfUtil.redirect("/SistemaAlugueis/landingPage.xhtml");
    }

    public boolean isUsuarioLogado() {
        return usuarioLogadoBean.getUsuarioLogado() != null;
    }
}
