package es.ejemplo.autoescuela.dao;

import es.ejemplo.autoescuela.modelo.Examen;
import es.ejemplo.autoescuela.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public class ExamenDao {

    public ExamenDao(String jdbcUrl) {  }

    // Usar transacciones y rollback en operaciones de escritura.
    public List<Examen> listar() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public Optional<Examen> buscarPorId(int id) {
        // TODO: implementar conforme al contrato del README.md.
        return Optional.empty();
    }
    public int insertar(Examen dato) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    public int actualizar(Examen dato) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    public int eliminar(int id) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    public List<Object[]> examenesDeAlumno(int idAlumno) {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public List<Object[]> resumenPorTipo() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public int registrarResultado(int idExamen, String resultado, int errores) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    
}
