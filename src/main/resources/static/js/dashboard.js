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
});