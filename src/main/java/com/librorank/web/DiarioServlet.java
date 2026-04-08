package com.librorank.web;

import com.librorank.dao.DiarioDAO;
import com.librorank.dao.LibroDAO;
import com.librorank.dao.UsuarioDAO;
import com.librorank.dao.ActividadDAO;
import com.librorank.dao.CitaDAO;
import com.librorank.model.DiarioLectura;
import com.librorank.model.Libro;
import com.librorank.model.Usuario;
import com.librorank.model.Cita;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/diario")
public class DiarioServlet extends HttpServlet {
    private final DiarioDAO diarioDAO = new DiarioDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ActividadDAO actividadDAO = new ActividadDAO();
    private final CitaDAO citaDAO = new CitaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        String idLibroStr = request.getParameter("idLibro");
        if (idLibroStr != null) {
            int idLibro = Integer.parseInt(idLibroStr);
            Libro libro = libroDAO.buscarPorId(idLibro, usuario.getId());
            List<DiarioLectura> entradas = diarioDAO.obtenerPorLibro(idLibro, usuario.getId());
            List<Cita> citas = citaDAO.obtenerPorLibro(idLibro, usuario.getId());

            request.setAttribute("libro", libro);
            request.setAttribute("entradas", entradas);
            request.setAttribute("citas", citas);
            request.getRequestDispatcher("/diario.jsp").forward(request, response);
        } else {
            response.sendRedirect("biblioteca");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        int idLibro = Integer.parseInt(request.getParameter("idLibro"));
        String accion = request.getParameter("accion");

        if ("cita".equals(accion)) {
            String texto = request.getParameter("texto");
            String pagina = request.getParameter("pagina");
            Cita cita = new Cita();
            cita.setUsuarioId(usuario.getId());
            cita.setLibroId(idLibro);
            cita.setTexto(texto);
            cita.setPagina(pagina);
            if (citaDAO.guardar(cita)) {
                actividadDAO.registrarActividad(usuario.getId(), "DIARIO_LOG", idLibro, "Guardó una cita inspiradora.");
            }
        } else {
            String capitulo = request.getParameter("capitulo");
            String comentario = request.getParameter("comentario");

            DiarioLectura entrada = new DiarioLectura();
            entrada.setLibroId(idLibro);
            entrada.setUsuarioId(usuario.getId());
            entrada.setCapitulo(capitulo);
            entrada.setComentario(comentario);

            if (diarioDAO.guardar(entrada)) {
                usuarioDAO.actualizarRacha(usuario.getId());
                actividadDAO.registrarActividad(usuario.getId(), "DIARIO_LOG", idLibro, "Actualizó su progreso: " + capitulo);
                Usuario usuarioActualizado = usuarioDAO.buscarPorId(usuario.getId());
                session.setAttribute("usuarioLogueado", usuarioActualizado);
            }
        }

        response.sendRedirect("diario?idLibro=" + idLibro);
    }
}
