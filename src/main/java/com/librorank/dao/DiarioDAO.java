package com.librorank.dao;

import com.librorank.config.DatabaseConfig;
import com.librorank.model.DiarioLectura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiarioDAO {
    private static final Logger logger = LoggerFactory.getLogger(DiarioDAO.class);

    public boolean guardar(DiarioLectura entrada) {
        String sql = "INSERT INTO diario_lectura (libro_id, usuario_id, capitulo, comentario) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, entrada.getLibroId());
            stmt.setInt(2, entrada.getUsuarioId());
            stmt.setString(3, entrada.getCapitulo());
            stmt.setString(4, entrada.getComentario());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error al guardar entrada en el diario", e);
            return false;
        }
    }

    public List<DiarioLectura> obtenerPorLibro(int libroId, int usuarioId) {
        List<DiarioLectura> lista = new ArrayList<>();
        String sql = "SELECT * FROM diario_lectura WHERE libro_id = ? AND usuario_id = ? ORDER BY fecha_creacion DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, libroId);
            stmt.setInt(2, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DiarioLectura d = new DiarioLectura();
                    d.setId(rs.getInt("id"));
                    d.setLibroId(rs.getInt("libro_id"));
                    d.setUsuarioId(rs.getInt("usuario_id"));
                    d.setCapitulo(rs.getString("capitulo"));
                    d.setComentario(rs.getString("comentario"));
                    d.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
                    lista.add(d);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener diario del libro ID {}", libroId, e);
        }
        return lista;
    }
}
