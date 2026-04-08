package com.librorank.model;

import java.time.LocalDateTime;

public class ActividadSocial {
    private Integer id;
    private Integer usuarioId;
    private String username; // Para mostrar quién hizo la acción
    private String avatarUrl;
    private String tipoActividad; // NUEVO_LIBRO, CAMBIO_ESTADO, NUEVA_CALIFICACION, DIARIO_LOG
    private Integer libroId;
    private String tituloLibro;
    private String portadaLibro;
    private String detalle;
    private LocalDateTime fechaCreacion;
    private int totalLikes;
    private boolean leGustaAlUsuario; // Para saber si el usuario logueado ya dio like

    public ActividadSocial() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getTipoActividad() { return tipoActividad; }
    public void setTipoActividad(String tipoActividad) { this.tipoActividad = tipoActividad; }

    public Integer getLibroId() { return libroId; }
    public void setLibroId(Integer libroId) { this.libroId = libroId; }

    public String getTituloLibro() { return tituloLibro; }
    public void setTituloLibro(String tituloLibro) { this.tituloLibro = tituloLibro; }

    public String getPortadaLibro() { return portadaLibro; }
    public void setPortadaLibro(String portadaLibro) { this.portadaLibro = portadaLibro; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public int getTotalLikes() { return totalLikes; }
    public void setTotalLikes(int totalLikes) { this.totalLikes = totalLikes; }

    public boolean isLeGustaAlUsuario() { return leGustaAlUsuario; }
    public void setLeGustaAlUsuario(boolean leGustaAlUsuario) { this.leGustaAlUsuario = leGustaAlUsuario; }
}
