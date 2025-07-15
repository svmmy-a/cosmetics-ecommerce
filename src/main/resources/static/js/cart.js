const CartManager = {
  loadItems: function() {
    return fetch('/api/cart')
      .then(response => response.json())
      .catch(error => {
        console.error('Error loading cart items:', error);
        return [];
      });
  },
  
  addItem: function(item) {
    return fetch('/api/cart/add', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(item)
    })
    .then(response => {
      if (!response.ok) {
        return response.text().then(errorMessage => {
          throw new Error(errorMessage);
        });
      }
      return response.json();
    })
    .then(cartItems => {
      this.updateCartCount();
      return cartItems;
    })
    .catch(error => {
      throw error; 
    });
  },
  
  removeItem: function(index) {
    return fetch(`/api/cart/remove/${index}`, {
      method: 'DELETE'
    })
    .then(response => response.json())
    .then(cartItems => {
      this.updateCartCount();
      return cartItems;
    })
    .catch(error => {
      console.error('Error removing item from cart:', error);
      return [];
    });
  },
  
  updateQuantity: function(index, quantity) {
    return fetch(`/api/cart/update/${index}?quantity=${quantity}`, {
      method: 'PUT'
    })
    .then(response => {
      if (!response.ok) {
        return response.text().then(errorMessage => {
          throw new Error(errorMessage);
        });
      }
      return response.json();
    })
    .then(cartItems => {
      this.updateCartCount();
      return cartItems;
    })
    .catch(error => {
      const errorMessage = error.message || "Sorry, we can't update the quantity - you've reached the maximum quantity for this product.";
      Swal.fire({
        title: 'Quantity Limit Reached',
        text: errorMessage,
        icon: 'error',
        timer: 1500,
        showConfirmButton: false
      });
      // reload cart items so UI reflects the correct state
      return this.loadItems();
    });
  },
  
  getCount: function() {
    return this.loadItems().then(cartItems => {
      return cartItems.reduce((count, item) => count + item.quantity, 0);
    });
  },
  
  getSubtotal: function() {
    return this.loadItems().then(cartItems => {
      return cartItems.reduce((subtotal, item) => subtotal + (item.price * item.quantity), 0);
    });
  },
  
  updateCartCount: function() {
    this.getCount().then(count => {
      const cartCountElement = document.getElementById('cart-count');
      if (cartCountElement) {
        cartCountElement.textContent = count;
        cartCountElement.classList.toggle('hidden', count === 0);
      }
      const bagTitleElement = document.querySelector('.bag-title');
      if (bagTitleElement) {
        bagTitleElement.textContent = `Shopping Bag (${count} item${count !== 1 ? 's' : ''})`;
      }
    });
  }
};

// attach CartManager to the global window object to make it accessible
window.CartManager = CartManager;
