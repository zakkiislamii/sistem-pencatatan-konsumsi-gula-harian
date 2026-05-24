document.addEventListener('DOMContentLoaded', function () {
    const openAdd = document.getElementById('open-consumption-add');
    const modal = document.getElementById('consumption-modal');
    const modalContent = document.getElementById('consumption-modal-content');
    const body = document.getElementById('consumption-modal-body');

    const originalFormHTML = body.innerHTML;

    function showModal() {
        modal.classList.remove('hidden');
        modal.classList.add('flex');
        setTimeout(() => {
            modalContent.classList.remove('scale-95', 'opacity-0');
            modalContent.classList.add('scale-100', 'opacity-100');
        }, 10);
    }

    function hideModal() {
        modalContent.classList.remove('scale-100', 'opacity-100');
        modalContent.classList.add('scale-95', 'opacity-0');
        setTimeout(() => {
            modal.classList.remove('flex');
            modal.classList.add('hidden');
        }, 200);
    }

    function attachCancelListener() {
        const cancel = document.getElementById('consumption-cancel');
        if (cancel) cancel.addEventListener('click', hideModal);
    }

    // Tombol Tambah Data
    if (openAdd) {
        openAdd.addEventListener('click', function (e) {
            e.preventDefault();
            body.innerHTML = originalFormHTML;
            attachCancelListener();
            showModal();
        });
    }


    // Tutup modal klik overlay
    const overlay = document.getElementById('consumption-modal-overlay');
    if (overlay) overlay.addEventListener('click', hideModal);

    // Cancel listener awal
    attachCancelListener();

    // Delete handling
    const confirmModal = document.getElementById('confirm-delete-modal');
    const confirmCancelBtn = document.getElementById('confirm-delete-cancel');
    const confirmYesBtn = document.getElementById('confirm-delete-yes');
    const deleteForm = document.getElementById('delete-consumption-form');
    const deleteDateInput = document.getElementById('delete-consumption-date');
    let pendingDelete = { id: null, date: null };

    function showConfirmModal() {
        if (!confirmModal) return;
        confirmModal.classList.remove('hidden');
        confirmModal.classList.add('flex');
    }

    function hideConfirmModal() {
        if (!confirmModal) return;
        confirmModal.classList.remove('flex');
        confirmModal.classList.add('hidden');
    }

    document.addEventListener('click', function (e) {
        const delEl = e.target.closest && e.target.closest('.consumption-delete');
        if (!delEl) return;
        e.preventDefault();
        pendingDelete.id = delEl.getAttribute('data-id');
        pendingDelete.date = delEl.getAttribute('data-date') || '';
        showConfirmModal();
    });

    if (confirmCancelBtn) confirmCancelBtn.addEventListener('click', function () {
        pendingDelete.id = null;
        pendingDelete.date = null;
        hideConfirmModal();
    });

    if (confirmYesBtn) confirmYesBtn.addEventListener('click', function () {
        if (!pendingDelete.id) return;
        if (deleteForm) {
            deleteForm.action = '/consumption/' + pendingDelete.id + '/delete';
        }
        if (deleteDateInput) {
            deleteDateInput.value = pendingDelete.date || deleteDateInput.value || '';
        }
        pendingDelete.id = null;
        pendingDelete.date = null;
        if (deleteForm) {
            deleteForm.submit();
        }
    });
});