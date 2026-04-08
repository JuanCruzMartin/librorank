<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Bingo Literario - LibroRank</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="Css/styles.css">
    <style>
        .bingo-grid {
            display: grid;
            grid-template-columns: repeat(5, 1fr);
            gap: 15px;
            max-width: 900px;
            margin: 0 auto;
        }
        .bingo-square {
            aspect-ratio: 1;
            background: var(--bg-card);
            border: 1px solid rgba(212, 175, 55, 0.2);
            border-radius: 12px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            text-align: center;
            padding: 15px;
            font-size: 0.85rem;
            cursor: pointer;
            transition: var(--transition);
            color: var(--text-muted);
        }
        .bingo-square:hover { 
            transform: translateY(-5px); 
            border-color: var(--accent-gold);
            background: rgba(212, 175, 55, 0.05);
            color: #fff;
        }
        .bingo-square.completed {
            background: rgba(212, 175, 55, 0.15);
            border-color: var(--accent-gold);
            color: #fff;
            cursor: default;
        }
        .bingo-square i { font-size: 1.8rem; color: var(--accent-gold); margin-bottom: 10px; }
        .bingo-square.completed i { color: var(--accent-gold); text-shadow: 0 0 10px var(--accent-gold-glow); }
    </style>
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <header class="library-header">
        <div class="container">
            <div class="row align-items-center g-4">
                <div class="col-lg-6">
                    <h1 class="mb-1">Bingo Literario</h1>
                    <p class="text-muted mb-0">Completa desafíos de lectura y gana <strong>recompensas épicas</strong>.</p>
                </div>
                <div class="col-lg-6 text-lg-end">
                    <div class="d-inline-flex align-items-center p-3 rounded-pill" style="background: rgba(212, 175, 55, 0.1); border: 1px solid rgba(212, 175, 55, 0.2);">
                        <i class="bi bi-info-circle text-gold me-2"></i>
                        <span class="small text-muted">Haz clic en una casilla para marcar tu progreso</span>
                    </div>
                </div>
            </div>
        </div>
    </header>

    <main class="container py-5">
        <div class="bingo-grid">
            <c:forEach var="c" items="${bingo}">
                <div class="bingo-square ${c.completado ? 'completed' : ''}" 
                     <c:if test="${!c.completado}">onclick="abrirModalBingo('${c.id}', '${c.tituloReto}')"</c:if>>
                    <c:choose>
                        <c:when test="${c.completado}">
                            <i class="bi bi-check-circle-fill"></i>
                        </c:when>
                        <c:otherwise>
                            <i class="bi bi-app"></i>
                        </c:otherwise>
                    </c:choose>
                    <span class="fw-bold">${c.tituloReto}</span>
                </div>
            </c:forEach>
        </div>
    </main>

    <!-- Modal para marcar casilla -->
    <div class="modal fade" id="modalBingo" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content" style="background: var(--bg-card); border: 1px solid var(--accent-gold); border-radius: 20px;">
                <div class="modal-header border-0 pb-0">
                    <h5 class="modal-title font-title" style="color: var(--accent-gold);">Completar Reto</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form action="bingo" method="post">
                    <div class="modal-body py-4">
                        <input type="hidden" name="retoId" id="modalRetoId">
                        <p class="text-muted mb-4">Vas a completar el reto:<br><strong id="modalTituloReto" class="text-white h4"></strong></p>
                        
                        <div class="mb-3">
                            <label class="form-label text-gold small fw-bold">LIBRO UTILIZADO</label>
                            <select name="libroId" class="form-select" required>
                                <option value="" disabled selected>Selecciona un libro de tu biblioteca...</option>
                                <c:forEach var="l" items="${misLibros}">
                                    <option value="${l.id}">${l.titulo}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer border-0 pt-0">
                        <button type="button" class="btn btn-outline-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-gold btn-sm px-4">Marcar como hecho</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function abrirModalBingo(id, titulo) {
            document.getElementById('modalRetoId').value = id;
            document.getElementById('modalTituloReto').innerText = titulo;
            new bootstrap.Modal(document.getElementById('modalBingo')).show();
        }
    </script>
    <jsp:include page="includes/footer.jsp" />
</body>
</html>
