package com.matheus.beans.cadastros;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.primefaces.event.SelectEvent;

import com.matheus.entidades.Quadra;
import com.matheus.entidades.Reserva;
import com.matheus.entidades.Usuarios;
import com.matheus.services.QuadraService;
import com.matheus.services.ReservaService;
import com.matheus.utils.JsfUtil;
import com.matheus.utils.StringUtil;
import com.matheus.beans.UsuarioLogadoBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;

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

	private List<String> todosOsHorarios;
	private List<String> horariosDisponiveis;

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
		for (int i = 8; i <= 21; i++) {
			todosOsHorarios.add(String.format("%02d:00 às %02d:00", i, i + 1));
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

		if (dataSelecionada == null || quadraSelecionada == null) {
			return;
		}

		Calendar calData = Calendar.getInstance();
		calData.setTime(dataSelecionada);
		int ano = calData.get(Calendar.YEAR);
		int mes = calData.get(Calendar.MONTH);
		int dia = calData.get(Calendar.DAY_OF_MONTH);

		for (String slot : todosOsHorarios) {

			int horaInicio = Integer.parseInt(slot.substring(0, 2));

			Reserva reservaFantasma = new Reserva();
			reservaFantasma.setQuadra(quadraSelecionada);

			Calendar calUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			calUTC.set(ano, mes, dia, horaInicio, 0, 0);
			calUTC.set(Calendar.MILLISECOND, 0);
			Date dtInicio = calUTC.getTime();

			calUTC.add(Calendar.HOUR_OF_DAY, 1);
			Date dtFim = calUTC.getTime();

			reservaFantasma.setResDtInicio(dtInicio);
			reservaFantasma.setResDtFim(dtFim);

			boolean ocupado = reservaService.existeConflito(reservaFantasma);

			if (!ocupado) {
				horariosDisponiveis.add(slot);
			}
		}
	}

	public void salvarReserva() {
		try {
			if (quadraSelecionada == null || dataSelecionada == null || StringUtil.isNullOrEmpty(horarioSelecionado)) {
				JsfUtil.warn("Preencha todos os campos: Quadra, Data e Horário.");
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

			Calendar calUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			calUTC.set(ano, mes, dia, horaInicio, 0, 0);
			calUTC.set(Calendar.MILLISECOND, 0);
			Date dtInicio = calUTC.getTime();
			calUTC.add(Calendar.HOUR_OF_DAY, 1);
			Date dtFim = calUTC.getTime();

			novaReserva.setUsuario(clienteLogado);
			novaReserva.setQuadra(this.quadraSelecionada);
			novaReserva.setResStatus("PENDENTE");
			novaReserva.setResValorTotal(quadraSelecionada.getQuaValorHora());
			novaReserva.setResDtInicio(dtInicio);
			novaReserva.setResDtFim(dtFim);

			if (reservaService.existeConflito(novaReserva)) {
				JsfUtil.error("Horário Ocupado!");
				JsfUtil.warn(
						"Este horário foi reservado por outra pessoa enquanto você decidia. Por favor, escolha outro.");
				atualizarHorariosDisponiveis();
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
}