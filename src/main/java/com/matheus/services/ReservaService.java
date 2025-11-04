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
import java.util.HashMap; 
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
*
* @author Matheus Fassicollo
*/

@Stateless
@Named
public class ReservaService extends BaseService<Reserva> { 

    @Override
    protected List<FiltrosPesquisa> getFiltros(Map<String, Object> filtros) {
        return new ArrayList<>(); 
    }

public List<Reserva> filtrar(Map<String, Object> filtros) {
        
        StringBuilder jpql = new StringBuilder("SELECT r FROM Reserva r WHERE 1=1 ");
        Map<String, Object> parametros = new HashMap<>();
        
        if (filtros.get("resId") != null) { 
            jpql.append("AND r.resId = :resId ");
            parametros.put("resId", filtros.get("resId"));
        }
        
        if (filtros.get("usuario") != null) { 
            jpql.append("AND r.usuario = :usuario ");
            parametros.put("usuario", filtros.get("usuario"));
        }
        if (filtros.get("quadra") != null) { 
            jpql.append("AND r.quadra = :quadra ");
            parametros.put("quadra", filtros.get("quadra"));
        }
        if (filtros.get("status") != null) { 
            jpql.append("AND r.resStatus = :status ");
            parametros.put("status", filtros.get("status"));
        }
        if (filtros.get("dtInicio") != null) { 
            jpql.append("AND r.resDtInicio >= :dtInicio ");
            parametros.put("dtInicio", filtros.get("dtInicio")); 
        }
        if (filtros.get("dtFim") != null) { 
            jpql.append("AND r.resDtInicio <= :dtFim ");
            parametros.put("dtFim", filtros.get("dtFim")); 
        }

        Query query = customEntityManager.getEntityManager().createQuery(jpql.toString());

        for (Map.Entry<String, Object> entry : parametros.entrySet()) {
            if (entry.getValue() instanceof Date) {
                query.setParameter(entry.getKey(), (Date) entry.getValue(), TemporalType.TIMESTAMP);
            } else {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }

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