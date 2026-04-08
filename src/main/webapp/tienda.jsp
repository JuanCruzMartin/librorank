<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Tienda — LibroRank</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="Css/styles.css">
    <style>
        .product-card.locked {
            filter: grayscale(0.8);
            opacity: 0.7;
            position: relative;
        }
        .lock-overlay {
            position: absolute;
            top: 0; left: 0; width: 100%; height: 100%;
            background: rgba(0,0,0,0.4);
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            z-index: 10;
            color: var(--accent-gold);
            border-radius: 15px;
            pointer-events: none;
        }
        .lock-overlay i { font-size: 2.5rem; }
        .lock-overlay span { font-weight: bold; text-transform: uppercase; font-size: 0.7rem; margin-top: 5px; background: black; padding: 2px 8px; border-radius: 5px; }
        
        .btn-buy-game.btn-locked {
            background: #333 !important;
            border-color: #444 !important;
            color: #666 !important;
            cursor: not-allowed;
        }
    </style>
</head>
<body>

<jsp:include page="/includes/header.jsp" />

<main class="page">
    <div class="container section">

        <div class="shop-header card">
            <div>
                <h1 style="margin:0; font-size:1.8rem; color: var(--accent-gold);">Mercado de Lectores</h1>
                <p class="text-muted" style="margin:0;">Personalizá tu espacio de lectura</p>
            </div>
            <div class="user-balance">
                <span class="text-gold">🪙</span> ${usuarioLogueado.monedas}
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger bg-dark text-danger border-danger mb-4" id="system-alert">
                <span>❌</span> ${error}
            </div>
            <c:remove var="error" scope="session" />
        </c:if>
        <c:if test="${not empty mensaje}">
            <div class="alert alert-success bg-dark text-success border-success mb-4" id="system-alert">
                <span>✅</span> ${mensaje}
            </div>
            <c:remove var="mensaje" scope="session" />
        </c:if>

        <div class="shop-grid">

            <c:forEach var="p" items="${productos}">
                <c:set var="rarityClass" value="${p.precio < 50 ? 'rarity-common' : (p.precio < 150 ? 'rarity-rare' : 'rarity-legendary')}" />
                <c:set var="rarityName" value="${p.precio < 50 ? 'COMÚN' : (p.precio < 150 ? 'RARO' : 'LEGENDARIO')}" />
                
                <%-- Lógica de bloqueo real: Comprobamos si el item tiene requisito y si el usuario NO lo tiene --%>
                <c:set var="isLocked" value="${not empty p.requisitoLogro and not logrosUsuario.contains(p.requisitoLogro)}" />

                <article class="product-card card ${rarityClass} ${isLocked ? 'locked' : ''}">
                    <div class="rarity-badge">${rarityName}</div>
                    
                    <c:if test="${isLocked}">
                        <div class="lock-overlay">
                            <i class="bi bi-lock-fill"></i>
                            <span>REQUISITO: ${p.requisitoLogro}</span>
                        </div>
                    </c:if>

                    <div class="product-image-container">
                        <div class="product-glow"></div>
                        <img src="img/${p.imagenUrl}/${p.imagenUrl}_0.png" alt="${p.nombre}" class="product-img">
                    </div>

                    <div class="product-info text-center">
                        <span class="product-type-badge">${p.tipo}</span>
                        <h3 class="product-title">${p.nombre}</h3>
                        <div class="price-container">
                            <span class="coin-icon">🪙</span>
                            <span class="price-value">${p.precio}</span>
                        </div>
                    </div>

                    <form action="comprar" method="post" class="mt-auto">
                        <input type="hidden" name="itemId" value="${p.id}">

                        <c:choose>
                            <c:when test="${isLocked}">
                                <button type="button" class="btn-buy-game btn-locked" disabled>
                                    <i class="bi bi-shield-lock"></i> BLOQUEADO
                                </button>
                            </c:when>
                            <c:when test="${usuarioLogueado.monedas >= p.precio}">
                                <button type="submit" class="btn-buy-game">
                                    <i class="bi bi-cart-plus-fill"></i> ADQUIRIR
                                </button>
                            </c:when>
                            <c:otherwise>
                                <button type="button" class="btn-buy-game btn-disabled" disabled>
                                    <i class="bi bi-coin"></i> INSUFICIENTE
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </form>
                </article>
            </c:forEach>

        </div>
    </div>
</main>

<script>
    const alertBox = document.getElementById('system-alert');
    if (alertBox) {
        setTimeout(() => {
            alertBox.style.opacity = '0';
            alertBox.style.transform = 'translateY(-10px)';
            alertBox.style.transition = 'all 0.5s ease';
            setTimeout(() => alertBox.remove(), 500);
        }, 4000);
    }
</script>

<jsp:include page="/includes/footer.jsp" />
</body>
</html>
