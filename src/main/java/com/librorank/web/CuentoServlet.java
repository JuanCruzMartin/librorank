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
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        String contenido = request.getParameter("contenido");
        int historiaId = cuentoDAO.obtenerOIdUnicaHistoria();

        if (!cuentoDAO.haEscritoYa(historiaId, usuario.getId())) {
            if (cuentoDAO.guardarHoja(historiaId, usuario.getId(), contenido)) {
                // Premio por participar
                libroDAO.otorgarMonedasPorBingo(usuario.getId(), 30); // Usamos el de bingo para dar 30 monedas
                actividadDAO.registrarActividad(usuario.getId(), "DIARIO_LOG", null, "Escribió una hoja en el Cuento Comunitario.");
                request.setAttribute("mensajeOk", "¡Tu hoja ha sido añadida a la historia! Ganaste 30 🪙");
            }
        }

        doGet(request, response);
    }
}
