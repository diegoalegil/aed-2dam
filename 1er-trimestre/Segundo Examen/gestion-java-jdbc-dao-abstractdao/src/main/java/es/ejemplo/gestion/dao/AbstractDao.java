package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T, ID> extends ConexionBD {

    public AbstractDao(String url) {
        super(url);
    }

    // ---------- 1. LO QUE CADA HIJA ESTÁ OBLIGADA A DECIR (abstract)
    // protected = solo lo ven las hijas (el "proteger")

    // De la fila del ResultSet a la clase Java (el "map", como el ObjectMapper de ficheros)
    protected abstract T mapRsToClass(ResultSet rs) throws SQLException;

    // Nombre de la tabla:          "Productos" / "Vehiculos"
    protected abstract String getTabla();

    // Columna de la clave primaria: "id"        / "matricula"
    protected abstract String getColumnaId();

    // ---------- 2. LO COMÚN: ejecutar y recorrer (escrito UNA sola vez) ----------

    // SELECT: recibe el PreparedStatement YA preparado (con sus ps.set hechos por la hija)
    public List<T> consultar(PreparedStatement ps) throws SQLException {
        List<T> lista = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRsToClass(rs));   // POLIMORFISMO: se ejecuta el map de la hija que sea
            }
        }
        return lista;
    }

    // INSERT / UPDATE / DELETE: devuelve cuántas filas ha cambiado
    public int actualizar(PreparedStatement ps) throws SQLException {
        return ps.executeUpdate();
    }

    // ---------- 3. CRUD GENÉRICO: sirve para cualquier tabla gracias a T e ID ----------

    public List<T> listar() {
        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM " + getTabla() + " ORDER BY " + getColumnaId())) {
            return consultar(ps);
        } catch (SQLException e) {
            throw new AplicationException("Error listando " + getTabla(), e);
        }
    }

    public Optional<T> buscarPorClave(ID id) {
        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM " + getTabla() + " WHERE " + getColumnaId() + " = ?")) {
            ps.setObject(1, id);   // setObject: el padre no sabe si ID es un número o un texto
            List<T> lista = consultar(ps);
            return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
        } catch (SQLException e) {
            throw new AplicationException("Error buscando en " + getTabla(), e);
        }
    }

    public boolean eliminarPorClave(ID id) {
        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM " + getTabla() + " WHERE " + getColumnaId() + " = ?")) {
            ps.setObject(1, id);
            return actualizar(ps) > 0;
        } catch (SQLException e) {
            throw new AplicationException("Error eliminando en " + getTabla(), e);
        }
    }

    public int contar() {
        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM " + getTabla());
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new AplicationException("Error contando en " + getTabla(), e);
        }
    }

    /*
     * ===================================================================
     *  PRIMER CONTACTO CON ESTA NUEVA METODOLOGÍA (mi primera versión en clase, 19-10-2026)
     *  Solo valía para Producto (sin <T, ID>) y consultar recibía un String,
     *  así que no servía para las consultas con "?".
     * ===================================================================
     *
     *  public abstract class AbstractDao extends ConexionBD {
     *
     *      public AbstractDao(String url) {
     *          super(url);
     *      }
     *
     *      abstract Producto mapToClass (ResultSet rs);
     *
     *      public List<Producto> consultar (String sql) {
     *
     *          List<Producto> lista = new ArrayList<>();
     *
     *          try (Connection connection = this.obtenerConexion()) {
     *
     *              PreparedStatement preparedStatement = connection.prepareStatement(sql);
     *
     *              ResultSet rs = preparedStatement.executeQuery();
     *
     *              while (rs.next()) {
     *                  lista.add(mapToClass(rs));
     *              }
     *
     *          } catch (SQLException e) {
     *              throw new AplicationException("No ha sido posible listar la base de datos", e);
     *          }
     *          return lista;
     *      }
     *  }
     */
}
