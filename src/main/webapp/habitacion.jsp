<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.librorank.model.Usuario" %>
<%@ page import="com.librorank.model.ItemInventario" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Habitación | LibroRank</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="Css/styles.css">
    <script src="https://cdn.jsdelivr.net/npm/phaser@3.60.0/dist/phaser.min.js"></script>
    <style>
        :root { --rustic-wall: #fdf5e6; --rustic-floor: #d2b48c; --rustic-accent: #8b4513; }
        .active-theme { background: var(--accent-gold) !important; color: #000 !important; font-weight: 800; }
        
        .pet-society-panel {
            max-width: 850px;
            margin: 20px auto;
            background: white;
            border-radius: 40px;
            border: 12px solid #fff;
            box-shadow: 0 20px 0 rgba(0,0,0,0.05), 0 30px 60px rgba(0,0,0,0.1);
            overflow: hidden;
            display: flex;
            flex-direction: column;
        }

        .room-canvas-container { height: 500px; position: relative; cursor: crosshair; }
        
        .action-menu-pet {
            background: #f8f9fa;
            padding: 25px;
            display: flex;
            justify-content: center;
            gap: 20px;
            border-top: 2px solid #eee;
        }

        .hexagon-btn-pet {
            width: 70px; height: 70px;
            background: #444; color: white;
            clip-path: polygon(25% 0%, 75% 0%, 100% 50%, 75% 100%, 25% 100%, 0% 50%);
            border: none; cursor: pointer;
            transition: all 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275);
            display: flex; align-items: center; justify-content: center; font-size: 28px;
        }

        .hexagon-btn-pet:hover { transform: scale(1.1) rotate(5deg); background: var(--rustic-accent); }
        .hexagon-btn-pet.active { background: #ffcc00; color: #000; box-shadow: 0 0 20px rgba(255,204,0,0.5); }
    </style>
</head>
<body class="room-page" style="background: #eef2f5;">

<%
    Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
    List<ItemInventario> mueblesEnCuarto = (List<ItemInventario>) request.getAttribute("mueblesEnCuarto");
    List<ItemInventario> mueblesEnInventario = (List<ItemInventario>) request.getAttribute("mueblesEnInventario");
    String currentAvatar = (usuario != null && usuario.getAvatarUrl() != null && usuario.getAvatarUrl().startsWith("personaje_")) ? usuario.getAvatarUrl() : "personaje_1";
    int monedas = (usuario != null) ? usuario.getMonedas() : 0;
%>

<jsp:include page="/includes/header.jsp" />

<div class="text-center my-4">
    <div class="btn-group p-1" style="background:white; border-radius:50px; box-shadow: 0 4px 15px rgba(0,0,0,0.05);">
        <button class="btn btn-sm px-4 rounded-pill active-theme" id="btnModern" onclick="switchTheme('modern')">🏠 Modern</button>
        <button class="btn btn-sm px-4 rounded-pill" id="btnRustic" onclick="switchTheme('rustic')">🌲 Rustic</button>
    </div>
</div>

<main class="container-fluid py-2">
    <div class="d-flex justify-content-center align-items-start gap-4">
        
        <aside class="inventory-panel" id="panelInventario" style="width: 280px; background: white; border-radius: 35px; padding: 20px; box-shadow: 0 10px 40px rgba(0,0,0,0.05); border: 1px solid #eee; min-height: 500px;">
            <h3 class="h6 text-muted mb-4 text-center text-uppercase fw-bold" style="letter-spacing: 2px;">Items</h3>
            <div class="inventory-grid" id="zonaInventario" style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px;">
                <% if (mueblesEnInventario != null) { 
                    for (ItemInventario inv : mueblesEnInventario) { 
                        String f = inv.getItem().getImagenUrl();
                        String src = f.contains("/") ? f : "img/" + f + "/" + f + "_0.png";
                %>
                    <div class="inventario-item" draggable="true" data-id="<%= inv.getId() %>" data-key="<%= f %>" data-src="<%= src %>" ondragstart="handleDragStart(event)" style="background: #f8f9fa; border-radius: 20px; padding: 10px; aspect-ratio: 1; display: flex; align-items: center; justify-content: center; border: 2px solid #eee;">
                        <img src="<%= src %>" style="max-width:85%; max-height:85%; object-fit: contain;">
                    </div>
                <% } } %>
            </div>
        </aside>

        <div class="pet-society-panel">
            <div class="room-canvas-container" id="roomArea">
                <div id="rusticBackground" style="display:none; position:absolute; inset:0; pointer-events:none;">
                    <div style="height:65%; background: #fdf5e6; border-bottom: 8px solid #8b4513;"></div>
                    <div style="height:35%; background: #d2b48c;"></div>
                </div>
                <div class="coin-badge" style="position:absolute; top:20px; right:20px; background:#ffcc00; color:#000; padding:8px 20px; border-radius:50px; font-weight:900; z-index:100; box-shadow: 0 4px 10px rgba(0,0,0,0.1);">🪙 <%= monedas %></div>
            </div>

            <div class="action-menu-pet">
                <div class="text-center"><button class="hexagon-btn-pet" id="btnDecor"><i class="bi bi-pencil-square"></i></button><small class="d-block mt-2 fw-bold text-muted">Decor</small></div>
                <div class="text-center"><button class="hexagon-btn-pet" id="btnAct"><i class="bi bi-stars"></i></button><small class="d-block mt-2 fw-bold text-muted">Action</small></div>
                <div class="text-center"><button class="hexagon-btn-pet"><i class="bi bi-heart-fill"></i></button><small class="d-block mt-2 fw-bold text-muted">Pet</small></div>
                <div class="text-center"><button class="hexagon-btn-pet"><i class="bi bi-bag-fill"></i></button><small class="d-block mt-2 fw-bold text-muted">Shop</small></div>
            </div>
        </div>

    </div>
</main>

<script>
    let currentTheme = 'modern';
    let editMode = false;
    let avatarKey = "<%= currentAvatar %>";
    const roomArea = document.getElementById('roomArea');
    const panelInv = document.getElementById('panelInventario');

    function switchTheme(theme) {
        currentTheme = theme;
        document.getElementById('rusticBackground').style.display = (theme === 'rustic' ? 'block' : 'none');
        roomArea.style.background = (theme === 'modern' ? '#fff9c4' : 'transparent');
        document.getElementById('btnModern').classList.toggle('active-theme', theme === 'modern');
        document.getElementById('btnRustic').classList.toggle('active-theme', theme === 'rustic');
    }

    function handleDragStart(e) {
        e.dataTransfer.setData('itemId', e.currentTarget.dataset.id);
        e.dataTransfer.setData('itemKey', e.currentTarget.dataset.key);
        e.dataTransfer.setData('itemSrc', e.currentTarget.dataset.src);
    }

    roomArea.addEventListener('dragover', e => e.preventDefault());
    roomArea.addEventListener('drop', e => {
        e.preventDefault();
        const rect = roomArea.getBoundingClientRect();
        spawnFurniture(e.dataTransfer.getData('itemId'), e.dataTransfer.getData('itemKey'), e.dataTransfer.getData('itemSrc'), e.clientX - rect.left, e.clientY - rect.top);
        document.querySelector(`.inventario-item[data-id="${e.dataTransfer.getData('itemId')}"]`)?.remove();
    });

    const config = {
        type: Phaser.AUTO, width: 850, height: 500, parent: 'roomArea', transparent: true,
        scene: { preload: preload, create: create }
    };
    const game = new Phaser.Game(config);

    function preload() {
        this.load.image('star', 'https://labs.phaser.io/assets/particles/yellow.png');
        this.load.image(avatarKey, 'assets/personajes/' + avatarKey + '.png');
        
        <% if (mueblesEnInventario != null) { for (ItemInventario inv : mueblesEnInventario) { 
            String f = inv.getItem().getImagenUrl();
            String s = f.contains("/") ? f : "img/" + f + "/" + f + "_0.png";
        %> this.load.image('<%= f %>', '<%= s %>'); <% } } %>
        
        <% if (mueblesEnCuarto != null) { for (ItemInventario m : mueblesEnCuarto) { 
            String f = m.getItem().getImagenUrl();
            String s = f.contains("/") ? f : "img/" + f + "/" + f + "_0.png";
        %> this.load.image('<%= f %>', '<%= s %>'); <% } } %>
    }

    let mainPlayer, sceneRef;

    function create() {
        sceneRef = this;
        const floor = this.add.rectangle(425, 420, 850, 160, 0xffffff, 0).setInteractive();
        
        <% if (mueblesEnCuarto != null) { for (ItemInventario m : mueblesEnCuarto) { %>
            createMovableItem(sceneRef, <%= m.getPosicionX() %>, <%= m.getPosicionY() %>, '<%= m.getItem().getImagenUrl() %>', 0.9, <%= m.getOrientacion() == 1 %>, <%= m.getId() %>);
        <% } } %>

        mainPlayer = this.add.image(425, 400, avatarKey).setScale(0.85).setDepth(2000);
        
        document.getElementById('btnDecor').onclick = function() {
            editMode = !editMode;
            this.classList.toggle('active', editMode);
        };

        document.getElementById('btnAct').onclick = () => {
            if(editMode) return;
            sceneRef.tweens.add({
                targets: mainPlayer, y: mainPlayer.y - 100, duration: 300, yoyo: true, ease: 'Bounce.easeOut',
                onStart: () => { sceneRef.add.particles(mainPlayer.x, mainPlayer.y, 'star', { speed:100, scale:{start:0.5, end:0}, lifespan:500, stopAfter:10 }).setDepth(3000); }
            });
        };

        floor.on('pointerdown', p => { if(!editMode) walkTo(sceneRef, p.x, p.y); });
    }

    function createMovableItem(scene, x, y, key, scale, isFlipped, dbId) {
        const shadow = scene.add.ellipse(x, y + 45, 100 * scale, 30 * scale, 0x000000, 0.15);
        const item = scene.add.image(x, y, key).setScale(scale).setInteractive({ draggable: true, cursor: 'grab' });
        item.setFlipX(isFlipped); item.setData({dbId, key, shadow});
        item.depth = y; shadow.depth = y - 1;

        item.on('drag', function(p, dragX, dragY) {
            if(!editMode) return;
            this.x = dragX; this.y = dragY; this.depth = dragY;
            shadow.x = dragX; shadow.y = dragY + 45; shadow.depth = dragY - 1;
            const rect = panelInv.getBoundingClientRect();
            panelInv.style.boxShadow = (p.event.clientX < rect.right) ? "0 0 30px rgba(255,204,0,0.3)" : "0 10px 40px rgba(0,0,0,0.05)";
        });

        item.on('dragend', function(p) {
            panelInv.style.boxShadow = "0 10px 40px rgba(0,0,0,0.05)";
            const rect = panelInv.getBoundingClientRect();
            if (p.event.clientX < rect.right) {
                addToHtmlInventory(this.getData('dbId'), this.getData('key'));
                savePosition(this.getData('dbId'), 0, 0, false, 0);
                this.getData('shadow').destroy(); this.destroy();
            } else {
                savePosition(this.getData('dbId'), Math.round(this.x), Math.round(this.y), true, this.flipX?1:0);
            }
        });

        item.on('pointerdown', function() {
            if(!editMode) return;
            let now = Date.now();
            if(now - (this.lastClick || 0) < 300) {
                this.setFlipX(!this.flipX);
                savePosition(this.getData('dbId'), this.x, this.y, true, this.flipX ? 1 : 0);
            }
            this.lastClick = now;
        });
        return item;
    }

    function walkTo(scene, tx, ty) {
        scene.tweens.killTweensOf(mainPlayer);
        mainPlayer.setFlipX(tx < mainPlayer.x);
        scene.tweens.add({ targets: mainPlayer, x: tx, y: ty, duration: Phaser.Math.Distance.Between(mainPlayer.x, mainPlayer.y, tx, ty) * 3, onUpdate: () => mainPlayer.depth = mainPlayer.y + 10 });
    }

    function spawnFurniture(id, key, src, x, y) {
        createMovableItem(sceneRef, x, y, key, 0.9, false, id);
        savePosition(id, Math.round(x), Math.round(y), true, 0);
    }

    function savePosition(id, x, y, enUso, ori) {
        const p = new URLSearchParams();
        p.set('inventarioId', id); p.set('posX', x); p.set('posY', y);
        p.set('enUso', enUso); p.set('orientacion', ori);
        fetch('guardarPosicion', { method: 'POST', body: p });
    }

    function addToHtmlInventory(id, key) {
        const div = document.createElement('div');
        div.className = 'inventario-item';
        div.draggable = true; div.dataset.id = id; div.dataset.key = key;
        div.style = "background: #f8f9fa; border-radius: 20px; padding: 10px; aspect-ratio: 1; display: flex; align-items: center; justify-content: center; border: 2px solid #eee;";
        div.ondragstart = handleDragStart;
        div.innerHTML = `<img src="img/${key}/${key}_0.png" style="max-width:85%; max-height:85%; object-fit: contain;">`;
        document.getElementById('zonaInventario').appendChild(div);
    }
</script>

<jsp:include page="/includes/footer.jsp" />
</body>
</html>
