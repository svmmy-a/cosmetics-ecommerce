document.addEventListener('DOMContentLoaded', function() {
    // filter panel functionality
    const filterButton = document.querySelector('.filter-button');
    const filterPanel = document.getElementById('filter-panel');
    const closeFilterPanel = document.getElementById('close-filter-panel');
    const filterCheckboxes = document.querySelectorAll('.filter-checkbox');

    if (filterButton && filterPanel && closeFilterPanel) {
        // open/close filter panel
        filterButton.addEventListener('click', function() {
            toggleFilterPanel(true);
        });

        closeFilterPanel.addEventListener('click', function() {
            toggleFilterPanel(false);
        });

        // close panel if clicking outside
        document.addEventListener('click', function(event) {
            if (!filterPanel.contains(event.target) && !filterButton.contains(event.target)) {
                toggleFilterPanel(false);
            }
        });

        // close panel with esc key
        document.addEventListener('keydown', function(event) {
            if (event.key === 'Escape' && !filterPanel.classList.contains('-translate-x-full')) {
                toggleFilterPanel(false);
            }
        });

        // focus management
        filterButton.addEventListener('keydown', function(event) {
            if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault();
                toggleFilterPanel(true);
            }
        });

        closeFilterPanel.addEventListener('keydown', function(event) {
            if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault();
                toggleFilterPanel(false);
            }
        });

        // toggle filter panel visibility with ARIA updates
        function toggleFilterPanel(isOpen) {
            if (isOpen) {
                filterPanel.classList.remove('-translate-x-full');
                filterButton.setAttribute('aria-expanded', 'true');
                // focus the first checkbox for accessibility
                const firstCheckbox = filterPanel.querySelector('.filter-checkbox');
                if (firstCheckbox) firstCheckbox.focus();
            } else {
                filterPanel.classList.add('-translate-x-full');
                filterButton.setAttribute('aria-expanded', 'false');
            }
        }

        // filter products based on selected categories
        filterCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', function() {
                // no immediate action on change to allow user to select multiple before applying
            });
        });

        // apply filters button
        const applyFiltersButton = document.getElementById('apply-filters');
        if (applyFiltersButton) {
            applyFiltersButton.addEventListener('click', function() {
                applyFilters();
                toggleFilterPanel(false); // Close panel after applying
            });
        }

        // clear all button
        const clearFiltersButton = document.getElementById('clear-filters');
        if (clearFiltersButton) {
            clearFiltersButton.addEventListener('click', function() {
                filterCheckboxes.forEach(checkbox => {
                    checkbox.checked = false;
                });
                applyFilters(); // reset filters
            });
        }

        /**
         * apply filters to show/hide products based on selected categories.
         * If no categories are selected, all products are shown.
         */
        function applyFilters() {
            const selectedCategories = Array.from(filterCheckboxes)
                .filter(checkbox => checkbox.checked)
                .map(checkbox => checkbox.value.toLowerCase());

            const productCards = document.querySelectorAll('.product-card');
            productCards.forEach(card => {
                const category = card.getAttribute('data-category')?.toLowerCase() || '';
                // show all products if no filters selected, or if the product's category matches a selected filter
                if (selectedCategories.length === 0 || (category && selectedCategories.includes(category))) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        }
    }
});
