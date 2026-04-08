<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%-- Modal de Confirmación Global --%>
<div class="modal fade" id="modalConfirmacion" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content" style="background-color: var(--bg-card); border: 1px solid var(--accent-gold); border-radius: 15px;">
            <div class="modal-header border-0">
                <h5 class="modal-title font-title" id="confirmTitle" style="color: var(--accent-gold);">¿Estás seguro?</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p id="confirmMessage" class="text-white">Esta acción no se puede deshacer.</p>
            </div>
            <div class="modal-footer border-0">
                <button type="button" class="btn btn-outline-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>
                <button type="button" id="btnConfirmarAccion" class="btn btn-gold btn-sm px-4">Confirmar</button>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    let confirmCallback = null;
    const confirmModal = new bootstrap.Modal(document.getElementById('modalConfirmacion'));

    function mostrarConfirmacion(titulo, mensaje, callback) {
        document.getElementById('confirmTitle').innerText = titulo;
        document.getElementById('confirmMessage').innerText = mensaje;
        confirmCallback = callback;
        confirmModal.show();
    }

    document.getElementById('btnConfirmarAccion').addEventListener('click', () => {
        if (confirmCallback) confirmCallback();
        confirmModal.hide();
    });

    // Función para manejar eliminaciones vía confirmación modal
    function confirmarEliminar(e, titulo, mensaje) {
        e.preventDefault();
        const url = e.currentTarget.href;
        mostrarConfirmacion(titulo, mensaje, () => {
            window.location.href = url;
        });
    }
</script>
