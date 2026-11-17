package es.ejemplo.arcade.model;

public class Videojuego {
    private final int id;
    private final String titulo;
    private final double precio;
    private final boolean multijugador;
    private final int nota;
    private final String plataforma;

    public Videojuego(int id, String titulo, double precio, boolean multijugador, int nota, String plataforma) {
        this.id = id;
        this.titulo = titulo;
        this.precio = precio;
        this.multijugador = multijugador;
        this.nota = nota;
        this.plataforma = plataforma;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public double getPrecio() { return precio; }
    public boolean isMultijugador() { return multijugador; }
    public int getNota() { return nota; }
    public String getPlataforma() { return plataforma; }
}
