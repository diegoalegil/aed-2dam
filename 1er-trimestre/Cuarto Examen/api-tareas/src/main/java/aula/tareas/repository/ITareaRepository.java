package aula.tareas.repository;

import aula.tareas.modelo.Tarea;

import java.util.List;
//ESTO ES LO QUE SE DOCUMENTA
public interface ITareaRepository {
    /**
     * Crea una tarea y la guarda.
     * @param titulo titulo de la tarea
     * @return la tarea creada con su id
     */
    public Tarea create(String titulo);

    /**
     * Borra la tarea con ese id.
     * @param identificador id de la tarea
     * @return true si se ha borrado, false si no existia
     */
    public boolean delete(Long identificador);

    /**
     * Devuelve todas las tareas.
     * @return lista de tareas, vacia si no hay ninguna
     */
    public List<Tarea> findAll();

    /**
     * Busca la tarea con ese id.
     * @param identificador id de la tarea
     * @return la tarea, o null si no existe
     */
    public Tarea findById(Long identificador);

    /**
     * Actualiza una tarea que ya existe.
     * @param tarea tarea con su id y los datos nuevos
     * @return la tarea actualizada, o null si no existe
     */
    public Tarea update(Tarea tarea);
}
