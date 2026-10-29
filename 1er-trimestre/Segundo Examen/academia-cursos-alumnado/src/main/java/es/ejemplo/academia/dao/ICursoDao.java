package es.ejemplo.academia.dao;

import es.ejemplo.academia.modelo.Curso;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Operaciones de acceso a datos de la tabla cursos. */
public interface ICursoDao {

    /**
     * Lista los cursos ordenados por id.
     *
     * @return lista de cursos; vacía si no hay filas, nunca null
     */
    List<Curso> listar();

    /**
     * Busca un curso por su identificador.
     *
     * @param id identificador del curso
     * @return el curso, u Optional.empty() si el id es null o no existe
     */
    Optional<Curso> buscarPorId(Integer id);

    /**
     * Inserta un curso.
     *
     * @param curso curso a guardar (su id se ignora)
     * @return el id que genera SQLite
     * @throws NullPointerException si el curso es null
     * @throws DaoException         si SQLite rechaza la fila (NOT NULL, UNIQUE, CHECK...)
     */
    int insertar(Curso curso);

    /**
     * Actualiza el título, el nivel, las horas y el precio del curso con ese id.
     *
     * @param curso curso con los datos nuevos
     * @return filas afectadas: 1, o 0 si el id no existe
     * @throws NullPointerException     si el curso es null
     * @throws IllegalArgumentException si el id del curso es null
     * @throws DaoException             si SQLite rechaza los datos
     */
    int actualizar(Curso curso);

    /**
     * Elimina el curso con ese id.
     *
     * @param id identificador del curso
     * @return filas afectadas: 1, o 0 si el id no existe
     * @throws NullPointerException si el id es null
     * @throws DaoException         si el curso tiene matrículas (clave foránea)
     */
    int eliminar(Integer id);

    /**
     * Lista los cursos de un nivel. Orden: precio ascendente y, en empate, id.
     *
     * @param nivel BASICO, MEDIO o AVANZADO
     * @return cursos de ese nivel; vacía si el nivel es null o está en blanco
     */
    List<Curso> listarPorNivel(String nivel);

    /**
     * Lista los cursos cuyo precio es menor o igual que el máximo (el máximo se incluye).
     * Orden: precio descendente y, en empate, id.
     *
     * @param maximo precio máximo
     * @return cursos que cumplen; vacía si el máximo es null
     */
    List<Curso> listarConPrecioHasta(BigDecimal maximo);

    /**
     * Lista los cursos en los que está matriculado un alumno. Orden: título.
     *
     * @param idAlumno identificador del alumno
     * @return cursos del alumno; vacía si el id es null o no tiene matrículas
     */
    List<Curso> listarDeAlumno(Integer idAlumno);
}
