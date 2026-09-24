package ies.puerto;

import java.nio.file.Path;

public interface IFichero {
    /**
     * Funcion que verifica si existe
     * @param path ruta de entrada a verificar
     * @return true o false
     */
    public boolean existe(Path path);



}
