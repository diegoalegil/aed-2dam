package es.ejemplo.arcade.model;

public class Plataforma {
    private final String codigo;
    private final String nombre;
    private final String fabricante;
    private final int anio;

    public Plataforma(String codigo, String nombre, String fabricante, int anio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.fabricante = fabricante;
        this.anio = anio;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getFabricante() { return fabricante; }
    public int getAnio() { return anio; }
}
