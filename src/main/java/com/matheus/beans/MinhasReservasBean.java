package com.matheus.beans;

import com.matheus.entidades.Reserva;
import com.matheus.entidades.Usuarios;
import com.matheus.services.ReservaService;
import com.matheus.utils.JsfUtil;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
*
* @author Matheus Fassicollo
*/

@Named
@ViewScoped
public class MinhasReservasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ReservaService reservaService;

    @Inject
    private UsuarioLogadoBean usuarioLogadoBean;

    private List<Reserva> minhasReservas;

    @PostConstruct
    public void init() {
        carregarReservas();
        
        if (minhasReservas != null) {
            minhasReservas.sort(Comparator.comparing(Reserva::getResId).reversed());
        }
    }

    private void carregarReservas() {
        Usuarios clienteLogado = usuarioLogadoBean.getUsuarioLogado();

        if (clienteLogado == null) {
            JsfUtil.redirect("/SistemaAlugueis/login.xhtml");
            return;
        }

        Map<String, Object> filtros = new HashMap<>();
        
        filtros.put("usuario", clienteLogado);

        minhasReservas = reservaService.filtrar(filtros);
        
    }

    public void cancelar(Reserva reserva) {
        try {
            if ("PENDENTE".equals(reserva.getResStatus()) || "CONFIRMADA".equals(reserva.getResStatus())) {
                
                reserva.setResStatus("CANCELADA");
                reservaService.salvar(reserva); 
                
                JsfUtil.info("Reserva cancelada com sucesso.");
                
            } else {
                JsfUtil.warn("Esta reserva não pode mais ser cancelada.");
            }
        } catch (Exception e) {
            JsfUtil.error("Erro ao tentar cancelar a reserva: " + e.getMessage());
        }
    }

    public List<Reserva> getMinhasReservas() {
        return minhasReservas;
    }
}