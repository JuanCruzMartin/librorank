<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Diario: ${libro.titulo} - LibroRank</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="Css/styles.css">
    <style>
        .diario-container {
            max-width: 1000px;
            margin: 0 auto;
        }
        .diario-form-card {
            background: var(--bg-card);
            border: 1px solid rgba(212, 175, 55, 0.2);
            border-radius: 20px;
            padding: 2rem;
            height: 100%;
        }
        .timeline-container {
            position: relative;
            padding-left: 20px;
            border-left: 2px solid rgba(212, 175, 55, 0.2);
        }
        .timeline-entry {
            background: rgba(255, 255, 255, 0.03);
            border: 1px solid rgba(255, 255, 255, 0.05);
            border-radius: 15px;
            padding: 1.25rem;
            margin-bottom: 1.5rem;
            position: relative;
            transition: var(--transition);
        }
        .timeline-entry:hover {
            background: rgba(255, 255, 255, 0.05);
            transform: translateX(5px);
        }
        .timeline-entry::before {
            content: '';
            position: absolute;
            left: -27px;
            top: 20px;
            width: 12px;
            height: 12px;
            background: var(--accent-gold);
            border-radius: 50%;
            box-shadow: 0 0 10px var(--accent-gold-glow);
        }
        .cita-card {
            background: linear-gradient(135deg, rgba(212, 175, 55, 0.05), rgba(0, 0, 0, 0.2));
            border: 1px dashed var(--accent-gold);
            border-radius: 15px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
        }
        .capitulo-badge {
            font-size: 0.7rem;
            text-transform: uppercase;
            letter-spacing: 1px;
            color: var(--accent-gold);
            font-weight: 800;
        }
    </style>
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <header class="library-header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-lg-8">
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb mb-2">
                            <li class="breadcrumb-item"><a href="biblioteca" class="text-decoration-none text-gold small">Biblioteca</a></li>
                            <li class="breadcrumb-item active text-muted small" aria-current="page">Diario de lectura</li>
                        </ol>
                    </nav>
                    <h1 class="mb-1">Diario: ${libro.titulo}</h1>
                    <p class="text-muted mb-0">Conversaciones con el autor <strong>${libro.autor}</strong></p>
                </div>
                <div class="col-lg-4 text-lg-end mt-3 mt-lg-0">
                    <a href="biblioteca" class="btn btn-outline-light btn-sm px-4 rounded-pill">
                        <i class="bi bi-arrow-left me-2"></i>Volver a la Biblioteca
                    </a>
                </div>
            </div>
        </div>
    </header>

    <main class="container py-5">
        <div class="diario-container">
            <div class="row g-4">
                <!-- Columna: Formulario de Entrada -->
                <div class="col-lg-6">
                    <div class="diario-form-card">
                        <h3 class="h5 text-gold mb-4"><i class="bi bi-journal-plus me-2"></i>Nueva Entrada</h3>
                        <form action="diario" method="post">
                            <input type="hidden" name="idLibro" value="${libro.id}">
                            <div class="mb-3">
                                <label class="form-label text-muted small fw-bold">CAPÍTULO / PROGRESO</label>
                                <input type="text" name="capitulo" class="form-control" placeholder="Ej: Capítulo 5 o Página 120" required>
                            </div>
                            <div class="mb-4">
                                <label class="form-label text-muted small fw-bold">TUS REFLEXIONES</label>
                                <textarea name="comentario" class="form-control" rows="4" placeholder="¿Qué está pasando? ¿Qué te hizo sentir?" required></textarea>
                            </div>
                            <button type="submit" class="btn btn-gold w-100">
                                <i class="bi bi-check2-circle me-2"></i>Guardar en el Diario
                            </button>
                        </form>
                    </div>
                </div>

                <!-- Columna: Formulario de Cita -->
                <div class="col-lg-6">
                    <div class="diario-form-card" style="border-color: rgba(255,255,255,0.1);">
                        <h3 class="h5 text-white mb-4"><i class="bi bi-quote me-2"></i>Inmortalizar una Cita</h3>
                        <form action="diario" method="post">
                            <input type="hidden" name="accion" value="cita">
                            <input type="hidden" name="idLibro" value="${libro.id}">
                            <div class="mb-3">
                                <label class="form-label text-muted small fw-bold">LA FRASE</label>
                                <textarea name="texto" class="form-control" rows="3" placeholder="Esa frase que merece ser recordada..." required></textarea>
                            </div>
                            <div class="mb-4">
                                <label class="form-label text-muted small fw-bold">PÁGINA (OPCIONAL)</label>
                                <input type="text" name="pagina" class="form-control" placeholder="Ej: Pág. 42">
                            </div>
                            <button type="submit" class="btn btn-outline-light w-100">
                                <i class="bi bi-bookmark-star me-2"></i>Guardar Cita
                            </button>
                        </form>
                    </div>
                </div>
            </div>

            <div class="row mt-5 pt-4">
                <!-- Línea de Tiempo -->
                <div class="col-lg-7">
                    <h2 class="h4 text-white mb-4 font-title">Timeline de Lectura</h2>
                    <c:choose>
                        <c:when test="${empty entradas}">
                            <div class="text-center py-5 opacity-50">
                                <i class="bi bi-chat-dots display-4 d-block mb-3"></i>
                                <p>Tu diario está esperando tu primera reflexión.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="timeline-container ms-3">
                                <c:forEach var="e" items="${entradas}">
                                    <div class="timeline-entry">
                                        <div class="d-flex justify-content-between align-items-center mb-2">
                                            <span class="capitulo-badge">${e.capitulo}</span>
                                            <span class="text-muted small">
                                                <i class="bi bi-clock me-1"></i>
                                                <c:out value="${e.fechaCreacion.toLocalDate()}" />
                                            </span>
                                        </div>
                                        <div class="text-white opacity-75">${e.comentario}</div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Citas Destacadas -->
                <div class="col-lg-5">
                    <h2 class="h4 text-white mb-4 font-title">Citas de Oro</h2>
                    <c:choose>
                        <c:when test="${empty citas}">
                            <div class="text-center py-5 opacity-50">
                                <i class="bi bi-star display-4 d-block mb-3"></i>
                                <p>No hay citas guardadas aún.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="c" items="${citas}">
                                <div class="cita-card">
                                    <i class="bi bi-quote fs-2 text-gold opacity-25 d-block mb-2"></i>
                                    <p class="text-white mb-3" style="font-style: italic; font-size: 1.1rem; line-height: 1.4;">
                                        ${c.texto}
                                    </p>
                                    <div class="d-flex justify-content-end align-items-center">
                                        <span class="badge rounded-pill bg-dark text-gold border border-gold px-3">
                                            <i class="bi bi-hash me-1"></i>${not empty c.pagina ? c.pagina : 'S/P'}
                                        </span>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </main>

    <jsp:include page="includes/footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
