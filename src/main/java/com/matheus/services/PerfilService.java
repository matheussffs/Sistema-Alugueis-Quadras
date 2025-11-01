package com.matheus.services;

import com.matheus.entidades.Perfil;
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
 * @author Matheus Fassicollo
 */
@Stateless
@Named
public class PerfilService extends BaseService<Perfil> {

	@Override
	protected List<FiltrosPesquisa> getFiltros(Map<String, Object> filtros) {
		List<FiltrosPesquisa> filtrosPesquisa = new ArrayList<>();
		add(filtrosPesquisa, "p.nome = '?nome'", "nome", filtros.get("nome"));
		add(filtrosPesquisa, "p.id = '?id'", "id", filtros.get("id"));
		return filtrosPesquisa;
	}

	public List<Perfil> filtrar(Map<String, Object> filtros) {
		String sql = "SELECT p FROM Perfil p ";
		sql = adicionarFiltros(sql, getFiltros(filtros));
		Query query = customEntityManager.getEntityManager().createQuery(sql);

		Set<Perfil> perfilList = new HashSet<>();
		perfilList.addAll(query.getResultList());
		return new ArrayList<>(perfilList);
	}
}