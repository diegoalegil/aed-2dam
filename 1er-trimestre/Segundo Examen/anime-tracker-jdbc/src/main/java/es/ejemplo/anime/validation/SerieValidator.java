package es.ejemplo.anime.validation;

import es.ejemplo.anime.exception.AplicationException;
import es.ejemplo.anime.model.Serie;

public class SerieValidator {

    /** true si el código son 3 letras mayúsculas, un guion y 3 cifras. Por ejemplo "NAR-001". */
    public boolean codigoValido(String codigo) {
        return false;
    }

    /**
     * Lanza AplicationException si la serie no es válida:
     * nula, código no válido, título o género vacíos, episodios 0 o menos, nota fuera de 0 a 10 (ambos incluidos).
     */
    public void validar(Serie serie) {

    }
}
