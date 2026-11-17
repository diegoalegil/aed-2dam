package es.ejemplo.banco.orm.repository;

import es.ejemplo.banco.modelo.Cliente;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

// README §4: repositorio ORM de clientes con el CRUD escrito a mano, método a método
public class ClienteOrmRepository implements IClienteRepository {

    private final EntityManagerFactory factory;

    public ClienteOrmRepository(EntityManagerFactory factory) {
        this.factory = factory;
    }

    @Override
    public List<Cliente> findAll() {
        // TODO
        throw new UnsupportedOperationException("Sin implementar");
    }

    @Override
    public Optional<Cliente> findById(Integer id) {
        // TODO
        throw new UnsupportedOperationException("Sin implementar");
    }

    @Override
    public void create(Cliente cliente) {
        // TODO
        throw new UnsupportedOperationException("Sin implementar");
    }

    @Override
    public boolean update(Cliente cliente) {
        // TODO
        throw new UnsupportedOperationException("Sin implementar");
    }

    @Override
    public boolean delete(Integer id) {
        // TODO
        throw new UnsupportedOperationException("Sin implementar");
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) {
        // TODO
        throw new UnsupportedOperationException("Sin implementar");
    }

    @Override
    public Optional<Cliente> buscarPorDni(String dni) {
        // TODO
        throw new UnsupportedOperationException("Sin implementar");
    }

    @Override
    public List<Cliente> listarPorNombre(String texto) {
        // TODO
        throw new UnsupportedOperationException("Sin implementar");
    }

    @Override
    public List<Cliente> listarAltasDesde(String fecha) {
        // TODO
        throw new UnsupportedOperationException("Sin implementar");
    }

    @Override
    public long contar() {
        // TODO
        throw new UnsupportedOperationException("Sin implementar");
    }
}
