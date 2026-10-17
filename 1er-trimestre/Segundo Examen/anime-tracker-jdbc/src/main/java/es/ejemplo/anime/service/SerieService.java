package es.ejemplo.anime.service;

import es.ejemplo.anime.dao.SerieDao;
import es.ejemplo.anime.exception.AplicationException;
import es.ejemplo.anime.model.Serie;
import es.ejemplo.anime.validation.SerieValidator;

import java.util.Optional;

public class SerieService {

    public SerieService(SerieDao dao) {

    }

    /** Valida la serie y la guarda. Lanza AplicationException si no es válida o si su código ya existe. */
    public boolean registrar(Serie serie) {
        return false;
    }

    /** La serie con ese código. Lanza AplicationException si no existe. */
    public Serie obtener(String codigo) {
        return null;
    }

    /**
     * Suma episodios nuevos a una serie que ya existe.
     * Lanza AplicationException si 'extra' es 0 o negativo, o si la serie no existe.
     */
    public boolean sumarEpisodios(String codigo, int extra) {
        return false;
    }
}
