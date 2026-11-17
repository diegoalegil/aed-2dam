package es.ejemplo.hotel;

import es.ejemplo.hotel.dao.DaoException;
import es.ejemplo.hotel.modelo.Habitacion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de HabitacionDao: un comportamiento por cada metodo @Test. */
class HabitacionDaoTest extends BaseDaoTest {

    /** Sin filas, listar devuelve una lista vacia (nunca null). */
    @Test
    void listarSinFilasDevuelveListaVaciaTest() {
        assertTrue(habitacionDao.listar().isEmpty());
    }

    /** El precio vuelve como BigDecimal con el mismo valor (se compara con compareTo, no con equals). */
    @Test
    void insertarYBuscarConservaElPrecioTest() {
        int id = insertarHabitacionDePrueba();
        Habitacion leida = habitacionDao.buscarPorId(id).orElseThrow();
        assertEquals("101", leida.getNumero());
        assertEquals("DOBLE", leida.getTipo());
        assertEquals(1, leida.getPlanta());
        assertEquals(0, leida.getPrecioNoche().compareTo(new BigDecimal("80")));
    }

    /** Un id null devuelve Optional vacio, sin excepcion. */
    @Test
    void buscarPorIdNuloDevuelveVacioTest() {
        assertTrue(habitacionDao.buscarPorId(null).isEmpty());
    }

    /** Para actualizar hace falta el id. */
    @Test
    void actualizarSinIdLanzaIllegalArgumentTest() {
        assertThrows(IllegalArgumentException.class,
                () -> habitacionDao.actualizar(new Habitacion(null, "101", "DOBLE", 1, new BigDecimal("90"))));
    }

    /** actualizar cambia el precio y devuelve true. */
    @Test
    void actualizarCambiaElPrecioYDevuelveTrueTest() {
        int id = insertarHabitacionDePrueba();
        assertTrue(habitacionDao.actualizar(new Habitacion(id, "101", "DOBLE", 1, new BigDecimal("92.50"))));
        assertEquals(0, habitacionDao.buscarPorId(id).orElseThrow().getPrecioNoche()
                .compareTo(new BigDecimal("92.5")));
    }

    /** Eliminar un id que no existe devuelve false. */
    @Test
    void eliminarInexistenteDevuelveFalseTest() {
        insertarHabitacionDePrueba();
        assertFalse(habitacionDao.eliminar(999));
    }

    /** El tipo lo controla el CHECK de la base de datos: un tipo no permitido es DaoException. */
    @Test
    void insertarTipoNoPermitidoLanzaDaoExceptionTest() {
        assertThrows(DaoException.class,
                () -> habitacionDao.insertar(new Habitacion(null, "501", "PRESIDENCIAL", 5, new BigDecimal("900"))));
    }

    /** El numero es un texto obligatorio: en blanco se rechaza en Java. */
    @Test
    void insertarNumeroEnBlancoLanzaIllegalArgumentTest() {
        assertThrows(IllegalArgumentException.class,
                () -> habitacionDao.insertar(new Habitacion(null, "", "DOBLE", 1, new BigDecimal("80"))));
    }

    /** Filtra por tipo y precio maximo (incluido); de mas cara a mas barata y en empate por numero. */
    @Test
    void buscarPorTipoYPrecioMaximoFiltraYOrdenaTest() {
        nuevaHabitacion("103", "DOBLE", 1, "80.00");
        nuevaHabitacion("202", "DOBLE", 2, "95.50");
        nuevaHabitacion("101", "DOBLE", 1, "80.00");
        nuevaHabitacion("104", "DOBLE", 1, "100.00");
        nuevaHabitacion("102", "DOBLE", 1, "120.00");
        nuevaHabitacion("201", "SUITE", 2, "90.00");
        List<Habitacion> resultado = habitacionDao.buscarPorTipoYPrecioMaximo("DOBLE", new BigDecimal("100.00"));
        assertEquals(List.of("104", "202", "101", "103"), resultado.stream().map(Habitacion::getNumero).toList());
    }

    /** El tipo se trata como dato, no como SQL: un intento de inyeccion no devuelve nada. */
    @Test
    void buscarPorTipoConTextoSqlNoDevuelveNadaTest() {
        nuevaHabitacion("101", "DOBLE", 1, "80.00");
        nuevaHabitacion("201", "SUITE", 2, "150.00");
        assertTrue(habitacionDao.buscarPorTipoYPrecioMaximo("DOBLE' OR 1=1 --", new BigDecimal("1000")).isEmpty());
    }

    /** Solo las habitaciones por encima de la media, de mas cara a mas barata y en empate por numero. */
    @Test
    void listarMasCarasQueLaMediaTest() {
        nuevaHabitacion("105", "SUITE", 1, "200.00");
        nuevaHabitacion("101", "INDIVIDUAL", 1, "60.00");
        nuevaHabitacion("102", "SUITE", 1, "200.00");
        nuevaHabitacion("103", "DOBLE", 1, "80.00");
        nuevaHabitacion("104", "DOBLE", 1, "100.00");
        nuevaHabitacion("106", "DOBLE", 1, "130.00");
        assertEquals(List.of("102", "105", "106"),
                habitacionDao.listarMasCarasQueLaMedia().stream().map(Habitacion::getNumero).toList());
    }

    /** Noches por tipo sin las CANCELADAS y solo los tipos que llegan al minimo, de mas a menos. */
    @Test
    void nochesPorTipoQuitaCanceladasYAplicaElMinimoTest() {
        int idHuesped = insertarHuespedDePrueba();
        int doble = nuevaHabitacion("101", "DOBLE", 1, "80.00");
        int suite = nuevaHabitacion("301", "SUITE", 3, "250.00");
        int individual = nuevaHabitacion("001", "INDIVIDUAL", 0, "55.00");
        nuevaReserva(idHuesped, doble, "2026-11-01", 3, "CONFIRMADA");
        nuevaReserva(idHuesped, doble, "2026-11-10", 4, "FINALIZADA");
        nuevaReserva(idHuesped, doble, "2026-12-01", 10, "CANCELADA");
        nuevaReserva(idHuesped, suite, "2026-11-15", 2, "CONFIRMADA");
        nuevaReserva(idHuesped, individual, "2026-11-20", 5, "CONFIRMADA");
        Map<String, Integer> noches = habitacionDao.nochesPorTipo(3);
        assertEquals(List.of("DOBLE", "INDIVIDUAL"), new ArrayList<>(noches.keySet()));
        assertEquals(List.of(7, 5), new ArrayList<>(noches.values()));
    }
}
