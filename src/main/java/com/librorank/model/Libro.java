package com.librorank.model;

/**
 * Clase Modelo para representar un libro.
 * Se corresponde con la tabla 'libros_usuario' en la base de datos.
 */
public class Libro {

    // Usamos Integer para permitir que los campos sean opcionales (null)
    private Integer id;
    private Integer usuarioId; // Relación con el ID del usuario dueño del libro
    private Integer libroGlobalId; // Relación con la DB global
    private String titulo;
    private String autor;
    private Integer anio;
    private Integer paginas;
    private String portadaUrl;
    private Integer estrellas;
    private String resena;
    private String genero;
    private String mood;
    /** * El estado define la lógica de la app:
     * PENDIENTE, LEYENDO, LEIDO, PAUSA.
     */
    private String estado;

    // --- Getters y Setters ---

    public Integer getLibroGlobalId() { return libroGlobalId; }
    public void setLibroGlobalId(Integer libroGlobalId) { this.libroGlobalId = libroGlobalId; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public Integer getPaginas() { return paginas; }
    public void setPaginas(Integer paginas) { this.paginas = paginas; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getPortadaUrl() {
        return portadaUrl;
    }

    public void setPortadaUrl(String portadaUrl) {
        this.portadaUrl = portadaUrl;
    }

    public Integer getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(Integer estrellas) {
        this.estrellas = estrellas;
    }

    public String getResena() {
        return resena;
    }

    public void setResena(String resena) {
        this.resena = resena;
    }
}
