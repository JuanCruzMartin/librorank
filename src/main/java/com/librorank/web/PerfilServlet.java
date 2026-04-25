package com.librorank.web;

import com.librorank.dao.LibroDAO;
import com.librorank.dao.UsuarioDAO;
import com.librorank.model.Libro;
import com.librorank.model.Logro;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/perfil")
public class PerfilServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(PerfilServlet.class);
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final LibroDAO libroDAO = new LibroDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioLogueado");

        // Determinar qué perfil mostrar
        String idParam = request.getParameter("id");
        int idPerfil;
        boolean esMiPerfil = true;

        if (idParam != null && !idParam.isEmpty()) {
            try {
                idPerfil = Integer.parseInt(idParam);
                if (idPerfil != usuarioSesion.getId()) {
                    esMiPerfil = false;
                }
            } catch (NumberFormatException e) {
                idPerfil = usuarioSesion.getId();
            }
        } else {
            idPerfil = usuarioSesion.getId();
        }

        // Buscar los datos del usuario a mostrar
        Usuario usuarioMostrado = usuarioDAO.buscarPorId(idPerfil);

        if (usuarioMostrado == null) {
            response.sendRedirect(request.getContextPath() + "/perfil");
            return;
        }

        // Si es MI perfil, actualizo el objeto en sesión por si cambió algo
        if (esMiPerfil) {
            session.setAttribute("usuarioLogueado", usuarioMostrado);
        }

        int leidosTotal = Math.max(libroDAO.contarLeidosTotalPorUsuario(usuarioMostrado.getId()), 0);
        int leidosEsteAnio = Math.max(libroDAO.contarLeidosEsteAnioPorUsuario(usuarioMostrado.getId()), 0);

        usuarioMostrado.setTotalLibrosLeidos(leidosTotal);

        int objetivoAnual = (usuarioMostrado.getObjetivoAnual() != null && usuarioMostrado.getObjetivoAnual() > 0)
                ? usuarioMostrado.getObjetivoAnual() : 10;

        int porcentaje = (int) Math.round(leidosEsteAnio * 100.0 / objetivoAnual);
        porcentaje = Math.min(porcentaje, 100);

        int restantesObjetivo = Math.max(objetivoAnual - leidosEsteAnio, 0);

        // Gamificación logic
        int proximaMetaNivel = ((leidosTotal / 10) + 1) * 10;
        int librosParaSiguienteTitulo = proximaMetaNivel - leidosTotal;

        Usuario usuarioDelFuturo = new Usuario();
        usuarioDelFuturo.setTotalLibrosLeidos(proximaMetaNivel);
        String proximoTitulo = usuarioDelFuturo.getTituloLector();

        request.setAttribute("usuarioMostrado", usuarioMostrado);
        request.setAttribute("esMiPerfil", esMiPerfil);
        request.setAttribute("porcentaje", porcentaje);
        request.setAttribute("leidosTotal", leidosTotal);
        request.setAttribute("leidosEsteAnio", leidosEsteAnio);
        request.setAttribute("objetivoAnual", objetivoAnual);
        request.setAttribute("restantesObjetivo", restantesObjetivo);
        request.setAttribute("tituloNivel", usuarioMostrado.getTituloLector());
        request.setAttribute("nivel", (leidosTotal / 5) + 1);
        request.setAttribute("librosParaSiguienteTitulo", librosParaSiguienteTitulo);
        request.setAttribute("proximoTitulo", proximoTitulo);

        List<Libro> ultimasLecturas = libroDAO.obtenerUltimasLecturas(usuarioMostrado.getId(), 3);
        request.setAttribute("ultimasLecturas", ultimasLecturas);

        // Cargar todos los libros para la pestaña de biblioteca en el perfil
        List<Libro> todosLosLibros = libroDAO.obtenerLibrosPorUsuario(usuarioMostrado.getId());
        request.setAttribute("todosLosLibros", todosLosLibros);

        // Achievements
        List<Logro> listaLogros = new ArrayList<>();
        listaLogros.add(new Logro("El despertar", "Leíste tu primer libro", "🌱", leidosTotal >= 1));
        listaLogros.add(new Logro("Rata de Biblioteca", "Alcanzaste los 5 libros", "🐭", leidosTotal >= 5));
        listaLogros.add(new Logro("Devorador", "¡10 libros! Esto se pone serio", "🔥", leidosTotal >= 10));
        listaLogros.add(new Logro("Erudito", "Una mente brillante (20 libros)", "🧠", leidosTotal >= 20));
        listaLogros.add(new Logro("Coleccionista de Mundos", "Has visitado 35 mundos distintos", "🌍", leidosTotal >= 35));
        listaLogros.add(new Logro("Misión Cumplida", "Alcanzaste tu objetivo anual", "🎯", leidosEsteAnio >= objetivoAnual));
        listaLogros.add(new Logro("Caballero de la Lectura", "50 libros. Una gesta heroica", "🛡️", leidosTotal >= 50));
        listaLogros.add(new Logro("Centurión Literario", "¡100 libros leídos! Increíble", "💯", leidosTotal >= 100));
        listaLogros.add(new Logro("Arquitecto de Sueños", "150 libros. Has construido un legado", "🏛️", leidosTotal >= 150));
        listaLogros.add(new Logro("Guardián de Sabiduría", "200 libros. Eres una fuente de conocimiento", "🦉", leidosTotal >= 200));
        listaLogros.add(new Logro("Trascendencia", "300 libros. Tu mente ha trascendido", "🌌", leidosTotal >= 300));
        listaLogros.add(new Logro("Inmortalidad", "500 libros. Tu nombre será eterno", "♾️", leidosTotal >= 500));

        request.setAttribute("listaLogros", listaLogros);
        request.getRequestDispatcher("/perfil.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioLogueado");

        String nombre = request.getParameter("nombre");
        String username = request.getParameter("usuario");
        String email = request.getParameter("email");
        String bio = request.getParameter("bio");
        String generos = request.getParameter("generos");
        String objetivoStr = request.getParameter("objetivo");

        if (nombre != null && !nombre.isBlank()) usuarioSesion.setNombre(nombre);
        if (username != null && !username.isBlank()) usuarioSesion.setUsername(username);
        if (email != null && !email.isBlank()) usuarioSesion.setEmail(email);

        usuarioSesion.setBio(bio != null ? bio : "");
        usuarioSesion.setGenerosFavoritos(generos != null ? generos : "");

        if (objetivoStr != null && !objetivoStr.isBlank()) {
            try {
                usuarioSesion.setObjetivoAnual(Integer.parseInt(objetivoStr));
            } catch (NumberFormatException ignored) {}
        }

        boolean actualizado = usuarioDAO.actualizarPerfilBasico(usuarioSesion);

        if (actualizado) {
            logger.info("Usuario ID {} actualizó su perfil", usuarioSesion.getId());
            session.setAttribute("usuarioLogueado", usuarioSesion);
            session.setAttribute("mensajeOk", "¡Perfil actualizado correctamente!");
        } else {
            logger.warn("Fallo al actualizar perfil para usuario ID {}", usuarioSesion.getId());
            session.setAttribute("mensajeError", "No se pudo guardar. Quizás el usuario o email ya existen.");
        }

        response.sendRedirect(request.getContextPath() + "/perfil");
    }
}
