(function() {
  let searchToggle, searchContainer, searchInput, searchSuggestions;
  let searchTimeout;
  
  // to initialse search feature
  function initializeSearch() {
    searchToggle = document.getElementById('search-toggle');
    searchContainer = document.getElementById('search-container');
    searchInput = document.getElementById('navbar-search');
    searchSuggestions = document.getElementById('search-suggestions');
    
    if (searchInput) {
      searchInput.placeholder = 'Search products...';
      bindEventListeners();
    }
  }
  
  // to bind event listeners once elements are available
  function bindEventListeners() {
    // toggle search 
    if (searchToggle) {
      searchToggle.addEventListener('click', function() {
        if (searchContainer.classList.contains('hidden')) {
          searchContainer.classList.remove('hidden');
          searchInput.focus();
        } else {
          searchContainer.classList.add('hidden');
          clearSuggestions();
        }
      });
    }
    
    // close dropdown when clicking outside
    document.addEventListener('click', function(event) {
      if (searchContainer && searchToggle && !searchContainer.contains(event.target) && !searchToggle.contains(event.target)) {
        searchContainer.classList.add('hidden');
        clearSuggestions();
      }
    });
    
    // handle search input for autocomplete
    if (searchInput) {
      searchInput.addEventListener('input', function() {
        clearTimeout(searchTimeout);
        const searchTerm = this.value.trim();
        
        if (searchTerm.length >= 2) {
          searchTimeout = setTimeout(function() {
            fetchProductSuggestions(searchTerm);
          }, 300); // Debounce to prevent excessive requests
        } else {
          clearSuggestions();
        }
      });
      
      // allow navigation in dropdown with keyboard
      searchInput.addEventListener('keydown', function(event) {
        const suggestions = searchSuggestions.querySelectorAll('a');
        if (suggestions.length === 0) return;
        
        let focusedIndex = -1;
        for (let i = 0; i < suggestions.length; i++) {
          if (document.activeElement === suggestions[i]) {
            focusedIndex = i;
            break;
          }
        }
        
        if (event.key === 'ArrowDown') {
          event.preventDefault();
          focusedIndex = (focusedIndex + 1) % suggestions.length;
          suggestions[focusedIndex].focus();
        } else if (event.key === 'ArrowUp') {
          event.preventDefault();
          focusedIndex = (focusedIndex - 1 + suggestions.length) % suggestions.length;
          suggestions[focusedIndex].focus();
        } else if (event.key === 'Enter' && focusedIndex >= 0) {
          event.preventDefault();
          suggestions[focusedIndex].click();
        }
      });
    }
  }
  
  /**
   * fetch product suggestions from the API based on the search term.
   * @param {string} searchTerm - the term to search for.
   */
  function fetchProductSuggestions(searchTerm) {
    const url = `/api/products/search?term=${encodeURIComponent(searchTerm)}`;

    fetch(url)
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then(products => {
        renderSuggestions(products.slice(0, 5)); // limit to 5 suggestions
      })
      .catch(error => {
        renderSuggestions([]);
      });
  }

  /**
   * render the product suggestions in the dropdown.
   * @param {Array} products - list of product objects to display as suggestions.
   */
  function renderSuggestions(products) {
    if (typeof searchSuggestions === 'undefined') {
      searchSuggestions = document.getElementById('search-suggestions');
    }
    if (typeof searchContainer === 'undefined') {
      searchContainer = document.getElementById('search-container');
    }
    if (typeof searchInput === 'undefined') {
      searchInput = document.getElementById('navbar-search');
    }
    searchSuggestions.innerHTML = '';
    if (products.length === 0) {
      searchSuggestions.innerHTML = '<p class="px-4 py-2 text-gray-500 text-sm">No products found.</p>';
      return;
    }

    products.forEach(product => {
      const suggestionItem = document.createElement('a');
      suggestionItem.href = `/product/${product.productId}`;
      suggestionItem.className = 'block px-4 py-2 text-gray-700 hover:bg-gray-50 hover:text-indigo-600 transition-all duration-200 text-sm font-medium';
      suggestionItem.textContent = product.name;
      suggestionItem.setAttribute('role', 'option');
      suggestionItem.setAttribute('tabindex', '0');
      suggestionItem.addEventListener('click', function() {
        searchContainer.classList.add('hidden');
        clearSuggestions();
        searchInput.value = product.name;
      });
      searchSuggestions.appendChild(suggestionItem);
    });
  }

  /**
   * clear the suggestions dropdown.
   */
  function clearSuggestions() {
    if (typeof searchSuggestions === 'undefined') {
      searchSuggestions = document.getElementById('search-suggestions');
    }
    searchSuggestions.innerHTML = '';
  }
  
  initializeSearch();
})();
