document.addEventListener('DOMContentLoaded', function() {
    // seclect both product card buttons and the product detail page button
    const addToBagButtons = document.querySelectorAll('.add-to-bag-button, .product-details button.w-full.bg-black');

    // the add to bag functionality using CartManager
    addToBagButtons.forEach(button => {
        button.addEventListener('click', function() {
            // determine if the button is in a product card or product details section
            let name, description, price, image;
            const productCard = button.closest('.product-card');
            const productDetails = button.closest('.product-details');

            if (productCard) {
                // get details from product card (products page)
                name = productCard.querySelector('h3')?.textContent || 'Unknown Product';
                description = productCard.querySelector('p')?.textContent || 'No description available';
                const priceElement = productCard.querySelector('p.text-lg.font-bold');
                const priceText = priceElement?.textContent.replace('£', '').trim() || '0.00';
                price = parseFloat(priceText) || 0.00;
                const imageElement = productCard.querySelector('img');
                image = imageElement?.getAttribute('src') || '/assets/product1.jpg';
            } else if (productDetails) {
                // get details from product details (product detail page)
                name = productDetails.querySelector('h1')?.textContent || 'Unknown Product';
                description = productDetails.querySelector('p.text-lg.text-gray-600')?.textContent || 'No description available';
                const priceElement = productDetails.querySelector('span.text-2xl.font-bold');
                const priceText = priceElement?.textContent.replace('£', '').trim() || '0.00';
                price = parseFloat(priceText) || 0.00;
                const imageElement = document.querySelector('.product-image img'); // image from the product image section
                image = imageElement?.getAttribute('src') || '/assets/product1.jpg';
            } else {
                return; // if neither context found then exit
            }
            // check if the image src contains thymeleaf expression, use default if it does
            if (image.includes('${')) {
                image = '/assets/product1.jpg';
            } else if (!image.startsWith('/')) {
                // relative paths are prefixed with /
                image = '/' + image;
            }

            // extract productId and stock quanitity from data attribute in the DOM if available
            let productId = null;
            let stockQuantity = 0;
            if (productCard) {
                productId = productCard.getAttribute('data-product-id');
            } else if (productDetails) {
                productId = productDetails.getAttribute('data-product-id');
            }
            // convert to integer if found, otherwise keep as null
            productId = productId ? parseInt(productId, 10) : null;
            const item = {
                productId: productId, // will be null if not found in DOM; update HTML to include data product id
                name: name,
                description: description,
                price: price,
                image: image,
                quantity: 1
            };

            // dont have the stock quantity info on the frontend
            // so added item to the cart and let the backend check if its in stock
            // if the stock quantity was included in the HTML, we could check it here before adding.
            CartManager.addItem(item).then(() => {
                Swal.fire({
                    title: 'Added to Bag!',
                    text: `${name} has been added to your bag!`,
                    icon: 'success',
                    timer: 1000,
                    showConfirmButton: false
                });
            }).catch(error => {
                const errorMessage = error.response?.data || error.message || `Sorry, we can't add to bag - you've reached the maximum quantity for this product.`;
                Swal.fire({
                    title: 'Quantity Limit Reached',
                    text: errorMessage,
                    icon: 'error',
                    timer: 1500,
                    showConfirmButton: false
                });
            });
        });
    });
});
