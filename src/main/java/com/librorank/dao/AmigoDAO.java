package com.librorank.dao;

import com.librorank.config.DatabaseConfig;
import com.librorank.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AmigoDAO {

    private static final Logger logger = LoggerFactory.getLogger(AmigoDAO.class);

    public boolean agregarAmigo(int usuarioId, int amigoId) {
        if (usuarioId == amigoId) return false;
        String sql = "INSERT IGNORE INTO amigos (usuario_id, amigo_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, amigoId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error al agregar amigo: {} -> {}", usuarioId, amigoId, e);
            return false;
        }
    }

    public boolean eliminarAmigo(int usuarioId, int amigoId) {
        String sql = "DELETE FROM amigos WHERE usuario_id = ? AND amigo_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, amigoId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error al eliminar amigo: {} -> {}", usuarioId, amigoId, e);
            return false;
        }
    }

    public List<Usuario> obtenerAmigos(int usuarioId) {
        List<Usuario> amigos = new ArrayList<>();
        String sql = "SELECT u.id, u.nombre, u.username, u.avatar_url " +
                "FROM usuarios u " +
                "JOIN amigos a ON u.id = a.amigo_id " +
                "WHERE a.usuario_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setUsername(rs.getString("username"));
                    u.setAvatarUrl(rs.getString("avatar_url"));
                    u.setLibrosEnComun(contarLibrosEnComun(usuarioId, u.getId()));
                    u.setListaTitulosEnComun(obtenerNombresLibrosEnComun(usuarioId, u.getId()));
                    amigos.add(u);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener amigos para usuario {}", usuarioId, e);
        }
        return amigos;
    }

    public List<Usuario> buscarUsuarios(String query, int usuarioActualId) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = """
            SELECT u.id, u.nombre, u.username, u.avatar_url, u.generos_favoritos, u.bio,
                   (SELECT COUNT(*) FROM libros_usuario lu WHERE lu.usuario_id = u.id AND lu.estado IN ('LEIDO', 'LEÍDO')) as total_leidos
            FROM usuarios u
            WHERE (u.username LIKE ? OR u.email LIKE ?) AND u.id <> ? LIMIT 10
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            stmt.setInt(3, usuarioActualId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setUsername(rs.getString("username"));
                    u.setAvatarUrl(rs.getString("avatar_url"));
                    u.setGenerosFavoritos(rs.getString("generos_favoritos"));
                    u.setBio(rs.getString("bio"));
                    u.setTotalLibrosLeidos(rs.getInt("total_leidos"));
                    
                    u.setLibrosEnComun(contarLibrosEnComun(usuarioActualId, u.getId()));
                    u.setListaTitulosEnComun(obtenerNombresLibrosEnComun(usuarioActualId, u.getId()));
                    
                    usuarios.add(u);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar usuarios con query {}", query, e);
        }
        return usuarios;
    }

    public int contarLibrosEnComun(int id1, int id2) {
        String sql = """
            SELECT COUNT(*) 
            FROM libros_usuario l1
            JOIN libros_usuario l2 ON TRIM(LOWER(l1.titulo)) = TRIM(LOWER(l2.titulo)) 
                                   AND TRIM(LOWER(l1.autor)) = TRIM(LOWER(l2.autor))
            WHERE l1.usuario_id = ? AND l2.usuario_id = ?
            AND UPPER(l1.estado) IN ('LEIDO', 'LEÍDO') 
            AND UPPER(l2.estado) IN ('LEIDO', 'LEÍDO')
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id1);
            stmt.setInt(2, id2);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error al contar libros en común entre {} y {}", id1, id2, e);
        }
        return 0;
    }

    public String obtenerNombresLibrosEnComun(int id1, int id2) {
        String sql = """
            SELECT l1.titulo 
            FROM libros_usuario l1
            JOIN libros_usuario l2 ON TRIM(LOWER(l1.titulo)) = TRIM(LOWER(l2.titulo)) 
                                   AND TRIM(LOWER(l1.autor)) = TRIM(LOWER(l2.autor))
            WHERE l1.usuario_id = ? AND l2.usuario_id = ?
            AND UPPER(l1.estado) IN ('LEIDO', 'LEÍDO') 
            AND UPPER(l2.estado) IN ('LEIDO', 'LEÍDO')
            LIMIT 5
        """;
        List<String> titulos = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id1);
            stmt.setInt(2, id2);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    titulos.add(rs.getString("titulo"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener nombres de libros en común", e);
        }
        return String.join(", ", titulos);
    }

    public List<Usuario> obtenerSugerencias(int usuarioId) {
        List<Usuario> sugerencias = new ArrayList<>();
        String sql = """
            SELECT DISTINCT u.id, u.nombre, u.username, u.avatar_url, u.generos_favoritos, u.bio,
                   (SELECT COUNT(*) FROM libros_usuario lu WHERE lu.usuario_id = u.id AND lu.estado IN ('LEIDO', 'LEÍDO')) as total_leidos
            FROM usuarios u
            WHERE u.id <> ? 
            AND u.id NOT IN (SELECT amigo_id FROM amigos WHERE usuario_id = ?)
            LIMIT 5
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setUsername(rs.getString("username"));
                    u.setAvatarUrl(rs.getString("avatar_url"));
                    u.setGenerosFavoritos(rs.getString("generos_favoritos"));
                    u.setBio(rs.getString("bio"));
                    u.setTotalLibrosLeidos(rs.getInt("total_leidos"));
                    
                    u.setLibrosEnComun(contarLibrosEnComun(usuarioId, u.getId()));
                    u.setListaTitulosEnComun(obtenerNombresLibrosEnComun(usuarioId, u.getId()));
                    
                    if (u.getLibrosEnComun() > 0) {
                        sugerencias.add(u);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener sugerencias para usuario {}", usuarioId, e);
        }
        return sugerencias;
    }
}
