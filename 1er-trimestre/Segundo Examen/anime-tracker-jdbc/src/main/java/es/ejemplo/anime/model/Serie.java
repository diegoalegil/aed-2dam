package es.ejemplo.anime.model;

public class Serie {
    private final String codigo;
    private final String titulo;
    private final String genero;
    private final int episodios;
    private final double nota;

    public Serie(String codigo, String titulo, String genero, int episodios, double nota) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.genero = genero;
        this.episodios = episodios;
        this.nota = nota;
    }

    public String getCodigo() { return codigo; }
    public String getTitulo() { return titulo; }
    public String getGenero() { return genero; }
    public int getEpisodios() { return episodios; }
    public double getNota() { return nota; }
}
