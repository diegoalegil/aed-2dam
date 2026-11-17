package es.ejemplo.arcade.service;

import es.ejemplo.arcade.dao.PlataformaDao;
import es.ejemplo.arcade.dao.VideojuegoDao;
import es.ejemplo.arcade.exception.AplicationException;
import es.ejemplo.arcade.model.Videojuego;

public class VideojuegoService {
    private final VideojuegoDao videojuegoDao;
    private final PlataformaDao plataformaDao;

    public VideojuegoService(VideojuegoDao videojuegoDao, PlataformaDao plataformaDao) {
        this.videojuegoDao = videojuegoDao;
        this.plataformaDao = plataformaDao;
    }

    public static boolean codigoPlataformaValido(String codigo) {
        return false;
    }

    public void registrar(Videojuego videojuego) {
    }

    public Videojuego obtener(int id) {
        return null;
    }

    public void rebajar(int id, int porcentaje) {
    }
}
