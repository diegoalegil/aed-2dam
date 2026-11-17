package es.ejemplo.hotel;

import es.ejemplo.hotel.dao.DaoException;
import es.ejemplo.hotel.modelo.Reserva;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de integracion: varios DAO trabajando sobre la misma base de datos. */
class IntegracionDaoTest extends BaseDaoTest {

    /** Un huesped con reservas no se puede borrar (FK activadas en la conexion del DELETE). */
    @Test
    void eliminarHuespedConReservasLanzaDaoExceptionTest() {
        int idHuesped = insertarHuespedDePrueba();
        nuevaReserva(idHuesped, insertarHabitacionDePrueba(), "2026-11-02", 2, "CONFIRMADA");
        assertThrows(DaoException.class, () -> huespedDao.eliminar(idHuesped));
    }

    /** Una habitacion con reservas no se puede borrar. */
    @Test
    void eliminarHabitacionConReservasLanzaDaoExceptionTest() {
        int idHabitacion = insertarHabitacionDePrueba();
        nuevaReserva(insertarHuespedDePrueba(), idHabitacion, "2026-11-02", 2, "CONFIRMADA");
        assertThrows(DaoException.class, () -> habitacionDao.eliminar(idHabitacion));
    }

    /** Borrando en orden inverso (consumo, reserva, huesped) todo se borra. */
    @Test
    void borrarEnOrdenInversoFuncionaTest() {
        int idReserva = insertarReservaDePrueba();
        int idHuesped = reservaDao.buscarPorId(idReserva).orElseThrow().getIdHuesped();
        int idConsumo = nuevoConsumo(idReserva, "Minibar", 1, "4.50", "2026-11-02");
        assertTrue(consumoDao.eliminar(idConsumo));
        assertTrue(reservaDao.eliminar(idReserva));
        assertTrue(huespedDao.eliminar(idHuesped));
    }

    /** El importe total de la reserva es el alojamiento mas el total de consumos de ConsumoDao. */
    @Test
    void importeTotalEsAlojamientoMasTotalDeConsumosTest() {
        int idReserva = insertarReservaDePrueba();
        nuevoConsumo(idReserva, "Cena", 2, "18.00", "2026-11-03");
        nuevoConsumo(idReserva, "Parking", 3, "12.50", "2026-11-03");
        BigDecimal alojamiento = new BigDecimal("240");
        BigDecimal esperado = alojamiento.add(consumoDao.calcularTotalReserva(idReserva));
        assertEquals(0, reservaDao.calcularImporteTotal(idReserva).compareTo(esperado));
        assertEquals(0, esperado.compareTo(new BigDecimal("313.50")));
    }

    /** Al cancelar su unica reserva, el huesped sigue en el recuento pero con 0. */
    @Test
    void cancelarUnaReservaLaQuitaDelRecuentoTest() {
        int idReserva = insertarReservaDePrueba();
        Reserva reserva = reservaDao.buscarPorId(idReserva).orElseThrow();
        reserva.setEstado("CANCELADA");
        assertTrue(reservaDao.actualizar(reserva));
        assertEquals(0, huespedDao.contarReservasPorHuesped().get("ana@hotel.es"));
    }
}
