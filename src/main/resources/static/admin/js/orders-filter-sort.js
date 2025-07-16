document.addEventListener('DOMContentLoaded', function() {
    // filter dropdowns
    const customerNameFilter = document.getElementById('customerNameFilter');
    const orderNumberFilter = document.getElementById('orderNumberFilter');
    const statusFilter = document.getElementById('statusFilter');

    // to apply filters
    function applyFilters() {
        const customerName = customerNameFilter.value.toLowerCase();
        const orderNumber = orderNumberFilter.value.toLowerCase();
        const status = statusFilter.value;

        const rows = document.querySelectorAll('tbody tr:not(.hidden):not([id^="details-"])');
        rows.forEach(row => {
            const customerText = row.cells[2].textContent.toLowerCase();
            const orderNumberText = row.cells[1].textContent.toLowerCase();
            const statusText = row.cells[4].querySelector('span').textContent.trim().toLowerCase();

            const matchesCustomer = customerName === '' || customerText.includes(customerName);
            const matchesOrderNumber = orderNumber === '' || orderNumberText.includes(orderNumber);
            const matchesStatus = status === '' || statusText === status;

            if (matchesCustomer && matchesOrderNumber && matchesStatus) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    }

    // event listeners for filter changes
    if (customerNameFilter) {
        customerNameFilter.addEventListener('input', applyFilters);
    }
    if (orderNumberFilter) {
        orderNumberFilter.addEventListener('input', applyFilters);
    }
    if (statusFilter) {
        statusFilter.addEventListener('change', applyFilters);
    }

    // population of status dropdown
    function populateStatusDropdown() {
        const statuses = ['pending', 'processing', 'shipped', 'delivered', 'cancelled'];

        if (statusFilter) {
            statusFilter.innerHTML = '<option value="">All Statuses</option>';
            statuses.forEach(status => {
                const option = document.createElement('option');
                option.value = status;
                option.textContent = status.charAt(0).toUpperCase() + status.slice(1);
                statusFilter.appendChild(option);
            });
        }
    }

    populateStatusDropdown();
});
