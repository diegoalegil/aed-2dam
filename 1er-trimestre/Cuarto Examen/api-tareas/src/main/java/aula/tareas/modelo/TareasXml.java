package aula.tareas.modelo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "tareas")
public class TareasXml {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "tarea")
    public List<Tarea> tareas = new ArrayList<>();

    public TareasXml() {
    }

    public TareasXml(List<Tarea> tareas) {
        this.tareas = new ArrayList<>(tareas);
    }

}
