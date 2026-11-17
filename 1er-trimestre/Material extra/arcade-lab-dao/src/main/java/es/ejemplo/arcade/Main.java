package es.ejemplo.arcade;

import es.ejemplo.arcade.config.ConexionBD;
import es.ejemplo.arcade.dao.VideojuegoDao;
import es.ejemplo.arcade.exception.AplicationException;
import es.ejemplo.arcade.model.Videojuego;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:datos/arcade.sqlite3";
        try {
            new ConexionBD(url).inicializar();
            List<Videojuego> videojuegos = new VideojuegoDao(url).listar();
            if (videojuegos == null || videojuegos.isEmpty()) {
                System.out.println("No hay videojuegos en datos/arcade.sqlite3");
                return;
            }
            for (Videojuego v : videojuegos) {
                System.out.println(v.getId() + " - " + v.getTitulo() + " (" + v.getPlataforma() + ") "
                        + v.getPrecio() + " €");
            }
        } catch (AplicationException e) {
            System.err.println("Error con la base de datos: " + e.getMessage());
        }
    }
}
