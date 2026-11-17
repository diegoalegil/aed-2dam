package es.ejemplo.hotel.dao;

import es.ejemplo.hotel.modelo.Reserva;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public final class ReservaDao implements IReservaDao {

    public ReservaDao(String url) {
    }

    @Override
    public List<Reserva> listar() {
        throw new UnsupportedOperationException("Unimplemented method 'listar'");
    }

    @Override
    public Optional<Reserva> buscarPorId(Integer id) {
        throw new UnsupportedOperationException("Unimplemented method 'buscarPorId'");
    }

    @Override
    public int insertar(Reserva reserva) {
        throw new UnsupportedOperationException("Unimplemented method 'insertar'");
    }

    @Override
    public boolean actualizar(Reserva reserva) {
        throw new UnsupportedOperationException("Unimplemented method 'actualizar'");
    }

    @Override
    public boolean eliminar(Integer id) {
        throw new UnsupportedOperationException("Unimplemented method 'eliminar'");
    }

    @Override
    public List<Reserva> buscarEntreFechas(String desde, String hasta) {
        throw new UnsupportedOperationException("Unimplemented method 'buscarEntreFechas'");
    }

    @Override
    public List<Reserva> buscarPorPaisYTipo(String pais, String tipo) {
        throw new UnsupportedOperationException("Unimplemented method 'buscarPorPaisYTipo'");
    }

    @Override
    public Optional<Reserva> buscarMasLarga() {
        throw new UnsupportedOperationException("Unimplemented method 'buscarMasLarga'");
    }

    /** TODO */
    @Override
    public BigDecimal calcularImporteTotal(Integer idReserva) {
        throw new UnsupportedOperationException("Unimplemented method 'calcularImporteTotal'");
    }
}
