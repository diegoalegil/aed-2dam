package es.ejemplo.hotel;

import es.ejemplo.hotel.dao.DaoException;
import es.ejemplo.hotel.modelo.Huesped;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de HuespedDao: un comportamiento por cada metodo @Test. */
class HuespedDaoTest extends BaseDaoTest {

    /** Sin filas, listar devuelve una lista vacia (nunca null). */
    @Test
    void listarSinFilasDevuelveListaVaciaTest() {
        List<Huesped> huespedes = huespedDao.listar();
        assertNotNull(huespedes);
        assertTrue(huespedes.isEmpty());
    }

    /** insertar devuelve el id generado y ademas lo guarda en el objeto. */
    @Test
    void insertarAsignaElIdGeneradoAlHuespedTest() {
        Huesped nuevo = new Huesped(null, "Ana Ruiz", "11111111A", "ana@hotel.es", "ES");
        int id = huespedDao.insertar(nuevo);
        assertTrue(id > 0);
        assertEquals(id, nuevo.getId());
    }

    /** buscarPorId recupera todos los campos guardados. */
    @Test
    void buscarPorIdDevuelveLosCamposGuardadosTest() {
        int id = insertarHuespedDePrueba();
        Huesped leido = huespedDao.buscarPorId(id).orElseThrow();
        assertEquals(id, leido.getId());
        assertEquals("Ana Ruiz", leido.getNombre());
        assertEquals("11111111A", leido.getDni());
        assertEquals("ana@hotel.es", leido.getEmail());
        assertEquals("ES", leido.getPais());
    }

    /** Un id que no existe devuelve Optional vacio. */
    @Test
    void buscarPorIdInexistenteDevuelveVacioTest() {
        insertarHuespedDePrueba();
        assertTrue(huespedDao.buscarPorId(5).isEmpty());
    }

    /** Un id null devuelve Optional vacio, sin excepcion. */
    @Test
    void buscarPorIdNuloDevuelveVacioTest() {
        assertTrue(huespedDao.buscarPorId(null).isEmpty());
    }

    /** listar devuelve los huespedes por id (orden de alta), no por nombre. */
    @Test
    void listarOrdenaPorIdTest() {
        int idZoe = nuevoHuesped("Zoe Sanz", "22222222B", "zoe@hotel.es", "ES");
        int idAna = nuevoHuesped("Ana Ruiz", "11111111A", "ana@hotel.es", "ES");
        assertEquals(List.of(idZoe, idAna), huespedDao.listar().stream().map(Huesped::getId).toList());
    }

    /** Un nombre en blanco se rechaza en Java y no llega a la base de datos. */
    @Test
    void insertarNombreEnBlancoLanzaIllegalArgumentYNoInsertaTest() {
        assertThrows(IllegalArgumentException.class,
                () -> huespedDao.insertar(new Huesped(null, "   ", "11111111A", "ana@hotel.es", "ES")));
        assertTrue(huespedDao.listar().isEmpty());
    }

    /** actualizar cambia los datos y devuelve true. */
    @Test
    void actualizarCambiaLosDatosYDevuelveTrueTest() {
        int id = insertarHuespedDePrueba();
        assertTrue(huespedDao.actualizar(new Huesped(id, "Ana Ruiz Gil", "11111111A", "ana.ruiz@hotel.es", "PT")));
        Huesped leido = huespedDao.buscarPorId(id).orElseThrow();
        assertEquals("Ana Ruiz Gil", leido.getNombre());
        assertEquals("ana.ruiz@hotel.es", leido.getEmail());
        assertEquals("PT", leido.getPais());
    }

    /** Actualizar un id que no existe devuelve false. */
    @Test
    void actualizarInexistenteDevuelveFalseTest() {
        assertFalse(huespedDao.actualizar(new Huesped(999, "Eva Mar", "33333333C", "eva@hotel.es", "ES")));
    }

    /** Eliminar un huesped existente devuelve true y ya no se encuentra. */
    @Test
    void eliminarExistenteDevuelveTrueTest() {
        int id = insertarHuespedDePrueba();
        assertTrue(huespedDao.eliminar(id));
        assertTrue(huespedDao.buscarPorId(id).isEmpty());
    }

    /** Eliminar con id null no lanza excepcion: devuelve false. */
    @Test
    void eliminarNuloDevuelveFalseTest() {
        insertarHuespedDePrueba();
        assertFalse(huespedDao.eliminar(null));
        assertEquals(1, huespedDao.listar().size());
    }

    /** El email es UNIQUE: repetirlo es un error de la base de datos. */
    @Test
    void insertarEmailDuplicadoLanzaDaoExceptionTest() {
        insertarHuespedDePrueba();
        assertThrows(DaoException.class,
                () -> huespedDao.insertar(new Huesped(null, "Ana Bis", "99999999Z", "ana@hotel.es", "ES")));
    }

    /** Solo salen los huespedes sin ninguna reserva (una CANCELADA tambien es una reserva), por id. */
    @Test
    void listarSinReservasDevuelveSoloLosQueNoTienenNingunaTest() {
        int idHabitacion = insertarHabitacionDePrueba();
        int idZoe = nuevoHuesped("Zoe Sanz", "22222222B", "zoe@hotel.es", "ES");
        int idBruno = nuevoHuesped("Bruno Paz", "44444444D", "bruno@hotel.es", "FR");
        nuevoHuesped("Carla Vidal", "55555555E", "carla@hotel.es", "ES");
        nuevoHuesped("Ana Ruiz", "11111111A", "ana@hotel.es", "ES");
        nuevaReserva(idZoe, idHabitacion, "2026-11-02", 2, "CANCELADA");
        nuevaReserva(idBruno, idHabitacion, "2026-11-10", 1, "CONFIRMADA");
        assertEquals(List.of("Carla Vidal", "Ana Ruiz"),
                huespedDao.listarSinReservas().stream().map(Huesped::getNombre).toList());
    }

    /** Recuento de reservas NO canceladas por email: incluye los ceros, de mas a menos y en empate por email. */
    @Test
    void contarReservasPorHuespedIncluyeCerosYOrdenaTest() {
        int idHabitacion = insertarHabitacionDePrueba();
        int idCarla = nuevoHuesped("Carla Vidal", "55555555E", "carla@hotel.es", "ES");
        nuevoHuesped("Ana Ruiz", "11111111A", "ana@hotel.es", "ES");
        int idDario = nuevoHuesped("Dario Leon", "66666666F", "dario@hotel.es", "IT");
        int idBruno = nuevoHuesped("Bruno Paz", "44444444D", "bruno@hotel.es", "FR");
        nuevaReserva(idCarla, idHabitacion, "2026-11-01", 2, "CONFIRMADA");
        nuevaReserva(idCarla, idHabitacion, "2026-12-01", 1, "FINALIZADA");
        nuevaReserva(idDario, idHabitacion, "2026-11-05", 3, "CONFIRMADA");
        nuevaReserva(idBruno, idHabitacion, "2026-11-08", 2, "CANCELADA");
        nuevaReserva(idBruno, idHabitacion, "2026-11-20", 2, "FINALIZADA");
        Map<String, Integer> recuento = huespedDao.contarReservasPorHuesped();
        assertEquals(List.of("carla@hotel.es", "bruno@hotel.es", "dario@hotel.es", "ana@hotel.es"),
                new ArrayList<>(recuento.keySet()));
        assertEquals(List.of(2, 1, 1, 0), new ArrayList<>(recuento.values()));
    }
}
