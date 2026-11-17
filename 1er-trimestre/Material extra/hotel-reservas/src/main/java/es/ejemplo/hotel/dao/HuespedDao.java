package es.ejemplo.hotel.dao;

import es.ejemplo.hotel.infra.AbstractDao;
import es.ejemplo.hotel.modelo.Huesped;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class HuespedDao extends AbstractDao<Huesped> implements IHuespedDao {

    public HuespedDao(String url) {
        super(url);
    }

    @Override
    protected List<Huesped> map(ResultSet rs) {
        List<Huesped> huespedes = new ArrayList<>();

        try {
            while (rs.next()) {
                huespedes.add(new Huesped(rs.getInt("id"), rs.getString("nombre"), rs.getString("dni"), rs.getString("email"), rs.getString("pais")));
            }
            return huespedes;
        } catch (SQLException e) {
            throw new DaoException("Error", e);
        }

    }

    @Override
    public List<Huesped> listar() {
        throw new UnsupportedOperationException("Unimplemented method 'listar'");
    }

    @Override
    public Optional<Huesped> buscarPorId(Integer id) {
        throw new UnsupportedOperationException("Unimplemented method 'buscarPorId'");
    }

    @Override
    public int insertar(Huesped huesped) {
        throw new UnsupportedOperationException("Unimplemented method 'insertar'");
    }

    @Override
    public boolean actualizar(Huesped huesped) {
        throw new UnsupportedOperationException("Unimplemented method 'actualizar'");
    }

    @Override
    public boolean eliminar(Integer id) {
        throw new UnsupportedOperationException("Unimplemented method 'eliminar'");
    }

    @Override
    public List<Huesped> listarSinReservas() {
        throw new UnsupportedOperationException("Unimplemented method 'listarSinReservas'");
    }

    @Override
    public Map<String, Integer> contarReservasPorHuesped() {
        throw new UnsupportedOperationException("Unimplemented method 'contarReservasPorHuesped'");
    }
}
