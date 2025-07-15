// current date in header
document.getElementById('current-date').textContent = new Date().toLocaleDateString('en-GB', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
});

// profile dropdown
document.addEventListener('DOMContentLoaded', function() {
    const profileIcon = document.getElementById('profileIcon');
    const profileDropdown = document.getElementById('profileDropdown');
    
    profileIcon.addEventListener('click', function() {
        profileDropdown.classList.toggle('hidden');
    });
    
    // close dropdown when clicking outside
    document.addEventListener('click', function(event) {
        if (!profileIcon.contains(event.target) && !profileDropdown.contains(event.target)) {
            profileDropdown.classList.add('hidden');
        }
    });
    
    // logout with feedback
    const logoutLink = document.querySelector('a[href="/logout"]');
    logoutLink.addEventListener('click', function(event) {
        event.preventDefault();
        Swal.fire({
            icon: 'success',
            title: 'Logging out',
            text: 'You have been successfully logged out.',
            confirmButtonText: 'OK',
            timer: 2000,
            timerProgressBar: true
        }).then(() => {
            window.location.href = '/logout';
        });
    });
});
