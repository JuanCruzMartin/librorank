package com.librorank.web;

import com.librorank.dao.ItemDAO;
import com.librorank.dao.LogroDAO;
import com.librorank.model.Item;
import com.librorank.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebServlet("/tienda")
public class TiendaServlet extends HttpServlet {
    private final ItemDAO itemDAO = new ItemDAO();
    private final LogroDAO logroDAO = new LogroDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        // Obtenemos los productos y los logros que ya tiene el usuario
        List<Item> productos = itemDAO.obtenerTodos();
        Set<String> logrosUsuario = logroDAO.obtenerLogrosKeysUsuario(usuario.getId());

        request.setAttribute("productos", productos);
        request.setAttribute("logrosUsuario", logrosUsuario);

        request.getRequestDispatcher("/tienda.jsp").forward(request, response);
    }
}
