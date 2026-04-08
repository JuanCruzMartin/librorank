<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Retos con Amigos - LibroRank</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="Css/styles.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <header class="library-header">
        <div class="container">
            <div class="row align-items-center g-4">
                <div class="col-lg-6">
                    <h1 class="mb-1">Retos con Amigos</h1>
                    <p class="text-muted mb-0">Compite o colabora en <strong>lecturas conjuntas</strong>.</p>
                </div>
                <div class="col-lg-6 text-lg-end">
                    <button class="btn btn-gold px-4" data-bs-toggle="modal" data-bs-target="#modalCrearReto">
                        <i class="bi bi-plus-circle me-2"></i>Crear Nuevo Reto
                    </button>
                </div>
            </div>
        </div>
    </header>

    <main class="container py-5">
        <div class="row g-4">
            <c:choose>
                <c:when test="${empty retos}">
                    <div class="col-12 text-center py-5">
                        <i class="bi bi-shield-slash display-1 text-muted"></i>
                        <p class="mt-3 text-muted">No hay retos activos. ¡Sé el primero en crear uno!</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="r" items="${retos}">
                        <div class="col-md-6">
                            <div class="card p-4 h-100 mb-0">
                                <div class="d-flex justify-content-between align-items-start mb-3">
                                    <div>
                                        <h3 class="h5 text-white mb-1 fw-bold">${r.nombreReto}</h3>
                                        <p class="text-muted small mb-0">Liderado por <span class="text-gold">@${r.creadorUsername}</span></p>
                                    </div>
                                    <span class="badge-cozy" style="background: rgba(255, 255, 255, 0.05); color: var(--text-muted);">
                                        <i class="bi bi-calendar-event me-1"></i> ${r.fechaFin}
                                    </span>
                                </div>
                                
                                <c:if test="${not empty r.tituloLibro}">
                                    <div class="p-3 mb-4 rounded d-flex align-items-center" style="background: rgba(212, 175, 55, 0.05); border: 1px dashed rgba(212, 175, 55, 0.3);">
                                        <i class="bi bi-book text-gold fs-4 me-3"></i>
                                        <div>
                                            <div class="small text-muted text-uppercase fw-bold" style="font-size: 0.65rem; letter-spacing: 1px;">Libro Objetivo</div>
                                            <div class="text-white fw-bold">${r.tituloLibro}</div>
                                        </div>
                                    </div>
                                </c:if>

                                <div class="participantes mt-auto">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <label class="small text-gold fw-bold">PROGRESO DEL GRUPO</label>
                                        <span class="small text-muted">${r.participantes.size()} participantes</span>
                                    </div>
                                    <c:forEach var="p" items="${r.participantes}">
                                        <div class="mb-3">
                                            <div class="d-flex justify-content-between small mb-1">
                                                <span class="text-muted">@${p.username}</span>
                                                <span class="text-gold fw-bold">${p.progreso}%</span>
                                            </div>
                                            <div class="progress" style="height: 6px; background: rgba(255,255,255,0.05); border-radius: 10px;">
                                                <div class="progress-bar" role="progressbar" style="width: ${p.progreso}%; background: linear-gradient(to right, #d4af37, #f1c40f); border-radius: 10px;"></div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>

                                <div class="actions mt-4 d-flex gap-2">
                                    <c:set var="estaEnReto" value="false" />
                                    <c:forEach var="p" items="${r.participantes}">
                                        <c:if test="${p.usuarioId == usuarioLogueado.id}">
                                            <c:set var="estaEnReto" value="true" />
                                        </c:if>
                                    </c:forEach>

                                    <c:choose>
                                        <c:when test="${estaEnReto}">
                                            <button class="btn btn-sm btn-outline-light w-100 py-2" onclick="abrirModalProgreso('${r.id}', '${r.nombreReto}')">
                                                <i class="bi bi-pencil-square me-2"></i>Actualizar mi Avance
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <form action="retos" method="post" class="w-100">
                                                <input type="hidden" name="accion" value="unirse">
                                                <input type="hidden" name="retoId" value="${r.id}">
                                                <button type="submit" class="btn btn-sm btn-gold w-100 py-2">
                                                    <i class="bi bi-person-plus me-2"></i>Unirme al Reto
                                                </button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <!-- Modales para Retos -->
    <div class="modal fade" id="modalCrearReto" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content" style="background: var(--bg-card); border: 1px solid var(--accent-gold); border-radius: 20px;">
                <div class="modal-header border-0">
                    <h5 class="modal-title font-title" style="color: var(--accent-gold);">Nuevo Reto Colectivo</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form action="retos" method="post">
                    <input type="hidden" name="accion" value="crear">
                    <div class="modal-body py-4">
                        <div class="mb-3">
                            <label class="form-label text-gold small fw-bold">NOMBRE DEL RETO</label>
                            <input type="text" name="nombre" class="form-control" placeholder="Ej: Maratón de Fantasía" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-gold small fw-bold">LIBRO OBJETIVO (OPCIONAL)</label>
                            <select name="libroId" class="form-select">
                                <option value="">Cualquier libro / Reto libre</option>
                                <c:forEach var="l" items="${misLibros}">
                                    <option value="${l.id}">${l.titulo}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-gold small fw-bold">FECHA LÍMITE</label>
                            <input type="date" name="fechaFin" class="form-control" required>
                        </div>
                    </div>
                    <div class="modal-footer border-0 pt-0">
                        <button type="button" class="btn btn-outline-secondary btn-sm" data-bs-dismiss="modal">Cerrar</button>
                        <button type="submit" class="btn btn-gold btn-sm px-4">Lanzar Reto</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Modal Progreso -->
    <div class="modal fade" id="modalProgreso" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content" style="background: var(--bg-card); border: 1px solid var(--accent-gold); border-radius: 20px;">
                <form action="retos" method="post">
                    <input type="hidden" name="accion" value="actualizar">
                    <input type="hidden" name="retoId" id="progresoRetoId">
                    <div class="modal-body text-center py-5">
                        <h5 class="text-gold font-title h4 mb-4" id="progresoRetoNombre"></h5>
                        <p class="text-muted mb-4">¿Cuánto has avanzado desde la última vez?</p>
                        <input type="range" name="progreso" class="form-range" min="0" max="100" id="rangeProgreso" oninput="valProgreso.innerText = this.value + '%'">
                        <div class="display-4 fw-bold text-gold mt-3" id="valProgreso">50%</div>
                    </div>
                    <div class="modal-footer border-0 pt-0">
                        <button type="button" class="btn btn-outline-secondary btn-sm" data-bs-dismiss="modal">Volver</button>
                        <button type="submit" class="btn btn-gold btn-sm px-4">Guardar Avance</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function abrirModalProgreso(id, nombre) {
            document.getElementById('progresoRetoId').value = id;
            document.getElementById('progresoRetoNombre').innerText = nombre;
            new bootstrap.Modal(document.getElementById('modalProgreso')).show();
        }
    </script>
    <jsp:include page="includes/footer.jsp" />
</body>
</html>
