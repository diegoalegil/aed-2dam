package aula.tareas.service;

import aula.tareas.api.TareaRequest;
import aula.tareas.repository.ITareaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tareaServiceOrm")
public class TareaServiceOrm implements ITareaService {

    private final ITareaRepository repository;

    public TareaServiceOrm(@Qualifier("repositoryOrm") ITareaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TareaRequest> listar() {
        return repository.findAll();
    }

    @Override
    public TareaRequest buscar(long id) {
        return repository.findById(id);
    }

    @Override
    public TareaRequest crear(String titulo) {

        if (titulo == null || titulo.isBlank()) {
            return null;
        }

        return repository.create(titulo);
    }

    @Override
    public TareaRequest actualizar(long id, String titulo) {

        if (titulo == null || titulo.isBlank()) {
            return null;
        }

        return repository.update(new TareaRequest(id, titulo.strip()));
    }

    @Override
    public boolean borrar(long id) {

        return repository.delete(id);
    }
}
