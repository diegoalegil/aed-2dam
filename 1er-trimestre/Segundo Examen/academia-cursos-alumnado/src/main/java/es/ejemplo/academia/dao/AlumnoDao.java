package es.ejemplo.academia.dao;

import es.ejemplo.academia.infra.AbstractDao;
import es.ejemplo.academia.modelo.Alumno;

import java.sql.*;
import java.util.*;

public final class AlumnoDao extends AbstractDao<Alumno> implements IAlumnoDao {

    public AlumnoDao(String url) {
        super(url);
    }

    @Override
    protected List<Alumno> map(ResultSet rs) {
        List<Alumno> lista = new ArrayList<>();
        try {
            while (rs.next()) {
                lista.add(new Alumno(rs.getInt("id"), rs.getString("nombre"), rs.getString("email"), rs.getString("telefono")));
            }
        } catch (SQLException e) {
            throw new DaoException("Error creando alumno", e);
        }
        return lista;
    }

    @Override
    public List<Alumno> listar() {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, nombre, email, telefono FROM alumnos ORDER BY id");
            ResultSet rs = preparedStatement.executeQuery();

            return map(rs);

        } catch (SQLException e) {
            throw new DaoException("Error listando los alumnos", e);
        }
    }

    @Override
    public Optional<Alumno> buscarPorId(Integer id) {

        if (id == null) {
            return Optional.empty();
        }

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, nombre, email, telefono FROM alumnos WHERE id = ?");
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            List<Alumno> lista = map(rs);
            if (lista.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(lista.get(0));

        } catch (SQLException e) {
            throw new DaoException("Error obteniendo alumno por id", e);
        }
    }

    @Override
    public int insertar(Alumno a) {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO alumnos (nombre, email, telefono) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, a.getNombre());
            preparedStatement.setString(2, a.getEmail());
            preparedStatement.setString(3, a.getTelefono());

            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new DaoException("Error insertando alumno", e);
        }

        return 0;
    }

    @Override
    public int actualizar(Alumno a) {

        if (a.getId() == null) {
            throw new IllegalArgumentException("El alumno no tiene id");
        }

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE alumnos SET nombre = ?, email = ?, telefono = ? WHERE id = ?");
            preparedStatement.setString(1, a.getNombre());
            preparedStatement.setString(2, a.getEmail());
            preparedStatement.setString(3, a.getTelefono());
            preparedStatement.setInt(4, a.getId());

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error actualizando el alumno", e);
        }
    }

    @Override
    public int eliminar(Integer id) {


        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM alumnos WHERE id = ?");
            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error eliminando el alumno", e);
        }
    }

    @Override
    public List<Alumno> buscarPorNombre(String texto) {

        if (texto == null || texto.isBlank()) {
            return new ArrayList<>();
        }

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, nombre, email, telefono FROM alumnos WHERE nombre LIKE ? ORDER BY nombre, id");
            preparedStatement.setString(1, "%" + texto + "%");
            ResultSet rs = preparedStatement.executeQuery();

            return map(rs);

        } catch (SQLException e) {
            throw new DaoException("Error buscando alumno por nombre", e);
        }

    }

}
