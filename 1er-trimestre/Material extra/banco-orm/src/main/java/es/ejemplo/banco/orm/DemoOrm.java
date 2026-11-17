package es.ejemplo.banco.orm;

import java.util.logging.Level;
import java.util.logging.Logger;

// README §3: demo de solo lectura sobre datos/banco.sqlite3
public class DemoOrm {

    // Silencia los mensajes INFO de Hibernate en la consola (cambia a Level.INFO para verlos).
    // Va en un atributo static: si el Logger solo fuera una variable local, se podría perder el nivel.
    private static final Logger LOG_HIBERNATE = Logger.getLogger("org.hibernate");
    static {
        LOG_HIBERNATE.setLevel(Level.SEVERE);
    }

    public static void main(String[] args) {
        // TODO 1: abrir la fábrica (OrmFactory) y un EntityManager sobre datos/banco.sqlite3 con try-with-resources
        // TODO 2: mostrar el número de tablas (consulta nativa sobre sqlite_master) y el valor de PRAGMA foreign_keys
        // TODO 3 (nivel 1): crear el repositorio de clientes y listar todos los clientes
    }
}
