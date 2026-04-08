package com.librorank.web;

import com.librorank.dao.InventarioDAO;
import com.librorank.dao.ItemDAO;
import com.librorank.dao.UsuarioDAO;
import com.librorank.model.Item;
import com.librorank.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/comprar")
public class CompraServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CompraServlet.class);
    private final ItemDAO itemDAO = new ItemDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final InventarioDAO inventarioDAO = new InventarioDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        String itemIdStr = request.getParameter("itemId");
        if (itemIdStr == null || itemIdStr.isBlank()) {
            response.sendRedirect("tienda");
            return;
        }

        try {
            int itemId = Integer.parseInt(itemIdStr);
            Item item = itemDAO.buscarPorId(itemId);

            if (item == null) {
                session.setAttribute("error", "El producto no existe");
                response.sendRedirect("tienda");
                return;
            }

            if (usuario.getMonedas() >= item.getPrecio()) {
                boolean cobroExitoso = usuarioDAO.restarMonedas(usuario.getId(), item.getPrecio());

                if (cobroExitoso) {
                    inventarioDAO.agregarItemAlInventario(usuario.getId(), itemId);
                    usuario.setMonedas(usuario.getMonedas() - item.getPrecio());
                    session.setAttribute("usuarioLogueado", usuario);
                    session.setAttribute("mensaje", "¡Compraste " + item.getNombre() + " con éxito!");
                    logger.info("Usuario ID {} compró item ID {}: {}", usuario.getId(), itemId, item.getNombre());
                } else {
                    session.setAttribute("error", "Error al procesar el pago.");
                }
            } else {
                session.setAttribute("error", "No tienes suficientes monedas");
            }
        } catch (NumberFormatException e) {
            logger.warn("Formato de ID de item inválido: {}", itemIdStr);
            session.setAttribute("error", "ID de producto inválido");
        }

        response.sendRedirect("tienda");
    }
}
