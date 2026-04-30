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

    // Cache para el ranking
    private static List<Usuario> cacheRanking = null;
    private static long ultimaActualizacion = 0;
    private static final long TIEMPO_CACHE = 5 * 60 * 1000; // 5 minutos

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long ahora = System.currentTimeMillis();
        List<Usuario> ranking;

        synchronized (this) {
            if (cacheRanking == null || (ahora - ultimaActualizacion) > TIEMPO_CACHE) {
                ranking = usuarioDAO.obtenerRankingLectores(50);
                cacheRanking = ranking;
                ultimaActualizacion = ahora;
                logger.info("Ranking actualizado desde la base de datos (Cache expirada o nula).");
            } else {
                ranking = cacheRanking;
                logger.debug("Ranking entregado desde la caché.");
            }
        }
        
        request.setAttribute("ranking", ranking);
        request.getRequestDispatcher("/ranking.jsp").forward(request, response);
    }
}
