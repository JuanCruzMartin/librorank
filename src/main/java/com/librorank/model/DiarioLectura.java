package com.librorank.model;

import java.time.LocalDateTime;

public class DiarioLectura {
    private Integer id;
    private Integer libroId;
    private Integer usuarioId;
    private String capitulo;
    private String comentario;
    private LocalDateTime fechaCreacion;

    public DiarioLectura() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getLibroId() { return libroId; }
    public void setLibroId(Integer libroId) { this.libroId = libroId; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getCapitulo() { return capitulo; }
    public void setCapitulo(String capitulo) { this.capitulo = capitulo; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
