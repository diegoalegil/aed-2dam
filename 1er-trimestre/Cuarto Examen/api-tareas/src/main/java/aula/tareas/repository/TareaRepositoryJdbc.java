package aula.tareas.repository;

import aula.tareas.Entity.TareaEntityJdbc;
import aula.tareas.Mapper.TareaMapperJdbc;
import aula.tareas.api.TareaRequest;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository("repositoryJdbc")
public class TareaRepositoryJdbc implements ITareaRepository {
    private String jdbcUrl;


    //data.tareas.sqlite3
    //data/tareas.sqlite3
    //jdbc:sqlite:data/tareas.sqlite3
    public TareaRepositoryJdbc() {
        this("jdbc:sqlite:data/tareas.sqlite3");
    }

    //si el de arriba no tiene llama siempre a este constructor
    public TareaRepositoryJdbc(String jdbcUrl) {
        //"Da igual"
        this.jdbcUrl = jdbcUrl;
        if (!validarUrl(jdbcUrl)) {
            throw new RuntimeException("No se encuentra la bbdd");
        }
    }

    private boolean validarUrl(String jdbcUrl) {
        //Donde va este codigo????
        String url = jdbcUrl.replace("jdbc:sqlite:", "");

        Path path = Path.of(url);

        return Files.exists(path);
    }


    @Override
    public TareaRequest create(String titulo) {
        String sql = "INSERT INTO tareas_jdbc (titulo) VALUES (?)";
        try (Connection conexion = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, titulo.strip());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return new TareaRequest(rs.getLong(1), titulo.strip());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(long identificador) {
        String sql = "DELETE FROM tareas_jdbc WHERE id = ?";
        try (Connection conexion = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, identificador);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TareaRequest> findAll() {
        String sql = "SELECT id, titulo FROM tareas_jdbc ORDER BY id";
        List<TareaRequest> tareas = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TareaEntityJdbc entidad = new TareaEntityJdbc(
                        rs.getLong("id"),
                        rs.getString("titulo"));
                tareas.add(TareaMapperJdbc.toDomain(entidad));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tareas;
    }

    @Override
    public TareaRequest findById(long identificador) {
        String sql = "SELECT id, titulo FROM tareas_jdbc WHERE id = ?";
        try (Connection conexion = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, identificador);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TareaRequest(rs.getLong("id"), rs.getString("titulo"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public TareaRequest update(TareaRequest tarea) {
        String sql = "UPDATE tareas_jdbc SET titulo = ? WHERE id = ?";
        try (Connection conexion = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, tarea.titulo());
            ps.setLong(2, tarea.id());
            if (ps.executeUpdate() > 0) {
                return tarea;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
