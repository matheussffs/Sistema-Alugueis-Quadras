package com.matheus.beans.cadastros;

import com.matheus.entidades.Reserva;
import com.matheus.services.ReservaService;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

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

            DefaultScheduleEvent<?> event = DefaultScheduleEvent.builder()
                .title(titulo)
                .startDate(startLocal)
                .endDate(endLocal)
                .data(r.getResId())
                .styleClass("PENDENTE".equals(r.getResStatus()) ? "evento-pendente" :
                           "CANCELADA".equals(r.getResStatus()) ? "evento-cancelado" :
                           "CONFIRMADA".equals(r.getResStatus()) ? "evento-confirmado" : "")
                .build();

            model.addEvent(event);
        }
    }

    public ScheduleModel getModel() {
        return model;
    }
}