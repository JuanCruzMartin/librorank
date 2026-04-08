package com.librorank.dao;

import com.librorank.config.DatabaseConfig;
import com.librorank.model.RetoAmigo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RetoDAO {
    private static final Logger logger = LoggerFactory.getLogger(RetoDAO.class);

    public boolean crearReto(int creadorId, String nombre, Integer libroId, String fechaFin) {
        String sql = "INSERT INTO retos_amigos (creador_id, nombre_reto, libro_id, fecha_fin) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, creadorId);
            stmt.setString(2, nombre);
            if (libroId != null) stmt.setInt(3, libroId); else stmt.setNull(3, Types.INTEGER);
            stmt.setString(4, fechaFin);
            
            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int retoIdGenerated = rs.getInt(1);
                        unirseAReto(retoIdGenerated, creadorId);
                        
                        // REGISTRO SOCIAL
                        new ActividadDAO().registrarActividad(creadorId, "NUEVO_LIBRO", libroId, "Ha lanzado un nuevo Reto: " + nombre);
                        
                        return true;
                    }
                }
            }
        } catch (SQLException e) { logger.error("Error al crear reto", e); }
        return false;
    }

    public boolean unirseAReto(int retoId, int usuarioId) {
        String sql = "INSERT IGNORE INTO participantes_reto (reto_id, usuario_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, retoId);
            stmt.setInt(2, usuarioId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { logger.error("Error al unirse al reto", e); return false; }
    }

    public List<RetoAmigo> obtenerRetosActivos(int usuarioId) {
        List<RetoAmigo> lista = new ArrayList<>();
        String sql = """
            SELECT r.*, u.username as creador_name, l.titulo as libro_name
            FROM retos_amigos r
            JOIN usuarios u ON r.creador_id = u.id
            LEFT JOIN libros_usuario l ON r.libro_id = l.id
            WHERE r.creador_id = ? 
               OR r.creador_id IN (SELECT amigo_id FROM amigos WHERE usuario_id = ?)
               OR r.id IN (SELECT reto_id FROM participantes_reto WHERE usuario_id = ?)
            ORDER BY r.id DESC
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, usuarioId);
            stmt.setInt(3, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RetoAmigo r = new RetoAmigo();
                    r.setId(rs.getInt("id"));
                    r.setCreadorId(rs.getInt("creador_id"));
                    r.setCreadorUsername(rs.getString("creador_name"));
                    r.setNombreReto(rs.getString("nombre_reto"));
                    r.setLibroId((Integer) rs.getObject("libro_id"));
                    r.setTituloLibro(rs.getString("libro_name"));
                    Date d = rs.getDate("fecha_fin");
                    if (d != null) r.setFechaFin(d.toLocalDate());
                    
                    r.setParticipantes(obtenerParticipantes(r.getId()));
                    lista.add(r);
                }
            }
        } catch (SQLException e) { logger.error("Error al obtener retos", e); }
        return lista;
    }

    private List<RetoAmigo.ParticipanteReto> obtenerParticipantes(int retoId) {
        List<RetoAmigo.ParticipanteReto> participantes = new ArrayList<>();
        String sql = """
            SELECT p.*, u.username, u.avatar_url 
            FROM participantes_reto p 
            JOIN usuarios u ON p.usuario_id = u.id 
            WHERE p.reto_id = ?
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, retoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RetoAmigo.ParticipanteReto p = new RetoAmigo.ParticipanteReto();
                    p.usuarioId = rs.getInt("usuario_id");
                    p.username = rs.getString("username");
                    p.avatarUrl = rs.getString("avatar_url");
                    p.progreso = rs.getInt("progreso");
                    participantes.add(p);
                }
            }
        } catch (SQLException e) { logger.error("Error al obtener participantes", e); }
        return participantes;
    }

    public boolean actualizarProgreso(int retoId, int usuarioId, int progreso) {
        String sql = "UPDATE participantes_reto SET progreso = ? WHERE reto_id = ? AND usuario_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, progreso);
            stmt.setInt(2, retoId);
            stmt.setInt(3, usuarioId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { logger.error("Error al actualizar progreso reto", e); return false; }
    }
}
