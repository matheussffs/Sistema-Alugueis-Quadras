package com.matheus.services;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;
import com.matheus.entidades.Quadra; 
import com.matheus.utils.FiltrosPesquisa;
import java.util.ArrayList;
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
        add(filtrosPesquisa, "u.quaNome LIKE '?quaNome'", "quaNome", filtros.get("quaNome"));
        return filtrosPesquisa;
    }

    public List<Quadra> filtrar(Map<String, Object> filtros) {
        String sql = "SELECT u FROM Quadra u "; 
        sql = adicionarFiltros(sql, getFiltros(filtros));
        Query query = customEntityManager.getEntityManager().createQuery(sql);

        Set<Quadra> setQuadras = new HashSet<>(); 
        setQuadras.addAll(query.getResultList());
        return new ArrayList<>(setQuadras);
    }
    
    public List<Quadra> listarTodas() {
        String jpql = "SELECT q FROM Quadra q ORDER BY q.qua_nome";
        Query query = customEntityManager.getEntityManager().createQuery(jpql);
        return query.getResultList();
    }

}