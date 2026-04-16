package com.librorank.dao;

import com.librorank.config.DatabaseConfig;
import com.librorank.model.Libro;
import com.librorank.model.PerfilStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la gestión de libros y economía del usuario.
 */
public class LibroDAO {

    private static final Logger logger = LoggerFactory.getLogger(LibroDAO.class);

    public boolean agregar(Libro libro) {
        // Lógica de sincronización Global
        int globalId = new LibroGlobalDAO().obtenerOCrear(libro);
        libro.setLibroGlobalId(globalId);

        String sql ="""
            INSERT INTO libros_usuario
              (usuario_id, libro_global_id, titulo, autor, anio, paginas, estado, portada_url, genero, mood)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, libro.getUsuarioId());
            stmt.setInt(2, libro.getLibroGlobalId());
            stmt.setString(3, libro.getTitulo());
            stmt.setString(4, libro.getAutor());
            stmt.setObject(5, libro.getAnio());
            stmt.setObject(6, libro.getPaginas());
            stmt.setString(7, libro.getEstado());
            stmt.setString(8, libro.getPortadaUrl());
            stmt.setString(9, libro.getGenero());
            stmt.setString(10, libro.getMood());

            int filas = stmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            logger.error("Error al agregar libro para usuario {}: {}", libro.getUsuarioId(), libro.getTitulo(), e);
            return false;
        }
    }

    public void otorgarMonedasPorLibroLeido(int usuarioId) {
        otorgarMonedas(usuarioId, 10, "Libro marcado como LEÍDO");
    }

    public void otorgarMonedasPorResena(int usuarioId) {
        otorgarMonedas(usuarioId, 15, "Reseña escrita por libro LEÍDO");
    }

    public void otorgarMonedasPorBingo(int usuarioId, int monto) {
        otorgarMonedas(usuarioId, monto, "Progreso en Bingo Literario");
    }

    private void otorgarMonedas(int usuarioId, int monto, String concepto) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            int saldoActual = 0;
            try (PreparedStatement stSaldo = conn.prepareStatement("SELECT monedas FROM usuarios WHERE id = ?")) {
                stSaldo.setInt(1, usuarioId);
                try (ResultSet rs = stSaldo.executeQuery()) {
                    if (rs.next()) {
                        saldoActual = rs.getInt("monedas");
                    }
                }
            }

            int nuevoSaldo = saldoActual + monto;

            try (PreparedStatement stUpdate = conn.prepareStatement("UPDATE usuarios SET monedas = ? WHERE id = ?")) {
                stUpdate.setInt(1, nuevoSaldo);
                stUpdate.setInt(2, usuarioId);
                stUpdate.executeUpdate();
            }

            try (PreparedStatement stMov = conn.prepareStatement(
                    "INSERT INTO movimientos_moneda (usuario_id, tipo, concepto, monto, saldo_resultante) " +
                            "VALUES (?, 'GANANCIA', ?, ?, ?)")) {
                stMov.setInt(1, usuarioId);
                stMov.setString(2, concepto);
                stMov.setInt(3, monto);
                stMov.setInt(4, nuevoSaldo);
                stMov.executeUpdate();
            }

            conn.commit();
            logger.info("Otorgadas {} monedas al usuario ID {} por concepto: {}", monto, usuarioId, concepto);

        } catch (SQLException e) {
            logger.error("Error en transacción de otorgar monedas para usuario ID {}", usuarioId, e);
        }
    }

    public Libro buscarPorId(int idLibro, int usuarioId) {
        String sql = "SELECT * FROM libros_usuario WHERE id = ? AND usuario_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLibro);
            stmt.setInt(2, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Libro libro = new Libro();
                    libro.setId(rs.getInt("id"));
                    libro.setUsuarioId(rs.getInt("usuario_id"));
                    try {
                        libro.setLibroGlobalId(rs.getInt("libro_global_id"));
                        if (rs.wasNull()) libro.setLibroGlobalId(null);
                    } catch (SQLException e) {
                        logger.warn("Columna 'libro_global_id' no encontrada. Es posible que el esquema de la BD esté desactualizado.");
                    }
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setAutor(rs.getString("autor"));
                    libro.setAnio(rs.getInt("anio"));
                    if (rs.wasNull()) libro.setAnio(null);
                    libro.setPaginas(rs.getInt("paginas"));
                    if (rs.wasNull()) libro.setPaginas(null);
                    libro.setEstado(rs.getString("estado"));
                    libro.setPortadaUrl(rs.getString("portada_url"));
                    libro.setEstrellas(rs.getInt("estrellas"));
                    libro.setResena(rs.getString("resena"));
                    libro.setGenero(rs.getString("genero"));
                    libro.setMood(rs.getString("mood"));
                    return libro;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar libro ID {} para usuario ID {}", idLibro, usuarioId, e);
        }
        return null;
    }

    public List<Libro> buscarPorUsuario(int usuarioId) {
        List<Libro> libros = new ArrayList<>();
        String sql = """
            SELECT *
            FROM libros_usuario
            WHERE usuario_id = ? AND estado != 'ARCHIVADO'
            ORDER BY id DESC
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Libro libro = new Libro();
                    libro.setId(rs.getInt("id"));
                    libro.setUsuarioId(rs.getInt("usuario_id"));
                    try {
                        libro.setLibroGlobalId(rs.getInt("libro_global_id"));
                        if (rs.wasNull()) libro.setLibroGlobalId(null);
                    } catch (SQLException e) {
                        logger.warn("Columna 'libro_global_id' no encontrada. Es posible que el esquema de la BD esté desactualizado.");
                    }
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setAutor(rs.getString("autor"));
                    libro.setAnio(rs.getInt("anio"));
                    if (rs.wasNull()) libro.setAnio(null);
                    libro.setPaginas(rs.getInt("paginas"));
                    if (rs.wasNull()) libro.setPaginas(null);
                    libro.setEstado(rs.getString("estado"));
                    libro.setPortadaUrl(rs.getString("portada_url"));
                    libro.setEstrellas(rs.getInt("estrellas"));
                    libro.setResena(rs.getString("resena"));
                    libro.setGenero(rs.getString("genero"));
                    libro.setMood(rs.getString("mood"));
                    libros.add(libro);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar libros para usuario ID {}", usuarioId, e);
        }
        return libros;
    }

    public int contarLeidosEsteAnioPorUsuario(int usuarioId) {
        String sql = "SELECT COUNT(*) AS total FROM libros_usuario WHERE usuario_id = ? AND UPPER(estado) IN ('LEIDO', 'LEÍDO') AND YEAR(fecha_creacion) = YEAR(CURDATE())";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) { logger.error("Error conteo leidos este año", e); }
        return 0;
    }

    public int contarLeidosTotalPorUsuario(int usuarioId) {
        String sql = "SELECT COUNT(*) AS total FROM libros_usuario WHERE usuario_id = ? AND UPPER(estado) IN ('LEIDO', 'LEÍDO')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) { logger.error("Error conteo leidos", e); }
        return 0;
    }

    public List<Libro> obtenerUltimasLecturas(int usuarioId, int limite) {
        List<Libro> lista = new ArrayList<>();
        String sql = """
            SELECT * FROM libros_usuario
            WHERE usuario_id = ? AND UPPER(estado) IN ('LEIDO', 'LEÍDO')
            ORDER BY id DESC
            LIMIT ?
            """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Libro libro = new Libro();
                    libro.setId(rs.getInt("id"));
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setAutor(rs.getString("autor"));
                    libro.setPortadaUrl(rs.getString("portada_url"));
                    lista.add(libro);
                }
            }
        } catch (SQLException e) { logger.error("Error obteniendo últimas lecturas", e); }
        return lista;
    }

    public PerfilStats obtenerStatsPorUsuario(int usuarioId) {
        String sql = """
            SELECT 
                COUNT(*) AS total,
                SUM(CASE WHEN UPPER(estado) IN ('LEIDO', 'LEÍDO') THEN 1 ELSE 0 END) AS leidos,
                SUM(CASE WHEN UPPER(estado) = 'LEYENDO' THEN 1 ELSE 0 END) AS leyendo,
                SUM(CASE WHEN UPPER(estado) = 'PENDIENTE' THEN 1 ELSE 0 END) AS pendiente,
                SUM(CASE WHEN UPPER(estado) = 'PAUSA' THEN 1 ELSE 0 END) AS pausa
            FROM libros_usuario 
            WHERE usuario_id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                PerfilStats stats = new PerfilStats();
                if (rs.next()) {
                    stats.setTotal(rs.getInt("total"));
                    stats.setLeidos(rs.getInt("leidos"));
                    stats.setLeyendo(rs.getInt("leyendo"));
                    stats.setPendientes(rs.getInt("pendiente"));
                    stats.setPausa(rs.getInt("pausa"));
                }
                return stats;
            }
        } catch (SQLException e) { return new PerfilStats(); }
    }

    public boolean eliminar(int idLibro, int idUsuario) {
        // Primero eliminamos dependencias (Diario de lectura) para evitar errores de FK
        String sqlDiario = "DELETE FROM diario_lectura WHERE libro_id = ?";
        String sqlLibro = "DELETE FROM libros_usuario WHERE id = ? AND usuario_id = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtDiario = conn.prepareStatement(sqlDiario);
                 PreparedStatement stmtLibro = conn.prepareStatement(sqlLibro)) {
                
                stmtDiario.setInt(1, idLibro);
                stmtDiario.executeUpdate();

                stmtLibro.setInt(1, idLibro);
                stmtLibro.setInt(2, idUsuario);
                int filas = stmtLibro.executeUpdate();
                
                conn.commit();
                return filas > 0;
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Error al eliminar libro ID {} y su diario", idLibro, e);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error de conexión al eliminar libro", e);
            return false;
        }
    }

    public boolean existeRegistroPrevio(int usuarioId, String titulo, String autor) {
        String sql = "SELECT COUNT(*) FROM libros_usuario WHERE usuario_id = ? AND LOWER(titulo) = LOWER(?) AND LOWER(autor) = LOWER(?) AND estado != 'ARCHIVADO'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setString(2, titulo.trim());
            stmt.setString(3, autor.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { return false; }
        return false;
    }

    public boolean actualizar(Libro libro){
        String sql= """
                UPDATE libros_usuario
                SET estado = ?, estrellas = ?, resena = ?, genero = ?, mood = ?
                WHERE id = ? AND usuario_id = ?                             
                """;
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, libro.getEstado().toUpperCase());
            stmt.setInt(2, libro.getEstrellas() != null ? libro.getEstrellas() : 0);
            stmt.setString(3, libro.getResena());
            stmt.setString(4, libro.getGenero());
            stmt.setString(5, libro.getMood());
            stmt.setInt(6, libro.getId());
            stmt.setInt(7, libro.getUsuarioId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e){ return false; }
    }

    public int sumarPaginasLeidas(int usuarioId){
        String sql = "SELECT SUM(paginas) AS total_paginas FROM libros_usuario WHERE usuario_id = ? AND UPPER(estado) IN ('LEIDO', 'LEÍDO')";
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, usuarioId);
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()) return rs.getInt("total_paginas");
            }
        } catch (SQLException e) { return 0; }
        return 0;
    }

    public String obtenerAutorMasLeido(int usuarioId){
        String sql = "SELECT autor FROM libros_usuario WHERE usuario_id = ? AND autor != '' GROUP BY autor ORDER BY COUNT(*) DESC LIMIT 1";
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, usuarioId);
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()) return rs.getString("autor");
            }
        } catch(SQLException e){ return "N/A"; }
        return "N/A";
    }

    public String obtenerMejorCalificado(int usuarioId){
        String sql = "SELECT titulo FROM libros_usuario WHERE usuario_id = ? AND estrellas > 0 ORDER BY estrellas DESC, id DESC LIMIT 1";
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, usuarioId);
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()) return rs.getString("titulo");
            }
        } catch (SQLException e ){ return "N/A"; }
        return "N/A";
    }

    public java.util.Map<String, Integer> obtenerConteoPorGenero(int usuarioId) {
        java.util.Map<String, Integer> mapa = new java.util.HashMap<>();
        String sql = "SELECT genero, COUNT(*) as total FROM libros_usuario WHERE usuario_id = ? AND genero IS NOT NULL AND genero != '' GROUP BY genero";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) mapa.put(rs.getString("genero"), rs.getInt("total"));
            }
        } catch (SQLException e) { }
        return mapa;
    }

    public java.util.Map<String, Integer> obtenerConteoPorMood(int usuarioId) {
        java.util.Map<String, Integer> mapa = new java.util.HashMap<>();
        String sql = "SELECT mood, COUNT(*) as total FROM libros_usuario WHERE usuario_id = ? AND mood IS NOT NULL AND mood != '' GROUP BY mood";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) mapa.put(rs.getString("mood"), rs.getInt("total"));
            }
        } catch (SQLException e) { }
        return mapa;
    }
}
