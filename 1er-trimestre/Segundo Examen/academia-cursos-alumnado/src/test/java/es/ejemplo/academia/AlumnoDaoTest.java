package es.ejemplo.academia;

import es.ejemplo.academia.dao.DaoException;
import es.ejemplo.academia.modelo.Alumno;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de AlumnoDao: CRUD, contrato de nulos y buscarPorNombre. */
class AlumnoDaoTest extends BaseDaoTest {

    /** Sin alumnos, listar devuelve una lista vacía (no null). */
    @Test
    void listarSinAlumnosDevuelveListaVaciaTest() {
        List<Alumno> alumnos = alumnoDao.listar();
        assertNotNull(alumnos);
        assertTrue(alumnos.isEmpty());
    }

    /** insertar devuelve el id que genera SQLite, que es positivo. */
    @Test
    void insertarDevuelveIdPositivoTest() {
        int id = alumnoDao.insertar(new Alumno(null, "Ana Ruiz", "ana@academia.es", "600111222"));
        assertTrue(id > 0);
    }

    /** buscarPorId devuelve el alumno con todos sus campos leídos de la base de datos. */
    @Test
    void buscarPorIdDevuelveAlumnoCompletoTest() {
        int id = insertarAlumnoDePrueba();
        Alumno alumno = alumnoDao.buscarPorId(id).orElseThrow();
        assertEquals(id, alumno.getId());
        assertEquals("Ana Ruiz", alumno.getNombre());
        assertEquals("ana@academia.es", alumno.getEmail());
        assertEquals("600111222", alumno.getTelefono());
    }

    /** buscarPorId(null) devuelve Optional vacío, sin lanzar excepción. */
    @Test
    void buscarPorIdNuloDevuelveVacioTest() {
        assertTrue(alumnoDao.buscarPorId(null).isEmpty());
    }

    /** buscarPorId de un id que no existe devuelve Optional vacío. */
    @Test
    void buscarPorIdInexistenteDevuelveVacioTest() {
        insertarAlumnoDePrueba();
        assertTrue(alumnoDao.buscarPorId(999).isEmpty());
    }

    /** listar devuelve los alumnos ordenados por id. */
    @Test
    void listarDevuelveAlumnosOrdenadosPorIdTest() {
        int idPrimero = nuevoAlumno("Zoe Vidal", "zoe@academia.es", "600000001");
        int idSegundo = nuevoAlumno("Ana Ruiz", "ana@academia.es", "600000002");
        assertEquals(List.of(idPrimero, idSegundo), alumnoDao.listar().stream().map(Alumno::getId).toList());
    }

    /** El teléfono es opcional: un alumno sin teléfono se guarda y se lee con telefono null. */
    @Test
    void insertarAlumnoSinTelefonoGuardaNullTest() {
        int id = nuevoAlumno("Luis Gil", "luis@academia.es", null);
        assertNull(alumnoDao.buscarPorId(id).orElseThrow().getTelefono());
    }

    /** actualizar cambia nombre, email y teléfono y devuelve 1 fila afectada. */
    @Test
    void actualizarModificaLosDatosYDevuelveUnoTest() {
        int id = insertarAlumnoDePrueba();
        int filas = alumnoDao.actualizar(new Alumno(id, "Ana Ruiz Pérez", "ana.ruiz@academia.es", "699999999"));
        assertEquals(1, filas);
        Alumno alumno = alumnoDao.buscarPorId(id).orElseThrow();
        assertEquals("Ana Ruiz Pérez", alumno.getNombre());
        assertEquals("ana.ruiz@academia.es", alumno.getEmail());
        assertEquals("699999999", alumno.getTelefono());
    }

    /** actualizar un alumno sin id lanza IllegalArgumentException. */
    @Test
    void actualizarSinIdLanzaIllegalArgumentTest() {
        assertThrows(IllegalArgumentException.class,
                () -> alumnoDao.actualizar(new Alumno(null, "Ana Ruiz", "ana@academia.es", "600111222")));
    }

    /** eliminar borra el alumno, devuelve 1 y después ya no se encuentra. */
    @Test
    void eliminarBorraElAlumnoYDevuelveUnoTest() {
        int id = insertarAlumnoDePrueba();
        assertEquals(1, alumnoDao.eliminar(id));
        assertTrue(alumnoDao.buscarPorId(id).isEmpty());
    }

    /** eliminar(null) lanza NullPointerException. */
    @Test
    void eliminarNuloLanzaNullPointerTest() {
        assertThrows(NullPointerException.class, () -> alumnoDao.eliminar(null));
    }

    /** Un nombre null llega a SQLite (NOT NULL) y sale como DaoException con la SQLException de causa. */
    @Test
    void nombreNuloLanzaDaoExceptionConCausaSqlTest() {
        DaoException excepcion = assertThrows(DaoException.class,
                () -> alumnoDao.insertar(new Alumno(null, null, "ana@academia.es", "600111222")));
        assertInstanceOf(SQLException.class, excepcion.getCause());
    }

    /** buscarPorNombre devuelve los nombres que contienen el texto, ordenados por nombre. */
    @Test
    void buscarPorNombreContieneYOrdenaPorNombreTest() {
        nuevoAlumno("Luis Ruiz", "luis@academia.es", null);
        nuevoAlumno("Pedro Gil", "pedro@academia.es", null);
        nuevoAlumno("Ana Ruiz", "ana@academia.es", null);
        List<String> nombres = alumnoDao.buscarPorNombre("Ruiz").stream().map(Alumno::getNombre).toList();
        assertEquals(List.of("Ana Ruiz", "Luis Ruiz"), nombres);
    }

    /** buscarPorNombre con texto null o en blanco (un espacio, que sí aparece dentro de «Ana Ruiz») devuelve una lista vacía. */
    @Test
    void buscarPorNombreNuloOBlancoDevuelveVacioTest() {
        insertarAlumnoDePrueba();
        assertTrue(alumnoDao.buscarPorNombre(null).isEmpty());
        assertTrue(alumnoDao.buscarPorNombre(" ").isEmpty());
    }
}
