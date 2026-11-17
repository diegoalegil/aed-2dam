package es.ejemplo.hotel;

import es.ejemplo.hotel.dao.DaoException;
import es.ejemplo.hotel.modelo.Consumo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de ConsumoDao: un comportamiento por cada metodo @Test. */
class ConsumoDaoTest extends BaseDaoTest {

    /** Sin filas, listar devuelve una lista vacia (nunca null). */
    @Test
    void listarSinFilasDevuelveListaVaciaTest() {
        assertTrue(consumoDao.listar().isEmpty());
    }

    /** El precio unitario vuelve como BigDecimal con el mismo valor. */
    @Test
    void insertarYBuscarConservaElPrecioTest() {
        int idReserva = insertarReservaDePrueba();
        int id = nuevoConsumo(idReserva, "Minibar", 2, "2.50", "2026-11-02");
        Consumo leido = consumoDao.buscarPorId(id).orElseThrow();
        assertEquals(idReserva, leido.getIdReserva());
        assertEquals("Minibar", leido.getConcepto());
        assertEquals(2, leido.getCantidad());
        assertEquals(0, leido.getPrecioUnitario().compareTo(new BigDecimal("2.5")));
        assertEquals("2026-11-02", leido.getFecha());
    }

    /** Un concepto null se rechaza en Java con IllegalArgumentException (no con NullPointerException). */
    @Test
    void insertarConceptoNuloLanzaIllegalArgumentTest() {
        int idReserva = insertarReservaDePrueba();
        assertThrows(IllegalArgumentException.class,
                () -> consumoDao.insertar(new Consumo(null, idReserva, null, 1, new BigDecimal("3"), "2026-11-02")));
    }

    /** cantidad tiene un CHECK (mayor que 0): 0 unidades es DaoException. */
    @Test
    void insertarCantidadCeroLanzaDaoExceptionTest() {
        int idReserva = insertarReservaDePrueba();
        assertThrows(DaoException.class,
                () -> consumoDao.insertar(new Consumo(null, idReserva, "Spa", 0, new BigDecimal("30"), "2026-11-02")));
    }

    /** Actualizar un consumo null es IllegalArgumentException. */
    @Test
    void actualizarConsumoNuloLanzaIllegalArgumentTest() {
        assertThrows(IllegalArgumentException.class, () -> consumoDao.actualizar(null));
    }

    /** Eliminar un consumo existente devuelve true. */
    @Test
    void eliminarExistenteDevuelveTrueTest() {
        int id = nuevoConsumo(insertarReservaDePrueba(), "Minibar", 1, "4.50", "2026-11-02");
        assertTrue(consumoDao.eliminar(id));
        assertTrue(consumoDao.listar().isEmpty());
    }

    /** Consumos de una reserva por fecha y en empate por id; los de otras reservas no salen. */
    @Test
    void listarPorReservaOrdenaPorFechaYIdTest() {
        int idReserva = insertarReservaDePrueba();
        int otraReserva = nuevaReserva(nuevoHuesped("Bruno Paz", "44444444D", "bruno@hotel.es", "FR"),
                nuevaHabitacion("102", "DOBLE", 1, "85.00"), "2026-11-02", 2, "CONFIRMADA");
        int c1 = nuevoConsumo(idReserva, "Cena", 2, "18.00", "2026-11-04");
        int c2 = nuevoConsumo(idReserva, "Minibar", 1, "4.50", "2026-11-02");
        nuevoConsumo(otraReserva, "Spa", 1, "30.00", "2026-11-03");
        int c4 = nuevoConsumo(idReserva, "Lavanderia", 1, "9.25", "2026-11-02");
        assertEquals(List.of(c2, c4, c1),
                consumoDao.listarPorReserva(idReserva).stream().map(Consumo::getId).toList());
    }

    /** Total = suma de cantidad x precio unitario de la reserva: 2 x 2.50 + 3 x 1.25 = 8.75. */
    @Test
    void calcularTotalReservaMultiplicaCantidadPorPrecioTest() {
        int idReserva = insertarReservaDePrueba();
        nuevoConsumo(idReserva, "Agua", 2, "2.50", "2026-11-02");
        nuevoConsumo(idReserva, "Cafe", 3, "1.25", "2026-11-03");
        assertEquals(0, consumoDao.calcularTotalReserva(idReserva).compareTo(new BigDecimal("8.75")));
    }

    /** Una reserva sin consumos suma 0 (nunca null). */
    @Test
    void calcularTotalReservaSinConsumosDevuelveCeroTest() {
        int idReserva = insertarReservaDePrueba();
        BigDecimal total = consumoDao.calcularTotalReserva(idReserva);
        assertNotNull(total);
        assertEquals(0, total.compareTo(BigDecimal.ZERO));
    }
}
