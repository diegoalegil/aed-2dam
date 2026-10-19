package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao extends ConexionBD {

    public AbstractDao(String url) {
        super(url);
    }

    abstract Producto mapToClass (ResultSet rs);

    public List<Producto> consultar (String sql) {

        List<Producto> lista = new ArrayList<>();

        try (Connection connection = this.obtenerConexion()) {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                lista.add(mapToClass(rs));
            }

        } catch (SQLException e) {
            throw new AplicationException("No ha sido posible listar la base de datos", e);
        }
        return lista;
    }
}
