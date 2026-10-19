package es.ejemplo.anime.dao;

import es.ejemplo.anime.config.ConexionBD;
import es.ejemplo.anime.exception.AplicationException;
import es.ejemplo.anime.model.Serie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class SerieDao extends ConexionBD {

    public SerieDao(String url) {
        super(url);
    }

    /**
     * Todas las series ordenadas por código.
     */
    public List<Serie> listar() {
        List<Serie> series = new ArrayList<>();

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT codigo, titulo, genero, episodios, nota FROM series ORDER BY codigo");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                series.add(map(rs));
            }
        } catch (SQLException e) {
            throw new AplicationException("Error listando las series", e);
        }
        return series;
    }

    /**
     * La serie con ese código, o vacío si no existe.
     */
    public Optional<Serie> buscarPorCodigo(String codigo) {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT codigo, titulo, genero, episodios, nota FROM series WHERE codigo = ?");
            preparedStatement.setString(1, codigo);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return Optional.of(map(rs));
            }

        } catch (SQLException e) {
            throw new AplicationException("Error buscando la serie por codigo", e);
        }
        return Optional.empty();
    }

    /**
     * Guarda una serie nueva. Si el código ya existe lanza AplicationException.
     */
    public boolean insertar(Serie serie) {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO series (codigo, titulo, genero, episodios, nota) VALUES (?,?,?,?,?)");
            preparedStatement.setString(1, serie.getCodigo());
            preparedStatement.setString(2, serie.getTitulo());
            preparedStatement.setString(3, serie.getGenero());
            preparedStatement.setInt(4, serie.getEpisodios());
            preparedStatement.setDouble(5, serie.getNota());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new AplicationException("Error insertando la serie", e);
        }
    }

    /**
     * Cambia título, género, episodios y nota de la serie con ese código. false si no existe.
     */
    public boolean actualizar(Serie serie) {
        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE series SET titulo = ?, genero = ?, episodios = ?, nota = ? WHERE codigo = ?");
            preparedStatement.setString(1, serie.getTitulo());
            preparedStatement.setString(2, serie.getGenero());
            preparedStatement.setInt(3, serie.getEpisodios());
            preparedStatement.setDouble(4, serie.getNota());
            preparedStatement.setString(5, serie.getCodigo());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new AplicationException("Error actualizando la serie", e);
        }
    }

    /**
     * Borra la serie con ese código. false si no existe.
     */
    public boolean eliminar(String codigo) {
        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM series WHERE codigo = ?");
            preparedStatement.setString(1, codigo);
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new AplicationException("Error eliminando la serie", e);
        }
    }

    /**
     * Series cuyo título contiene el texto, sin distinguir mayúsculas, ordenadas por título.
     */
    public List<Serie> buscarPorTitulo(String texto) {
        List<Serie> seriesPorTitulo = new ArrayList<>();
        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT codigo,titulo, genero, episodios, nota FROM series WHERE titulo LIKE ? ORDER BY titulo");
            preparedStatement.setString(1, "%" + texto + "%");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                seriesPorTitulo.add(map(rs));
            }

        } catch (SQLException e) {
            throw new AplicationException("Error buscando series por titulo", e);
        }
        return seriesPorTitulo;
    }

    /**
     * Las mejores series por nota, de mayor a menor. Si hay empate de nota, por código.
     * Devuelve como mucho 'cantidad' series. Si cantidad es 0 o negativa, lista vacía.
     */
    public List<Serie> topPorNota(int cantidad) {
        List<Serie> seriesPorNota = new ArrayList<>();
        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT codigo, titulo, genero, episodios, nota FROM series ORDER BY nota DESC, codigo LIMIT ?");
            preparedStatement.setInt(1, cantidad);
            ResultSet rs = preparedStatement.executeQuery();

            if (cantidad <= 0) {
                return new ArrayList<>();
            }

            while (rs.next()) {
                seriesPorNota.add(map(rs));
            }

        } catch (SQLException e) {
            throw new AplicationException("Error listando las series por nota ", e);
        }
        return seriesPorNota;
    }

    /**
     * Géneros que tienen al menos 'minimo' series, ordenados alfabéticamente.
     */
    public List<String> generosConAlMenos(int minimo) {
        List<String> generosConMinimo = new ArrayList<>();
        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT genero FROM series GROUP BY genero HAVING COUNT(*) >= ? ORDER BY genero");
            preparedStatement.setInt(1, minimo);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                generosConMinimo.add(rs.getString("genero"));
            }

        } catch (SQLException e) {
            throw new AplicationException("Error listando las series con al menos ciertos generos", e);
        }
        return generosConMinimo;
    }

    private Serie map(ResultSet rs) {
        try {
            return new Serie(rs.getString("codigo"), rs.getString("titulo"), rs.getString("genero"), rs.getInt("episodios"), rs.getDouble("nota"));
        } catch (SQLException e) {
            throw new AplicationException("Error creando el map", e);
        }
    }
}
