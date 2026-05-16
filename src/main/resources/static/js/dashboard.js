document.addEventListener('DOMContentLoaded', function () {
    const openAdd = document.getElementById('open-consumption-add');
    const modal = document.getElementById('consumption-modal');
    const modalContent = document.getElementById('consumption-modal-content');
    const body = document.getElementById('consumption-modal-body');

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
            showModal();
        });
    }

    // Tombol Edit (load fragment via AJAX)
    document.addEventListener('click', function (e) {
        const el = e.target.closest && e.target.closest('.consumption-edit');
        if (!el) return;
        e.preventDefault();

        const id = el.getAttribute('data-id');
        fetch('/consumption/' + id + '/fragment', {
            credentials: 'same-origin',
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'Accept': 'text/html'
            }
        })
        .then(resp => {
            if (!resp.ok) throw resp;
            return resp.text();
        })
        .then(html => {
            body.innerHTML = html;
            attachCancelListener();
            showModal();
        })
        .catch(err => {
            console.error('Failed to load fragment', err);
            window.location.href = '/consumption/' + id + '/edit';
        });
    });

    // Tutup modal klik overlay
    const overlay = document.getElementById('consumption-modal-overlay');
    if (overlay) overlay.addEventListener('click', hideModal);

    // Cancel listener awal untuk form Tambah
    attachCancelListener();

    // Delete handling: modal confirm + AJAX
    const confirmModal = document.getElementById('confirm-delete-modal');
    const confirmCancelBtn = document.getElementById('confirm-delete-cancel');
    const confirmYesBtn = document.getElementById('confirm-delete-yes');
    let pendingDelete = { id: null, li: null };

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
        pendingDelete.li = delEl.closest('li');
        showConfirmModal();
    });

    if (confirmCancelBtn) confirmCancelBtn.addEventListener('click', function () {
        pendingDelete.id = null; pendingDelete.li = null; hideConfirmModal();
    });

    if (confirmYesBtn) confirmYesBtn.addEventListener('click', function () {
        if (!pendingDelete.id) return;

        // read CSRF header/token from meta
        const headerMeta = document.querySelector('meta[name="_csrf_header"]');
        const tokenMeta = document.querySelector('meta[name="_csrf"]');
        const headers = {
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'application/json'
        };
        if (headerMeta && tokenMeta) {
            headers[headerMeta.getAttribute('content')] = tokenMeta.getAttribute('content');
        }

        // capture amount before removing element
        let amount = 0;
        if (pendingDelete.li) {
            const amtSpan = pendingDelete.li.querySelector('.bg-green-50 span');
            if (amtSpan) {
                amount = parseFloat(amtSpan.textContent.replace(',', '.')) || 0;
            }
        }

        fetch('/consumption/' + pendingDelete.id + '/delete', {
            method: 'POST',
            credentials: 'same-origin',
            headers: headers
        })
        .then(resp => {
            if (!resp.ok) throw resp;
            // remove item from DOM
            if (pendingDelete.li && pendingDelete.li.parentNode) pendingDelete.li.parentNode.removeChild(pendingDelete.li);

            // update daily total & status
            const dailyTotalEl = document.getElementById('daily-total');
            const statusEl = document.getElementById('daily-status');
            let current = parseFloat(dailyTotalEl.textContent) || 0;
            let newTotal = current - amount;
            if (newTotal < 0) newTotal = 0;
            // format: remove trailing zeros when integer
            dailyTotalEl.textContent = (Math.round(newTotal * 100) / 100).toString();

            if (statusEl) {
                const label = statusEl.querySelector('span');
                if (newTotal <= 50) {
                    statusEl.className = 'px-3 py-1 rounded-lg text-sm font-semibold bg-green-100 text-green-700';
                    if (label) label.textContent = 'normal';
                } else {
                    statusEl.className = 'px-3 py-1 rounded-lg text-sm font-semibold bg-red-100 text-red-700';
                    if (label) label.textContent = 'melebihi batas konsumsi';
                }
            }

            pendingDelete.id = null; pendingDelete.li = null;
            hideConfirmModal();
        })
        .catch(err => {
            console.error('Failed to delete', err);
            alert('Gagal menghapus data');
            pendingDelete.id = null; pendingDelete.li = null;
            hideConfirmModal();
        });
    });
});