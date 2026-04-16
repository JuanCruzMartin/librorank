package com.librorank.model;

public class ParticipanteReto {
    private int usuarioId;
    private String username;
    private String avatarUrl;
    private int progreso;

    public ParticipanteReto() {}

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public int getProgreso() { return progreso; }
    public void setProgreso(int progreso) { this.progreso = progreso; }
}
