package es.ejemplo.autoescuela.dao;

import es.ejemplo.autoescuela.modelo.Profesor;
import es.ejemplo.autoescuela.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public class ProfesorDao {

    public ProfesorDao(String jdbcUrl) {  }

    // Usar transacciones y rollback en operaciones de escritura.
    public List<Profesor> listar() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public Optional<Profesor> buscarPorId(int id) {
        // TODO: implementar conforme al contrato del README.md.
        return Optional.empty();
    }
    public int insertar(Profesor dato) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    public int actualizar(Profesor dato) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    public int eliminar(int id) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    public Optional<Profesor> buscarPorDni(String dni) {
        // TODO: implementar conforme al contrato del README.md.
        return Optional.empty();
    }
    public List<Object[]> alumnosPorTutor() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public List<Object[]> clasesPorProfesor() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public List<Object[]> rankingIngresos(int limite) {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    
}
