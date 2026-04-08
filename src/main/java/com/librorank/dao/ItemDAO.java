package com.librorank.dao;

import com.librorank.config.DatabaseConfig;
import com.librorank.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(ItemDAO.class);

    public List<Item> obtenerTodos() {
        List<Item> catalogo = new ArrayList<>();
        String sql = "SELECT * FROM items ORDER BY precio ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setNombre(rs.getString("nombre"));
                item.setDescripcion(rs.getString("descripcion"));
                item.setTipo(rs.getString("tipo"));
                item.setPrecio(rs.getInt("precio"));
                item.setImagenUrl(rs.getString("imagen_url"));
                item.setRequisitoLogro(rs.getString("requisito_logro"));
                catalogo.add(item);
            }

        } catch (SQLException e) {
            logger.error("Error al obtener todos los items", e);
        }
        return catalogo;
    }

    public Item buscarPorId(int id) {
        String sql = "SELECT * FROM items WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Item item = new Item();
                    item.setId(rs.getInt("id"));
                    item.setNombre(rs.getString("nombre"));
                    item.setDescripcion(rs.getString("descripcion"));
                    item.setTipo(rs.getString("tipo"));
                    item.setPrecio(rs.getInt("precio"));
                    item.setImagenUrl(rs.getString("imagen_url"));
                    item.setRequisitoLogro(rs.getString("requisito_logro"));
                    return item;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar item por ID {}", id, e);
        }
        return null;
    }
}
