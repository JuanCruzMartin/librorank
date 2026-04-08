package com.librorank.model;

public class Logro {
    private String  titulo;
    private String descripcion ;
    private String icono;
    private boolean desbloqueado;

        public Logro(String titulo, String descripcion,String icono , boolean desbloqueado){
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.icono = icono;
            this.desbloqueado = desbloqueado;
        }
        //getters

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getIcono() {
        return icono;
    }

    public boolean isDesbloqueado() {
        return desbloqueado;
    }
}
