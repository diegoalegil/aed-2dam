package es.ejemplo.academia.dao;

import es.ejemplo.academia.modelo.Alumno;
import java.util.List;
import java.util.Optional;

/** Operaciones de acceso a datos de la tabla alumnos. */
public interface IAlumnoDao {

    /**
     * Lista los alumnos ordenados por id.
     *
     * @return lista de alumnos; vacía si no hay filas, nunca null
     */
    List<Alumno> listar();

    /**
     * Busca un alumno por su identificador.
     *
     * @param id identificador del alumno
     * @return el alumno, u Optional.empty() si el id es null o no existe
     */
    Optional<Alumno> buscarPorId(Integer id);

    /**
     * Inserta un alumno.
     *
     * @param alumno alumno a guardar (su id se ignora)
     * @return el id que genera SQLite
     * @throws NullPointerException si el alumno es null
     * @throws DaoException         si SQLite rechaza la fila (NOT NULL, UNIQUE...)
     */
    int insertar(Alumno alumno);

    /**
     * Actualiza el nombre, el email y el teléfono del alumno con ese id.
     *
     * @param alumno alumno con los datos nuevos
     * @return filas afectadas: 1, o 0 si el id no existe
     * @throws NullPointerException     si el alumno es null
     * @throws IllegalArgumentException si el id del alumno es null
     * @throws DaoException             si SQLite rechaza los datos
     */
    int actualizar(Alumno alumno);

    /**
     * Elimina el alumno con ese id.
     *
     * @param id identificador del alumno
     * @return filas afectadas: 1, o 0 si el id no existe
     * @throws NullPointerException si el id es null
     * @throws DaoException         si el alumno tiene matrículas (clave foránea)
     */
    int eliminar(Integer id);

    /**
     * Busca los alumnos cuyo nombre contiene el texto, sin distinguir mayúsculas de minúsculas.
     * Orden: nombre y, en empate, id.
     *
     * @param texto texto a buscar dentro del nombre
     * @return alumnos que coinciden; vacía si el texto es null o está en blanco
     */
    List<Alumno> buscarPorNombre(String texto);
}
