package com.librorank.model;

public class Item {
    private int id;
    private String nombre;
    private String descripcion;
    private String tipo;
    private int precio;
    private String imagenUrl;
    private String requisitoLogro; // Nuevo campo para gamificación

    public Item() {}

    public Item(int id, String nombre, String descripcion, String tipo, int precio, String imagenUrl, String requisitoLogro) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
        this.requisitoLogro = requisitoLogro;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getPrecio() { return precio; }
    public void setPrecio(int precio) { this.precio = precio; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public String getRequisitoLogro() { return requisitoLogro; }
    public void setRequisitoLogro(String requisitoLogro) { this.requisitoLogro = requisitoLogro; }
}
