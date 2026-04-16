package com.librorank.web;

import com.librorank.dao.LibroDAO;
import com.librorank.dao.RetoDAO;
import com.librorank.model.Libro;
import com.librorank.model.RetoAmigo;
import com.librorank.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/retos")
public class RetosServlet extends HttpServlet {
    private final RetoDAO retoDAO = new RetoDAO();
    private final LibroDAO libroDAO = new LibroDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        List<RetoAmigo> retos = retoDAO.obtenerRetosActivos(usuario.getId());
        List<Libro> misLibros = libroDAO.buscarPorUsuario(usuario.getId());

        request.setAttribute("retos", retos);
        request.setAttribute("misLibros", misLibros);
        request.getRequestDispatcher("/retos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        String accion = request.getParameter("accion");
        String mensaje = null;
        String tipoMensaje = "success";

        try {
            if ("crear".equals(accion)) {
                String nombre = request.getParameter("nombre");
                String fechaFin = request.getParameter("fechaFin");
                String libroIdStr = request.getParameter("libroId");
                Integer libroId = (libroIdStr != null && !libroIdStr.isEmpty()) ? Integer.parseInt(libroIdStr) : null;
                
                boolean creado = retoDAO.crearReto(usuario.getId(), nombre, libroId, fechaFin);
                if (creado) {
                    mensaje = "¡Reto creado exitosamente!";
                } else {
                    mensaje = "No se pudo crear el reto. Por favor, verifica los datos.";
                    tipoMensaje = "danger";
                }
            } else if ("unirse".equals(accion)) {
                int retoId = Integer.parseInt(request.getParameter("retoId"));
                boolean unido = retoDAO.unirseAReto(retoId, usuario.getId());
                if (unido) {
                    mensaje = "¡Te has unido al reto!";
                } else {
                    mensaje = "Ya participas en este reto o no se pudo procesar tu solicitud.";
                    tipoMensaje = "warning";
                }
            } else if ("actualizar".equals(accion)) {
                int retoId = Integer.parseInt(request.getParameter("retoId"));
                int progreso = Integer.parseInt(request.getParameter("progreso"));
                boolean actualizado = retoDAO.actualizarProgreso(retoId, usuario.getId(), progreso);
                if (actualizado) {
                    mensaje = "¡Progreso actualizado!";
                } else {
                    mensaje = "No se pudo actualizar el progreso.";
                    tipoMensaje = "danger";
                }
            }
        } catch (Exception e) {
            mensaje = "Error al procesar la solicitud: " + e.getMessage();
            tipoMensaje = "danger";
        }

        if (mensaje != null) {
            session.setAttribute("mensajeFeedback", mensaje);
            session.setAttribute("tipoMensajeFeedback", tipoMensaje);
        }

        response.sendRedirect("retos");
    }
}
