package aula.tareas.service;

import aula.tareas.api.TareaRequest;

import java.util.List;

public interface ITareaService {

    /**
     * Soy la documentacion del metodo xD
     *
     * @return
     */
    public List<TareaRequest> listar();

    /**
     * Soy la documentacion del metodo x2
     *
     * @param id
     * @return
     */
    public TareaRequest buscar(long id);

    /**
     * Sigo siendo la documentacion del metodo, tan importante y solo suma nota si tienes mas de un 8 xd
     *
     * @param titulo
     * @return
     */
    public TareaRequest crear(String titulo);

    public TareaRequest actualizar(long id, String titulo);

    public boolean borrar(long id);
}
