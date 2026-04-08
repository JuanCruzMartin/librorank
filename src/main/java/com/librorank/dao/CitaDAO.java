package com.librorank.dao;

import com.librorank.config.DatabaseConfig;
import com.librorank.model.Cita;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {
    private static final Logger logger = LoggerFactory.getLogger(CitaDAO.class);

    public boolean guardar(Cita cita) {
        String sql = "INSERT INTO citas (usuario_id, libro_id, texto, pagina) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cita.getUsuarioId());
            stmt.setInt(2, cita.getLibroId());
            stmt.setString(3, cita.getTexto());
            stmt.setString(4, cita.getPagina());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { logger.error("Error al guardar cita", e); return false; }
    }

    public List<Cita> obtenerPorLibro(int libroId, int usuarioId) {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT * FROM citas WHERE libro_id = ? AND usuario_id = ? ORDER BY id DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, libroId);
            stmt.setInt(2, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Cita c = new Cita();
                    c.setId(rs.getInt("id"));
                    c.setTexto(rs.getString("texto"));
                    c.setPagina(rs.getString("pagina"));
                    lista.add(c);
                }
            }
        } catch (SQLException e) { logger.error("Error obteniendo citas libro", e); }
        return lista;
    }

    public Cita obtenerCitaAleatoria(int usuarioId) {
        String sql = """
            SELECT c.*, l.titulo 
            FROM citas c 
            JOIN libros_usuario l ON c.libro_id = l.id 
            WHERE c.usuario_id = ? 
            ORDER BY RAND() LIMIT 1
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cita c = new Cita();
                    c.setTexto(rs.getString("texto"));
                    c.setTituloLibro(rs.getString("titulo"));
                    return c;
                }
            }
        } catch (SQLException e) { logger.error("Error cita aleatoria", e); }
        return null;
    }
}
