package com.librorank.web;

import com.librorank.dao.UsuarioDAO;
import com.librorank.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Servlet para mostrar el ranking global de lectores.
 */
@WebServlet("/ranking")
public class RankingServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RankingServlet.class);
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int limite = 50;
        List<Usuario> ranking = usuarioDAO.obtenerRankingLectores(limite);
        request.setAttribute("ranking", ranking);
        
        logger.debug("Ranking cargado con {} usuarios", ranking.size());
        request.getRequestDispatcher("/ranking.jsp").forward(request, response);
    }
}
