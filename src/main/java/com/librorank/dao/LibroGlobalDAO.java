package com.librorank.dao;

import com.librorank.config.DatabaseConfig;
import com.librorank.model.LibroGlobal;
import com.librorank.model.Libro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroGlobalDAO {
    private static final Logger logger = LoggerFactory.getLogger(LibroGlobalDAO.class);

    public int obtenerOCrear(Libro libro) {
        // 1. Intentar buscarlo
        String sqlSearch = "SELECT id FROM libros_global WHERE LOWER(titulo) = LOWER(?) AND LOWER(autor) = LOWER(?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlSearch)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        } catch (SQLException e) { logger.error("Error buscando libro global", e); }

        // 2. Si no existe, crearlo
        String sqlInsert = "INSERT INTO libros_global (titulo, autor, portada_url, anio, paginas) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getPortadaUrl());
            stmt.setObject(4, libro.getAnio());
            stmt.setObject(5, libro.getPaginas());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { logger.error("Error creando libro global", e); }
        
        return -1;
    }

    public LibroGlobal buscarPorId(int id) {
        String sql = """
            SELECT lg.*, 
                   (SELECT AVG(estrellas) FROM libros_usuario WHERE libro_global_id = lg.id AND estrellas > 0) as media,
                   (SELECT COUNT(*) FROM libros_usuario WHERE libro_global_id = lg.id) as lectores
            FROM libros_global lg WHERE id = ?
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LibroGlobal lg = new LibroGlobal();
                    lg.setId(rs.getInt("id"));
                    lg.setTitulo(rs.getString("titulo"));
                    lg.setAutor(rs.getString("autor"));
                    lg.setPortadaUrl(rs.getString("portada_url"));
                    lg.setNotaMedia(rs.getDouble("media"));
                    lg.setTotalLectores(rs.getInt("lectores"));
                    return lg;
                }
            }
        } catch (SQLException e) { logger.error("Error buscando detalles libro global", e); }
        return null;
    }

    public List<Libro> obtenerReviewsGlobales(int libroGlobalId) {
        List<Libro> reviews = new ArrayList<>();
        String sql = """
            SELECT lu.*, u.username 
            FROM libros_usuario lu 
            JOIN usuarios u ON lu.usuario_id = u.id 
            WHERE lu.libro_global_id = ? AND lu.resena IS NOT NULL AND lu.resena != ''
            ORDER BY lu.id DESC
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, libroGlobalId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Libro l = new Libro();
                    l.setResena(rs.getString("resena"));
                    l.setEstrellas(rs.getInt("estrellas"));
                    // Usaremos el autor para guardar el username temporalmente para mostrarlo en el JSP
                    l.setAutor(rs.getString("username")); 
                    reviews.add(l);
                }
            }
        } catch (SQLException e) { logger.error("Error obteniendo reviews globales", e); }
        return reviews;
    }
}
