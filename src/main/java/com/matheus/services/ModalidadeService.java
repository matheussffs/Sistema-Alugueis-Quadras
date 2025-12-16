package com.matheus.services;

import com.matheus.entidades.Modalidade;
import com.matheus.utils.FiltrosPesquisa;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;
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
public class ModalidadeService extends BaseService<Modalidade> {

    @Override
    protected List<FiltrosPesquisa> getFiltros(Map<String, Object> filtros) {
        List<FiltrosPesquisa> filtrosPesquisa = new ArrayList<>();

        add(filtrosPesquisa, "m.modId = '?modId'", "modId", filtros.get("modId"));
        add(filtrosPesquisa, "LOWER(m.modNome) LIKE LOWER('%?modNome%')", "modNome", filtros.get("modNome"));

        return filtrosPesquisa;
    }

    public List<Modalidade> filtrar(Map<String, Object> filtros) {
        String sql = "SELECT m FROM Modalidade m ";
        sql = adicionarFiltros(sql, getFiltros(filtros));

        Query query = customEntityManager.getEntityManager().createQuery(sql);

        Set<Modalidade> modalidades = new HashSet<>();
        modalidades.addAll(query.getResultList());

        return new ArrayList<>(modalidades);
    }

    public List<Modalidade> listarTodas() {
        String sql = "SELECT m FROM Modalidade m ORDER BY m.modNome";
        Query query = customEntityManager.getEntityManager().createQuery(sql);
        return query.getResultList();
    }

    public List<Modalidade> buscarPorNome(String nome) {
        String sql = "SELECT m FROM Modalidade m WHERE LOWER(m.modNome) = LOWER('" + nome + "')";
        Query query = customEntityManager.getEntityManager().createQuery(sql);
        return query.getResultList();
    }

    public boolean existeModalidadeComNome(String nome) {
        String sql = "SELECT COUNT(m) FROM Modalidade m WHERE LOWER(m.modNome) = LOWER('" + nome + "')";
        Query query = customEntityManager.getEntityManager().createQuery(sql);
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }
}
