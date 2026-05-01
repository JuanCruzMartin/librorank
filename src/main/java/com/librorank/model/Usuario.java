package com.librorank.model;

/**
 * Clase Modelo (Entidad).
 * Representa una fila exacta de la tabla "usuarios" en tu base de datos.
 */
public class Usuario {

    // Atributos que coinciden con las columnas de tu BD MySQL
    private int id;
    private String nombre;
    private String username;
    private String email;
    private String passwordHash; // ¡Ojo! Aquí guardamos la contraseña encriptada, nunca en texto plano.
    private String bio;
    private String avatarUrl;
    private Integer nivelId;       // Usamos Integer (objeto) en vez de int (primitivo) para permitir nulos (null).
    private Integer objetivoAnual; // Lo mismo aquí, el usuario puede no tener objetivo definido.

    // Campo calculado: No existe necesariamente en la tabla 'usuarios',
    // pero lo llenamos con consultas SQL (como en el Ranking).
    private Integer totalLibrosLeidos;

    // Nuevo campo agregado recientemente
    private String generosFavoritos;

    private int puntos; // Puntos virtuales de la app.

    private int rachaActual;
    private java.time.LocalDate ultimaFechaLectura;

    // Campo para mostrar afinidad en búsquedas/sugerencias
    private int librosEnComun;
    private String listaTitulosEnComun;

    // 1. Constructor Vacío
    // Necesario para que herramientas como JSP o Frameworks puedan crear una instancia sin datos iniciales.
    public Usuario() {
    }

    // 2. Constructor para REGISTRO (Sign Up)
    // Solo pide los datos obligatorios para crear una cuenta nueva.
    public Usuario(String nombre, String username, String email, String passwordHash) {
        this.nombre = nombre;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;

        // Inicializamos valores por defecto para que no queden en null
        this.bio = "";
        this.avatarUrl = "";
        this.nivelId = 1;        // Por defecto todos empiezan en Nivel 1
        this.objetivoAnual = 0;
        this.puntos = 0;
        this.rachaActual = 0;
    }

    // 3. Constructor COMPLETO
    // Se usa cuando traemos los datos DE la base de datos HACIA Java (ej: al hacer login o ver perfil).
    public Usuario(int id, String nombre, String username, String email, String passwordHash, String bio, String avatarUrl, Integer nivelId, Integer objetivoAnual, int puntos, int rachaActual, java.time.LocalDate ultimaFechaLectura) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.nivelId = nivelId;
        this.objetivoAnual = objetivoAnual;
        this.puntos = puntos;
        this.rachaActual = rachaActual;
        this.ultimaFechaLectura = ultimaFechaLectura;
    }

    // --- GETTERS Y SETTERS ---
    // Métodos para leer (get) y escribir (set) los datos privados de arriba.

    public int getRachaActual() { return rachaActual; }
    public void setRachaActual(int rachaActual) { this.rachaActual = rachaActual; }

    public java.time.LocalDate getUltimaFechaLectura() { return ultimaFechaLectura; }
    public void setUltimaFechaLectura(java.time.LocalDate ultimaFechaLectura) { this.ultimaFechaLectura = ultimaFechaLectura; }

    public String getGenerosFavoritos() { return generosFavoritos; }
    public void setGenerosFavoritos(String generosFavoritos) { this.generosFavoritos = generosFavoritos; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Integer getNivelId() { return nivelId; }
    public void setNivelId(Integer nivelId) { this.nivelId = nivelId; }

    public Integer getObjetivoAnual() { return objetivoAnual; }
    public void setObjetivoAnual(Integer objetivoAnual) { this.objetivoAnual = objetivoAnual; }

    public Integer getTotalLibrosLeidos() { return totalLibrosLeidos; }
    public void setTotalLibrosLeidos(Integer totalLibrosLeidos) { this.totalLibrosLeidos = totalLibrosLeidos; }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    public int getLibrosEnComun() { return librosEnComun; }
    public void setLibrosEnComun(int librosEnComun) { this.librosEnComun = librosEnComun; }

    public String getListaTitulosEnComun() { return listaTitulosEnComun; }
    public void setListaTitulosEnComun(String listaTitulosEnComun) { this.listaTitulosEnComun = listaTitulosEnComun; }

    /**
     * MÉTODO CALCULADO PARA JSTL
     * Progresión épica de títulos según la cantidad de libros leídos.
     */
    public String getTituloLector() {
        int leidos = (this.totalLibrosLeidos != null) ? this.totalLibrosLeidos : 0;

        if (leidos < 5)   return "Novato del Papel";
        if (leidos < 10)  return "Iniciado de la Tinta";
        if (leidos < 20)  return "Lector de Bronce";
        if (leidos < 30)  return "Lector de Plata";
        if (leidos < 40)  return "Lector de Oro";
        if (leidos < 50)  return "Lector de Platino";
        if (leidos < 75)  return "Lector de Esmeralda";
        if (leidos < 100) return "Lector de Diamante";
        if (leidos < 150) return "Erudito Imperial";
        if (leidos < 200) return "Bibliotecario Real";
        if (leidos < 300) return "Sabio Ancestral";
        if (leidos < 500) return "Maestro de Historias";
        
        return "Leyenda Literaria Universal";
    }

}