package com.matheus.beans.cadastros;

import com.matheus.entidades.Reserva;
import com.matheus.services.ReservaService;
import com.matheus.utils.JsfUtil; 
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.event.SelectEvent; 

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * @author Matheus Fassicollo
 */
@Named
@ViewScoped
public class ReservaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ReservaService reservaService;

    private ScheduleModel model;
    
    private Reserva reservaSelecionada; 

    @PostConstruct
    public void init() {
        model = new DefaultScheduleModel();
        List<Reserva> todasReservas = reservaService.filtrar(new HashMap<>());

        for (Reserva r : todasReservas) {
            String titulo = r.getQuadra().getQuaNome() 
                        + " (" + r.getUsuario().getUserNome() + ")";

            Date dtInicioUTC = r.getResDtInicio();
            Date dtFimUTC = r.getResDtFim();
            ZoneId fusoLocal = ZoneId.of("America/Sao_Paulo");
            LocalDateTime startLocal = dtInicioUTC.toInstant().atZone(fusoLocal).toLocalDateTime();
            LocalDateTime endLocal = dtFimUTC.toInstant().atZone(fusoLocal).toLocalDateTime();

            DefaultScheduleEvent event = DefaultScheduleEvent.builder()
                .title(titulo)
                .startDate(startLocal)
                .endDate(endLocal)
                .id(String.valueOf(r.getResId())) 
                .build();
            
            if ("PENDENTE".equals(r.getResStatus())) {
                event.setStyleClass("evento-pendente");
            } else if ("CANCELADA".equals(r.getResStatus())) {
                event.setStyleClass("evento-cancelado");
            } else if ("CONFIRMADA".equals(r.getResStatus())) {
                event.setStyleClass("evento-confirmado");
            }

            model.addEvent(event);
        }
    }

    public void onEventSelect(SelectEvent<ScheduleEvent> selectEvent) {
        ScheduleEvent event = selectEvent.getObject();
        
        try {
            Integer id = Integer.valueOf(event.getId());
            
            Map<String, Object> filtroId = new HashMap<>();
            filtroId.put("resId", id);
            
            List<Reserva> resultados = reservaService.filtrar(filtroId);
            
            if (resultados != null && !resultados.isEmpty()) {
                this.reservaSelecionada = resultados.get(0);
            } else {
                this.reservaSelecionada = null;
                JsfUtil.warn("A reserva não foi encontrada no banco.");
            }
            
        } catch (NumberFormatException e) {
            JsfUtil.error("Erro ao ler ID da reserva.");
            this.reservaSelecionada = null;
        }
    }

    public void confirmarReserva() {
        if (reservaSelecionada == null) return;

        try {
            reservaSelecionada.setResStatus("CONFIRMADA");
            reservaService.salvar(reservaSelecionada); 
            JsfUtil.info("Reserva Confirmada!");
            JsfUtil.pfHideDialog("dlgEvento"); 
            
            init(); 
            
        } catch (Exception e) {
            JsfUtil.error("Erro ao confirmar reserva: " + e.getMessage());
        }
    }
    
    public void cancelarReserva() {
        if (reservaSelecionada == null) return;

        try {
            reservaSelecionada.setResStatus("CANCELADA");
            reservaService.salvar(reservaSelecionada); 
            JsfUtil.info("Reserva Cancelada!");
            JsfUtil.pfHideDialog("dlgEvento"); 
            
            init(); 
            
        } catch (Exception e) {
            JsfUtil.error("Erro ao cancelar reserva: " + e.getMessage());
        }
    }
    
    
    public ScheduleModel getModel() {
        return model;
    }

    public Reserva getReservaSelecionada() {
        return reservaSelecionada;
    }
}