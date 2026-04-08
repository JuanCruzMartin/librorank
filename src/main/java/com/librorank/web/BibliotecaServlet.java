package com.librorank.web;

import com.librorank.dao.LibroDAO;
import com.librorank.dao.ActividadDAO;
import com.librorank.model.Libro;
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
import java.util.List;

/**
 * Servlet encargado de gestionar la biblioteca personal del usuario.
 */
@WebServlet("/biblioteca")
public class BibliotecaServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(BibliotecaServlet.class);
    private final LibroDAO libroDAO = new LibroDAO();
    private final ActividadDAO actividadDAO = new ActividadDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        String idEliminarStr = request.getParameter("eliminar");
        if (idEliminarStr != null){
            try{
                int idLibro = Integer.parseInt(idEliminarStr);
                boolean eliminado = libroDAO.eliminar(idLibro, usuario.getId());
                if (eliminado){
                    request.setAttribute("mensajeOK","Libro eliminado correctamente.");
                }
            }catch (NumberFormatException e){
                request.setAttribute("mensajeError","ID de libro inválido");
            }
        }

        List<Libro> libros = libroDAO.buscarPorUsuario(usuario.getId());

        request.setAttribute("statLeidos", libroDAO.contarLeidosTotalPorUsuario(usuario.getId()));
        request.setAttribute("statPaginas", libroDAO.sumarPaginasLeidas(usuario.getId()));
        request.setAttribute("statAutor", libroDAO.obtenerAutorMasLeido(usuario.getId()));
        request.setAttribute("statMejorLibro", libroDAO.obtenerMejorCalificado(usuario.getId()));

        request.setAttribute("libros", libros);
        request.getRequestDispatcher("/biblioteca.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        String accion = request.getParameter("accion");

        if("editar".equals(accion)){
            try{
                int idLibro = Integer.parseInt(request.getParameter("idLibro"));
                String estadoEdit = request.getParameter("estado");
                int estrellasEdit = Integer.parseInt(request.getParameter("estrellas"));
                String resenaEdit = request.getParameter("resena");
                String generoEdit = request.getParameter("genero");
                String moodEdit = request.getParameter("mood");

                Libro libroPrevio = libroDAO.buscarPorId(idLibro, usuario.getId());

                Libro libroEditado = new Libro();
                libroEditado.setId(idLibro);
                libroEditado.setUsuarioId(usuario.getId());
                libroEditado.setEstado(estadoEdit);
                libroEditado.setEstrellas(estrellasEdit);
                libroEditado.setResena(resenaEdit);
                libroEditado.setGenero(generoEdit);
                libroEditado.setMood(moodEdit);

                if (libroDAO.actualizar(libroEditado)){
                    if (libroPrevio != null && !estadoEdit.equals(libroPrevio.getEstado())) {
                        actividadDAO.registrarActividad(usuario.getId(), "CAMBIO_ESTADO", idLibro, estadoEdit);
                    }
                    if (libroPrevio != null && estrellasEdit > 0 && estrellasEdit != libroPrevio.getEstrellas()) {
                        actividadDAO.registrarActividad(usuario.getId(), "NUEVA_CALIFICACION", idLibro, estrellasEdit + " estrellas");
                    }

                    boolean esLeido = "LEIDO".equalsIgnoreCase(estadoEdit);
                    boolean tieneResenaNueva = resenaEdit != null && !resenaEdit.isBlank();
                    boolean noTeniaResena = libroPrevio != null && (libroPrevio.getResena() == null || libroPrevio.getResena().isBlank());

                    if (esLeido && tieneResenaNueva && noTeniaResena) {
                        libroDAO.otorgarMonedasPorResena(usuario.getId());
                        usuario.setMonedas(usuario.getMonedas() + 15);
                        session.setAttribute("usuarioLogueado", usuario);
                        request.setAttribute("mensajeOK", "¡Libro actualizado correctamente! Has ganado 15 🪙 por tu reseña.");
                    } else {
                        request.setAttribute("mensajeOK", "Libro actualizado correctamente.");
                    }
                }
            }catch (NumberFormatException e) {
                logger.warn("Error de formato al editar libro");
            }
            doGet(request, response);
            return;
        }

        String titulo  = request.getParameter("titulo");
        String autor   = request.getParameter("autor");
        String paginasStr = request.getParameter("paginas");
        String estado  = request.getParameter("estado");
        String portadaUrl = request.getParameter("portadaUrl");
        String genero = request.getParameter("genero");
        String mood = request.getParameter("mood");

        Integer paginas = null;
        try { if (paginasStr != null && !paginasStr.isBlank()) paginas = Integer.valueOf(paginasStr); } catch (NumberFormatException ignored) {}

        Libro libro = new Libro();
        libro.setUsuarioId(usuario.getId());
        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libro.setPaginas(paginas);
        libro.setEstado(estado);
        libro.setPortadaUrl(portadaUrl);
        libro.setGenero(genero);
        libro.setMood(mood);

        if (libroDAO.agregar(libro)) {
            List<Libro> misLibros = libroDAO.buscarPorUsuario(usuario.getId());
            if (!misLibros.isEmpty()) {
                actividadDAO.registrarActividad(usuario.getId(), "NUEVO_LIBRO", misLibros.get(0).getId(), titulo);
            }
            if ("LEIDO".equalsIgnoreCase(estado)) {
                libroDAO.otorgarMonedasPorLibroLeido(usuario.getId());
                usuario.setMonedas(usuario.getMonedas() + 10);
                session.setAttribute("usuarioLogueado", usuario);
            }
        }

        doGet(request, response);
    }
}
