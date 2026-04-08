package com.librorank.model;

import java.time.LocalDateTime;

public class FragmentoHistoria {
    private Integer id;
    private Integer historiaId;
    private Integer usuarioId;
    private String username; // Para mostrar quién escribió la hoja
    private String contenido;
    private int numeroHoja;
    private LocalDateTime fechaCreacion;

    public FragmentoHistoria() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getHistoriaId() { return historiaId; }
    public void setHistoriaId(Integer historiaId) { this.historiaId = historiaId; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public int getNumeroHoja() { return numeroHoja; }
    public void setNumeroHoja(int numeroHoja) { this.numeroHoja = numeroHoja; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
