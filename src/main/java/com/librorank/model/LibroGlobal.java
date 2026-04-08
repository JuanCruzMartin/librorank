package com.librorank.model;

public class LibroGlobal {
    private Integer id;
    private String titulo;
    private String autor;
    private String portadaUrl;
    private Integer anio;
    private Integer paginas;
    private String isbn;
    
    // Estadísticas calculadas
    private double notaMedia;
    private int totalLectores;

    public LibroGlobal() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getPortadaUrl() { return portadaUrl; }
    public void setPortadaUrl(String portadaUrl) { this.portadaUrl = portadaUrl; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
    public Integer getPaginas() { return paginas; }
    public void setPaginas(Integer paginas) { this.paginas = paginas; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public double getNotaMedia() { return notaMedia; }
    public void setNotaMedia(double notaMedia) { this.notaMedia = notaMedia; }
    public int getTotalLectores() { return totalLectores; }
    public void setTotalLectores(int totalLectores) { this.totalLectores = totalLectores; }
}
