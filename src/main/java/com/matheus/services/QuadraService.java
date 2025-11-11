package com.matheus.services;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;
import com.matheus.entidades.Quadra; 
import com.matheus.utils.FiltrosPesquisa;
import java.util.ArrayList;
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
public class QuadraService extends BaseService<Quadra> {

    @Override
    protected List<FiltrosPesquisa> getFiltros(Map<String, Object> filtros) {
        List<FiltrosPesquisa> filtrosPesquisa = new ArrayList<>();
        add(filtrosPesquisa, "u.quaNome = '?quaNome'", "quaNome", filtros.get("quaNome"));
        add(filtrosPesquisa, "u.modalidade.modId = '?modalidadeId'", "modalidadeId", filtros.get("modalidadeId"));
        add(filtrosPesquisa, "u.quaAtiva = '?quaAtiva'", "quaAtiva", filtros.get("quaAtiva"));
        
        add(filtrosPesquisa, "u.quaId = '?quaId'", "quaId", filtros.get("quaId"));
        
        return filtrosPesquisa;
    }

    public List<Quadra> filtrar(Map<String, Object> filtros) {
        
        StringBuilder jpql = new StringBuilder("SELECT u FROM Quadra u WHERE 1=1 ");
        Map<String, Object> parametros = new HashMap<>();

        if (filtros.get("quaNome") != null) { 
            jpql.append("AND u.quaNome = :quaNome ");
            parametros.put("quaNome", filtros.get("quaNome"));
        }
        
        if (filtros.get("modalidadeId") != null) { 
            jpql.append("AND u.modalidade.modId = :modalidadeId ");
            parametros.put("modalidadeId", filtros.get("modalidadeId"));
        }
        
        if (filtros.get("quaAtiva") != null) { 
            jpql.append("AND u.quaAtiva = :quaAtiva ");
            parametros.put("quaAtiva", filtros.get("quaAtiva"));
        }
        
        if (filtros.get("quaId") != null) { 
            jpql.append("AND u.quaId = :quaId ");
            parametros.put("quaId", filtros.get("quaId"));
        }

        Query query = customEntityManager.getEntityManager().createQuery(jpql.toString());

        for (Map.Entry<String, Object> entry : parametros.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        Set<Quadra> setQuadras = new HashSet<>(); 
        setQuadras.addAll(query.getResultList());
        return new ArrayList<>(setQuadras);
    }
    
    public List<Quadra> listarTodas() {
        String jpql = "SELECT q FROM Quadra q ORDER BY q.quaNome";
        Query query = customEntityManager.getEntityManager().createQuery(jpql);
        return query.getResultList();
    }
}