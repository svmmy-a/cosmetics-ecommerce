// JS validation here
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const email = document.getElementById('email');
    const confirmEmail = document.getElementById('confirmEmail');

    form.addEventListener('submit', function(event) {
        let isValid = true;

        if (password.value.length < 8 || !/[a-z]/.test(password.value) || !/[A-Z]/.test(password.value) || !/\d/.test(password.value)) {
            alert('Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one number.');
            isValid = false;
        }

        if (password.value !== confirmPassword.value) {
            alert('Passwords do not match.');
            isValid = false;
        }

        if (email.value !== confirmEmail.value) {
            alert('Emails do not match.');
            isValid = false;
        }

        if (!isValid) {
            event.preventDefault(); // prevent form submission if validation fails
        }
    });
});
