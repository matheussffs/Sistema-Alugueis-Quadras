package com.matheus.beans;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Matheus Fassicollo
 */
@Named
@SessionScoped
public class BuscaBean implements Serializable {

    private static Object objetoSelecionado;
    private List<Object> objetosSelecionados;

    public static <T> T getResultadoPesquisa(Class<T> clazz) {
        T retorno = (T) objetoSelecionado;
        objetoSelecionado = null;

        return retorno;
    }

    public <T> List<T> getResultadosPesquisa(Class<T> clazz) {
        List<T> retornos = (List<T>) (List<?>) objetosSelecionados;
        objetosSelecionados = null;

        return retornos;
    }

    public static <T> void resultadoPesquisa(Object object) {
        objetoSelecionado = object;
    }

    public static <T> void resultadoPesquisaInputConverter(SelectEvent<T> event) {
        objetoSelecionado = event.getObject();
    }

    public List<Object> getObjetosSelecionados() {
        return objetosSelecionados;
    }

    public void setObjetosSelecionados(List<Object> aObjetosSelecionados) {
        objetosSelecionados = aObjetosSelecionados;
    }

    public void finalizarBusca() {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void finalizarBuscaSelectEvent(SelectEvent event) {
        PrimeFaces.current().dialog().closeDynamic(event.getObject());
    }

}
