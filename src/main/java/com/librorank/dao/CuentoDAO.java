package com.librorank.dao;

import com.librorank.config.DatabaseConfig;
import com.librorank.model.FragmentoHistoria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CuentoDAO {
    private static final Logger logger = LoggerFactory.getLogger(CuentoDAO.class);

    public List<FragmentoHistoria> obtenerHistoriaCompleta(int historiaId) {
        List<FragmentoHistoria> historia = new ArrayList<>();
        String sql = """
            SELECT f.*, u.username 
            FROM fragmentos_historia f 
            JOIN usuarios u ON f.usuario_id = u.id 
            WHERE f.historia_id = ? 
            ORDER BY f.numero_hoja ASC
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, historiaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FragmentoHistoria f = new FragmentoHistoria();
                    f.setId(rs.getInt("id"));
                    f.setUsername(rs.getString("username"));
                    f.setContenido(rs.getString("contenido"));
                    f.setNumeroHoja(rs.getInt("numero_hoja"));
                    historia.add(f);
                }
            }
        } catch (SQLException e) { logger.error("Error al obtener historia", e); }
        return historia;
    }

    public boolean haEscritoYa(int historiaId, int usuarioId) {
        String sql = "SELECT 1 FROM fragmentos_historia WHERE historia_id = ? AND usuario_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, historiaId);
            stmt.setInt(2, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { return false; }
    }

    public boolean guardarHoja(int historiaId, int usuarioId, String contenido) {
        // Calcular el siguiente número de hoja
        int siguienteHoja = 1;
        String sqlCount = "SELECT MAX(numero_hoja) FROM fragmentos_historia WHERE historia_id = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(sqlCount)) {
                st.setInt(1, historiaId);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) siguienteHoja = rs.getInt(1) + 1;
                }
            }
            
            String sqlInsert = "INSERT INTO fragmentos_historia (historia_id, usuario_id, contenido, numero_hoja) VALUES (?, ?, ?, ?)";
            try (PreparedStatement st = conn.prepareStatement(sqlInsert)) {
                st.setInt(1, historiaId);
                st.setInt(2, usuarioId);
                st.setString(3, contenido);
                st.setInt(4, siguienteHoja);
                return st.executeUpdate() > 0;
            }
        } catch (SQLException e) { logger.error("Error al guardar hoja", e); return false; }
    }

    public int obtenerOIdUnicaHistoria() {
        // Para simplificar, asumimos que siempre hay una historia activa llamada "El Origen"
        String sql = "SELECT id FROM historias_comunitarias LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
            
            // Si no existe, la creamos
            String insert = "INSERT INTO historias_comunitarias (titulo) VALUES ('La Gran Crónica de LibroRank')";
            try (PreparedStatement st = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                st.executeUpdate();
                try (ResultSet rsKey = st.getGeneratedKeys()) {
                    if (rsKey.next()) return rsKey.getInt(1);
                }
            }
        } catch (SQLException e) { }
        return 1;
    }
}
