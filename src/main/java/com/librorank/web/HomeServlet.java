package com.librorank.web;

import com.librorank.dao.ActividadDAO;
import com.librorank.dao.CitaDAO;
import com.librorank.model.ActividadSocial;
import com.librorank.model.Cita;
import com.librorank.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private final ActividadDAO actividadDAO = new ActividadDAO();
    private final CitaDAO citaDAO = new CitaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String likeId = request.getParameter("like");
        if (likeId != null) {
            actividadDAO.toggleLike(Integer.parseInt(likeId), usuario.getId());
            response.sendRedirect("home");
            return;
        }

        List<ActividadSocial> feed = actividadDAO.obtenerFeedAmigos(usuario.getId());
        Cita citaDelDia = citaDAO.obtenerCitaAleatoria(usuario.getId());

        request.setAttribute("feed", feed);
        request.setAttribute("citaDelDia", citaDelDia);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
