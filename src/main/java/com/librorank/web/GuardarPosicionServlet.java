package com.librorank.web;

import com.librorank.dao.InventarioDAO;
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

@WebServlet("/guardarPosicion")
public class GuardarPosicionServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(GuardarPosicionServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        if (usuario == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            int inventarioId = Integer.parseInt(request.getParameter("inventarioId"));
            int posX = Integer.parseInt(request.getParameter("posX"));
            int posY = Integer.parseInt(request.getParameter("posY"));
            int orientacion = Integer.parseInt(request.getParameter("orientacion"));
            boolean enUso = Boolean.parseBoolean(request.getParameter("enUso"));

            InventarioDAO dao = new InventarioDAO();
            boolean exito = dao.actualizarPosicionMueble(inventarioId, usuario.getId(), posX, posY, enUso, orientacion);

            if (exito) {
                if ("GET".equalsIgnoreCase(request.getMethod())) {
                    // Si es GET (desde el inventario), redirigimos de vuelta a la habitación
                    response.sendRedirect(request.getContextPath() + "/habitacion");
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Guardado");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            logger.error("Error al guardar posición", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
