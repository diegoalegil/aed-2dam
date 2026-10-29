package es.ejemplo.academia;

import es.ejemplo.academia.dao.DaoException;
import es.ejemplo.academia.modelo.Curso;
import es.ejemplo.academia.modelo.Matricula;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de integración: varios DAO trabajando juntos sobre la misma base de datos. */
class IntegracionDaoTest extends BaseDaoTest {

    /** Matricular a un alumno en dos cursos: sus cursos, sus matrículas y su total cuadran. */
    @Test
    void matricularAlumnoEnDosCursosYConsultarTest() {
        int idAna = insertarAlumnoDePrueba();
        int idPython = nuevoCurso("Python", "MEDIO", 30, "95.00");
        int idBases = nuevoCurso("Bases de datos", "BASICO", 25, "70.50");
        nuevaMatricula(idAna, idPython, "2026-09-20", "95.00");
        nuevaMatricula(idAna, idBases, "2026-09-10", "70.50");
        assertEquals(List.of("Bases de datos", "Python"),
                cursoDao.listarDeAlumno(idAna).stream().map(Curso::getTitulo).toList());
        assertEquals(2, matriculaDao.listarPorAlumno(idAna).size());
        assertEquals(0, matriculaDao.calcularTotalPagado(idAna).compareTo(new BigDecimal("165.50")));
    }

    /** No se puede borrar un curso con matrículas (sin cascada): DaoException y el curso sigue. */
    @Test
    void borrarCursoConMatriculasLanzaDaoExceptionYNoLoBorraTest() {
        int idCurso = insertarCursoDePrueba();
        nuevaMatricula(insertarAlumnoDePrueba(), idCurso, "2026-09-15", "120.50");
        assertThrows(DaoException.class, () -> cursoDao.eliminar(idCurso));
        assertTrue(cursoDao.buscarPorId(idCurso).isPresent());
    }

    /** Borrando primero las matrículas y después el alumno, el borrado funciona. */
    @Test
    void borrarPrimeroMatriculasYDespuesAlumnoFuncionaTest() {
        int idAlumno = insertarAlumnoDePrueba();
        int idMatricula = nuevaMatricula(idAlumno, insertarCursoDePrueba(), "2026-09-15", "120.50");
        assertEquals(1, matriculaDao.eliminar(idMatricula));
        assertEquals(1, alumnoDao.eliminar(idAlumno));
        assertTrue(alumnoDao.listar().isEmpty());
    }

    /** Una matrícula de un alumno que existe en un curso que no existe falla por la clave foránea. */
    @Test
    void matriculaConCursoInexistenteLanzaDaoExceptionTest() {
        int idAlumno = insertarAlumnoDePrueba();
        assertThrows(DaoException.class, () -> matriculaDao.insertar(
                new Matricula(null, idAlumno, 999, "2026-09-15", new BigDecimal("50.00"))));
        assertTrue(matriculaDao.listar().isEmpty());
    }

    /** Con dos alumnos, las consultas de uno no mezclan los datos del otro y el recuento suma a los dos. */
    @Test
    void dosAlumnosNoSeMezclanTest() {
        int idAna = insertarAlumnoDePrueba();
        int idLuis = nuevoAlumno("Luis Gil", "luis@academia.es", null);
        int idJava = insertarCursoDePrueba();
        int idGit = nuevoCurso("Git", "BASICO", 10, "45.00");
        nuevaMatricula(idAna, idJava, "2026-09-15", "120.50");
        nuevaMatricula(idLuis, idJava, "2026-09-16", "100.00");
        nuevaMatricula(idLuis, idGit, "2026-09-17", "45.00");
        assertEquals(List.of("Java desde cero"), cursoDao.listarDeAlumno(idAna).stream().map(Curso::getTitulo).toList());
        assertEquals(0, matriculaDao.calcularTotalPagado(idLuis).compareTo(new BigDecimal("145.00")));
        Map<String, Integer> recuento = matriculaDao.contarAlumnosPorCurso();
        assertEquals(2, recuento.get("Java desde cero"));
        assertEquals(1, recuento.get("Git"));
    }
}
