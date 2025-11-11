package com.matheus.services;

import com.matheus.entidades.Usuarios;
import com.matheus.utils.FiltrosPesquisa;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager; 
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Stateless
@Named
public class UsuariosService extends BaseService<Usuarios> {

    @Override
    protected List<FiltrosPesquisa> getFiltros(Map<String, Object> filtros) {
        return new ArrayList<>(); 
    }

    public List<Usuarios> filtrar(Map<String, Object> filtros) {
        StringBuilder jpql = new StringBuilder("SELECT u FROM Usuarios u WHERE 1=1 ");
        Map<String, Object> parametros = new HashMap<>();
        if (filtros.get("userCpf") != null) { 
            jpql.append("AND u.userCpf = :userCpf ");
            parametros.put("userCpf", filtros.get("userCpf"));
        }
        if (filtros.get("userEmail") != null) { 
            jpql.append("AND u.userEmail = :userEmail ");
            parametros.put("userEmail", filtros.get("userEmail"));
        }
        if (filtros.get("userId") != null) { 
            jpql.append("AND u.userId = :userId ");
            parametros.put("userId", filtros.get("userId"));
        }
        Query query = customEntityManager.getEntityManager().createQuery(jpql.toString());
        for (Map.Entry<String, Object> entry : parametros.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        Set<Usuarios> setUsuarios = new HashSet<>();
        setUsuarios.addAll(query.getResultList());
        return new ArrayList<>(setUsuarios);
    }

    public List<Usuarios> getPessoaPorCpf(String cpf) {
        String sql = "SELECT * FROM usuarios u WHERE u.user_cpf = ?";
        
        EntityManager em = customEntityManager.getEntityManager();
        
        Query query = em.createNativeQuery(sql, Usuarios.class);
        
        query.setParameter(1, cpf);
        
        return query.getResultList();
    }
}