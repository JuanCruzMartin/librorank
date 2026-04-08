package com.librorank.model;

import java.time.LocalDate;
import java.util.List;

public class RetoAmigo {
    private Integer id;
    private Integer creadorId;
    private String creadorUsername;
    private String nombreReto;
    private Integer libroId;
    private String tituloLibro;
    private LocalDate fechaFin;
    private List<ParticipanteReto> participantes;

    public static class ParticipanteReto {
        public int usuarioId;
        public String username;
        public String avatarUrl;
        public int progreso;
    }

    public RetoAmigo() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCreadorId() { return creadorId; }
    public void setCreadorId(Integer creadorId) { this.creadorId = creadorId; }
    public String getCreadorUsername() { return creadorUsername; }
    public void setCreadorUsername(String creadorUsername) { this.creadorUsername = creadorUsername; }
    public String getNombreReto() { return nombreReto; }
    public void setNombreReto(String nombreReto) { this.nombreReto = nombreReto; }
    public Integer getLibroId() { return libroId; }
    public void setLibroId(Integer libroId) { this.libroId = libroId; }
    public String getTituloLibro() { return tituloLibro; }
    public void setTituloLibro(String tituloLibro) { this.tituloLibro = tituloLibro; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public List<ParticipanteReto> getParticipantes() { return participantes; }
    public void setParticipantes(List<ParticipanteReto> participantes) { this.participantes = participantes; }
}
