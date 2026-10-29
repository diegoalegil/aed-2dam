package es.ejemplo.academia.dao;

import es.ejemplo.academia.infra.AbstractDao;
import es.ejemplo.academia.modelo.Matricula;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public final class MatriculaDao extends AbstractDao<Matricula> implements IMatriculaDao {

    public MatriculaDao(String url) {
        super(url);
    }

    @Override
    protected List<Matricula> map(ResultSet rs) {
        List<Matricula> matriculas = new ArrayList<>();
        try {

            while (rs.next()) {
                matriculas.add(new Matricula(rs.getInt("id"), rs.getInt("id_alumno"), rs.getInt("id_curso"), rs.getString("fecha"), rs.getBigDecimal("importe")));
            }
        } catch (SQLException e) {
            throw new DaoException("Error haciendo map", e);
        }
        return matriculas;
    }

    @Override
    public List<Matricula> listar() {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, id_alumno, id_curso, fecha, importe FROM matriculas ORDER BY id");
            ResultSet rs = preparedStatement.executeQuery();

            return map(rs);

        } catch (SQLException e) {
            throw new DaoException("Error obteniendo matriculas", e);
        }
    }

    @Override
    public Optional<Matricula> buscarPorId(Integer id) {

        if (id == null) {
            return Optional.empty();
        }

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, id_alumno, id_curso, fecha, importe FROM matriculas WHERE id = ?");
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            List<Matricula> lista = map(rs);
            if (lista.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(lista.get(0));

        } catch (SQLException e) {
            throw new DaoException("Error obteniendo matricula por id", e);
        }
    }

    @Override
    public int insertar(Matricula m) {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO matriculas (id_alumno, id_curso, fecha, importe) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, m.getIdAlumno());
            preparedStatement.setInt(2, m.getIdCurso());
            preparedStatement.setString(3, m.getFecha());
            preparedStatement.setBigDecimal(4, m.getImporte());

            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new DaoException("Error insertando matricula", e);
        }

        return 0;
    }

    @Override
    public int actualizar(Matricula m) {

        if (m.getId() == null) {
            throw new IllegalArgumentException("La matricula no tiene id");
        }

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE matriculas SET id_alumno = ?, id_curso = ?, fecha = ?, importe = ? WHERE id = ?");
            preparedStatement.setInt(1, m.getIdAlumno());
            preparedStatement.setInt(2, m.getIdCurso());
            preparedStatement.setString(3, m.getFecha());
            preparedStatement.setBigDecimal(4, m.getImporte());
            preparedStatement.setInt(5, m.getId());

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error actualizando la matricula", e);
        }
    }

    @Override
    public int eliminar(Integer id) {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM matriculas WHERE id = ?");
            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error eliminando la matricula", e);
        }
    }

    @Override
    public List<Matricula> listarPorAlumno(Integer idAlumno) {

        if (idAlumno == null) {
            return new ArrayList<>();
        }

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT matriculas.id, matriculas.id_alumno, matriculas.id_curso, matriculas.fecha, matriculas.importe FROM matriculas INNER JOIN alumnos ON alumnos.id = matriculas.id_alumno WHERE alumnos.id = ? ORDER BY matriculas.fecha, matriculas.id");
            preparedStatement.setInt(1, idAlumno);
            ResultSet rs = preparedStatement.executeQuery();

            return map(rs);

        } catch (SQLException e) {
            throw new DaoException("Error listando por alumno", e);
        }

    }

    @Override
    public BigDecimal calcularTotalPagado(Integer idAlumno) {

        if (idAlumno == null) {
            return BigDecimal.ZERO;
        }

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT ROUND(COALESCE(SUM(importe), 0), 2) AS total_matricula FROM matriculas WHERE id_alumno = ?");
            preparedStatement.setInt(1, idAlumno);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("total_matricula");
            }
            return BigDecimal.ZERO;

        } catch (SQLException e) {
            throw new DaoException("Error calculando total pagado", e);
        }
    }

    @Override
    public Map<String, Integer> contarAlumnosPorCurso() {

        Map<String, Integer> mapa = new LinkedHashMap<>();

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT c.titulo AS clave, COUNT(m.id) AS total FROM cursos c LEFT JOIN matriculas m ON m.id_curso = c.id GROUP BY c.id, c.titulo ORDER BY c.id");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                mapa.put(rs.getString("clave"), rs.getInt("total"));
            }
            return mapa;

        } catch (SQLException e) {
            throw new DaoException("Error contando alumnos por curso", e);
        }
    }
}
