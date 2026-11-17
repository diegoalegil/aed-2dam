package es.ejemplo.banco.orm.repository;

import es.ejemplo.banco.modelo.Cliente;

import java.util.List;
import java.util.Optional;

public interface IClienteRepository extends IRepository<Cliente, Integer> {

    Optional<Cliente> buscarPorEmail(String email);

    Optional<Cliente> buscarPorDni(String dni);

    List<Cliente> listarPorNombre(String texto);

    List<Cliente> listarAltasDesde(String fecha);

    long contar();
}
