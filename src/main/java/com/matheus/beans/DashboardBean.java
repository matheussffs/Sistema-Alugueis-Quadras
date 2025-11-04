package com.matheus.beans;

import com.matheus.entidades.Reserva;
import com.matheus.entidades.Usuarios;
import com.matheus.services.ReservaService;
import com.matheus.services.UsuariosService;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType; 
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar; 
import java.util.Date;     
import java.util.List;

/**
*
* @author Matheus Fassicollo
*/

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ReservaService reservaService;
    @EJB
    private UsuariosService usuariosService;
    
    @PersistenceContext
    private EntityManager em;

    private BigDecimal faturamentoMes;
    private Long reservasPendentesHoje;
    private Long novosClientesMes;
    
    private List<Reserva> proximasReservas;
    private List<Usuarios> ultimosClientes;

    @PostConstruct
    public void init() {
        carregarKPIs();
        carregarListas(); 
    }

    private void carregarKPIs() {
        try {
            Calendar cal = Calendar.getInstance();

            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0);
            Date hojeInicio = cal.getTime(); 
            
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date amanhaInicio = cal.getTime();

            cal.setTime(new Date()); 
            cal.set(Calendar.DAY_OF_MONTH, 1); 
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0);
            Date inicioDoMes = cal.getTime();

            
            Query queryFat = em.createQuery(
                "SELECT SUM(r.resValorTotal) FROM Reserva r " +
                "WHERE r.resStatus = 'CONFIRMADA' " +
                "AND r.resDtInicio >= :inicioDoMes");
            queryFat.setParameter("inicioDoMes", inicioDoMes, TemporalType.TIMESTAMP);
            
            BigDecimal resultFat = (BigDecimal) queryFat.getSingleResult();
            faturamentoMes = (resultFat == null) ? BigDecimal.ZERO : resultFat;

            Query queryPend = em.createQuery(
                "SELECT COUNT(r) FROM Reserva r " +
                "WHERE r.resStatus = 'PENDENTE' " +
                "AND r.resDtInicio >= :hoje " + 
                "AND r.resDtInicio < :amanha"); 
            queryPend.setParameter("hoje", hojeInicio, TemporalType.TIMESTAMP);
            queryPend.setParameter("amanha", amanhaInicio, TemporalType.TIMESTAMP);
            
            reservasPendentesHoje = (Long) queryPend.getSingleResult();

            Query queryCli = em.createQuery(
                "SELECT COUNT(u) FROM Usuarios u " +
                "WHERE u.perfil.perfDesc = 'CLIENTE' " + 
                "AND u.userDataCadastro >= :inicioDoMes");
            queryCli.setParameter("inicioDoMes", inicioDoMes, TemporalType.TIMESTAMP);
            
            novosClientesMes = (Long) queryCli.getSingleResult();
            
        } catch (Exception e) {
            e.printStackTrace();
            faturamentoMes = BigDecimal.ZERO;
            reservasPendentesHoje = 0L;
            novosClientesMes = 0L;
        }
    }

    private void carregarListas() {
        try {
            proximasReservas = em.createQuery(
                "SELECT r FROM Reserva r " +
                "WHERE r.resStatus = 'CONFIRMADA' AND r.resDtInicio >= CURRENT_TIMESTAMP " +
                "ORDER BY r.resDtInicio ASC", Reserva.class)
                .setMaxResults(5) 
                .getResultList();
            
            ultimosClientes = em.createQuery(
                "SELECT u FROM Usuarios u " +
                "WHERE u.perfil.perfDesc = 'CLIENTE' " + 
                "ORDER BY u.userDataCadastro DESC", Usuarios.class)
                .setMaxResults(5)
                .getResultList();
                
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BigDecimal getFaturamentoMes() {
        return faturamentoMes;
    }
    public Long getReservasPendentesHoje() {
        return reservasPendentesHoje;
    }
    public Long getNovosClientesMes() {
        return novosClientesMes;
    }
    public List<Reserva> getProximasReservas() {
        return proximasReservas;
    }
    public List<Usuarios> getUltimosClientes() {
        return ultimosClientes;
    }
}