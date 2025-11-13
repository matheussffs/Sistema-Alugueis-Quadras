package com.matheus.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.donut.DonutChartOptions;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.title.Title;

/**
 * @author Matheus Fassicollo
 * 
 */
@Named
@ViewScoped
public class DashboardGraficosBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext
	private EntityManager em;

	private LineChartModel faturamentoModel;
	private BarChartModel quadrasModel;
	private DonutChartModel modalidadesModel;
	private DonutChartModel statusModel;
	private LineChartModel clienteGrowthModel;
	private BarChartModel peakHoursModel;

	@PostConstruct
	public void init() {
		createFaturamentoModel();
		createQuadrasModel();
		createModalidadesModel();
		createStatusModel();
		createClienteGrowthModel();
		createPeakHoursModel();
	}

	private void createFaturamentoModel() {
		faturamentoModel = new LineChartModel();
		ChartData data = new ChartData();
		LineChartDataSet dataSet = new LineChartDataSet();
		dataSet.setLabel("Faturamento R$ (Confirmado)");
		dataSet.setFill(true);
		dataSet.setBackgroundColor("rgba(22, 163, 74, 0.2)");
		dataSet.setBorderColor("rgb(22, 163, 74)");

		List<Object> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		Query q = em.createNativeQuery("SELECT MONTHNAME(res_dt_inicio) as mes, SUM(res_valor_total) as total "
				+ "FROM reservas " + "WHERE res_status = 'CONFIRMADA' AND YEAR(res_dt_inicio) = YEAR(CURRENT_DATE()) "
				+ "GROUP BY MONTH(res_dt_inicio), mes " + "ORDER BY MONTH(res_dt_inicio)");

		List<Object[]> resultados = q.getResultList();
		for (Object[] resultado : resultados) {
			labels.add((String) resultado[0]);
			values.add(((Number) resultado[1]).doubleValue());
		}

		dataSet.setData(values);
		data.addChartDataSet(dataSet);
		data.setLabels(labels);
		faturamentoModel.setData(data);

		LineChartOptions options = new LineChartOptions();
		Title title = new Title();
		title.setDisplay(true);
		title.setText("Faturamento Mensal (Este Ano)");
		options.setTitle(title);
		faturamentoModel.setOptions(options);
	}

	private void createQuadrasModel() {
		quadrasModel = new BarChartModel();
		ChartData data = new ChartData();
		BarChartDataSet barDataSet = new BarChartDataSet();
		barDataSet.setLabel("Nº de Reservas");
		barDataSet.setBackgroundColor("rgba(54, 162, 235, 0.6)");
		barDataSet.setBorderColor("rgb(54, 162, 235)");

		List<Number> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		Query q = em.createQuery("SELECT q.quaNome, COUNT(r.resId) as total " + "FROM Reserva r JOIN r.quadra q "
				+ "GROUP BY q.quaNome ORDER BY total DESC");

		List<Object[]> resultados = q.getResultList();
		for (Object[] resultado : resultados) {
			labels.add((String) resultado[0]);
			values.add((Long) resultado[1]);
		}

		barDataSet.setData(values);
		data.addChartDataSet(barDataSet);
		data.setLabels(labels);
		quadrasModel.setData(data);

		BarChartOptions options = new BarChartOptions();
		Title title = new Title();
		title.setDisplay(true);
		title.setText("Quadras Mais Populares");
		options.setTitle(title);
		CartesianScales scales = new CartesianScales();
		CartesianLinearAxes yAxis = new CartesianLinearAxes();
		yAxis.setBeginAtZero(true);
		scales.addYAxesData(yAxis);
		options.setScales(scales);
		quadrasModel.setOptions(options);
	}

	private void createModalidadesModel() {
		modalidadesModel = new DonutChartModel();
		ChartData data = new ChartData();
		DonutChartDataSet dataSet = new DonutChartDataSet();
		List<Number> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		Query q = em.createQuery("SELECT m.modNome, COUNT(r.resId) as total "
				+ "FROM Reserva r JOIN r.quadra q JOIN q.modalidade m " + "GROUP BY m.modNome");

		List<Object[]> resultados = q.getResultList();
		for (Object[] resultado : resultados) {
			labels.add((String) resultado[0]);
			values.add((Long) resultado[1]);
		}

		dataSet.setData(values);
		dataSet.setBackgroundColor(Arrays.asList("#36A2EB", "#FFCE56", "#4BC0C0", "#FF6384"));
		data.addChartDataSet(dataSet);
		data.setLabels(labels);
		modalidadesModel.setData(data);

		DonutChartOptions options = new DonutChartOptions();
		Title title = new Title();
		title.setDisplay(true);
		title.setText("Reservas por Esporte");
		options.setTitle(title);
		modalidadesModel.setOptions(options);
	}

	private void createStatusModel() {
		statusModel = new DonutChartModel();
		ChartData data = new ChartData();
		DonutChartDataSet dataSet = new DonutChartDataSet();
		List<Number> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		Query q = em.createQuery("SELECT r.resStatus, COUNT(r.resId) as total "
				+ "FROM Reserva r GROUP BY r.resStatus ORDER BY r.resStatus");

		List<Object[]> resultados = q.getResultList();

		Map<String, String> statusColorMap = new HashMap<>();
		statusColorMap.put("CANCELADA", "rgb(239, 68, 68)"); 
		statusColorMap.put("CONFIRMADA", "rgb(22, 163, 74)"); 
		statusColorMap.put("PENDENTE", "rgb(245, 158, 11)"); 
		statusColorMap.put("CONCLUIDA", "rgb(107, 114, 128)"); 

		List<String> colors = new ArrayList<>();

		for (Object[] resultado : resultados) {
			String status = (String) resultado[0];
			labels.add(status);
			values.add((Long) resultado[1]);
			colors.add(statusColorMap.getOrDefault(status, "rgb(150, 150, 150)"));
		}

		dataSet.setData(values);
		dataSet.setBackgroundColor(colors);
		data.addChartDataSet(dataSet);
		data.setLabels(labels);
		statusModel.setData(data);

		DonutChartOptions options = new DonutChartOptions();
		Title title = new Title();
		title.setDisplay(true);
		title.setText("Reservas por Status");
		options.setTitle(title);

		Legend legend = new Legend();
		legend.setDisplay(true);
		legend.setPosition("top");
		options.setLegend(legend);

		statusModel.setOptions(options);
	}

	private void createClienteGrowthModel() {
		clienteGrowthModel = new LineChartModel();
		ChartData data = new ChartData();

		LineChartDataSet dataSet = new LineChartDataSet();
		dataSet.setLabel("Novos Clientes");
		dataSet.setBackgroundColor("rgba(239, 68, 68, 0.2)");
		dataSet.setBorderColor("rgb(239, 68, 68)");

		List<Object> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		Query q = em.createNativeQuery("SELECT MONTHNAME(user_data_cadastro) as mes, COUNT(user_id) as total "
				+ "FROM usuarios " + "WHERE user_perf_id = (SELECT perf_id FROM perfis WHERE perf_nome = 'CLIENTE') "
				+ "AND YEAR(user_data_cadastro) = YEAR(CURRENT_DATE()) " + "GROUP BY MONTH(user_data_cadastro), mes "
				+ "ORDER BY MONTH(user_data_cadastro)");

		List<Object[]> resultados = q.getResultList();

		for (Object[] resultado : resultados) {
			labels.add((String) resultado[0]);
			values.add(((Number) resultado[1]).longValue());
		}

		dataSet.setData(values);
		data.addChartDataSet(dataSet);
		data.setLabels(labels);
		clienteGrowthModel.setData(data);

		LineChartOptions options = new LineChartOptions();
		Title title = new Title();
		title.setDisplay(true);
		title.setText("Novos Clientes (Este Ano)");
		options.setTitle(title);
		clienteGrowthModel.setOptions(options);
	}

	private void createPeakHoursModel() {
		peakHoursModel = new BarChartModel();
		ChartData data = new ChartData();

		BarChartDataSet barDataSet = new BarChartDataSet();
		barDataSet.setLabel("Nº de Reservas");
		barDataSet.setBackgroundColor("rgba(245, 158, 11, 0.6)");
		barDataSet.setBorderColor("rgb(245, 158, 11)");

		List<Number> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		Query q = em.createNativeQuery("SELECT HOUR(res_dt_inicio) as hora, COUNT(res_id) as total " + "FROM reservas "
				+ "GROUP BY hora ORDER BY hora ASC");

		List<Object[]> resultados = q.getResultList();

		for (Object[] resultado : resultados) {
			String horaFormatada = String.format("%02d:00", (Integer) resultado[0]);
			labels.add(horaFormatada);
			values.add(((Number) resultado[1]).longValue());
		}

		barDataSet.setData(values);
		data.addChartDataSet(barDataSet);
		data.setLabels(labels);
		peakHoursModel.setData(data);

		BarChartOptions options = new BarChartOptions();
		Title title = new Title();
		title.setDisplay(true);
		title.setText("Horários de Pico (Total de Reservas)");
		options.setTitle(title);

		peakHoursModel.setOptions(options);
	}

	public LineChartModel getFaturamentoModel() {
		return faturamentoModel;
	}

	public BarChartModel getQuadrasModel() {
		return quadrasModel;
	}

	public DonutChartModel getModalidadesModel() {
		return modalidadesModel;
	}

	public DonutChartModel getStatusModel() {
		return statusModel;
	}

	public LineChartModel getClienteGrowthModel() {
		return clienteGrowthModel;
	}

	public BarChartModel getPeakHoursModel() {
		return peakHoursModel;
	}
}