package es.ejemplo.hotel;

import es.ejemplo.hotel.dao.DaoException;
import es.ejemplo.hotel.modelo.Reserva;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de ReservaDao: un comportamiento por cada metodo @Test. */
class ReservaDaoTest extends BaseDaoTest {

    /** Sin filas, listar devuelve una lista vacia (nunca null). */
    @Test
    void listarSinFilasDevuelveListaVaciaTest() {
        assertTrue(reservaDao.listar().isEmpty());
    }

    /** buscarPorId recupera todos los campos guardados. */
    @Test
    void insertarYBuscarPorIdDevuelveLosCamposTest() {
        int idHuesped = insertarHuespedDePrueba();
        int idHabitacion = insertarHabitacionDePrueba();
        int id = nuevaReserva(idHuesped, idHabitacion, "2026-11-02", 3, "CONFIRMADA");
        Reserva leida = reservaDao.buscarPorId(id).orElseThrow();
        assertEquals(idHuesped, leida.getIdHuesped());
        assertEquals(idHabitacion, leida.getIdHabitacion());
        assertEquals("2026-11-02", leida.getFechaEntrada());
        assertEquals(3, leida.getNoches());
        assertEquals("CONFIRMADA", leida.getEstado());
    }

    /** noches tiene un CHECK (mayor que 0): 0 noches es DaoException. */
    @Test
    void insertarNochesCeroLanzaDaoExceptionTest() {
        int idHuesped = insertarHuespedDePrueba();
        int idHabitacion = insertarHabitacionDePrueba();
        assertThrows(DaoException.class,
                () -> reservaDao.insertar(new Reserva(null, idHuesped, idHabitacion, "2026-11-02", 0, "CONFIRMADA")));
    }

    /** El estado es un texto obligatorio: en blanco se rechaza en Java. */
    @Test
    void insertarEstadoEnBlancoLanzaIllegalArgumentTest() {
        int idHuesped = insertarHuespedDePrueba();
        int idHabitacion = insertarHabitacionDePrueba();
        assertThrows(IllegalArgumentException.class,
                () -> reservaDao.insertar(new Reserva(null, idHuesped, idHabitacion, "2026-11-02", 2, " ")));
    }

    /** Cambiar el estado de una reserva existente devuelve true y queda guardado. */
    @Test
    void actualizarEstadoDevuelveTrueTest() {
        int id = insertarReservaDePrueba();
        Reserva reserva = reservaDao.buscarPorId(id).orElseThrow();
        reserva.setEstado("FINALIZADA");
        assertTrue(reservaDao.actualizar(reserva));
        assertEquals("FINALIZADA", reservaDao.buscarPorId(id).orElseThrow().getEstado());
    }

    /** No se puede borrar una reserva que tiene consumos (ON DELETE RESTRICT con las FK activadas). */
    @Test
    void eliminarReservaConConsumosLanzaDaoExceptionTest() {
        int id = insertarReservaDePrueba();
        nuevoConsumo(id, "Minibar", 1, "4.50", "2026-11-02");
        assertThrows(DaoException.class, () -> reservaDao.eliminar(id));
    }

    /** BETWEEN con fechas ISO incluye los dos extremos; orden por fecha y en empate por id. */
    @Test
    void buscarEntreFechasIncluyeLosExtremosYOrdenaTest() {
        int idHuesped = insertarHuespedDePrueba();
        int idHabitacion = insertarHabitacionDePrueba();
        int r1 = nuevaReserva(idHuesped, idHabitacion, "2026-11-10", 1, "CONFIRMADA");
        nuevaReserva(idHuesped, idHabitacion, "2026-10-31", 1, "CONFIRMADA");
        int r3 = nuevaReserva(idHuesped, idHabitacion, "2026-11-05", 1, "CONFIRMADA");
        int r4 = nuevaReserva(idHuesped, idHabitacion, "2026-11-01", 1, "CANCELADA");
        nuevaReserva(idHuesped, idHabitacion, "2026-11-11", 1, "CONFIRMADA");
        int r6 = nuevaReserva(idHuesped, idHabitacion, "2026-11-05", 2, "FINALIZADA");
        assertEquals(List.of(r4, r3, r6, r1),
                reservaDao.buscarEntreFechas("2026-11-01", "2026-11-10").stream().map(Reserva::getId).toList());
    }

    /** Reservas de huespedes de un pais en habitaciones de un tipo (cualquier estado), por fecha y id. */
    @Test
    void buscarPorPaisYTipoUneTresTablasTest() {
        int ana = nuevoHuesped("Ana Ruiz", "11111111A", "ana@hotel.es", "ES");
        int bruno = nuevoHuesped("Bruno Paz", "44444444D", "bruno@hotel.es", "FR");
        int carla = nuevoHuesped("Carla Vidal", "55555555E", "carla@hotel.es", "ES");
        int doble101 = nuevaHabitacion("101", "DOBLE", 1, "80.00");
        int suite301 = nuevaHabitacion("301", "SUITE", 3, "250.00");
        int doble102 = nuevaHabitacion("102", "DOBLE", 1, "85.00");
        nuevaReserva(ana, suite301, "2026-11-01", 2, "CONFIRMADA");
        nuevaReserva(bruno, doble101, "2026-11-02", 2, "CONFIRMADA");
        int r3 = nuevaReserva(carla, doble102, "2026-11-20", 1, "CONFIRMADA");
        int r4 = nuevaReserva(ana, doble101, "2026-11-03", 4, "FINALIZADA");
        int r5 = nuevaReserva(ana, doble102, "2026-11-20", 2, "CANCELADA");
        assertEquals(List.of(r4, r3, r5),
                reservaDao.buscarPorPaisYTipo("ES", "DOBLE").stream().map(Reserva::getId).toList());
    }

    /** La mas larga sin contar las CANCELADAS; en empate de noches, la de fecha de entrada mas antigua. */
    @Test
    void buscarMasLargaIgnoraCanceladasYDesempataPorFechaTest() {
        int idHuesped = insertarHuespedDePrueba();
        int idHabitacion = insertarHabitacionDePrueba();
        nuevaReserva(idHuesped, idHabitacion, "2026-11-10", 5, "CONFIRMADA");
        nuevaReserva(idHuesped, idHabitacion, "2026-11-01", 9, "CANCELADA");
        int esperada = nuevaReserva(idHuesped, idHabitacion, "2026-11-03", 5, "FINALIZADA");
        nuevaReserva(idHuesped, idHabitacion, "2026-10-01", 2, "CONFIRMADA");
        assertEquals(esperada, reservaDao.buscarMasLarga().orElseThrow().getId());
    }

    /** Sin reservas, buscarMasLarga devuelve Optional vacio. */
    @Test
    void buscarMasLargaSinReservasDevuelveVacioTest() {
        assertTrue(reservaDao.buscarMasLarga().isEmpty());
    }

    /** Importe total = noches x precio_noche + suma de los consumos (cada consumo una sola vez). */
    @Test
    void calcularImporteTotalSumaAlojamientoYConsumosTest() {
        int id = insertarReservaDePrueba();
        nuevoConsumo(id, "Minibar", 2, "2.50", "2026-11-02");
        nuevoConsumo(id, "Restaurante", 1, "12.75", "2026-11-03");
        assertEquals(0, reservaDao.calcularImporteTotal(id).compareTo(new BigDecimal("257.75")));
    }

    /** Sin consumos, el importe total es solo el alojamiento (3 x 80 = 240). */
    @Test
    void calcularImporteTotalSinConsumosEsSoloElAlojamientoTest() {
        int id = insertarReservaDePrueba();
        assertEquals(0, reservaDao.calcularImporteTotal(id).compareTo(new BigDecimal("240")));
    }
}
