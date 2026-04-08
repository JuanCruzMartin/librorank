package com.librorank.dao;

import com.librorank.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class LogroDAO {
    private static final Logger logger = LoggerFactory.getLogger(LogroDAO.class);

    /**
     * Obtiene los códigos (nombre_key) de los logros que ya tiene el usuario.
     */
    public Set<String> obtenerLogrosKeysUsuario(int usuarioId) {
        Set<String> logros = new HashSet<>();
        String sql = "SELECT l.nombre_key FROM logros l " +
                     "JOIN usuario_logros ul ON l.id = ul.logro_id " +
                     "WHERE ul.usuario_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logros.add(rs.getString("nombre_key"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener logros para usuario " + usuarioId, e);
        }
        return logros;
    }

    /**
     * Intenta desbloquear un logro para el usuario.
     */
    public boolean desbloquearLogro(int usuarioId, String logroKey) {
        String sql = "INSERT IGNORE INTO usuario_logros (usuario_id, logro_id) " +
                     "SELECT ?, id FROM logros WHERE nombre_key = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setString(2, logroKey);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error al desbloquear logro " + logroKey + " para usuario " + usuarioId, e);
            return false;
        }
    }
}
