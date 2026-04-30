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
            <button type="button" class="btn btn--sm btn--ghost p-0 mt-1" style="color: var(--primary); font-size: 0.75rem;" onclick="toggleAfinidad('afin-${context}-${user.id}')">
                ✨ ${user.librosEnComun} en común
            </button>
            <div id="afin-${context}-${user.id}" style="display: none; margin-top: 8px; background: rgba(0,0,0,0.2); padding: 8px; border-radius: 8px; border: 1px dashed rgba(76, 209, 55, 0.3);">
                <p class="small mb-1" style="color: var(--primary);"><strong>Libros compartidos:</strong> ${user.listaTitulosEnComun}</p>
                <c:if test="${not empty user.bio}">
                    <p class="small mb-0 text-muted" style="font-style: italic;">"${user.bio}"</p>
                </c:if>
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
