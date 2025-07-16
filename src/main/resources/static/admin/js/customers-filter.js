document.addEventListener('DOMContentLoaded', function() {
    const nameFilter = document.getElementById('customerNameFilter');
    const emailFilter = document.getElementById('emailFilter');
    const tableBody = document.querySelector('tbody');

    // check if elements exist
    if (!nameFilter || !emailFilter || !tableBody) {
        return;
    }

    function filterTable() {
        const nameText = nameFilter.value.toLowerCase();
        const emailText = emailFilter.value.toLowerCase();
        const rows = tableBody.querySelectorAll('tr');

        rows.forEach(row => {
            // check if row has cells for name and email to avoid errors
            if (row.cells.length > 1) {
                const name = row.cells[0].textContent.toLowerCase();
                const email = row.cells[1].textContent.toLowerCase();
                
                const nameMatch = name.includes(nameText) || nameText === '';
                const emailMatch = email.includes(emailText) || emailText === '';
                
                if (nameMatch && emailMatch) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            }
        });
    }

    nameFilter.addEventListener('input', filterTable);
    emailFilter.addEventListener('input', filterTable);
});
