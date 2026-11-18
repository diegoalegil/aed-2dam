package aula.tareas.service;

import aula.tareas.modelo.Tarea;

import java.util.List;

public interface ITareaService {

    /**
     * Soy la documentacion del metodo xD
     * @return
     */
    public List<Tarea> listar();

    /**
     * Soy la documentacion del metodo x2
     * @param id
     * @return
     */
    public Tarea buscar(long id);

    /**
     * Sigo siendo la documentacion del metodo, tan importante y solo suma nota si tienes mas de un 8 xd
     * @param titulo
     * @return
     */
    public Tarea crear(String titulo);
}
