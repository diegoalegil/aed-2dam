package es.ejemplo.hotel.dao;

import es.ejemplo.hotel.modelo.Consumo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public final class ConsumoDao implements IConsumoDao {

    public ConsumoDao(String url) {
    }

    @Override
    public List<Consumo> listar() {
        throw new UnsupportedOperationException("Unimplemented method 'listar'");
    }

    @Override
    public Optional<Consumo> buscarPorId(Integer id) {
        throw new UnsupportedOperationException("Unimplemented method 'buscarPorId'");
    }

    @Override
    public int insertar(Consumo consumo) {
        throw new UnsupportedOperationException("Unimplemented method 'insertar'");
    }

    @Override
    public boolean actualizar(Consumo consumo) {
        throw new UnsupportedOperationException("Unimplemented method 'actualizar'");
    }

    @Override
    public boolean eliminar(Integer id) {
        throw new UnsupportedOperationException("Unimplemented method 'eliminar'");
    }

    @Override
    public List<Consumo> listarPorReserva(Integer idReserva) {
        throw new UnsupportedOperationException("Unimplemented method 'listarPorReserva'");
    }

    @Override
    public BigDecimal calcularTotalReserva(Integer idReserva) {
        throw new UnsupportedOperationException("Unimplemented method 'calcularTotalReserva'");
    }
}
