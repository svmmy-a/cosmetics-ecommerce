document.addEventListener('DOMContentLoaded', function() {
    // filter dropdowns
    const productNameFilter = document.getElementById('productNameFilter');

    // to apply filters
    function applyFilters() {
        const productName = productNameFilter.value.toLowerCase();

        const rows = document.querySelectorAll('tbody tr:not(.hidden):not([id^="history-"])');
        rows.forEach(row => {
            const productText = row.cells[1].textContent.toLowerCase();

            const matchesProduct = productName === '' || productText.includes(productName);

            if (matchesProduct) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    }

    // event listeners for filter changes
    if (productNameFilter) {
        productNameFilter.addEventListener('input', applyFilters);
    }
});
