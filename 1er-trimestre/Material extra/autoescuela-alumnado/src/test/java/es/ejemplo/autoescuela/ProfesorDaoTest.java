package es.ejemplo.autoescuela;

import es.ejemplo.autoescuela.dao.ProfesorDao;
import es.ejemplo.autoescuela.modelo.Profesor;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas visibles de ProfesorDao. Cada una trabaja sobre su propia copia de la base en target/.
 */
class ProfesorDaoTest {

    static String copia(String nombre) {
        try {
            Path dir = Path.of("target", "bases-prueba");
            Files.createDirectories(dir);
            Path p = dir.resolve(nombre + ".sqlite3");
            Files.copy(Path.of("datos", "autoescuela.sqlite3"), p, StandardCopyOption.REPLACE_EXISTING);
            return "jdbc:sqlite:" + p;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void listarDevuelveLosCuatroProfesores() {
        ProfesorDao dao = new ProfesorDao(copia("profesor-listar"));
        assertEquals(4, dao.listar().size());
    }

    @Test
    void buscarPorIdDevuelveLaura() {
        ProfesorDao dao = new ProfesorDao(copia("profesor-buscar"));
        assertEquals("Laura Medina", dao.buscarPorId(1).map(Profesor::getNombre).orElse(null));
    }

    @Test
    void insertarDevuelveElIdNuevo() {
        ProfesorDao dao = new ProfesorDao(copia("profesor-insertar"));
        Profesor p = new Profesor();
        p.setNombre("Nuria Alonso");
        p.setDni("55555555E");
        p.setTelefono("600555555");
        p.setActivo(1);
        assertTrue(dao.insertar(p) > 4, "insertar devuelve el id generado");
    }
}
