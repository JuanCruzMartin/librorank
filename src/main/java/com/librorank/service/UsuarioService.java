package com.librorank.service;

import com.librorank.dao.UsuarioDAO;
import com.librorank.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Autentica a un usuario y maneja la migración a BCrypt si es necesario.
     */
    public Usuario login(String identificador, String password) {
        Usuario usuario = usuarioDAO.buscarPorEmailOUsername(identificador);

        if (usuario == null) {
            return null;
        }

        String storedHash = usuario.getPasswordHash();
        boolean matches = false;

        if (isBCrypt(storedHash)) {
            try {
                matches = BCrypt.checkpw(password, storedHash);
            } catch (IllegalArgumentException e) {
                logger.error("Error validando hash de BCrypt para {}", identificador, e);
            }
        } else {
            // Migración desde texto plano
            matches = password.equals(storedHash);
            if (matches) {
                String newHash = BCrypt.hashpw(password, BCrypt.gensalt());
                usuarioDAO.actualizarPasswordHash(usuario.getId(), newHash);
                usuario.setPasswordHash(newHash); // Actualizar en el objeto actual
                logger.info("Usuario {} migrado exitosamente a BCrypt", identificador);
            }
        }

        return matches ? usuario : null;
    }

    public boolean registrar(Usuario usuario, String plainPassword) {
        // Verificar si ya existe
        if (usuarioDAO.buscarPorEmailOUsername(usuario.getEmail()) != null ||
            usuarioDAO.buscarPorEmailOUsername(usuario.getUsername()) != null) {
            logger.warn("Intento de registro con email o username ya existente: {} / {}", 
                        usuario.getEmail(), usuario.getUsername());
            return false;
        }

        // Hash the password
        String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        usuario.setPasswordHash(hashed);
        
        // Valores por defecto para nuevos usuarios
        if (usuario.getBio() == null) usuario.setBio("");
        if (usuario.getAvatarUrl() == null) usuario.setAvatarUrl("avatar_explorador");
        if (usuario.getNivelId() == null) usuario.setNivelId(1);
        if (usuario.getPuntos() == 0) usuario.setPuntos(0);
        if (usuario.getObjetivoAnual() == null) usuario.setObjetivoAnual(0);
        if (usuario.getRachaActual() == 0) usuario.setRachaActual(0);

        return usuarioDAO.registrar(usuario);
    }

    private boolean isBCrypt(String hash) {
        return hash != null && (hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"));
    }

    public List<Usuario> getTopLectores(int limite) {
        return usuarioDAO.obtenerRankingLectores(limite);
    }
    
    public Usuario getUsuarioById(int id) {
        return usuarioDAO.buscarPorId(id);
    }
}
