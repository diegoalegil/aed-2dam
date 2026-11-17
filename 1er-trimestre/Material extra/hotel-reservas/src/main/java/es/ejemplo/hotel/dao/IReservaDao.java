package es.ejemplo.hotel.dao;

import es.ejemplo.hotel.modelo.Reserva;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IReservaDao {
    /** Lista las reservas. */
    List<Reserva> listar();
    /** Busca una reserva por su identificador. */
    Optional<Reserva> buscarPorId(Integer id);
    /** Inserta una reserva y devuelve el identificador generado. */
    int insertar(Reserva reserva);
    /** Actualiza una reserva; true si se ha modificado una fila. */
    boolean actualizar(Reserva reserva);
    /** Elimina una reserva; true si se ha borrado una fila. */
    boolean eliminar(Integer id);

    /** Reservas con fecha de entrada entre dos fechas ISO. */
    List<Reserva> buscarEntreFechas(String desde, String hasta);
    /** Reservas de huespedes de un pais en habitaciones de un tipo. */
    List<Reserva> buscarPorPaisYTipo(String pais, String tipo);
    /** La reserva no cancelada con mas noches. */
    Optional<Reserva> buscarMasLarga();
    /** Importe total de una reserva: alojamiento mas consumos. */
    BigDecimal calcularImporteTotal(Integer idReserva);
}
