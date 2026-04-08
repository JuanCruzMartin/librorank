package com.librorank.model;

/**
 * Clase auxiliar para agrupar las estadísticas de lectura de un usuario.
 * Se llena mediante una consulta SQL con agregaciones (COUNT, SUM).
 */
public class PerfilStats {
    private int total;      // Total de libros en la biblioteca
    private int leidos;     // Cantidad con estado 'LEIDO'
    private int leyendo;    // Cantidad con estado 'LEYENDO'
    private int pendientes; // Cantidad con estado 'PENDIENTE'
    private int pausa;      // Cantidad con estado 'PAUSA'

    // Getters y Setters para que el JSP pueda dibujar las barras de progreso o contadores

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public int getLeidos() { return leidos; }
    public void setLeidos(int leidos) { this.leidos = leidos; }

    public int getLeyendo() { return leyendo; }
    public void setLeyendo(int leyendo) { this.leyendo = leyendo; }

    public int getPendientes() { return pendientes; }
    public void setPendientes(int pendientes) { this.pendientes = pendientes; }

    public int getPausa() { return pausa; }
    public void setPausa(int pausa) { this.pausa = pausa; }
}