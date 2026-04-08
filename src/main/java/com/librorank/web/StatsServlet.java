package com.librorank.web;

import com.librorank.dao.LibroDAO;
import com.librorank.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

@WebServlet("/stats")
public class StatsServlet extends HttpServlet {
    private final LibroDAO libroDAO = new LibroDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        Map<String, Integer> generos = libroDAO.obtenerConteoPorGenero(usuario.getId());
        Map<String, Integer> moods = libroDAO.obtenerConteoPorMood(usuario.getId());

        request.setAttribute("generosJson", gson.toJson(generos));
        request.setAttribute("moodsJson", gson.toJson(moods));
        
        request.getRequestDispatcher("/stats.jsp").forward(request, response);
    }
}
