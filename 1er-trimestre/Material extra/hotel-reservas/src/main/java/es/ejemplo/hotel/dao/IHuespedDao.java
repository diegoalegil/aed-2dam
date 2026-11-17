package es.ejemplo.hotel.dao;

import es.ejemplo.hotel.modelo.Huesped;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IHuespedDao {
    /** Lista los huespedes. */
    List<Huesped> listar();
    /** Busca un huesped por su identificador. */
    Optional<Huesped> buscarPorId(Integer id);
    /** Inserta un huesped y devuelve el identificador generado. */
    int insertar(Huesped huesped);
    /** Actualiza un huesped; true si se ha modificado una fila. */
    boolean actualizar(Huesped huesped);
    /** Elimina un huesped; true si se ha borrado una fila. */
    boolean eliminar(Integer id);

    /** Huespedes que no tienen ninguna reserva. */
    List<Huesped> listarSinReservas();
    /** Numero de reservas de cada huesped, por email. */
    Map<String, Integer> contarReservasPorHuesped();
}
