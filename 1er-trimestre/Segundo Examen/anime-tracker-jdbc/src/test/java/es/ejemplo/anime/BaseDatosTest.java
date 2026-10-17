package es.ejemplo.anime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

/**
 * Cada test trabaja con una base nueva dentro de una carpeta temporal.
 * La carpeta "datos" NO existe al empezar: la tiene que crear ConexionBD.
 */
public abstract class BaseDatosTest {
    @TempDir
    protected Path carpetaTemporal;
    protected String url;

    @BeforeEach
    void prepararUrl() {
        url = "jdbc:sqlite:" + carpetaTemporal.resolve("datos").resolve("anime.sqlite3").toAbsolutePath();
    }
}
