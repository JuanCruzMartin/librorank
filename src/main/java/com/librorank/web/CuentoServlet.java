package com.librorank.web;

import com.librorank.dao.CuentoDAO;
import com.librorank.dao.LibroDAO;
import com.librorank.dao.ActividadDAO;
import com.librorank.model.FragmentoHistoria;
import com.librorank.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/cuento")
public class CuentoServlet extends HttpServlet {
    private final CuentoDAO cuentoDAO = new CuentoDAO();
    private final ActividadDAO actividadDAO = new ActividadDAO();
    private final LibroDAO libroDAO = new LibroDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        int historiaId = cuentoDAO.obtenerOIdUnicaHistoria();
        List<FragmentoHistoria> fragmentos = cuentoDAO.obtenerHistoriaCompleta(historiaId);
        boolean yaEscribio = cuentoDAO.haEscritoYa(historiaId, usuario.getId());

        request.setAttribute("fragmentos", fragmentos);
        request.setAttribute("yaEscribio", yaEscribio);
        request.getRequestDispatcher("/cuento.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        String contenido = request.getParameter("contenido");

        if (contenido == null || contenido.trim().isEmpty()) {
            request.setAttribute("mensajeError", "El contenido de tu hoja no puede estar vacío.");
            doGet(request, response);
            return;
        }

        try {
            int historiaId = cuentoDAO.obtenerOIdUnicaHistoria();

            if (cuentoDAO.haEscritoYa(historiaId, usuario.getId())) {
                request.setAttribute("mensajeError", "Ya has participado en esta crónica. ¡Espera a que otros lectores continúen!");
            } else {
                if (cuentoDAO.guardarHoja(historiaId, usuario.getId(), contenido.trim())) {
                    // Premio por participar
                    libroDAO.otorgarPuntosPorActividad(usuario.getId(), 50, "Participación en Cuento Comunitario"); 
                    actividadDAO.registrarActividad(usuario.getId(), "DIARIO_LOG", null, "Escribió una hoja en el Cuento Comunitario.");
                    
                    // Actualizar usuario en sesión para ver puntos nuevos
                    usuario.setPuntos(usuario.getPuntos() + 50);
                    session.setAttribute("usuarioLogueado", usuario);
                    
                    request.setAttribute("mensajeOk", "¡Tu hoja ha sido añadida a la historia! Ganaste 50 pts ⭐");
                } else {
                    request.setAttribute("mensajeError", "Hubo un problema técnico al guardar tu hoja. Por favor, inténtalo de nuevo.");
                }
            }
        } catch (Exception e) {
            request.setAttribute("mensajeError", "Ocurrió un error inesperado: " + e.getMessage());
        }

        doGet(request, response);
    }
}
