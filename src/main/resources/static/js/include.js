window.addEventListener('DOMContentLoaded', () => {
  fetch('/components/accessibility.html')
    .then(res => res.text())
    .then(data => {
      const accessibilityPlaceholder = document.getElementById('accessibility-placeholder');
      if (accessibilityPlaceholder) {
        accessibilityPlaceholder.innerHTML = data;
      }
    });

  fetch('/components/navbar.html')
    .then(res => res.text())
    .then(data => {
      const navbarPlaceholder = document.getElementById('navbar-placeholder');
      if (navbarPlaceholder) {
        navbarPlaceholder.innerHTML = data;
        // update cart count after navbar is loaded using CartManager if available
        if (window.CartManager) {
          window.CartManager.getCount().then(count => {
            const cartCountElement = document.getElementById('cart-count');
            if (cartCountElement) {
              cartCountElement.textContent = count;
              cartCountElement.classList.toggle('hidden', count === 0);
            }
          });
        }
        // check for user data to update navbar greeting
        const userDataElement = document.getElementById('userData');
        if (userDataElement) {
          const userName = userDataElement.getAttribute('data-name');
          if (userName) {
            // Use a more specific selector for the Account dropdown
            const accountDropdown = document.querySelector('div.relative.mr-8.group:nth-child(2) .absolute.navbar-dropdown');
            if (accountDropdown) {
              let greetingDiv = accountDropdown.querySelector('.greeting');
              if (!greetingDiv) {
                greetingDiv = document.createElement('div');
                greetingDiv.className = 'greeting px-4 py-3 text-gray-800 font-medium text-sm border-b border-gray-100';
                accountDropdown.insertBefore(greetingDiv, accountDropdown.firstChild);
              }
              greetingDiv.textContent = `Hi, ${userName}`;
              // hide login and register links if user is logged in
              const loginLink = accountDropdown.querySelector('a[href="/login"]');
              const registerLink = accountDropdown.querySelector('a[href="/customer/signup"]');
              if (loginLink) loginLink.style.display = 'none';
              if (registerLink) registerLink.style.display = 'none';
              // show logout link if it's hidden
              const logoutLink = accountDropdown.querySelector('#logoutLink');
              if (logoutLink) logoutLink.style.display = 'block';
            }
          }
        }
        // load navbar-search.js after navbar is inserted
        const script = document.createElement('script');
        script.src = '/js/navbar-search.js';
        script.async = false;
        document.head.appendChild(script);
      }
    });

    // load free shipping banner
  fetch('/components/free-shipping-banner.html')
    .then(res => res.text())
    .then(data => {
      const freeShippingPlaceholder = document.getElementById('free-shipping-banner-placeholder');
      if (freeShippingPlaceholder) {
        freeShippingPlaceholder.innerHTML = data;
      }
    });
});
