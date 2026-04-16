package com.librorank.dao;

import com.librorank.config.DatabaseConfig;
import com.librorank.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioDAO.class);

    /**
     * REGISTRAR USUARIO
     * Recibe un objeto Usuario lleno desde el formulario de registro y lo guarda en la BD.
     */
    public boolean registrar(Usuario usuario) {
        String sql = "INSERT INTO usuarios(nombre, username, email, password_hash, bio, avatar_url, nivel_id, objetivo_anual) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getUsername());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getPasswordHash());
            stmt.setString(5, usuario.getBio());
            stmt.setString(6, usuario.getAvatarUrl());
            stmt.setObject(7, usuario.getNivelId());
            stmt.setObject(8, usuario.getObjetivoAnual());

            int filas = stmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            logger.error("Error al registrar usuario: {}", usuario.getEmail(), e);
            return false;
        }
    }

    /**
     * LOGIN: BUSCAR POR EMAIL O USERNAME
     * Permite al usuario loguearse usando cualquiera de los dos datos.
     */
    public Usuario buscarPorEmailOUsername(String identificador) {
        String sql = "SELECT id, nombre, username, email, password_hash, bio, avatar_url, " +
                "       nivel_id, objetivo_anual, monedas, generos_favoritos, racha_actual, ultima_fecha_lectura " +
                "FROM usuarios " +
                "WHERE email = ? OR username = ? " +
                "LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, identificador);
            stmt.setString(2, identificador);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setBio(rs.getString("bio"));
                    u.setAvatarUrl(rs.getString("avatar_url"));
                    u.setNivelId((Integer) rs.getObject("nivel_id"));
                    u.setObjetivoAnual((Integer) rs.getObject("objetivo_anual"));
                    u.setMonedas(rs.getInt("monedas"));
                    u.setGenerosFavoritos(rs.getString("generos_favoritos"));
                    u.setRachaActual(rs.getInt("racha_actual"));
                    Date fecha = rs.getDate("ultima_fecha_lectura");
                    if (fecha != null) u.setUltimaFechaLectura(fecha.toLocalDate());
                    return u;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar usuario por identificador: {}", identificador, e);
        }
        return null;
    }

    public boolean actualizarRacha(int usuarioId) {
        // Lógica de racha:
        // 1. Obtener racha y última fecha
        // 2. Comparar con hoy
        String selectSql = "SELECT racha_actual, ultima_fecha_lectura FROM usuarios WHERE id = ?";
        String updateSql = "UPDATE usuarios SET racha_actual = ?, ultima_fecha_lectura = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            int racha = 0;
            java.time.LocalDate ultima = null;
            
            try (PreparedStatement pst = conn.prepareStatement(selectSql)) {
                pst.setInt(1, usuarioId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        racha = rs.getInt("racha_actual");
                        Date d = rs.getDate("ultima_fecha_lectura");
                        if (d != null) ultima = d.toLocalDate();
                    }
                }
            }

            java.time.LocalDate hoy = java.time.LocalDate.now();
            if (ultima == null) {
                racha = 1;
            } else if (ultima.equals(hoy)) {
                return true; // Ya actualizó hoy
            } else if (ultima.equals(hoy.minusDays(1))) {
                racha++;
            } else {
                racha = 1;
            }

            try (PreparedStatement upst = conn.prepareStatement(updateSql)) {
                upst.setInt(1, racha);
                upst.setDate(2, Date.valueOf(hoy));
                upst.setInt(3, usuarioId);
                return upst.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar racha para ID {}", usuarioId, e);
            return false;
        }
    }

    /**
     * RANKING DE LECTORES
     */
    public List<Usuario> obtenerRankingLectores(int limite) {
        List<Usuario> ranking = new ArrayList<>();
        String sql = "SELECT u.id, u.nombre, u.username, u.email, u.bio, u.avatar_url, u.objetivo_anual, " +
                "       COUNT(l.id) AS total_leidos " +
                "FROM usuarios u " +
                "LEFT JOIN libros_usuario l " +
                "  ON u.id = l.usuario_id " +
                " AND l.estado = 'LEIDO' " +
                "GROUP BY u.id, u.nombre, u.username, u.email, u.bio, u.avatar_url, u.objetivo_anual " +
                "ORDER BY total_leidos DESC, u.nombre ASC " +
                "LIMIT ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limite);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    u.setBio(rs.getString("bio"));
                    u.setAvatarUrl(rs.getString("avatar_url"));
                    u.setObjetivoAnual((Integer) rs.getObject("objetivo_anual"));
                    u.setTotalLibrosLeidos(rs.getInt("total_leidos"));
                    ranking.add(u);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener ranking de lectores", e);
        }
        return ranking;
    }

    /**
     * ACTUALIZAR PERFIL
     */
    public boolean actualizarPerfilBasico(Usuario usuario) {
        String sql = "UPDATE usuarios " +
                "SET nombre = ?, username = ?, email = ?, bio = ?, objetivo_anual = ?, generos_favoritos = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getUsername());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getBio());
            stmt.setObject(5, usuario.getObjetivoAnual());
            stmt.setString(6, usuario.getGenerosFavoritos());
            stmt.setInt(7, usuario.getId());

            int filas = stmt.executeUpdate();
            logger.debug("Perfil actualizado para ID {}: {} filas afectadas", usuario.getId(), filas);
            return filas > 0;

        } catch (SQLException e) {
            logger.error("Error al actualizar perfil para ID: {}", usuario.getId(), e);
            return false;
        }
    }

    /**
     * ACTUALIZAR AVATAR SELECCIONADO
     */
    public boolean actualizarAvatarActual(int usuarioId, String folderAvatar) {
        String sql = "UPDATE usuarios SET avatar_url = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, folderAvatar);
            stmt.setInt(2, usuarioId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error al actualizar avatar para usuario ID {}", usuarioId, e);
            return false;
        }
    }

    /**
     * ACTUALIZAR PASSWORD HASH
     * Se usa para migrar contraseñas antiguas a BCrypt.
     */
    public void actualizarPasswordHash(int id, String nuevoHash) {
        String sql = "UPDATE usuarios SET password_hash = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoHash);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            logger.info("Contraseña migrada a BCrypt para el usuario ID {}", id);
        } catch (SQLException e) {
            logger.error("Error al migrar contraseña para ID {}", id, e);
        }
    }

    /**
     * BUSCAR POR ID
     */
    public Usuario buscarPorId(int id) {
        String sql = "SELECT id,nombre,username,email,bio,avatar_url,monedas,nivel_id,objetivo_anual,generos_favoritos " +
                "FROM usuarios WHERE id= ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    u.setBio(rs.getString("bio"));
                    u.setAvatarUrl(rs.getString("avatar_url"));
                    u.setMonedas(rs.getInt("monedas"));
                    u.setGenerosFavoritos(rs.getString("generos_favoritos"));
                    u.setNivelId((Integer) rs.getObject("nivel_id"));
                    u.setObjetivoAnual((Integer) rs.getObject("objetivo_anual"));
                    return u;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar usuario por ID: {}", id, e);
        }
        return null;
    }
}
