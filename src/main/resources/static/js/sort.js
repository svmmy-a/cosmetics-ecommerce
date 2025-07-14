// sorting functionality for products page
document.addEventListener('DOMContentLoaded', function() {
    const sortSelect = document.getElementById('sort');
    if (sortSelect) {
        sortSelect.addEventListener('change', function() {
            const sortOption = this.value;
            const productGrid = document.querySelector('.grid');
            const productCards = Array.from(productGrid.querySelectorAll('.product-card'));

            productCards.sort((a, b) => {
                if (sortOption === 'price-low-to-high') {
                    const priceA = parseFloat(a.querySelector('.text-lg.font-bold').textContent.replace('£', '').trim()) || 0;
                    const priceB = parseFloat(b.querySelector('.text-lg.font-bold').textContent.replace('£', '').trim()) || 0;
                    return priceA - priceB;
                } else if (sortOption === 'price-high-to-low') {
                    const priceA = parseFloat(a.querySelector('.text-lg.font-bold').textContent.replace('£', '').trim()) || 0;
                    const priceB = parseFloat(b.querySelector('.text-lg.font-bold').textContent.replace('£', '').trim()) || 0;
                    return priceB - priceA;
                } else if (sortOption === 'rating') {
                    const ratingA = parseFloat(a.querySelector('.rateyo-rating').getAttribute('data-rating')) || 0;
                    const ratingB = parseFloat(b.querySelector('.rateyo-rating').getAttribute('data-rating')) || 0;
                    return ratingB - ratingA;
                } else if (sortOption === 'newest') {
                    const isNewA = a.querySelector('.bg-green-500') ? 1 : 0;
                    const isNewB = b.querySelector('.bg-green-500') ? 1 : 0;
                    return isNewB - isNewA;
                }
                return 0;
            });

            // clear and reappend sorted cards
            while (productGrid.firstChild) {
                productGrid.removeChild(productGrid.firstChild);
            }
            productCards.forEach(card => productGrid.appendChild(card));
        });
    }
});
