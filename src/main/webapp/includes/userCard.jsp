<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- 
    Fragmento reutilizable para tarjetas de usuario.
    Parámetros esperados:
    - user: El objeto Usuario a mostrar.
    - esAmigo: booleano (opcional) para mostrar botón eliminar.
    - esSugerencia: booleano (opcional) para mostrar afinidad expandible.
    - context: 'search', 'friends', 'ranking'
--%>

<div class="card-lectura-mini d-flex align-items-center p-3 mb-2" style="background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.05); border-radius: 12px;">
    <div class="user-avatar-small me-3" style="width: 50px; height: 50px; flex-shrink: 0;">
        <c:set var="uAvatar" value="${not empty user.avatarUrl ? user.avatarUrl : 'personaje_1'}" />
        <c:choose>
            <c:when test="${fn:contains(uAvatar, '/')}">
                <img src="${uAvatar}" style="width:100%; height:100%; object-fit: cover; border-radius: 50%;">
            </c:when>
            <c:when test="${fn:startsWith(uAvatar, 'personaje_')}">
                <img src="assets/personajes/${uAvatar}.png" style="width:100%; height:100%; object-fit: contain;">
            </c:when>
            <c:otherwise>
                <img src="img/${uAvatar}/${uAvatar}_0.png" style="width:100%; height:100%; object-fit: contain;">
            </c:otherwise>
        </c:choose>
    </div>
    
    <div style="flex-grow: 1; overflow: hidden;">
        <h4 class="mb-0 h6 fw-bold">
            <a href="perfil?id=${user.id}" class="text-gold text-decoration-none" style="font-size: 0.95rem;">${user.nombre}</a>
        </h4>
        <p class="mb-0" style="color: rgba(255,255,255,0.7); font-size: 0.8rem;">@${user.username} · <span class="text-gold opacity-75">${user.tituloLector}</span></p>
        
        <c:if test="${user.librosEnComun > 0}">
            <button type="button" class="btn btn--sm btn--ghost p-0 mt-1 text-gold d-flex align-items-center" style="font-size: 0.75rem; border: none; background: none;" onclick="toggleAfinidad('afin-${context}-${user.id}')">
                <i class="bi bi-stars me-1"></i> ${user.librosEnComun} libros en común
            </button>
            <div id="afin-${context}-${user.id}" style="display: none; margin-top: 10px; background: rgba(212, 175, 55, 0.05); padding: 12px; border-radius: 12px; border: 1px solid rgba(212, 175, 55, 0.2);">
                <p class="small mb-2 text-white-50 fw-bold text-uppercase" style="font-size: 0.65rem; letter-spacing: 0.5px;">Títulos compartidos:</p>
                <div class="d-flex flex-wrap gap-2">
                    <c:forEach var="titulo" items="${fn:split(user.listaTitulosEnComun, ',')}">
                        <span class="badge bg-dark text-gold border border-warning-subtle" style="font-size: 0.7rem; font-weight: 500;">
                            <i class="bi bi-book me-1"></i> ${titulo.trim()}
                        </span>
                    </c:forEach>
                </div>
            </div>
        </c:if>
    </div>

    <div class="ms-2">
        <c:choose>
            <c:when test="${context == 'friends'}">
                <form action="amigos" method="post" class="m-0" id="form-eliminar-${user.id}">
                    <input type="hidden" name="action" value="eliminar">
                    <input type="hidden" name="amigoId" value="${user.id}">
                    <button type="button" class="btn btn-sm btn-outline-danger border-0" 
                            onclick="mostrarConfirmacion('Eliminar Amigo', '¿Seguro que quieres eliminar a ${user.nombre} (@${user.username})?', () => document.getElementById('form-eliminar-${user.id}').submit())">
                        <i class="bi bi-person-x-fill"></i>
                    </button>
                </form>
            </c:when>
            <c:when test="${context == 'search' || context == 'sugerencia'}">
                <form action="amigos" method="post" class="m-0">
                    <input type="hidden" name="action" value="agregar">
                    <input type="hidden" name="amigoId" value="${user.id}">
                    <button type="submit" class="btn btn-sm btn-gold">
                        <i class="bi bi-person-plus-fill"></i> Agregar
                    </button>
                </form>
            </c:when>
        </c:choose>
    </div>
</div>
