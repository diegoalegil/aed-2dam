package es.ejemplo.hotel.dao;

import es.ejemplo.hotel.modelo.Habitacion;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IHabitacionDao {
    /** Lista las habitaciones. */
    List<Habitacion> listar();
    /** Busca una habitacion por su identificador. */
    Optional<Habitacion> buscarPorId(Integer id);
    /** Inserta una habitacion y devuelve el identificador generado. */
    int insertar(Habitacion habitacion);
    /** Actualiza una habitacion; true si se ha modificado una fila. */
    boolean actualizar(Habitacion habitacion);
    /** Elimina una habitacion; true si se ha borrado una fila. */
    boolean eliminar(Integer id);

    /** Habitaciones de un tipo con precio por noche menor o igual que el indicado. */
    List<Habitacion> buscarPorTipoYPrecioMaximo(String tipo, BigDecimal precioMaximo);
    /** Habitaciones cuyo precio por noche supera la media. */
    List<Habitacion> listarMasCarasQueLaMedia();
    /** Noches reservadas por tipo de habitacion, solo los tipos que llegan al minimo. */
    Map<String, Integer> nochesPorTipo(int minimoNoches);
}
