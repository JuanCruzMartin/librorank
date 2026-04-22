package com.librorank.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Servlet que actúa como puente para la API de Google Books.
 * Esto oculta la API Key del lado del cliente y permite manejar cuotas desde el servidor.
 */
@WebServlet("/api/buscar-libro")
public class BusquedaLibroServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(BusquedaLibroServlet.class);
    private String apiKey;

    @Override
    public void init() throws ServletException {
        // Intentar obtener la API Key de variables de entorno (Render) o config.properties (Local)
        apiKey = System.getenv("GOOGLE_BOOKS_API_KEY");
        
        if (apiKey == null || apiKey.isBlank()) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                Properties props = new Properties();
                if (is != null) {
                    props.load(is);
                    apiKey = props.getProperty("google.books.api.key");
                }
            } catch (IOException e) {
                logger.error("No se pudo cargar la API Key de Google Books", e);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String query = request.getParameter("q");
        if (query == null || query.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro de búsqueda");
            return;
        }

        // Construir la URL de Google Books con la API Key
        String urlString = "https://www.googleapis.com/books/v1/volumes?q=" 
                + URLEncoder.encode(query, StandardCharsets.UTF_8) 
                + "&maxResults=9&printType=books";
        
        if (apiKey != null && !apiKey.isBlank()) {
            urlString += "&key=" + apiKey;
        }

        logger.debug("Consultando Google Books API para: {}", query);

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int status = conn.getResponseCode();
            
            // Reenviar el tipo de contenido y el estado
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(status);

            InputStream is = (status < 400) ? conn.getInputStream() : conn.getErrorStream();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.getWriter().write(line);
                }
            }
            conn.disconnect();

        } catch (Exception e) {
            logger.error("Error conectando con Google Books API", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error en el servidor de búsqueda");
        }
    }
}
