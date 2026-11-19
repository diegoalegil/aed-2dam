package aula.tareas.Mapper;

import aula.tareas.Entity.TareaEntityJdbc;
import aula.tareas.api.TareaRequest;

public class TareaMapperJdbc {

    static public TareaRequest toDomain(TareaEntityJdbc tareaEntityJdbc) {
        return new TareaRequest(tareaEntityJdbc.getId(), tareaEntityJdbc.getTitulo());
    }

    static public TareaEntityJdbc toJdbc(TareaRequest tarea) {
        return new TareaEntityJdbc(tarea.id(), tarea.titulo());
    }


}
