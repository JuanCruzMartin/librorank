package com.librorank.dao;

import com.librorank.config.DatabaseConfig;
import com.librorank.model.ActividadSocial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActividadDAO {
    private static final Logger logger = LoggerFactory.getLogger(ActividadDAO.class);

    public void registrarActividad(int usuarioId, String tipo, Integer libroId, String detalle) {
        String sql = "INSERT INTO actividad_social (usuario_id, tipo_actividad, libro_id, detalle) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setString(2, tipo);
            if (libroId != null) stmt.setInt(3, libroId); else stmt.setNull(3, Types.INTEGER);
            stmt.setString(4, detalle);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error al registrar actividad social", e);
        }
    }

    public List<ActividadSocial> obtenerFeedAmigos(int usuarioId) {
        List<ActividadSocial> feed = new ArrayList<>();
        String sql = """
            SELECT a.*, u.username, u.avatar_url, l.titulo, l.portada_url,
                   (SELECT COUNT(*) FROM actividad_likes WHERE actividad_id = a.id) as total_likes,
                   (SELECT COUNT(*) FROM actividad_likes WHERE actividad_id = a.id AND usuario_id = ?) as user_liked
            FROM actividad_social a
            JOIN usuarios u ON a.usuario_id = u.id
            LEFT JOIN libros_usuario l ON a.libro_id = l.id
            WHERE a.usuario_id = ? 
               OR a.usuario_id IN (SELECT amigo_id FROM amigos WHERE usuario_id = ?)
            ORDER BY a.fecha_creacion DESC
            LIMIT 50
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, usuarioId);
            stmt.setInt(3, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ActividadSocial act = new ActividadSocial();
                    act.setId(rs.getInt("id"));
                    act.setUsuarioId(rs.getInt("usuario_id"));
                    act.setUsername(rs.getString("username"));
                    act.setAvatarUrl(rs.getString("avatar_url"));
                    act.setTipoActividad(rs.getString("tipo_actividad"));
                    act.setLibroId(rs.getInt("libro_id"));
                    act.setTituloLibro(rs.getString("titulo"));
                    act.setPortadaLibro(rs.getString("portada_url"));
                    act.setDetalle(rs.getString("detalle"));
                    act.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
                    act.setTotalLikes(rs.getInt("total_likes"));
                    act.setLeGustaAlUsuario(rs.getInt("user_liked") > 0);
                    feed.add(act);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener feed de amigos", e);
        }
        return feed;
    }

    public boolean toggleLike(int actividadId, int usuarioId) {
        String checkSql = "SELECT 1 FROM actividad_likes WHERE actividad_id = ? AND usuario_id = ?";
        String deleteSql = "DELETE FROM actividad_likes WHERE actividad_id = ? AND usuario_id = ?";
        String insertSql = "INSERT INTO actividad_likes (actividad_id, usuario_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setInt(1, actividadId);
                check.setInt(2, usuarioId);
                if (check.executeQuery().next()) {
                    try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
                        del.setInt(1, actividadId);
                        del.setInt(2, usuarioId);
                        del.executeUpdate();
                        return false; // Like quitado
                    }
                } else {
                    try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                        ins.setInt(1, actividadId);
                        ins.setInt(2, usuarioId);
                        ins.executeUpdate();
                        return true; // Like puesto
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error en toggleLike", e);
            return false;
        }
    }
}
