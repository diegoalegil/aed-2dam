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
import java.util.Optional;

public class SerieDao {

    public SerieDao(String url) {

    }

    /** Todas las series ordenadas por código. */
    public List<Serie> listar() {
        return null;
    }

    /** La serie con ese código, o vacío si no existe. */
    public Optional<Serie> buscarPorCodigo(String codigo) {
        return null;
    }

    /** Guarda una serie nueva. Si el código ya existe lanza AplicationException. */
    public boolean insertar(Serie serie) {
        return false;
    }

    /** Cambia título, género, episodios y nota de la serie con ese código. false si no existe. */
    public boolean actualizar(Serie serie) {
        return false;
    }

    /** Borra la serie con ese código. false si no existe. */
    public boolean eliminar(String codigo) {
        return false;
    }

    /** Series cuyo título contiene el texto, sin distinguir mayúsculas, ordenadas por título. */
    public List<Serie> buscarPorTitulo(String texto) {
        return null;
    }

    /**
     * Las mejores series por nota, de mayor a menor. Si hay empate de nota, por código.
     * Devuelve como mucho 'cantidad' series. Si cantidad es 0 o negativa, lista vacía.
     */
    public List<Serie> topPorNota(int cantidad) {
        return null;
    }

    /** Géneros que tienen al menos 'minimo' series, ordenados alfabéticamente. */
    public List<String> generosConAlMenos(int minimo) {
        return null;
    }
}
