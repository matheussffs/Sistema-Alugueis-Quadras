package com.matheus.services;

import com.matheus.entidades.Usuarios;
import com.matheus.utils.FiltrosPesquisa;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;
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
public class UsuariosService extends BaseService<Usuarios> {

    @Override
    protected List<FiltrosPesquisa> getFiltros(Map<String, Object> filtros) {
        List<FiltrosPesquisa> filtrosPesquisa = new ArrayList<>();
        add(filtrosPesquisa, "u.userCpf = '?userCpf'", "userCpf", filtros.get("userCpf"));
        add(filtrosPesquisa, "u.userEmail = '?userEmail'", "userEmail", filtros.get("userEmail"));
        add(filtrosPesquisa, "u.userId = '?userId'", "userId", filtros.get("userId"));
        return filtrosPesquisa;
    }

    public List<Usuarios> filtrar(Map<String, Object> filtros) {
        String sql = "SELECT u FROM Usuarios u ";
        sql = adicionarFiltros(sql, getFiltros(filtros));
        Query query = customEntityManager.getEntityManager().createQuery(sql);

        Set<Usuarios> setUsuarios = new HashSet<>();
        setUsuarios.addAll(query.getResultList());
        return new ArrayList<>(setUsuarios);
    }

    public List<Usuarios> getPessoaPorCpf(String cpf) {
       String sql = "SELECT * FROM usuarios u "
                + "WHERE u.user_cpf = '" + cpf + "'";
        return customEntityManager.executeNativeQuery(Usuarios.class, sql);
    }
}
