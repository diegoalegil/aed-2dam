package es.ejemplo.academia;

import es.ejemplo.academia.dao.*;
import es.ejemplo.academia.modelo.Alumno;
import es.ejemplo.academia.modelo.Matricula;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de la conexión que abre AbstractDao en cada operación y de DaoException. */
class ConexionDaoTest extends BaseDaoTest {

    @TempDir
    Path carpeta;

    /** Con la url a null el DAO se puede crear, pero al usarlo lanza IllegalArgumentException. */
    @Test
    void urlNulaLanzaIllegalArgumentAlUsarElDaoTest() {
        IAlumnoDao dao = new AlumnoDao(null);
        assertThrows(IllegalArgumentException.class, dao::listar);
    }

    /** Una url de otro gestor (MySQL) no empieza por jdbc:sqlite: y lanza IllegalArgumentException. */
    @Test
    void urlDeOtroGestorLanzaIllegalArgumentTest() {
        ICursoDao dao = new CursoDao("jdbc:mysql://localhost:3306/academia");
        assertThrows(IllegalArgumentException.class, dao::listar);
    }

    /** Si el fichero no existe: IllegalArgumentException y el fichero NO se crea (DriverManager lo crearía vacío). */
    @Test
    void ficheroInexistenteLanzaIllegalArgumentYNoSeCreaTest() {
        Path noExiste = carpeta.resolve("no-existe.sqlite3");
        IMatriculaDao dao = new MatriculaDao("jdbc:sqlite:" + noExiste);
        assertThrows(IllegalArgumentException.class, dao::listar);
        assertFalse(Files.exists(noExiste));
    }

    /** Una carpeta no es un fichero de base de datos: IllegalArgumentException también al insertar. */
    @Test
    void carpetaEnLugarDeFicheroLanzaIllegalArgumentTest() {
        IAlumnoDao dao = new AlumnoDao("jdbc:sqlite:" + carpeta);
        assertThrows(IllegalArgumentException.class,
                () -> dao.insertar(new Alumno(null, "Ana Ruiz", "ana@academia.es", "600111222")));
    }

    /** Las claves foráneas están activas: una matrícula de un alumno y un curso que no existen falla. */
    @Test
    void clavesForaneasActivasAlInsertarMatriculaTest() {
        assertThrows(DaoException.class, () -> matriculaDao.insertar(
                new Matricula(null, 999, 999, "2026-09-15", new BigDecimal("50.00"))));
    }

    /** Las claves foráneas están activas: no se puede borrar un alumno con matrículas, y sigue existiendo. */
    @Test
    void clavesForaneasActivasAlBorrarAlumnoConMatriculasTest() {
        int idAlumno = insertarAlumnoDePrueba();
        nuevaMatricula(idAlumno, insertarCursoDePrueba(), "2026-09-15", "120.50");
        assertThrows(DaoException.class, () -> alumnoDao.eliminar(idAlumno));
        assertTrue(alumnoDao.buscarPorId(idAlumno).isPresent());
    }

    /** DaoException es no comprobada (hereda de RuntimeException) y guarda el mensaje y la causa. */
    @Test
    void daoExceptionEsRuntimeYGuardaLaCausaTest() {
        SQLException causa = new SQLException("fallo de prueba");
        DaoException excepcion = new DaoException("mensaje de prueba", causa);
        assertInstanceOf(RuntimeException.class, excepcion);
        assertEquals("mensaje de prueba", excepcion.getMessage());
        assertSame(causa, excepcion.getCause());
    }

    /** La conexión se abre en cada operación: varias operaciones seguidas funcionan (no queda cerrada ni compartida). */
    @Test
    void variasOperacionesSeguidasFuncionanTest() {
        int idAlumno = insertarAlumnoDePrueba();
        int idCurso = insertarCursoDePrueba();
        nuevaMatricula(idAlumno, idCurso, "2026-09-15", "120.50");
        assertEquals(1, alumnoDao.listar().size());
        assertEquals(1, alumnoDao.listar().size());
        assertEquals(1, cursoDao.listar().size());
        assertEquals(1, matriculaDao.listar().size());
    }
}
