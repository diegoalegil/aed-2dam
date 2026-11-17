package es.ejemplo.hotel.dao;

import es.ejemplo.hotel.modelo.Habitacion;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class HabitacionDao implements IHabitacionDao {

    public HabitacionDao(String url) {
    }

    @Override
    public List<Habitacion> listar() {
        throw new UnsupportedOperationException("Unimplemented method 'listar'");
    }

    @Override
    public Optional<Habitacion> buscarPorId(Integer id) {
        throw new UnsupportedOperationException("Unimplemented method 'buscarPorId'");
    }

    @Override
    public int insertar(Habitacion habitacion) {
        throw new UnsupportedOperationException("Unimplemented method 'insertar'");
    }

    @Override
    public boolean actualizar(Habitacion habitacion) {
        throw new UnsupportedOperationException("Unimplemented method 'actualizar'");
    }

    @Override
    public boolean eliminar(Integer id) {
        throw new UnsupportedOperationException("Unimplemented method 'eliminar'");
    }

    @Override
    public List<Habitacion> buscarPorTipoYPrecioMaximo(String tipo, BigDecimal precioMaximo) {
        throw new UnsupportedOperationException("Unimplemented method 'buscarPorTipoYPrecioMaximo'");
    }

    @Override
    public List<Habitacion> listarMasCarasQueLaMedia() {
        throw new UnsupportedOperationException("Unimplemented method 'listarMasCarasQueLaMedia'");
    }

    @Override
    public Map<String, Integer> nochesPorTipo(int minimoNoches) {
        throw new UnsupportedOperationException("Unimplemented method 'nochesPorTipo'");
    }
}
