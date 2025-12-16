package com.matheus.filters;

import com.matheus.beans.UsuarioLogadoBean;
import com.matheus.entidades.Usuarios;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author matheus Fassicollo
 */
@WebFilter("/restrito/*")
public class LoginFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		res.setHeader("Pragma", "no-cache");
		res.setDateHeader("Expires", 0);

		HttpSession session = req.getSession(false);
		UsuarioLogadoBean usuarioBean = null;
		boolean logado = false;

		if (session != null) {
			usuarioBean = (UsuarioLogadoBean) session.getAttribute("usuarioLogadoBean");
			if (usuarioBean != null && usuarioBean.getUsuarioLogado() != null) {
				logado = true;
			}
		}

		if (logado) {
			Usuarios usuario = usuarioBean.getUsuarioLogado();
			String perfil = usuario.getPerfil().getPerfDesc().toUpperCase();
			String url = req.getRequestURI();

			if ("CLIENTE".equals(perfil)) {

				boolean tentandoAcessarAdmin = url.contains("/dashboard.xhtml") || url.contains("/usuarios.xhtml")
						|| url.contains("/quadras.xhtml") ||
						(url.contains("/reserva.xhtml") && !url.contains("minhasReservas"));

				if (tentandoAcessarAdmin) {
					res.sendRedirect(req.getContextPath() + "/restrito/consultas/clienteHome.xhtml");
					return; 
				}
			}

			else if ("ADMIN".equals(perfil)) {

				boolean tentandoAcessarAreaCliente = url.contains("/clienteHome.xhtml")
						|| url.contains("/minhasReservas.xhtml");

				if (tentandoAcessarAreaCliente) {
					res.sendRedirect(req.getContextPath() + "/restrito/consultas/dashboard.xhtml");
					return; 
				}
			}

			chain.doFilter(request, response);

		} else {
			res.sendRedirect(req.getContextPath() + "/login.xhtml");
		}
	}

	@Override
	public void destroy() {
	}
}