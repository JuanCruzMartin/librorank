package com.librorank.web;

import com.librorank.dao.UsuarioDAO;
import com.librorank.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/seleccionarAvatar")
public class SeleccionarAvatarServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String folder = request.getParameter("folder");
        if (folder != null && !folder.isBlank()) {
            // Aquí podrías agregar una validación de nivel extra antes de guardar
            boolean ok = usuarioDAO.actualizarAvatarActual(usuario.getId(), folder);
            if (ok) {
                usuario.setAvatarUrl(folder); // Actualizamos el objeto en sesión
                session.setAttribute("usuarioLogueado", usuario);
            }
        }

        response.sendRedirect(request.getContextPath() + "/habitacion");
    }
}
