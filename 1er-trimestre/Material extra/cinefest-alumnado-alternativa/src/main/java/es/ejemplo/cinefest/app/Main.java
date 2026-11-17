// Paquete app: aquí está el programa que comprueba los DAO
package es.ejemplo.cinefest.app;

// Imports del proyecto: los 7 DAO y el modelo Sede (para la demo del CRUD)
import es.ejemplo.cinefest.dao.EntradaDao;
import es.ejemplo.cinefest.dao.EspectadorDao;
import es.ejemplo.cinefest.dao.PeliculaDao;
import es.ejemplo.cinefest.dao.SalaDao;
import es.ejemplo.cinefest.dao.SedeDao;
import es.ejemplo.cinefest.dao.SesionDao;
import es.ejemplo.cinefest.dao.ValoracionDao;
import es.ejemplo.cinefest.modelo.Sede;

// Imports de Java: Files y Path para copiar la base de datos, List y Arrays para imprimir las filas, y Level y Logger para callar a Hibernate
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Programa de comprobación: ejecuta las consultas de los 7 DAO y enseña lo que devuelven.
 * Trabaja siempre con una copia de la base de datos, así que la real no se toca nunca.
 */
public class Main {

    // Base de datos real (solo se lee para hacer la copia) y copias donde se prueba todo
    private static final Path ORIGEN = Path.of("datos/cinefest.sqlite3");
    private static final Path COPIA = Path.of("target/prueba-main.sqlite3");
    private static final Path COPIA_C30 = Path.of("target/prueba-main-c30.sqlite3");

    /**
     * Punto de entrada: copia la base de datos, crea los DAO y llama a una prueba por cada DAO.
     *
     * @param args no se usan
     * @throws IOException si no se puede copiar la base de datos
     */
    public static void main(String[] args) throws IOException {
        // Hibernate escribe mucho en la consola: solo se dejan los errores graves
        Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);

        // copia la base de datos real a target/ (REPLACE_EXISTING: si ya había una copia, la sustituye)
        String url = copiar(COPIA);

        // un DAO por tabla, todos sobre la copia
        SedeDao sedes = new SedeDao(url);
        SalaDao salas = new SalaDao(url);
        PeliculaDao peliculas = new PeliculaDao(url);
        SesionDao sesiones = new SesionDao(url);
        EspectadorDao espectadores = new EspectadorDao(url);
        EntradaDao entradas = new EntradaDao(url);
        ValoracionDao valoraciones = new ValoracionDao(url);

        // una prueba pequeña por DAO
        probarSede(sedes);
        probarSala(salas);
        probarPelicula(peliculas);
        probarSesion(sesiones);
        probarEspectador(espectadores);
        probarEntrada(entradas);
        probarValoracion(valoraciones);

        // CRUD de Sede, y después las dos operaciones con transacción (C29 y C30)
        probarCrudSede(sedes);
        probarC29(sesiones);
        probarC30();
    }

    /**
     * Copia la base de datos real al destino y devuelve la URL JDBC de la copia.
     *
     * @param destino fichero de la copia, por ejemplo target/prueba-main.sqlite3
     * @return URL JDBC de la copia
     * @throws IOException si no se puede copiar
     */
    private static String copiar(Path destino) throws IOException {
        // crea target/ si no existe
        Files.createDirectories(destino.getParent());
        // copia el fichero; REPLACE_EXISTING sustituye la copia anterior
        Files.copy(ORIGEN, destino, StandardCopyOption.REPLACE_EXISTING);
        // URL JDBC de SQLite: "jdbc:sqlite:" + ruta del fichero
        return "jdbc:sqlite:" + destino;
    }

    /**
     * Imprime el título de un reto y una fila por línea.
     *
     * @param titulo reto y método, por ejemplo "C12 aforoTotalPorSede"
     * @param filas  filas Object[] que devolvió la consulta
     */
    private static void imprimir(String titulo, List<Object[]> filas) {
        System.out.println("== " + titulo + " (" + filas.size() + " filas)");
        // Arrays.toString convierte cada Object[] en texto: [a, b, c]
        for (Object[] fila : filas) System.out.println("   " + Arrays.toString(fila));
    }

    /**
     * Imprime el título de un reto y una línea de texto por cada objeto del modelo
     * (las consultas que devuelven List de modelo).
     * Los modelos no tienen toString (saldría Pelicula@1b2c3d), así que antes de llamar
     * se convierte cada objeto en texto con sus getters: .stream().map(p -> p.getTitulo() + ...).toList()
     *
     * @param titulo reto y método
     * @param lineas un texto por objeto, ya con sus campos
     */
    private static void imprimirModelo(String titulo, List<String> lineas) {
        System.out.println("== " + titulo + " (" + lineas.size() + " filas)");
        for (String linea : lineas) System.out.println("   " + linea);
    }

    /** Prueba las consultas de SedeDao: C12. */
    private static void probarSede(SedeDao dao) {
        imprimir("C12 Sede.aforoTotalPorSede", dao.aforoTotalPorSede());
    }

    /** Prueba las consultas de SalaDao: C06 y C28. */
    private static void probarSala(SalaDao dao) {
        imprimir("C06 Sala.salasConSede", dao.salasConSede());
        imprimirModelo("C28 Sala.salasSinSesiones", dao.salasSinSesiones().stream()
                .map(s -> s.getIdSala() + " | " + s.getNombre() + " | aforo " + s.getAforo()).toList());
    }

    /** Prueba las consultas de PeliculaDao: C03, C16, C26, C14, C19 y C27. */
    private static void probarPelicula(PeliculaDao dao) {
        imprimirModelo("C03 Pelicula.buscarPorGenero(\"Drama\")", dao.buscarPorGenero("Drama").stream()
                .map(p -> p.getTitulo() + " | " + p.getPais() + " | " + p.getAnio()).toList());
        imprimirModelo("C16 Pelicula.peliculasSinSesiones", dao.peliculasSinSesiones().stream()
                .map(p -> p.getTitulo()).toList());
        imprimir("C26 Pelicula.peliculasConMediaValoraciones", dao.peliculasConMediaValoraciones());
        imprimir("C14 Pelicula.mejorValoradas(2)", dao.mejorValoradas(2));
        imprimir("C19 Pelicula.masVendidasQueMedia", dao.masVendidasQueMedia());
        imprimir("C27 Pelicula.estadisticasGenero", dao.estadisticasGenero());
    }

    /** Prueba las consultas de SesionDao: C07, C17, C15, C18, C21, C22 y C25. */
    private static void probarSesion(SesionDao dao) {
        imprimir("C07 Sesion.sesionesConDetalles", dao.sesionesConDetalles());
        imprimir("C17 Sesion.sesionesSinVentas", dao.sesionesSinVentas());
        imprimir("C15 Sesion.disponibilidadSesiones", dao.disponibilidadSesiones());
        imprimir("C18 Sesion.sesionesAgotadas", dao.sesionesAgotadas());
        imprimir("C21 Sesion.rankingRecaudacion", dao.rankingRecaudacion());
        imprimir("C22 Sesion.ingresosPorSede", dao.ingresosPorSede());
        imprimir("C25 Sesion.informeDiario", dao.informeDiario());
    }

    /** Prueba las consultas de EspectadorDao: C09, C13 y C20. */
    private static void probarEspectador(EspectadorDao dao) {
        imprimirModelo("C09 Espectador.espectadoresSinEntradas", dao.espectadoresSinEntradas().stream()
                .map(es -> es.getNombre() + " | " + es.getEmail()).toList());
        imprimir("C13 Espectador.rankingEspectadores", dao.rankingEspectadores());
        imprimir("C20 Espectador.espectadoresVariasSedes(2)", dao.espectadoresVariasSedes(2));
    }

    /** Prueba las consultas de EntradaDao: C08, C24 y C23. */
    private static void probarEntrada(EntradaDao dao) {
        imprimir("C08 Entrada.entradasConDetalles", dao.entradasConDetalles());
        imprimir("C24 Entrada.entradasPorEspectador(1)", dao.entradasPorEspectador(1));
        imprimir("C23 Entrada.ventasPorDia", dao.ventasPorDia());
    }

    /** Prueba las consultas de ValoracionDao: C10. */
    private static void probarValoracion(ValoracionDao dao) {
        imprimir("C10 Valoracion.valoracionesConDetalles", dao.valoracionesConDetalles());
    }

    /**
     * Demo del CRUD con SedeDao: insertar devuelve el id, actualizar y eliminar devuelven 1,
     * y eliminar otra vez devuelve 0 porque ya no existe.
     */
    private static void probarCrudSede(SedeDao dao) {
        System.out.println("== CRUD Sede");
        // insertar: el id lo genera la base de datos
        Sede nueva = new Sede();
        nueva.setNombre("Sede Prueba");
        nueva.setMunicipio("Municipio Prueba");
        int id = dao.insertar(nueva);
        System.out.println("   insertar -> id " + id);
        // buscarPorId devuelve Optional: map pasa la sede a texto y orElse da el texto si viene vacío
        System.out.println("   buscarPorId -> " + dao.buscarPorId(id)
                .map(s -> s.getIdSede() + " | " + s.getNombre() + " | " + s.getMunicipio()).orElse("vacío"));
        // actualizar: se cambian el nombre y el municipio de la misma sede (mismo id)
        nueva.setIdSede(id);
        nueva.setNombre("Sede Prueba 2");
        nueva.setMunicipio("Otro Municipio");
        System.out.println("   actualizar -> " + dao.actualizar(nueva));
        System.out.println("   eliminar -> " + dao.eliminar(id));
        System.out.println("   eliminar otra vez -> " + dao.eliminar(id));
    }

    /**
     * C29: venderEntrada devuelve 1 si vende y 0 si falla alguna regla.
     * La sala de la sesión 1 tiene aforo 5 y 4 entradas válidas: la primera venta la llena,
     * así que la segunda (otro espectador, el 6) devuelve 0 por aforo.
     */
    private static void probarC29(SesionDao dao) {
        System.out.println("== C29 Sesion.venderEntrada");
        System.out.println("   venderEntrada(1, 8, \"2026-11-10\") -> " + dao.venderEntrada(1, 8, "2026-11-10"));
        System.out.println("   venderEntrada(1, 6, \"2026-11-10\") -> " + dao.venderEntrada(1, 6, "2026-11-10"));
    }

    /**
     * C30: cancelarEntrada devuelve 1 si cancela y 0 si la entrada no existe o ya está cancelada.
     * Usa una copia nueva de la base de datos para partir de los datos originales.
     *
     * @throws IOException si no se puede copiar la base de datos
     */
    private static void probarC30() throws IOException {
        EntradaDao dao = new EntradaDao(copiar(COPIA_C30));
        System.out.println("== C30 Entrada.cancelarEntrada");
        System.out.println("   cancelarEntrada(1) -> " + dao.cancelarEntrada(1));
        System.out.println("   cancelarEntrada(1) otra vez -> " + dao.cancelarEntrada(1));
    }
}
