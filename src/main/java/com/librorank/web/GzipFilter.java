package com.librorank.web;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

/**
 * Filtro que comprime las respuestas del servidor usando GZIP para ahorrar ancho de banda
 * y mejorar la velocidad de carga.
 */
@WebFilter(urlPatterns = {"*.jsp", "*.js", "*.css", "/biblioteca", "/ranking", "/perfil", "/home", "/diario"})
public class GzipFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String acceptEncoding = httpRequest.getHeader("Accept-Encoding");

        if (acceptEncoding != null && acceptEncoding.contains("gzip")) {
            GZipServletResponseWrapper gzipResponse = new GZipServletResponseWrapper(httpResponse);
            chain.doFilter(request, gzipResponse);
            gzipResponse.close();
        } else {
            chain.doFilter(request, response);
        }
    }

    private static class GZipServletResponseWrapper extends HttpServletResponseWrapper {
        private GZipServletOutputStream gzipOutputStream = null;
        private PrintWriter printWriter = null;

        public GZipServletResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        public void close() throws IOException {
            if (this.printWriter != null) {
                this.printWriter.close();
            }
            if (this.gzipOutputStream != null) {
                this.gzipOutputStream.close();
            }
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (this.printWriter != null) {
                throw new IllegalStateException("PrintWriter already obtained");
            }
            if (this.gzipOutputStream == null) {
                this.gzipOutputStream = new GZipServletOutputStream(getResponse().getOutputStream());
            }
            return this.gzipOutputStream;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (this.printWriter == null && this.gzipOutputStream != null) {
                throw new IllegalStateException("OutputStream already obtained");
            }
            if (this.printWriter == null) {
                this.gzipOutputStream = new GZipServletOutputStream(getResponse().getOutputStream());
                this.printWriter = new PrintWriter(new OutputStreamWriter(this.gzipOutputStream, getResponse().getCharacterEncoding()));
            }
            return this.printWriter;
        }

        @Override
        public void setContentLength(int len) {
            // No establecemos la longitud porque cambia al comprimir
        }
    }

    private static class GZipServletOutputStream extends ServletOutputStream {
        private GZIPOutputStream gzipStream;

        public GZipServletOutputStream(ServletOutputStream output) throws IOException {
            this.gzipStream = new GZIPOutputStream(output);
        }

        @Override
        public void close() throws IOException {
            this.gzipStream.close();
        }

        @Override
        public void flush() throws IOException {
            this.gzipStream.flush();
        }

        @Override
        public void write(int b) throws IOException {
            this.gzipStream.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            this.gzipStream.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            this.gzipStream.write(b, off, len);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
        }
    }
}
