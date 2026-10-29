package es.ejemplo.academia;

import es.ejemplo.academia.dao.DaoException;
import es.ejemplo.academia.modelo.Matricula;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de MatriculaDao: CRUD, CHECK, matrículas de un alumno, total pagado y recuento por curso. */
class MatriculaDaoTest extends BaseDaoTest {

    /** Lo insertado se recupera igual: alumno, curso, fecha e importe (compareTo). */
    @Test
    void insertarYBuscarPorIdDevuelveMatriculaCompletaTest() {
        int idAlumno = insertarAlumnoDePrueba();
        int idCurso = insertarCursoDePrueba();
        int id = nuevaMatricula(idAlumno, idCurso, "2026-09-15", "100.25");
        Matricula matricula = matriculaDao.buscarPorId(id).orElseThrow();
        assertEquals(id, matricula.getId());
        assertEquals(idAlumno, matricula.getIdAlumno());
        assertEquals(idCurso, matricula.getIdCurso());
        assertEquals("2026-09-15", matricula.getFecha());
        assertEquals(0, matricula.getImporte().compareTo(new BigDecimal("100.25")));
    }

    /** listar devuelve las matrículas ordenadas por id. */
    @Test
    void listarDevuelveMatriculasOrdenadasPorIdTest() {
        int idAlumno = insertarAlumnoDePrueba();
        int idPrimera = nuevaMatricula(idAlumno, nuevoCurso("Python", "MEDIO", 30, "95.00"), "2026-10-01", "95.00");
        int idSegunda = nuevaMatricula(idAlumno, nuevoCurso("Git", "BASICO", 10, "45.00"), "2026-09-01", "45.00");
        assertEquals(List.of(idPrimera, idSegunda), matriculaDao.listar().stream().map(Matricula::getId).toList());
    }

    /** actualizar cambia la fecha y el importe y devuelve 1 fila afectada. */
    @Test
    void actualizarModificaFechaEImporteYDevuelveUnoTest() {
        int idAlumno = insertarAlumnoDePrueba();
        int idCurso = insertarCursoDePrueba();
        int id = nuevaMatricula(idAlumno, idCurso, "2026-09-15", "120.50");
        int filas = matriculaDao.actualizar(new Matricula(id, idAlumno, idCurso, "2026-09-20", new BigDecimal("60.25")));
        assertEquals(1, filas);
        Matricula matricula = matriculaDao.buscarPorId(id).orElseThrow();
        assertEquals("2026-09-20", matricula.getFecha());
        assertEquals(0, matricula.getImporte().compareTo(new BigDecimal("60.25")));
    }

    /** actualizar una matrícula sin id lanza IllegalArgumentException. */
    @Test
    void actualizarSinIdLanzaIllegalArgumentTest() {
        assertThrows(IllegalArgumentException.class,
                () -> matriculaDao.actualizar(new Matricula(null, 1, 1, "2026-09-15", BigDecimal.TEN)));
    }

    /** eliminar borra la matrícula, devuelve 1 y después ya no se encuentra. */
    @Test
    void eliminarMatriculaDevuelveUnoYYaNoExisteTest() {
        int id = nuevaMatricula(insertarAlumnoDePrueba(), insertarCursoDePrueba(), "2026-09-15", "120.50");
        assertEquals(1, matriculaDao.eliminar(id));
        assertTrue(matriculaDao.buscarPorId(id).isEmpty());
    }

    /** eliminar un id que no existe devuelve 0 filas afectadas. */
    @Test
    void eliminarInexistenteDevuelveCeroTest() {
        assertEquals(0, matriculaDao.eliminar(999));
    }

    /** CHECK (importe >= 0): un importe negativo lanza DaoException. */
    @Test
    void importeNegativoLanzaDaoExceptionTest() {
        int idAlumno = insertarAlumnoDePrueba();
        int idCurso = insertarCursoDePrueba();
        assertThrows(DaoException.class, () -> matriculaDao.insertar(
                new Matricula(null, idAlumno, idCurso, "2026-09-15", new BigDecimal("-1.00"))));
    }

    /** listarPorAlumno devuelve solo las de ese alumno, por fecha y, con la misma fecha, por id. */
    @Test
    void listarPorAlumnoOrdenaPorFechaYDespuesPorIdTest() {
        int idAna = insertarAlumnoDePrueba();
        int idLuis = nuevoAlumno("Luis Gil", "luis@academia.es", null);
        int idPython = nuevoCurso("Python", "MEDIO", 30, "95.00");
        int idGit = nuevoCurso("Git", "BASICO", 10, "45.00");
        int idSql = nuevoCurso("SQL", "MEDIO", 30, "80.00");
        int octubre = nuevaMatricula(idAna, idPython, "2026-10-01", "95.00");
        int septiembreA = nuevaMatricula(idAna, idSql, "2026-09-12", "80.00");
        nuevaMatricula(idLuis, idGit, "2026-09-01", "45.00");
        int septiembreB = nuevaMatricula(idAna, idGit, "2026-09-12", "45.00");
        List<Integer> ids = matriculaDao.listarPorAlumno(idAna).stream().map(Matricula::getId).toList();
        assertEquals(List.of(septiembreA, septiembreB, octubre), ids);
    }

    /** listarPorAlumno(null) devuelve una lista vacía. */
    @Test
    void listarPorAlumnoNuloDevuelveVacioTest() {
        assertTrue(matriculaDao.listarPorAlumno(null).isEmpty());
    }

    /** calcularTotalPagado suma solo los importes del alumno pedido (compareTo). */
    @Test
    void calcularTotalPagadoSumaSoloLosImportesDelAlumnoTest() {
        int idAna = insertarAlumnoDePrueba();
        int idLuis = nuevoAlumno("Luis Gil", "luis@academia.es", null);
        int idPython = nuevoCurso("Python", "MEDIO", 30, "95.00");
        int idGit = nuevoCurso("Git", "BASICO", 10, "45.00");
        nuevaMatricula(idAna, idPython, "2026-09-10", "95.50");
        nuevaMatricula(idAna, idGit, "2026-09-12", "40.25");
        nuevaMatricula(idLuis, idPython, "2026-09-12", "95.00");
        assertEquals(0, matriculaDao.calcularTotalPagado(idAna).compareTo(new BigDecimal("135.75")));
    }

    /**
     * contarAlumnosPorCurso devuelve título y número de alumnos en el orden de id de los cursos,
     * también los cursos sin matrículas (con 0).
     */
    @Test
    void contarAlumnosPorCursoDevuelveTituloYNumeroEnOrdenDeIdTest() {
        int idAna = insertarAlumnoDePrueba();
        int idLuis = nuevoAlumno("Luis Gil", "luis@academia.es", null);
        int idSql = nuevoCurso("SQL", "MEDIO", 30, "80.00");
        nuevoCurso("HTML y CSS", "BASICO", 20, "45.00");
        int idGit = nuevoCurso("Git", "BASICO", 10, "45.00");
        nuevaMatricula(idAna, idSql, "2026-09-10", "80.00");
        nuevaMatricula(idLuis, idSql, "2026-09-11", "80.00");
        nuevaMatricula(idLuis, idGit, "2026-09-12", "45.00");
        Map<String, Integer> recuento = matriculaDao.contarAlumnosPorCurso();
        assertEquals(List.of("SQL", "HTML y CSS", "Git"), new ArrayList<>(recuento.keySet()));
        assertEquals(List.of(2, 0, 1), new ArrayList<>(recuento.values()));
    }

    /** Sin cursos, contarAlumnosPorCurso devuelve un mapa vacío (no null). */
    @Test
    void contarAlumnosPorCursoSinCursosDevuelveMapaVacioTest() {
        Map<String, Integer> recuento = matriculaDao.contarAlumnosPorCurso();
        assertNotNull(recuento);
        assertTrue(recuento.isEmpty());
    }
}
