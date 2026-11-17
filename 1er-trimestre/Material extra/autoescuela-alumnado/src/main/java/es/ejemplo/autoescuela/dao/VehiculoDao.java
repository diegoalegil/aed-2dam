package es.ejemplo.autoescuela.dao;

import es.ejemplo.autoescuela.modelo.Vehiculo;
import es.ejemplo.autoescuela.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public class VehiculoDao {

    public VehiculoDao(String jdbcUrl) {  }

    // Usar transacciones y rollback en operaciones de escritura.
    public List<Vehiculo> listar() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public Optional<Vehiculo> buscarPorId(String matricula) {
        // TODO: implementar conforme al contrato del README.md.
        return Optional.empty();
    }
    public int insertar(Vehiculo dato) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    public int actualizar(Vehiculo dato) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    public int eliminar(String matricula) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    public List<Vehiculo> buscarPorTipo(String tipo) {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public List<Object[]> ingresosPorVehiculo() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public List<Vehiculo> vehiculosSinReservas() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    
}
