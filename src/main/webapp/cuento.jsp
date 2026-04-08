<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Cuento Comunitario - LibroRank</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="Css/styles.css">
    <style>
        .book-container {
            max-width: 850px;
            margin: 0 auto;
            background: #fff9f0;
            padding: 60px;
            box-shadow: 0 15px 40px rgba(0,0,0,0.3);
            border-radius: 4px;
            color: #2c3e50;
            font-family: 'Georgia', serif;
            line-height: 1.8;
            position: relative;
        }
        .book-container::after {
            content: '';
            position: absolute;
            top: 0; left: 20px; bottom: 0; width: 1px;
            background: rgba(0,0,0,0.05);
            box-shadow: 2px 0 0 rgba(0,0,0,0.02);
        }
        .hoja-meta {
            font-size: 0.75rem;
            color: #7f8c8d;
            margin-bottom: 12px;
            font-family: 'Plus Jakarta Sans', sans-serif;
            text-transform: uppercase;
            letter-spacing: 1.5px;
            font-weight: bold;
        }
        .fragmento {
            margin-bottom: 50px;
            position: relative;
            padding-left: 20px;
        }
        .contenido-texto {
            font-size: 1.15rem;
            text-align: justify;
            color: #1a1a1a;
        }
        .form-nueva-hoja {
            background: #1e1e1e; /* Fondo oscuro sólido para el formulario */
            padding: 40px;
            border-radius: 20px;
            border: 2px solid var(--accent-gold);
            margin-top: 60px;
            box-shadow: var(--shadow);
        }
        .ya-participaste {
            text-align: center;
            padding: 30px;
            background: rgba(212, 175, 55, 0.1);
            color: var(--accent-gold);
            border-radius: 15px;
            margin-top: 40px;
            border: 1px solid rgba(212, 175, 55, 0.2);
            font-weight: bold;
        }
        /* Estilo para que el placeholder se vea bien */
        .form-control::placeholder {
            color: rgba(255, 255, 255, 0.6) !important;
            opacity: 1;
        }
        .form-control {
            background-color: #2c2c2c !important;
            color: white !important;
            border: 1px solid #444 !important;
        }
        .form-control:focus {
            background-color: #333 !important;
            border-color: var(--accent-gold) !important;
            box-shadow: 0 0 0 0.25rem rgba(212, 175, 55, 0.25) !important;
        }
    </style>
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <header class="library-header">
        <div class="container">
            <div class="row align-items-center g-4">
                <div class="col-lg-7">
                    <h1 class="mb-1">Crónica Comunitaria</h1>
                    <p class="text-muted mb-0">Cada lector escribe una hoja. <strong>Forja la historia</strong> junto a la comunidad.</p>
                </div>
                <div class="col-lg-5 text-lg-end">
                    <div class="d-inline-flex align-items-center p-3 rounded-pill" style="background: rgba(212, 175, 55, 0.1); border: 1px solid rgba(212, 175, 55, 0.2);">
                        <i class="bi bi-lightning-charge text-gold me-2"></i>
                        <span class="small text-muted">Gana <strong>30 🪙</strong> por cada hoja publicada</span>
                    </div>
                </div>
            </div>
        </div>
    </header>

    <main class="container py-5">
        <c:if test="${not empty mensajeOk}">
            <div class="alert alert-success bg-dark text-success border-success text-center mb-5 py-3 rounded-4">
                <i class="bi bi-check-circle-fill me-2"></i> ${mensajeOk}
            </div>
        </c:if>

        <div class="book-container mb-5">
            <c:choose>
                <c:when test="${empty fragmentos}">
                    <div class="text-center py-5">
                        <i class="bi bi-feather fs-1 text-dark opacity-25 mb-3"></i>
                        <p class="text-dark fw-bold fs-4" style="color: #1a1a1a !important;">"Érase una vez..."</p>
                        <p class="small text-secondary">Nadie ha empezado la historia aún. Sé tú el primer autor.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="f" items="${fragmentos}">
                        <div class="fragmento">
                            <div class="hoja-meta">
                                <i class="bi bi-file-earmark-text me-1"></i> Hoja #${f.numeroHoja} 
                                <span class="mx-2">—</span> 
                                <i class="bi bi-person me-1"></i> @${f.username}
                            </div>
                            <div class="contenido-texto">${f.contenido}</div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="row justify-content-center">
            <div class="col-lg-8">
                <c:choose>
                    <c:when test="${yaEscribio}">
                        <div class="ya-participaste">
                            <i class="bi bi-stars me-2 fs-4"></i> 
                            Ya has dejado tu marca en esta crónica. ¡Vuelve pronto para leer cómo continúa!
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="form-nueva-hoja">
                            <div class="d-flex align-items-center mb-4">
                                <div class="bg-gold rounded-circle d-flex align-items-center justify-content-center me-3" style="width: 40px; height: 40px; color: black;">
                                    <i class="bi bi-pen-fill"></i>
                                </div>
                                <h3 class="text-gold font-title mb-0">Escribir la Siguiente Hoja</h3>
                            </div>
                            <form action="cuento" method="post">
                                <div class="mb-4">
                                    <label class="form-label text-gold small fw-bold mb-2" style="letter-spacing: 1px;">TU CONTRIBUCIÓN</label>
                                    <textarea name="contenido" rows="8" class="form-control" placeholder="Imagina, crea y continúa el relato..." required style="border-radius: 15px; resize: none;"></textarea>
                                </div>
                                <div class="text-center">
                                    <button type="submit" class="btn btn-gold px-5 py-3 fw-bold">
                                        <i class="bi bi-send-fill me-2"></i>Publicar mi Hoja y ganar 30 🪙
                                    </button>
                                </div>
                            </form>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <jsp:include page="includes/footer.jsp" />
</body>
</html>
