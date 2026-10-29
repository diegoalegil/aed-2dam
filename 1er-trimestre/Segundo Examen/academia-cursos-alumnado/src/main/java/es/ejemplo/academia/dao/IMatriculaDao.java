package es.ejemplo.academia.dao;

import es.ejemplo.academia.modelo.Matricula;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Operaciones de acceso a datos de la tabla matriculas. */
public interface IMatriculaDao {

    /**
     * Lista las matrículas ordenadas por id.
     *
     * @return lista de matrículas; vacía si no hay filas, nunca null
     */
    List<Matricula> listar();

    /**
     * Busca una matrícula por su identificador.
     *
     * @param id identificador de la matrícula
     * @return la matrícula, u Optional.empty() si el id es null o no existe
     */
    Optional<Matricula> buscarPorId(Integer id);

    /**
     * Inserta una matrícula.
     *
     * @param matricula matrícula a guardar (su id se ignora)
     * @return el id que genera SQLite
     * @throws NullPointerException si la matrícula es null
     * @throws DaoException         si SQLite rechaza la fila (FK, UNIQUE, CHECK, NOT NULL...)
     */
    int insertar(Matricula matricula);

    /**
     * Actualiza el alumno, el curso, la fecha y el importe de la matrícula con ese id.
     *
     * @param matricula matrícula con los datos nuevos
     * @return filas afectadas: 1, o 0 si el id no existe
     * @throws NullPointerException     si la matrícula es null
     * @throws IllegalArgumentException si el id de la matrícula es null
     * @throws DaoException             si SQLite rechaza los datos
     */
    int actualizar(Matricula matricula);

    /**
     * Elimina la matrícula con ese id.
     *
     * @param id identificador de la matrícula
     * @return filas afectadas: 1, o 0 si el id no existe
     * @throws NullPointerException si el id es null
     */
    int eliminar(Integer id);

    /**
     * Lista las matrículas de un alumno. Orden: fecha y, en empate, id.
     *
     * @param idAlumno identificador del alumno
     * @return matrículas del alumno; vacía si el id es null o no tiene matrículas
     */
    List<Matricula> listarPorAlumno(Integer idAlumno);

    /**
     * Suma los importes de las matrículas de un alumno.
     *
     * @param idAlumno identificador del alumno
     * @return la suma; BigDecimal.ZERO si el id es null o no tiene matrículas, nunca null
     */
    BigDecimal calcularTotalPagado(Integer idAlumno);

    /**
     * Cuenta los alumnos matriculados en cada curso. Salen también los cursos sin matrículas (con 0).
     * Orden: id del curso.
     *
     * @return LinkedHashMap de título del curso a número de matrículas
     */
    Map<String, Integer> contarAlumnosPorCurso();
}
