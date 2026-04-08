package com.librorank.dao;

import com.librorank.config.DatabaseConfig;
import com.librorank.model.Item;
import com.librorank.model.ItemInventario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    private static final Logger logger = LoggerFactory.getLogger(InventarioDAO.class);

    public boolean agregarItemAlInventario(int usuarioId, int itemId) {
        String sql = "INSERT INTO inventario (usuario_id, item_id, en_uso) VALUES (?, ?, false)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, itemId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error al agregar item ID {} al inventario del usuario ID {}", itemId, usuarioId, e);
            return false;
        }
    }

    public List<ItemInventario> obtenerInventarioUsuario(int usuarioId) {
        List<ItemInventario> miInventario = new ArrayList<>();
        String sql = "SELECT inv.id AS inv_id, inv.usuario_id, inv.en_uso, inv.posicion_x, inv.posicion_y, inv.orientacion,  " +
                "it.id AS item_id, it.nombre, it.tipo, it.precio, it.imagen_url " +
                "FROM inventario inv " +
                "INNER JOIN items it ON inv.item_id = it.id " +
                "WHERE inv.usuario_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Item item = new Item();
                    item.setId(rs.getInt("item_id"));
                    item.setNombre(rs.getString("nombre"));
                    item.setTipo(rs.getString("tipo"));
                    item.setPrecio(rs.getInt("precio"));
                    item.setImagenUrl(rs.getString("imagen_url"));

                    ItemInventario inv = new ItemInventario();
                    inv.setId(rs.getInt("inv_id"));
                    inv.setUsuarioId(rs.getInt("usuario_id"));
                    inv.setEnUso(rs.getBoolean("en_uso"));
                    inv.setPosicionX(rs.getInt("posicion_x"));
                    inv.setPosicionY(rs.getInt("posicion_y"));
                    inv.setOrientacion(rs.getInt("orientacion"));
                    inv.setItem(item);

                    miInventario.add(inv);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener inventario para usuario ID {}", usuarioId, e);
        }
        return miInventario;
    }

    public boolean actualizarPosicionMueble(int inventarioId, int usuarioId, int posX, int posY, boolean enUso, int orientacion) {
        String sql = "UPDATE inventario SET posicion_x = ?, posicion_y = ?, en_uso = ?, orientacion = ? WHERE id = ? AND usuario_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, posX);
            stmt.setInt(2, posY);
            stmt.setBoolean(3, enUso);
            stmt.setInt(4, orientacion);
            stmt.setInt(5, inventarioId);
            stmt.setInt(6, usuarioId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error al actualizar posición de mueble ID {} para usuario ID {}", inventarioId, usuarioId, e);
            return false;
        }
    }
}
