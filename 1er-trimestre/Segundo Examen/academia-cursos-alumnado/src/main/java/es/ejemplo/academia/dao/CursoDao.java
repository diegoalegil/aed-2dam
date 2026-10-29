package es.ejemplo.academia.dao;

import es.ejemplo.academia.infra.AbstractDao;
import es.ejemplo.academia.modelo.Curso;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public final class CursoDao extends AbstractDao<Curso> implements ICursoDao {

    public CursoDao(String url) {
        super(url);
    }

    @Override
    protected List<Curso> map(ResultSet rs) {
        List<Curso> cursos = new ArrayList<>();
        try {

            while (rs.next()) {
                cursos.add(new Curso(rs.getInt("id"), rs.getString("titulo"), rs.getString("nivel"), rs.getInt("horas"), rs.getBigDecimal("precio")));
            }
        } catch (SQLException e) {
            throw new DaoException("Error haciendo map", e);
        }
        return cursos;
    }

    @Override
    public List<Curso> listar() {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM cursos ORDER BY id");
            ResultSet rs = preparedStatement.executeQuery();

            return map(rs);

        } catch (SQLException e) {
            throw new DaoException("Error obteniendo cursos", e);
        }
    }

    @Override
    public Optional<Curso> buscarPorId(Integer id) {

        if (id == null) {
            return Optional.empty();
        }

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, titulo, nivel, horas, precio FROM cursos WHERE id = ?");
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            List<Curso> lista = map(rs);
            if (lista.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(lista.get(0));

        } catch (SQLException e) {
            throw new DaoException("Error obteniendo curso por id", e);
        }
    }

    @Override
    public int insertar(Curso c) {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO cursos (titulo, nivel, horas, precio) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, c.getTitulo());
            preparedStatement.setString(2, c.getNivel());
            preparedStatement.setInt(3, c.getHoras());
            preparedStatement.setBigDecimal(4, c.getPrecio());

            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new DaoException("Error insertando curso", e);
        }

        return 0;
    }

    @Override
    public int actualizar(Curso c) {

        if (c.getId() == null) {
            throw new IllegalArgumentException("El curso no tiene id");
        }

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE cursos SET titulo = ?, nivel = ?, horas = ?, precio = ? WHERE id = ?");
            preparedStatement.setString(1, c.getTitulo());
            preparedStatement.setString(2, c.getNivel());
            preparedStatement.setInt(3, c.getHoras());
            preparedStatement.setBigDecimal(4, c.getPrecio());
            preparedStatement.setInt(5, c.getId());

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error actualizando el curso", e);
        }
    }

    @Override
    public int eliminar(Integer id) {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM cursos WHERE id = ?");
            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error eliminando el curso", e);
        }
    }

    @Override
    public List<Curso> listarPorNivel(String nivel) {

        if (nivel == null || nivel.isBlank()) {
            return new ArrayList<>();
        }

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, titulo, nivel, horas, precio FROM cursos WHERE nivel = ? ORDER BY precio, id");
            preparedStatement.setString(1, nivel);
            ResultSet rs = preparedStatement.executeQuery();

            return map(rs);

        } catch (SQLException e) {
            throw new DaoException("Error listando por nivel", e);
        }
    }

    @Override
    public List<Curso> listarConPrecioHasta(BigDecimal maximo) {

        if (maximo == null) {
            return new ArrayList<>();
        }

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM cursos WHERE precio <= ? ORDER BY precio DESC, id");
            preparedStatement.setBigDecimal(1, maximo);
            ResultSet rs = preparedStatement.executeQuery();

            return map(rs);

        } catch (SQLException e) {
            throw new DaoException("Error listando con precio", e);
        }
    }

    @Override
    public List<Curso> listarDeAlumno(Integer idAlumno) {

        if (idAlumno == null){
            return new ArrayList<>();
        }

        try (Connection connection = this.obtenerConexion()){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT c.id, c.titulo, c.nivel, c.horas, c.precio FROM cursos c INNER JOIN matriculas m ON m.id_curso = c.id WHERE m.id_alumno = ? ORDER BY c.titulo");
            preparedStatement.setInt(1, idAlumno);
            ResultSet rs = preparedStatement.executeQuery();

            return map(rs);

        } catch (SQLException e) {
            throw new DaoException("Error listar de alumno", e);
        }
    }
}
