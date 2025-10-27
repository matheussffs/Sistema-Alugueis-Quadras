package com.matheus.beans;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.matheus.entidades.Usuarios;

import java.io.Serializable;

/**
 * @author Matheus Fassicollo
 */
@Named
@SessionScoped
public class UsuarioLogadoBean implements Serializable {

    private static final long serialVersionUID = 1L;
	private Usuarios usuarioLogado;
	
	public Usuarios getUsuarioLogado() {
		return usuarioLogado;
	}
	public void setUsuarioLogado(Usuarios usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}
}
