package com.librorank.web;

import com.librorank.dao.AmigoDAO;
import com.librorank.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/amigos")
public class AmigosServlet extends HttpServlet {

    private final AmigoDAO amigoDAO = new AmigoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String query = req.getParameter("q");
        if (query != null && !query.trim().isEmpty()) {
            List<Usuario> resultados = amigoDAO.buscarUsuarios(query, usuarioLogueado.getId());
            req.setAttribute("resultadosBusqueda", resultados);
        }

        List<Usuario> sugerencias = amigoDAO.obtenerSugerencias(usuarioLogueado.getId());
        req.setAttribute("sugerencias", sugerencias);

        List<Usuario> amigos = amigoDAO.obtenerAmigos(usuarioLogueado.getId());
        req.setAttribute("amigos", amigos);

        req.getRequestDispatcher("/amigos.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String action = req.getParameter("action");
        String amigoIdStr = req.getParameter("amigoId");

        if (amigoIdStr != null) {
            int amigoId = Integer.parseInt(amigoIdStr);
            if ("agregar".equals(action)) {
                amigoDAO.agregarAmigo(usuarioLogueado.getId(), amigoId);
                session.setAttribute("mensajeOk", "¡Amigo agregado!");
            } else if ("eliminar".equals(action)) {
                amigoDAO.eliminarAmigo(usuarioLogueado.getId(), amigoId);
                session.setAttribute("mensajeOk", "Amigo eliminado.");
            }
        }

        resp.sendRedirect("amigos");
    }
}
