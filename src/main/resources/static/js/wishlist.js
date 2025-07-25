document.addEventListener('DOMContentLoaded', function() {
    const Wishlist = {
        // constant
        STORAGE_KEY: 'wishlist',
        
        // get wishlist from local storage with error handling
        getLocalWishlist: function() {
            try {
                const wishlist = localStorage.getItem(this.STORAGE_KEY);
                return wishlist ? JSON.parse(wishlist) : [];
            } catch (error) {
                console.error('Error retrieving wishlist from local storage:', error);
                return [];
            }
        },
        
        // save wishlist to local storage with error handling
        saveLocalWishlist: function(wishlist) {
            try {
                localStorage.setItem(this.STORAGE_KEY, JSON.stringify(wishlist));
                return true;
            } catch (error) {
                console.error('Error saving wishlist to local storage:', error);
                return false;
            }
        },
        
        // toggle wishlist item in local storage
        toggleLocalWishlist: function(productId) {
            let wishlist = this.getLocalWishlist();
            const index = wishlist.indexOf(productId);
            if (index === -1) {
                wishlist.push(productId);
            } else {
                wishlist.splice(index, 1);
            }
            this.saveLocalWishlist(wishlist);
            return index === -1;
        },
        
        // remove item from local wishlist
        removeFromLocalWishlist: function(productId) {
            let wishlist = this.getLocalWishlist();
            const index = wishlist.indexOf(productId);
            if (index !== -1) {
                wishlist.splice(index, 1);
                this.saveLocalWishlist(wishlist);
            }
        },
        
        // check if product is in local wishlist
        isInLocalWishlist: function(productId) {
            return this.getLocalWishlist().includes(productId);
        },
        
        // update heart icon state based on wishlist status
        updateHeartIcon: function(iconElement, isInWishlist) {
            if (isInWishlist) {
                iconElement.classList.add('text-red-500');
                iconElement.classList.remove('text-gray-500');
                iconElement.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clip-rule="evenodd" /></svg>';
            } else {
                iconElement.classList.add('text-gray-500');
                iconElement.classList.remove('text-red-500');
                iconElement.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" /></svg>';
            }
        },
        
        // Initialise wishlist state on page load
        initializeWishlistState: function() {
            const heartIcons = document.querySelectorAll('.heart-icon');
            heartIcons.forEach(icon => {
                const productId = icon.closest('.product-card, .product-details').getAttribute('data-product-id');
                this.updateHeartIcon(icon, this.isInLocalWishlist(productId));
            });
        },
        
        // attach event listeners to heart icons
        attachWishlistListeners: function() {
            const heartIcons = document.querySelectorAll('.heart-icon');
            heartIcons.forEach(icon => {
                icon.addEventListener('click', () => {
                    const productCard = icon.closest('.product-card, .product-details');
                    const productId = productCard.getAttribute('data-product-id');
                    const isInWishlist = this.toggleLocalWishlist(productId);
                    this.updateHeartIcon(icon, isInWishlist);
                    // trigger event for other parts of the application to listen to wishlist changes
                    document.dispatchEvent(new CustomEvent('wishlistUpdated', { detail: { productId, isInWishlist } }));
                });
            });
        },

        // fetch product details from API
        fetchProductDetails: async function(productId) {
            try {
                const response = await fetch(`/api/products/${productId}`);
                if (response.ok) {
                    const data = await response.json();
                    console.log(`Product details for ID ${productId}:`, data);
                    return data;
                } else {
                    console.error(`Failed to fetch product details for ID ${productId}. Status:`, response.status);
                    return null;
                }
            } catch (error) {
                console.error('Error fetching product details:', error);
                return null;
            }
        },

        // render wishlist items
        renderWishlistItems: async function(items, wishlistContainer, emptyWishlistMessage) {
            wishlistContainer.innerHTML = '';
            if (items.length === 0) {
                emptyWishlistMessage.classList.remove('hidden');
            } else {
                emptyWishlistMessage.classList.add('hidden');
                for (const productId of items) {
                    const product = await this.fetchProductDetails(productId);
                    if (product) {
                        const itemElement = document.createElement('div');
                        itemElement.className = 'product-card bg-white rounded-lg shadow-sm border border-gray-200 hover:shadow-md transition duration-300 overflow-hidden';
                        itemElement.setAttribute('data-product-id', productId);
                        itemElement.innerHTML = `
                            <div class="relative overflow-hidden mb-4">
                                <a href="/product/${productId}" class="block">
                                    <img src="${product.imageUrl}" alt="${product.name}" class="w-full h-64 object-cover transition-transform duration-500">
                                    <div class="absolute inset-0 bg-gradient-to-t from-black to-transparent opacity-0 hover:opacity-20 transition-opacity duration-300"></div>
                                </a>
                                <div class="absolute top-3 right-3 cursor-pointer heart-icon text-red-500">
                                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                                        <path fill-rule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clip-rule="evenodd" />
                                    </svg>
                                </div>
                            </div>
                            <div class="p-4">
                                <h3 class="text-lg font-semibold text-gray-900 mb-1 tracking-tight font-serif">
                                    <a href="/product/${productId}">${product.name}</a>
                                </h3>
                                <p class="text-sm text-gray-600 mb-3 line-clamp-2 leading-relaxed font-serif min-h-[3rem]">${product.description}</p>
                                <p class="text-sm text-gray-600 mb-3 font-serif">Size: ${product.size || 'N/A'}</p>
                                <div class="flex justify-between items-center mb-3">
                                    <p class="text-lg font-bold font-serif" style="color: #000000;">£${product.price.toFixed(2)}</p>
                                    <div class="flex items-center">
                                        <div class="rateyo-rating" data-rating="${product.averageRating || 0}"></div>
                                        <span class="text-sm text-gray-600 ml-2">(${product.reviewCount || 0})</span>
                                    </div>
                                </div>
                                <div class="flex justify-between items-center">
                                    <button class="w-full text-white py-2 px-4 rounded-none transition duration-300 font-medium tracking-wide add-to-bag-button font-serif bg-black hover:bg-gray-800">Add to Bag</button>
                                </div>
                            </div>
                        `;
                        wishlistContainer.appendChild(itemElement);

                        // add event listener for add to bag button
                        itemElement.querySelector('.add-to-bag-button').addEventListener('click', function() {
                            // assuming a function to handle adding to cart
                            incrementCartCount(product.name, product.description, product.price, product.imageUrl);
                            Swal.fire({
                                icon: 'success',
                                title: 'Added to Bag!',
                                text: 'Item has been added to your shopping bag.',
                                confirmButtonText: 'OK',
                                timer: 1000,
                                timerProgressBar: true
                            });
                        });

                        // add event listener for remove from wishlist button
                        itemElement.querySelector('.heart-icon').addEventListener('click', async function() {
                            Wishlist.removeFromLocalWishlist(productId);
                            itemElement.remove();
                            const remainingItems = Wishlist.getLocalWishlist();
                            if (remainingItems.length === 0) {
                                emptyWishlistMessage.classList.remove('hidden');
                            }
                        });
                    } else {
                        const unavailableItemElement = document.createElement('div');
                        unavailableItemElement.className = 'bg-white rounded-lg shadow-sm border border-gray-200 p-4 text-center';
                        unavailableItemElement.setAttribute('data-product-id', productId);
                        unavailableItemElement.innerHTML = `
                            <p class="text-lg text-gray-500 mb-2">Product ID: ${productId}</p>
                            <p class="text-md text-gray-500">This item is no longer available.</p>
                            <button class="text-red-500 mt-2 remove-unavailable-button">Remove from Wishlist</button>
                        `;
                        wishlistContainer.appendChild(unavailableItemElement);

                        // add event listener for remove button
                        unavailableItemElement.querySelector('.remove-unavailable-button').addEventListener('click', async function() {
                            Wishlist.removeFromLocalWishlist(productId);
                            unavailableItemElement.remove();
                            const remainingItems = Wishlist.getLocalWishlist();
                            if (remainingItems.length === 0) {
                                emptyWishlistMessage.classList.remove('hidden');
                            }
                        });
                    }
                }
            }
        },

        // initialize wishlist rendering on page load
        initializeWishlistRendering: function() {
            const wishlistContainer = document.getElementById('wishlist-items');
            const emptyWishlistMessage = document.getElementById('empty-wishlist');
            const wishlistItems = this.getLocalWishlist();
            console.log('Rendering wishlist items from local storage:', wishlistItems);
            this.renderWishlistItems(wishlistItems, wishlistContainer, emptyWishlistMessage);
        }
    };
    
    // initialise wishlist functionality
    Wishlist.initializeWishlistState();
    Wishlist.attachWishlistListeners();
    if (document.getElementById('wishlist-items') && document.getElementById('empty-wishlist')) {
        Wishlist.initializeWishlistRendering();
    }
    
    // expose wishlist object globally for use in other scripts
    window.Wishlist = Wishlist;
});
