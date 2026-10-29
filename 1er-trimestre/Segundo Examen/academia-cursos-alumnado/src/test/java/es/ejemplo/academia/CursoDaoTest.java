package es.ejemplo.academia;

import es.ejemplo.academia.dao.DaoException;
import es.ejemplo.academia.modelo.Curso;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de CursoDao: CRUD, CHECK, filtros por nivel y por precio, y cursos de un alumno. */
class CursoDaoTest extends BaseDaoTest {

    /** Lo insertado se recupera igual: título, nivel, horas y precio (BigDecimal comparado con compareTo). */
    @Test
    void insertarYBuscarPorIdDevuelveCursoCompletoTest() {
        int id = insertarCursoDePrueba();
        Curso curso = cursoDao.buscarPorId(id).orElseThrow();
        assertEquals(id, curso.getId());
        assertEquals("Java desde cero", curso.getTitulo());
        assertEquals("BASICO", curso.getNivel());
        assertEquals(40, curso.getHoras());
        assertEquals(0, curso.getPrecio().compareTo(new BigDecimal("120.50")));
    }

    /** listar devuelve los cursos ordenados por id. */
    @Test
    void listarDevuelveCursosOrdenadosPorIdTest() {
        int idPrimero = nuevoCurso("Python", "MEDIO", 30, "95.00");
        int idSegundo = nuevoCurso("Git", "BASICO", 10, "45.00");
        assertEquals(List.of(idPrimero, idSegundo), cursoDao.listar().stream().map(Curso::getId).toList());
    }

    /** insertar(null) lanza NullPointerException. */
    @Test
    void insertarNuloLanzaNullPointerTest() {
        assertThrows(NullPointerException.class, () -> cursoDao.insertar(null));
    }

    /** actualizar cambia el nivel y el precio y devuelve 1 fila afectada. */
    @Test
    void actualizarModificaNivelYPrecioYDevuelveUnoTest() {
        int id = insertarCursoDePrueba();
        int filas = cursoDao.actualizar(new Curso(id, "Java desde cero", "MEDIO", 40, new BigDecimal("150.25")));
        assertEquals(1, filas);
        Curso curso = cursoDao.buscarPorId(id).orElseThrow();
        assertEquals("MEDIO", curso.getNivel());
        assertEquals(0, curso.getPrecio().compareTo(new BigDecimal("150.25")));
    }

    /** actualizar(null) lanza NullPointerException. */
    @Test
    void actualizarNuloLanzaNullPointerTest() {
        assertThrows(NullPointerException.class, () -> cursoDao.actualizar(null));
    }

    /** eliminar un curso sin matrículas devuelve 1 y deja de existir. */
    @Test
    void eliminarCursoSinMatriculasDevuelveUnoTest() {
        int id = insertarCursoDePrueba();
        assertEquals(1, cursoDao.eliminar(id));
        assertTrue(cursoDao.buscarPorId(id).isEmpty());
    }

    /** CHECK (horas > 0): un curso de 0 horas lanza DaoException. */
    @Test
    void horasCeroLanzaDaoExceptionTest() {
        assertThrows(DaoException.class,
                () -> cursoDao.insertar(new Curso(null, "Curso vacío", "BASICO", 0, new BigDecimal("10.00"))));
    }

    /** listarPorNivel devuelve solo los cursos de ese nivel, del más barato al más caro. */
    @Test
    void listarPorNivelFiltraYOrdenaPorPrecioTest() {
        nuevoCurso("Java desde cero", "BASICO", 40, "120.50");
        nuevoCurso("Spring Boot", "AVANZADO", 50, "210.75");
        nuevoCurso("Git", "BASICO", 10, "45.00");
        nuevoCurso("SQL", "MEDIO", 30, "95.00");
        List<String> titulos = cursoDao.listarPorNivel("BASICO").stream().map(Curso::getTitulo).toList();
        assertEquals(List.of("Git", "Java desde cero"), titulos);
    }

    /** listarPorNivel con nivel null o en blanco devuelve una lista vacía. */
    @Test
    void listarPorNivelNuloOBlancoDevuelveVacioTest() {
        insertarCursoDePrueba();
        assertTrue(cursoDao.listarPorNivel(null).isEmpty());
        assertTrue(cursoDao.listarPorNivel(" ").isEmpty());
    }

    /** listarConPrecioHasta incluye el precio máximo y ordena de más caro a más barato; en empate, por id. */
    @Test
    void listarConPrecioHastaIncluyeElLimiteYOrdenaDescendenteTest() {
        nuevoCurso("Git", "BASICO", 10, "45.00");
        nuevoCurso("Spring Boot", "AVANZADO", 50, "210.75");
        nuevoCurso("SQL", "MEDIO", 30, "80.00");
        nuevoCurso("Java desde cero", "BASICO", 40, "120.50");
        nuevoCurso("HTML y CSS", "BASICO", 20, "80.00");
        List<String> titulos = cursoDao.listarConPrecioHasta(new BigDecimal("120.50"))
                .stream().map(Curso::getTitulo).toList();
        assertEquals(List.of("Java desde cero", "SQL", "HTML y CSS", "Git"), titulos);
    }

    /** listarConPrecioHasta(null) devuelve una lista vacía. */
    @Test
    void listarConPrecioHastaNuloDevuelveVacioTest() {
        insertarCursoDePrueba();
        assertTrue(cursoDao.listarConPrecioHasta(null).isEmpty());
    }

    /**
     * listarDeAlumno devuelve solo los cursos en los que está matriculado el alumno, ordenados por título
     * y con el id del curso (no el de la matrícula).
     */
    @Test
    void listarDeAlumnoDevuelveSusCursosOrdenadosPorTituloTest() {
        int idAna = insertarAlumnoDePrueba();
        int idLuis = nuevoAlumno("Luis Gil", "luis@academia.es", null);
        int idPython = nuevoCurso("Python", "MEDIO", 30, "95.00");
        int idGit = nuevoCurso("Git", "BASICO", 10, "45.00");
        int idSql = nuevoCurso("SQL", "MEDIO", 30, "80.00");
        nuevaMatricula(idLuis, idSql, "2026-09-12", "80.00");
        nuevaMatricula(idAna, idPython, "2026-09-10", "95.00");
        nuevaMatricula(idAna, idGit, "2026-09-12", "45.00");
        List<Curso> cursos = cursoDao.listarDeAlumno(idAna);
        assertEquals(List.of("Git", "Python"), cursos.stream().map(Curso::getTitulo).toList());
        assertEquals(List.of(idGit, idPython), cursos.stream().map(Curso::getId).toList());
    }

    /** listarDeAlumno(null) devuelve una lista vacía. */
    @Test
    void listarDeAlumnoNuloDevuelveVacioTest() {
        assertTrue(cursoDao.listarDeAlumno(null).isEmpty());
    }
}
