package com.matheus.services;

import com.matheus.entidades.Quadra;
import com.matheus.entidades.Reserva;
import com.matheus.utils.FiltrosPesquisa;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;
import javax.persistence.TemporalType; 
import java.util.ArrayList;
import java.util.Date; 
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Matheus Fassicollo
 */
@Stateless
@Named
public class ReservaService extends BaseService<Reserva> { 

    @Override
    protected List<FiltrosPesquisa> getFiltros(Map<String, Object> filtros) {
        List<FiltrosPesquisa> filtrosPesquisa = new ArrayList<>();
        add(filtrosPesquisa, "r.usuario = :usuario", "usuario", filtros.get("usuario"));
        add(filtrosPesquisa, "r.quadra = :quadra", "quadra", filtros.get("quadra"));
        add(filtrosPesquisa, "r.resStatus = :status", "status", filtros.get("status"));
        add(filtrosPesquisa, "r.resDtInicio >= :dtInicio", "dtInicio", filtros.get("dtInicio"));
        add(filtrosPesquisa, "r.resDtInicio <= :dtFim", "dtFim", filtros.get("dtFim"));
        return filtrosPesquisa;
    }

    public List<Reserva> filtrar(Map<String, Object> filtros) {
        String sql = "SELECT r FROM Reserva r "; 
        sql = adicionarFiltros(sql, getFiltros(filtros));
        Query query = customEntityManager.getEntityManager().createQuery(sql);

        
        Set<Reserva> reservaList = new HashSet<>(); 
        reservaList.addAll(query.getResultList());
        return new ArrayList<>(reservaList);
    }
    
    public boolean existeConflito(Reserva reserva) {
        Integer idParaIgnorar = reserva.getResId(); 
        
        return existeConflito(
            reserva.getQuadra(), 
            reserva.getResDtInicio(), 
            reserva.getResDtFim(), 
            idParaIgnorar
        );
    }
    
    public boolean existeConflito(Quadra quadra, Date dtInicio, Date dtFim, Integer reservaIdExcluir) {
        
        StringBuilder jpql = new StringBuilder("SELECT COUNT(r) FROM Reserva r WHERE ");
        
        jpql.append("r.quadra = :quadra ");
        
        jpql.append("AND r.resStatus != 'CANCELADA' ");
        
        jpql.append("AND r.resDtInicio < :dtFim ");
        
        jpql.append("AND r.resDtFim > :dtInicio ");
        
        if (reservaIdExcluir != null && reservaIdExcluir > 0) {
            jpql.append("AND r.resId != :reservaIdExcluir ");
        }

        Query query = customEntityManager.getEntityManager().createQuery(jpql.toString());
        
        query.setParameter("quadra", quadra);
        query.setParameter("dtFim", dtFim, TemporalType.TIMESTAMP);
        query.setParameter("dtInicio", dtInicio, TemporalType.TIMESTAMP);
        
        if (reservaIdExcluir != null && reservaIdExcluir > 0) {
            query.setParameter("reservaIdExcluir", reservaIdExcluir);
        }

        Long count = (Long) query.getSingleResult();
        
        return count > 0;
    }
}