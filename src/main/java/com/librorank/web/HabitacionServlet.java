package com.librorank.web;

import com.librorank.dao.InventarioDAO;
import com.librorank.dao.LibroDAO;
import com.librorank.dao.UsuarioDAO;
import com.librorank.model.ItemInventario;
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

@WebServlet("/habitacion")
public class HabitacionServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(HabitacionServlet.class);
    private final InventarioDAO inventarioDAO = new InventarioDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        // SINCRONIZAR DATOS EN TIEMPO REAL
        int librosLeidos = libroDAO.contarLeidosTotalPorUsuario(usuario.getId());
        usuario.setTotalLibrosLeidos(librosLeidos);
        
        // También actualizamos monedas por si acaso
        Usuario usuarioActualizado = usuarioDAO.buscarPorId(usuario.getId());
        if (usuarioActualizado != null) {
            usuario.setMonedas(usuarioActualizado.getMonedas());
            usuario.setAvatarUrl(usuarioActualizado.getAvatarUrl());
        }
        
        session.setAttribute("usuarioLogueado", usuario);

        List<ItemInventario> todoElInventario = inventarioDAO.obtenerInventarioUsuario(usuario.getId());

        List<ItemInventario> mueblesEnCuarto = new ArrayList<>();
        List<ItemInventario> mueblesEnInventario = new ArrayList<>();

        for (ItemInventario mueble : todoElInventario) {
            if (mueble.isEnUso()) {
                mueblesEnCuarto.add(mueble);
            } else {
                mueblesEnInventario.add(mueble);
            }
        }

        request.setAttribute("mueblesEnCuarto", mueblesEnCuarto);
        request.setAttribute("mueblesEnInventario", mueblesEnInventario);

        logger.debug("Habitación cargada para usuario ID {}: {} muebles en cuarto, {} en inventario", 
                usuario.getId(), mueblesEnCuarto.size(), mueblesEnInventario.size());
        
        request.getRequestDispatcher("habitacion.jsp").forward(request, response);
    }
}
