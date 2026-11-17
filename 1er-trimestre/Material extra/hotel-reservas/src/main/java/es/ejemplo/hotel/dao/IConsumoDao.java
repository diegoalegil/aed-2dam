package es.ejemplo.hotel.dao;

import es.ejemplo.hotel.modelo.Consumo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IConsumoDao {
    /** Lista los consumos. */
    List<Consumo> listar();
    /** Busca un consumo por su identificador. */
    Optional<Consumo> buscarPorId(Integer id);
    /** Inserta un consumo y devuelve el identificador generado. */
    int insertar(Consumo consumo);
    /** Actualiza un consumo; true si se ha modificado una fila. */
    boolean actualizar(Consumo consumo);
    /** Elimina un consumo; true si se ha borrado una fila. */
    boolean eliminar(Integer id);

    /** Consumos de una reserva. */
    List<Consumo> listarPorReserva(Integer idReserva);
    /** Suma de cantidad por precio unitario de los consumos de una reserva. */
    BigDecimal calcularTotalReserva(Integer idReserva);
}
