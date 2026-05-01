package com.librorank.dao;

import com.librorank.config.DatabaseConfig;
import com.librorank.model.BingoCasilla;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BingoDAO {
    private static final Logger logger = LoggerFactory.getLogger(BingoDAO.class);
    private final LogroDAO logroDAO = new LogroDAO();

    public List<BingoCasilla> obtenerBingo(int usuarioId) {
        List<BingoCasilla> lista = new ArrayList<>();
        String sql = "SELECT r.id as reto_id, r.titulo, r.posicion, " +
                     "ub.completado, ub.libro_id " +
                     "FROM bingo_retos r " +
                     "LEFT JOIN usuario_bingo ub ON r.id = ub.reto_id AND ub.usuario_id = ? " +
                     "ORDER BY r.posicion ASC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BingoCasilla c = new BingoCasilla();
                    c.setId(rs.getInt("reto_id"));
                    c.setUsuarioId(usuarioId);
                    c.setTituloReto(rs.getString("titulo"));
                    c.setPosicion(rs.getInt("posicion"));
                    c.setCompletado(rs.getBoolean("completado"));
                    int libId = rs.getInt("libro_id");
                    c.setLibroId(rs.wasNull() ? null : libId);
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener bingo para usuario " + usuarioId, e);
        }
        return lista;
    }

    public boolean marcarCasilla(int usuarioId, int retoId, int libroId) {
        String sql = "INSERT INTO usuario_bingo (usuario_id, reto_id, libro_id, completado) " +
                     "VALUES (?, ?, ?, true) " +
                     "ON DUPLICATE KEY UPDATE libro_id = ?, completado = true";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, retoId);
            stmt.setInt(3, libroId);
            stmt.setInt(4, libroId);
            
            boolean ok = stmt.executeUpdate() > 0;
            if (ok) {
                otorgarPuntos(usuarioId, 10, "Casilla de Bingo completada");
                verificarYPremiarLineas(usuarioId);
            }
            return ok;
        } catch (SQLException e) {
            logger.error("Error al marcar casilla", e);
            return false;
        }
    }

    private void verificarYPremiarLineas(int usuarioId) {
        List<BingoCasilla> bingo = obtenerBingo(usuarioId);
        boolean[] c = new boolean[25];
        int completadas = 0;
        for (BingoCasilla casilla : bingo) {
            if (casilla.isCompletado()) {
                c[casilla.getPosicion()] = true;
                completadas++;
            }
        }

        int lineasNuevas = 0;
        // Filas
        for (int i = 0; i < 5; i++) {
            if (c[i*5] && c[i*5+1] && c[i*5+2] && c[i*5+3] && c[i*5+4]) lineasNuevas++;
        }
        // Columnas
        for (int i = 0; i < 5; i++) {
            if (c[i] && c[i+5] && c[i+10] && c[i+15] && c[i+20]) lineasNuevas++;
        }
        // Diagonales
        if (c[0] && c[6] && c[12] && c[18] && c[24]) lineasNuevas++;
        if (c[4] && c[8] && c[12] && c[16] && c[20]) lineasNuevas++;

        if (completadas == 25) {
            otorgarPuntos(usuarioId, 100, "¡BINGO COMPLETO!");
            logroDAO.desbloquearLogro(usuarioId, "BINGO_MASTER");
        } else if (lineasNuevas > 0) {
            otorgarPuntos(usuarioId, 30, "Línea de Bingo");
        }
    }

    private void otorgarPuntos(int usuarioId, int puntos, String motivo) {
        String sql = "UPDATE usuarios SET monedas = monedas + ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, puntos);
            stmt.setInt(2, usuarioId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error al otorgar puntos", e);
        }
    }
}
