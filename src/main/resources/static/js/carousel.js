// for smooth scrolling functionality
document.addEventListener('DOMContentLoaded', function() {
    const bestsellersCarousel = document.querySelector('.bestsellers-carousel');
    if (bestsellersCarousel) {
        bestsellersCarousel.style.scrollBehavior = 'smooth';
    }
});
