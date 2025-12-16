package com.matheus.services;

import com.matheus.utils.FiltrosPesquisa;
import javax.ejb.EJB;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author matheus Fassicollo
 */
public abstract class BaseService<T> implements Serializable {

    @EJB
    protected CustomEntityManager customEntityManager;

    protected abstract List<FiltrosPesquisa> getFiltros(Map<String, Object> filtros);

    protected void add(List<FiltrosPesquisa> list, String expressao, String campo, Object valor) {
        if (valor == null || (valor instanceof String && String.valueOf(valor).isEmpty())) {
            return;
        }
        FiltrosPesquisa filtro = new FiltrosPesquisa();
        filtro.add(expressao, campo, valor);
        list.add(filtro);
    }

    protected String adicionarFiltros(String sql, List<FiltrosPesquisa> filtros) {
        String retorno = sql + "";
        if (filtros.isEmpty()) {
            return retorno;
        }
        retorno += " WHERE ";
        List<String> filtrosReplace = new ArrayList<>();
        for (FiltrosPesquisa filtro : filtros) {
            filtrosReplace.add(filtro.getExpressao().replace(filtro.getCampoJpql(), filtro.getValor().toString()));
        }
        retorno += String.join(" AND ", filtrosReplace);
        return retorno;
    }

    public <T> T salvar(T obj) {
        return customEntityManager.salvar(obj);
    }

    public boolean deletar(Object obj) {
        return customEntityManager.deletar(obj);
    }

    public void executeNativeUpdate(String query) {
        customEntityManager.executeNativeUpdate(query);
    }

    public List<Object> executeNativeQuery(String query) {
        return customEntityManager.executeNativeQuery(query);
    }

    public List executeNativeQuery(Class<?> classe, String query) {
        return customEntityManager.executeNativeQuery(classe, query);
    }
}
