package es.ejemplo.arcade.dao;

import es.ejemplo.arcade.model.Videojuego;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VideojuegoDao {

    public VideojuegoDao(String url) {
    }

    public List<Videojuego> listar() {
        return null;
    }

    public Optional<Videojuego> buscarPorId(int id) {
        return null;
    }

    public boolean insertar(Videojuego videojuego) {
        return false;
    }

    public boolean actualizar(Videojuego videojuego) {
        return false;
    }

    public boolean eliminar(int id) {
        return false;
    }

    public List<Videojuego> buscarPorPlataforma(String codigo) {
        return null;
    }

    public List<Videojuego> multijugadorHasta(double precioMaximo) {
        return null;
    }

    public List<Videojuego> masCaros(int n) {
        return null;
    }

    public int contar() {
        return 0;
    }

    public double mediaPrecio() {
        return 0.0;
    }

    public Map<String, Integer> contarPorPlataforma() {
        return null;
    }

    public List<String> plataformasConAlMenos(int minimo) {
        return null;
    }

    public List<String> titulosDeFabricante(String fabricante) {
        return null;
    }
}
