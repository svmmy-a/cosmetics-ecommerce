document.addEventListener('DOMContentLoaded', function() {
    // select all elements with class 'show-password'
    document.querySelectorAll('.show-password').forEach(function(element) {
        element.addEventListener('click', function(e) {
            e.preventDefault();
            // find the associated password input field
            var passwordInput = this.closest('.form-group').querySelector('input[type="password"], input[type="text"]');
            if (passwordInput) {
                if (passwordInput.type === 'password') {
                    passwordInput.type = 'text';
                    this.textContent = 'Hide';
                } else {
                    passwordInput.type = 'password';
                    this.textContent = 'Show';
                }
            }
        });
    });
});
