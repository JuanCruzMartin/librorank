package com.librorank.web;

import com.librorank.dao.UsuarioDAO;
import com.librorank.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@WebServlet("/subirFoto")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 15    // 15MB
)
public class SubirFotoServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            Part filePart = request.getPart("fotoPerfil");
            if (filePart != null && filePart.getSize() > 0) {
                // Obtener nombre de archivo y extensión
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String extension = fileName.substring(fileName.lastIndexOf("."));
                
                // Nombre único: usuario_ID_UUID.ext
                String uniqueFileName = "user_" + usuario.getId() + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
                
                // Ruta real en el servidor
                String uploadPath = getServletContext().getRealPath("/") + "img" + File.separator + "usuarios";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();

                filePart.write(uploadPath + File.separator + uniqueFileName);
                
                // Guardar ruta relativa en BD
                String relativePath = "img/usuarios/" + uniqueFileName;
                boolean ok = usuarioDAO.actualizarAvatarActual(usuario.getId(), relativePath);
                
                if (ok) {
                    usuario.setAvatarUrl(relativePath);
                    session.setAttribute("usuarioLogueado", usuario);
                    session.setAttribute("mensajeOK", "¡Foto de perfil actualizada con éxito!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("perfil");
    }
}
