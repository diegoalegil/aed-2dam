package com.ejemplo.catalogo.repository.database;

import org.apache.commons.codec.binary.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class DatabaseInitializer {
    String url; //data/app.db
    Path path;
    //File/Files Para leer la ruta

    public DatabaseInitializer(String url){

        //StringUtils (Metodo con el isBlank hecho busaarlo)

        if (url == null || url.isBlank()){
            url = "data/app.db";
        }
        path = Path.of(url);
        if (!Files.exists(path)){
            //hacer algo si no existe
        }
    }
}
