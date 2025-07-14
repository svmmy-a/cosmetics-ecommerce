document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('search');
    const productCards = document.querySelectorAll('.product-card');

    if (searchInput) {
        searchInput.addEventListener('input', function () {
            const searchTerm = this.value.trim().toLowerCase();
            filterProducts(searchTerm);
        });
    }

    function filterProducts(searchTerm) {
        const regex = new RegExp(searchTerm, 'i');
        let visibleProducts = 0;

        productCards.forEach(card => {
            const productName = card.querySelector('h3 a').textContent.toLowerCase();
            const productDescription = card.querySelector('p').textContent.toLowerCase();

            if (regex.test(productName) || regex.test(productDescription)) {
                card.style.display = 'block';
                visibleProducts++;
            } else {
                card.style.display = 'none';
            }
        });

        const noProductsMessage = document.querySelector('.no-products-message');
        if (visibleProducts === 0 && searchTerm !== '') {
            if (!noProductsMessage) {
                const container = document.querySelector('.grid');
                const message = document.createElement('p');
                message.className = 'text-center text-gray-600 no-products-message';
                message.textContent = 'No products found.';
                container.appendChild(message);
            }
        } else {
            if (noProductsMessage) {
                noProductsMessage.remove();
            }
        }
    }
});
