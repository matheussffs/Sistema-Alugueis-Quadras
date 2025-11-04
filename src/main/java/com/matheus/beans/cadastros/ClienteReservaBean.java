package com.matheus.beans.cadastros;

import java.util.TimeZone;
import java.util.Calendar;
import com.matheus.beans.UsuarioLogadoBean;
import com.matheus.entidades.Quadra;
import com.matheus.entidades.Reserva;
import com.matheus.entidades.Usuarios;
import com.matheus.services.QuadraService;
import com.matheus.services.ReservaService;
import com.matheus.utils.JsfUtil;
import com.matheus.utils.StringUtil;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Matheus Fassicollo
 */
@Named
@ViewScoped
public class ClienteReservaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ReservaService reservaService;
    @EJB
    private QuadraService quadraService;
    @Inject
    private UsuarioLogadoBean usuarioLogadoBean;

    private List<Quadra> quadrasDisponiveis;
    private Reserva novaReserva;
    private Quadra quadraSelecionada; 
    private Date dataSelecionada;             
    private String horarioSelecionado;        
    private List<String> horariosParaEscolha; 

    @PostConstruct
    public void init() {
        carregarHorarios();
        
        Map<String, String> params = FacesContext.getCurrentInstance()
                                                 .getExternalContext()
                                                 .getRequestParameterMap();
        String modalidadeIdParam = params.get("modalidadeId");
        
        HashMap<String, Object> filtros = new HashMap<>();
        filtros.put("quaAtiva", true); 
        
        if (modalidadeIdParam != null && !modalidadeIdParam.isEmpty()) {
            try {
                Integer modId = Integer.valueOf(modalidadeIdParam);
                filtros.put("modalidadeId", modId); 
            } catch (NumberFormatException e) {
            }
        }
        
        quadrasDisponiveis = quadraService.filtrar(filtros);
    }
    
    private void carregarHorarios() {
        horariosParaEscolha = new ArrayList<>();
        for (int i = 8; i <= 21; i++) { 
            horariosParaEscolha.add(String.format("%02d:00 às %02d:00", i, i + 1));
        }
    }
    
    public void selecionarQuadra(Quadra quadra) {
        this.quadraSelecionada = quadra;
        this.novaReserva = new Reserva();
        this.dataSelecionada = new Date(); 
        this.horarioSelecionado = null;    
    }

    public void salvarReserva() {
        try {
            if (quadraSelecionada == null) { JsfUtil.warn("Nenhuma quadra foi selecionada."); return; }
            if (dataSelecionada == null) { JsfUtil.warn("Você precisa selecionar uma data."); return; }
            if (StringUtil.isNullOrEmpty(horarioSelecionado)) { JsfUtil.warn("Você precisa selecionar um horário."); return; }
            
            Usuarios clienteLogado = usuarioLogadoBean.getUsuarioLogado();
            if (clienteLogado == null) { JsfUtil.error("Sua sessão expirou."); JsfUtil.redirect("/SistemaAlugueis/login.xhtml"); return; }
            
            int horaInicio = Integer.parseInt(horarioSelecionado.substring(0, 2));
            
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")); 

            cal.setTime(dataSelecionada);
            
            cal.set(Calendar.HOUR_OF_DAY, horaInicio);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            
            Date dtInicio = cal.getTime();
            
            cal.add(Calendar.HOUR_OF_DAY, 1);
            Date dtFim = cal.getTime();

            novaReserva.setResDtInicio(dtInicio);
            novaReserva.setResDtFim(dtFim);
            
            Date agoraEmUTC = new Date(System.currentTimeMillis() - TimeZone.getTimeZone("America/Sao_Paulo").getOffset(System.currentTimeMillis()));
             if (novaReserva.getResDtInicio().before(agoraEmUTC)) {
                 JsfUtil.warn("A data da reserva não pode ser no passado.");
                 return;
            }

            novaReserva.setUsuario(clienteLogado);
            novaReserva.setQuadra(this.quadraSelecionada);
            novaReserva.setResStatus("PENDENTE");
            novaReserva.setResValorTotal(quadraSelecionada.getQuaValorHora());
            
            if (reservaService.existeConflito(novaReserva)) {
                JsfUtil.error("Conflito de horário!");
                JsfUtil.warn("Esta quadra já está reservada neste mesmo dia e hora.");
                return; 
            }

            reservaService.salvar(novaReserva);
            
            JsfUtil.info("Reserva efetuada com sucesso!");
            JsfUtil.pfHideDialog("dlgReserva"); 
            
            novaReserva = null;
            quadraSelecionada = null;
            dataSelecionada = null;
            horarioSelecionado = null;

        } catch (Exception e) {
            JsfUtil.error("Erro ao salvar a reserva: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    public List<Quadra> getQuadrasDisponiveis() {
        return quadrasDisponiveis;
    }
    public void setQuadrasDisponiveis(List<Quadra> quadrasDisponiveis) {
        this.quadrasDisponiveis = quadrasDisponiveis;
    }
    public Reserva getNovaReserva() {
        return novaReserva;
    }
    public void setNovaReserva(Reserva novaReserva) {
        this.novaReserva = novaReserva;
    }
    public Quadra getQuadraSelecionada() {
        return quadraSelecionada;
    }
    public void setQuadraSelecionada(Quadra quadraSelecionada) {
        this.quadraSelecionada = quadraSelecionada;
    }
    public Date getDataSelecionada() {
        return dataSelecionada;
    }
    public void setDataSelecionada(Date dataSelecionada) {
        this.dataSelecionada = dataSelecionada;
    }
    public String getHorarioSelecionado() {
        return horarioSelecionado;
    }
    public void setHorarioSelecionado(String horarioSelecionado) {
        this.horarioSelecionado = horarioSelecionado;
    }
    public List<String> getHorariosParaEscolha() {
        return horariosParaEscolha;
    }
}