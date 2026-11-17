package es.ejemplo.banco;

import es.ejemplo.banco.modelo.Cliente;
import es.ejemplo.banco.orm.repository.ClienteOrmRepository;
import es.ejemplo.banco.orm.repository.IClienteRepository;
import es.ejemplo.banco.orm.repository.RepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas del nivel 1: CRUD manual y consultas de ClienteOrmRepository. */
class ClienteOrmRepositoryTest extends BaseOrmTest {

    private IClienteRepository repo;

    @BeforeEach
    void crearRepositorio() {
        repo = new ClienteOrmRepository(factory);
    }

    private Cliente nuevo(String dni, String nombre, String email, String fechaAlta) {
        return new Cliente(null, dni, nombre, email, "600111222", fechaAlta, null);
    }

    private Cliente ana() {
        return nuevo("12345678Z", "Ana Martín", "ana@ejemplo.es", "2026-01-15");
    }

    private Cliente carlos() {
        return nuevo("23456789D", "Carlos Pérez", "carlos@ejemplo.es", "2026-02-10");
    }

    private Cliente marta() {
        return nuevo("34567890V", "Marta Gil", "marta@ejemplo.es", "2026-03-01");
    }

    private Integer guardar(Cliente cliente) {
        repo.create(cliente);
        return cliente.getIdCliente();
    }

    // ---------- CRUD ----------

    /** Sin clientes, findAll devuelve una lista vacía. */
    @Test
    void findAllSinClientesDevuelveListaVaciaTest() {
        assertTrue(repo.findAll().isEmpty());
    }

    /** create copia en el dominio el id positivo que genera la base de datos. */
    @Test
    void createCopiaIdGeneradoTest() {
        Cliente cliente = ana();
        repo.create(cliente);
        assertNotNull(cliente.getIdCliente());
        assertTrue(cliente.getIdCliente() > 0);
    }

    /** Dos clientes creados reciben ids distintos. */
    @Test
    void createDosClientesGeneraIdsDistintosTest() {
        assertNotEquals(guardar(ana()), guardar(carlos()));
    }

    /** create escribe la fila en la tabla física clientes. */
    @Test
    void createGuardaFilaEnTablaClientesTest() throws Exception {
        int id = guardar(ana());
        assertEquals(1, contarFilas("clientes"));
        assertEquals("ana@ejemplo.es", columnaCliente("email", id));
    }

    /** findById devuelve el cliente existente con todos sus datos. */
    @Test
    void findByIdDevuelveClienteExistenteTest() {
        int id = guardar(ana());
        Cliente leido = repo.findById(id).orElseThrow();
        assertEquals(id, leido.getIdCliente());
        assertEquals("12345678Z", leido.getDni());
        assertEquals("Ana Martín", leido.getNombre());
        assertEquals("ana@ejemplo.es", leido.getEmail());
        assertEquals("600111222", leido.getTelefono());
        assertEquals("2026-01-15", leido.getFechaAlta());
        assertNull(leido.getIdGestor());
    }

    /** findById con un id inexistente devuelve Optional vacío. */
    @Test
    void findByIdInexistenteDevuelveVacioTest() {
        assertTrue(repo.findById(9999).isEmpty());
    }

    /** findById con id nulo devuelve Optional vacío. */
    @Test
    void findByIdNuloDevuelveVacioTest() {
        assertTrue(repo.findById(null).isEmpty());
    }

    /** findAll ordena por id ascendente, no por nombre. */
    @Test
    void findAllDevuelveClientesOrdenadosPorIdTest() {
        int idMarta = guardar(marta());
        int idAna = guardar(ana());
        assertEquals(List.of(idMarta, idAna), repo.findAll().stream().map(Cliente::getIdCliente).toList());
    }

    /** update modifica los datos de un cliente existente. */
    @Test
    void updateModificaDatosTest() {
        int id = guardar(ana());
        Cliente cambiado = new Cliente(id, "12345678Z", "Ana María Martín", "ana.maria@ejemplo.es",
                "699000111", "2026-01-15", null);
        assertTrue(repo.update(cambiado));
        Cliente leido = repo.findById(id).orElseThrow();
        assertEquals("Ana María Martín", leido.getNombre());
        assertEquals("ana.maria@ejemplo.es", leido.getEmail());
        assertEquals("699000111", leido.getTelefono());
    }

    /** update de un id inexistente devuelve false y no inserta ninguna fila. */
    @Test
    void updateInexistenteDevuelveFalseTest() throws Exception {
        guardar(ana());
        Cliente fantasma = new Cliente(9999, "99999999R", "Nadie", "nadie@ejemplo.es", null, "2026-01-01", null);
        assertFalse(repo.update(fantasma));
        assertEquals(1, contarFilas("clientes"));
    }

    /** delete borra un cliente existente y la fila desaparece de la tabla. */
    @Test
    void deleteExistenteBorraFilaTest() throws Exception {
        int id = guardar(ana());
        assertTrue(repo.delete(id));
        assertEquals(0, contarFilas("clientes"));
    }

    /** delete de un id inexistente devuelve false. */
    @Test
    void deleteInexistenteDevuelveFalseTest() {
        assertFalse(repo.delete(9999));
    }

    /** Un email repetido se rechaza con RepositoryException. */
    @Test
    void emailDuplicadoLanzaRepositoryExceptionTest() {
        guardar(ana());
        Cliente otra = nuevo("23456789D", "Otra Ana", "ana@ejemplo.es", "2026-02-01");
        assertThrows(RepositoryException.class, () -> repo.create(otra));
    }

    /** Tras un error de escritura el repositorio sigue funcionando y no queda basura. */
    @Test
    void repositorioSigueFuncionandoTrasErrorTest() throws Exception {
        guardar(ana());
        assertThrows(RepositoryException.class,
                () -> repo.create(nuevo("23456789D", "Otra Ana", "ana@ejemplo.es", "2026-02-01")));
        assertEquals(1, contarFilas("clientes"));
        guardar(carlos());
        assertEquals(2, repo.findAll().size());
    }

    /** Un gestor inexistente se rechaza por la clave foránea (PRAGMA activo). */
    @Test
    void gestorInexistenteRechazadoTest() throws Exception {
        Cliente cliente = ana();
        cliente.setIdGestor(999);
        assertThrows(RepositoryException.class, () -> repo.create(cliente));
        assertEquals(0, contarFilas("clientes"));
    }

    /** Un cliente con gestor existente guarda y recupera su idGestor. */
    @Test
    void createConGestorGuardaIdGestorTest() throws Exception {
        int idGestor = insertarGestorDePrueba();
        Cliente cliente = ana();
        cliente.setIdGestor(idGestor);
        int id = guardar(cliente);
        assertEquals(idGestor, repo.findById(id).orElseThrow().getIdGestor());
    }

    // ---------- Consultas ----------

    /** buscarPorEmail encuentra al cliente por su correo exacto. */
    @Test
    void buscarPorEmailCoincideTest() {
        guardar(ana());
        guardar(carlos());
        assertEquals("Carlos Pérez", repo.buscarPorEmail("carlos@ejemplo.es").orElseThrow().getNombre());
    }

    /** buscarPorEmail con un correo desconocido devuelve Optional vacío. */
    @Test
    void buscarPorEmailSinCoincidenciaTest() {
        guardar(ana());
        assertTrue(repo.buscarPorEmail("nadie@ejemplo.es").isEmpty());
    }

    /** buscarPorDni encuentra al cliente por su DNI. */
    @Test
    void buscarPorDniCoincideTest() {
        guardar(ana());
        guardar(carlos());
        assertEquals("Ana Martín", repo.buscarPorDni("12345678Z").orElseThrow().getNombre());
    }

    /** buscarPorDni con un DNI desconocido devuelve Optional vacío. */
    @Test
    void buscarPorDniSinCoincidenciaTest() {
        guardar(ana());
        assertTrue(repo.buscarPorDni("00000000T").isEmpty());
    }

    /** listarPorNombre devuelve los nombres que contienen el texto, ordenados por nombre. */
    @Test
    void listarPorNombreContieneOrdenadoTest() {
        guardar(marta());
        guardar(carlos());
        guardar(ana());
        assertEquals(List.of("Ana Martín", "Marta Gil"),
                repo.listarPorNombre("mar").stream().map(Cliente::getNombre).toList());
    }

    /** listarPorNombre sin coincidencias devuelve una lista vacía. */
    @Test
    void listarPorNombreSinCoincidenciaTest() {
        guardar(ana());
        assertTrue(repo.listarPorNombre("zzz").isEmpty());
    }

    /** listarAltasDesde devuelve las altas desde la fecha, ordenadas por fecha. */
    @Test
    void listarAltasDesdeCoincideTest() {
        int idMarta = guardar(marta());
        guardar(ana());
        int idCarlos = guardar(carlos());
        assertEquals(List.of(idCarlos, idMarta),
                repo.listarAltasDesde("2026-02-01").stream().map(Cliente::getIdCliente).toList());
    }

    /** listarAltasDesde sin altas posteriores devuelve una lista vacía. */
    @Test
    void listarAltasDesdeSinCoincidenciaTest() {
        guardar(ana());
        assertTrue(repo.listarAltasDesde("2027-01-01").isEmpty());
    }

    /** contar devuelve el número de clientes guardados. */
    @Test
    void contarDevuelveNumeroDeClientesTest() {
        assertEquals(0L, repo.contar());
        guardar(ana());
        guardar(carlos());
        guardar(marta());
        assertEquals(3L, repo.contar());
    }
}
