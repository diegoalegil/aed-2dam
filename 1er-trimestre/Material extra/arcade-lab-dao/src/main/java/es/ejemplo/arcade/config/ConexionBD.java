package es.ejemplo.arcade.config;

import java.sql.Connection;

/**
 * Abre conexiones con la base SQLite indicada por la url y prepara las tablas.
 */
public class ConexionBD {
    private final String url;

    public ConexionBD(String url) {
        this.url = url;
    }

    /**
     * Devuelve una conexión abierta con la base de la url.
     * Si la carpeta donde va el fichero de la base no existe, la crea antes de conectar.
     */
    public Connection obtenerConexion() {

    }

    /**
     * Crea las tablas plataforma y videojuego si todavía no existen (las sentencias están en sql/schema.sql).
     * Se puede llamar varias veces sin perder los datos guardados.
     */
    public void inicializar() {

    }
}
