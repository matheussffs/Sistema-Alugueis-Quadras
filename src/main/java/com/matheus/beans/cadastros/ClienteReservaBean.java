package com.matheus.beans.cadastros;

import com.matheus.entidades.Quadra;
import com.matheus.entidades.Reserva;
import com.matheus.entidades.Usuarios;
import com.matheus.services.QuadraService;
import com.matheus.services.ReservaService;
import com.matheus.services.UsuariosService;
import com.matheus.beans.UsuarioLogadoBean;
import com.matheus.utils.JsfUtil;
import com.matheus.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import java.io.ByteArrayInputStream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;

@Named
@ViewScoped
public class ClienteReservaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private ReservaService reservaService;
	@EJB
	private QuadraService quadraService;
	@EJB
	private UsuariosService usuariosService;
	@Inject
	private UsuarioLogadoBean usuarioLogadoBean;

	private List<Quadra> quadrasDisponiveis;
	private Reserva novaReserva;
	private Quadra quadraSelecionada;
	private Date dataSelecionada;
	private String horarioSelecionado;
	private List<String> todosOsHorarios;
	private List<String> horariosDisponiveis;
	private Integer antecedenciaSelecionada = 60;

	@PostConstruct
	public void init() {
		carregarTodosOsHorarios();
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String modalidadeIdParam = params.get("modalidadeId");
		HashMap<String, Object> filtros = new HashMap<>();
		filtros.put("quaAtiva", true);
		if (modalidadeIdParam != null && !modalidadeIdParam.isEmpty()) {
			try {
				filtros.put("modalidadeId", Integer.valueOf(modalidadeIdParam));
			} catch (NumberFormatException e) {
			}
		}
		quadrasDisponiveis = quadraService.filtrar(filtros);
	}

	private void carregarTodosOsHorarios() {
		todosOsHorarios = new ArrayList<>();
		for (int i = 0; i <= 23; i++) {
			int horaFim = i + 1;
			if (i == 23) {
				horaFim = 0;
			}
			todosOsHorarios.add(String.format("%02d:00 às %02d:00", i, horaFim));
		}
	}

	public void selecionarQuadra(Quadra quadra) {
		this.quadraSelecionada = quadra;
		this.novaReserva = new Reserva();
		this.dataSelecionada = new Date();
		this.horarioSelecionado = null;
		atualizarHorariosDisponiveis();
	}

	public void onDateSelect(SelectEvent<Date> event) {
		this.dataSelecionada = event.getObject();
		atualizarHorariosDisponiveis();
	}

	private void atualizarHorariosDisponiveis() {
		horariosDisponiveis = new ArrayList<>();
		if (dataSelecionada == null || quadraSelecionada == null)
			return;

		Calendar calData = Calendar.getInstance();
		calData.setTime(dataSelecionada);
		int ano = calData.get(Calendar.YEAR);
		int mes = calData.get(Calendar.MONTH);
		int dia = calData.get(Calendar.DAY_OF_MONTH);

		ZoneId zoneSystem = ZoneId.systemDefault(); 
		LocalDate dataSel = dataSelecionada.toInstant().atZone(zoneSystem).toLocalDate();
		LocalDate hoje = LocalDate.now(zoneSystem);

		int horaAtual = LocalTime.now(zoneSystem).getHour();
		boolean ehHoje = dataSel.isEqual(hoje);

		for (String slot : todosOsHorarios) {
			int horaInicio = Integer.parseInt(slot.substring(0, 2));

			if (ehHoje && horaInicio <= horaAtual) {
				continue;
			}

			Reserva reservaFantasma = new Reserva();
			reservaFantasma.setQuadra(quadraSelecionada);

			Calendar calCheck = Calendar.getInstance();
			calCheck.set(ano, mes, dia, horaInicio, 0, 0);
			calCheck.set(Calendar.MILLISECOND, 0);

			Date dtInicio = calCheck.getTime();
			calCheck.add(Calendar.HOUR_OF_DAY, 1);
			Date dtFim = calCheck.getTime();

			reservaFantasma.setResDtInicio(dtInicio);
			reservaFantasma.setResDtFim(dtFim);

			if (!reservaService.existeConflito(reservaFantasma)) {
				horariosDisponiveis.add(slot);
			}
		}
	}

	public void salvarReserva() {
		try {
			if (quadraSelecionada == null || dataSelecionada == null || StringUtil.isNullOrEmpty(horarioSelecionado)) {
				JsfUtil.warn("Preencha todos os campos.");
				return;
			}
			Usuarios clienteLogado = usuarioLogadoBean.getUsuarioLogado();
			if (clienteLogado == null) {
				JsfUtil.error("Sua sessão expirou.");
				JsfUtil.redirect("/SistemaAlugueis/login.xhtml");
				return;
			}

			Calendar calData = Calendar.getInstance();
			calData.setTime(dataSelecionada);
			int ano = calData.get(Calendar.YEAR);
			int mes = calData.get(Calendar.MONTH);
			int dia = calData.get(Calendar.DAY_OF_MONTH);
			int horaInicio = Integer.parseInt(horarioSelecionado.substring(0, 2));

			Calendar calFinal = Calendar.getInstance();
			calFinal.set(ano, mes, dia, horaInicio, 0, 0);
			calFinal.set(Calendar.MILLISECOND, 0);

			Date dtInicio = calFinal.getTime();
			calFinal.add(Calendar.HOUR_OF_DAY, 1);
			Date dtFim = calFinal.getTime();

			novaReserva.setUsuario(clienteLogado);
			novaReserva.setQuadra(this.quadraSelecionada);
			novaReserva.setResStatus("PENDENTE");
			novaReserva.setResValorTotal(quadraSelecionada.getQuaValorHora());
			novaReserva.setResDtInicio(dtInicio);
			novaReserva.setResDtFim(dtFim);

			if (reservaService.existeConflito(novaReserva)) {
				JsfUtil.error("Horário Ocupado!");
				atualizarHorariosDisponiveis();
				return;
			}
			reservaService.salvar(novaReserva);
			JsfUtil.info("Reserva efetuada com sucesso!");
			JsfUtil.pfHideDialog("dlgReserva");
			JsfUtil.pfShowDialog("dlgNotificacao");

			novaReserva = null;
			quadraSelecionada = null;
			dataSelecionada = null;
			horarioSelecionado = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void ativarNotificacoes() {
		try {
			Usuarios clienteLogado = usuarioLogadoBean.getUsuarioLogado();
			clienteLogado.setUserNotificaWhatsapp(true);
			clienteLogado.setUserNotificaAntecedenciaMin(this.antecedenciaSelecionada);
			usuariosService.salvar(clienteLogado);
			JsfUtil.info("Preferência salva!");
			JsfUtil.pfHideDialog("dlgNotificacao");
		} catch (Exception e) {
			JsfUtil.error("Erro ao salvar preferência: " + e.getMessage());
		}
	}

	public StreamedContent getImagemQuadra(Integer id) {
		if (id != null) {
			try {
				HashMap<String, Object> filtro = new HashMap<>();
				filtro.put("quaId", id);
				List<Quadra> lista = quadraService.filtrar(filtro);

				if (!lista.isEmpty()) {
					Quadra q = lista.get(0);
					if (q.getQuaImagemDados() != null) {
						return DefaultStreamedContent.builder().contentType(q.getQuaImagemTipo())
								.stream(() -> new ByteArrayInputStream(q.getQuaImagemDados())).build();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new DefaultStreamedContent();
	}

	public List<Quadra> getQuadrasDisponiveis() {
		return quadrasDisponiveis;
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

	public List<String> getHorariosDisponiveis() {
		return horariosDisponiveis;
	}

	public Integer getAntecedenciaSelecionada() {
		return antecedenciaSelecionada;
	}

	public void setAntecedenciaSelecionada(Integer antecedenciaSelecionada) {
		this.antecedenciaSelecionada = antecedenciaSelecionada;
	}
}