package aula.tareas.service;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import aula.tareas.modelo.Tarea;
import aula.tareas.repository.ITareaRepository;
import org.springframework.stereotype.Service;

@Service
public class TareaService {
    private final AtomicLong secuencia = new AtomicLong();
    private final ConcurrentSkipListMap<Long, Tarea> tareas = new ConcurrentSkipListMap<>();
        private final ITareaRepository repository;

    //Esta es la manera de hacerlo con Spring
    public TareaService(ITareaRepository repository) {
        this.repository = repository;
    }

    //En el pasado le quitamos el valor de los parentesis y despues del = creamos con new

    public List<Tarea> listar() {
        return this.repository.findAll();
    }

    public Tarea buscar(long id) {
        return this.repository.findById(id);
    }

    public Tarea crear(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            return null;
        }
        return repository.create(titulo);
    }
    //Duda cual es la diferencia de devolver como el profe sin el this o como yo que lo puse para probar..
}
