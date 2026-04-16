package com.librorank.model;

public class Cita {
    private Integer id;
    private Integer usuarioId;
    private Integer libroId;
    private String texto;
    private String pagina;
    private String tituloLibro; // Campo opcional para el dashboard

    public Cita() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public Integer getLibroId() { return libroId; }
    public void setLibroId(Integer libroId) { this.libroId = libroId; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getPagina() { return pagina; }
    public void setPagina(String pagina) { this.pagina = pagina; }

    public String getTituloLibro() { return tituloLibro; }
    public void setTituloLibro(String tituloLibro) { this.tituloLibro = tituloLibro; }
}
