// Small progressive-enhancement helpers for the Maritime Terminal ERP UI.
// No frameworks - plain DOM APIs only, per the project's "keep it simple" requirement.

function formatCurrency(value) {
    var numericValue = Number(value) || 0;
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(numericValue);
}

document.addEventListener('DOMContentLoaded', function () {
    // Confirm before submitting any "process departure" / "approve" / "pay" action buttons.
    document.querySelectorAll('form button[type="submit"]').forEach(function (btn) {
        var label = btn.textContent.trim().toLowerCase();
        var sensitive = ['approve', 'mark paid', 'record payment', 'process departure', 'create vendor bill'];
        if (sensitive.some(function (s) { return label.indexOf(s) !== -1; })) {
            btn.closest('form').addEventListener('submit', function (evt) {
                if (!confirm('Confirm: ' + btn.textContent.trim() + '?')) {
                    evt.preventDefault();
                }
            });
        }
    });

    // Highlight table rows on hover for readability (fallback if :hover CSS is overridden).
    document.querySelectorAll('table tbody tr').forEach(function (row) {
        row.addEventListener('mouseenter', function () { row.style.background = '#f7fafd'; });
        row.addEventListener('mouseleave', function () { row.style.background = ''; });
    });

    function animateNumber(element, endValue, prefix, suffix) {
        if (!element) return;

        var start = 0;
        var duration = 800;
        var startTime = null;

        function step(timestamp) {
            if (!startTime) startTime = timestamp;
            var progress = Math.min((timestamp - startTime) / duration, 1);
            var current = Math.floor(start + ((endValue - start) * progress));
            var value = prefix + current + suffix;
            element.textContent = value;
            if (progress < 1) requestAnimationFrame(step);
        }

        requestAnimationFrame(step);
    }

    document.querySelectorAll('.card-value').forEach(function (card) {
        var rawValue = card.getAttribute('data-value');
        if (rawValue === null) return;

        var cleanValue = rawValue.replace(/[^0-9.-]/g, '');
        var numericValue = Number(cleanValue || 0);
        var isCurrency = card.textContent.indexOf('₹') !== -1;
        var startValue = 0;

        if (isCurrency) {
            card.textContent = '₹0';
            animateNumber(card, numericValue, '₹', '');
        } else {
            card.textContent = '0';
            animateNumber(card, numericValue, '', '');
        }
    });

    // Dynamic summary for purchase-order forms.
    var poForm = document.querySelector('.purchase-order-form');
    if (poForm) {
        var productSelect = poForm.querySelector('select[name="productId"]');
        var quantityInput = poForm.querySelector('input[name="quantity"]');
        var unitPriceInput = poForm.querySelector('input[name="unitPrice"]');
        var poTotalEl = document.getElementById('po-total-preview');

        function updatePoPreview() {
            if (!poTotalEl) return;

            var selectedProduct = productSelect && productSelect.options[productSelect.selectedIndex];
            var selectedPrice = selectedProduct && selectedProduct.dataset.price ? Number(selectedProduct.dataset.price) : 0;
            var quantity = Number(quantityInput && quantityInput.value) || 0;
            var unitPrice = Number(unitPriceInput && unitPriceInput.value) || selectedPrice || 0;

            if (unitPriceInput && (!unitPriceInput.value || unitPriceInput.value === '0')) {
                unitPriceInput.value = selectedPrice ? selectedPrice.toFixed(2) : '';
            }

            var total = quantity * unitPrice;
            poTotalEl.textContent = formatCurrency(total);
        }

        if (productSelect) {
            productSelect.addEventListener('change', function () {
                var selectedProduct = productSelect.options[productSelect.selectedIndex];
                if (selectedProduct && selectedProduct.dataset.price) {
                    unitPriceInput.value = Number(selectedProduct.dataset.price).toFixed(2);
                }
                updatePoPreview();
            });
        }

        if (quantityInput) quantityInput.addEventListener('input', updatePoPreview);
        if (unitPriceInput) unitPriceInput.addEventListener('input', updatePoPreview);
        updatePoPreview();
    }

    // Dynamic summary for docking selections.
    var dockingForm = document.querySelector('.docking-form');
    if (dockingForm) {
        var vesselSelect = dockingForm.querySelector('select[name="vesselId"]');
        var berthSelect = dockingForm.querySelector('select[name="berthId"]');
        var dockingSummary = document.getElementById('docking-preview');

        function updateDockingPreview() {
            if (!dockingSummary) return;

            var vesselName = vesselSelect && vesselSelect.options[vesselSelect.selectedIndex] ? vesselSelect.options[vesselSelect.selectedIndex].text : 'No vessel selected';
            var berthOption = berthSelect && berthSelect.options[berthSelect.selectedIndex];
            var berthName = berthOption ? berthOption.text : 'No berth selected';
            var berthRate = berthOption && berthOption.dataset.rate ? Number(berthOption.dataset.rate) : 0;

            dockingSummary.innerHTML = '<div><strong>Selected vessel:</strong> ' + vesselName + '</div>' +
                '<div><strong>Selected berth:</strong> ' + berthName + '</div>' +
                '<div><strong>Base berth rate:</strong> ' + formatCurrency(berthRate) + ' / hour</div>';
        }

        if (vesselSelect) vesselSelect.addEventListener('change', updateDockingPreview);
        if (berthSelect) berthSelect.addEventListener('change', updateDockingPreview);
        updateDockingPreview();
    }
});
