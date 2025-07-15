document.addEventListener('DOMContentLoaded', function() {
});

/**
 * opens modal for leaving or updating a review for a product
 * @param {HTMLElement} button - button element that triggered the modal
 */
function openReviewModalFromData(button) {
    const productId = button.getAttribute('data-product-id');
    const productName = button.getAttribute('data-product-name');
    const hasReviewed = button.getAttribute('data-has-reviewed') === 'true';
    const existingRating = button.getAttribute('data-existing-rating');
    const existingComment = button.getAttribute('data-existing-comment');
    Swal.fire({
        title: hasReviewed ? 'Update Review for ' + productName : 'Leave a Review for ' + productName,
        html: `
            <div class="mb-4">
                <label class="block text-gray-700 text-sm font-bold mb-2">Your Rating:</label>
                <div class="star-rating" style="display: flex; justify-content: center;">
                    <span class="star" data-value="1" style="cursor: pointer; font-size: 30px; color: #d1d5db; margin-right: 5px;">&#9733;</span>
                    <span class="star" data-value="2" style="cursor: pointer; font-size: 30px; color: #d1d5db; margin-right: 5px;">&#9733;</span>
                    <span class="star" data-value="3" style="cursor: pointer; font-size: 30px; color: #d1d5db; margin-right: 5px;">&#9733;</span>
                    <span class="star" data-value="4" style="cursor: pointer; font-size: 30px; color: #d1d5db; margin-right: 5px;">&#9733;</span>
                    <span class="star" data-value="5" style="cursor: pointer; font-size: 30px; color: #d1d5db; margin-right: 5px;">&#9733;</span>
                </div>
                <input type="hidden" id="rating" value="5">
            </div>
            <div class="mb-4">
                <label for="comment" class="block text-gray-700 text-sm font-bold mb-2">Comment:</label>
                <textarea id="comment" class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline h-24"></textarea>
            </div>
            <input type="hidden" id="productId" value="${productId}">
        `,
        confirmButtonText: hasReviewed ? 'Update Review' : 'Submit',
        showCancelButton: true,
        cancelButtonText: 'Cancel',
        didOpen: () => {
            if (hasReviewed) {
                document.getElementById('comment').value = existingComment;
                highlightStars(existingRating);
            } else {
                highlightStars(0); // start with 0 stars
            }
            setupStarListeners();
        },
        preConfirm: () => {
            const rating = document.getElementById('rating').value;
            const comment = Swal.getPopup().querySelector('#comment').value;
            const productId = Swal.getPopup().querySelector('#productId').value;
            
            return fetch('/customer/submit-review', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `productId=${productId}&rating=${rating}&comment=${encodeURIComponent(comment)}`
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error(response.statusText);
                }
                return response;
            })
            .catch(error => {
                Swal.showValidationMessage(`Request failed: ${error}`);
            });
        },
        allowOutsideClick: () => !Swal.isLoading()
    }).then((result) => {
        if (result.isConfirmed) {
            Swal.fire({
                title: 'Success!',
                text: hasReviewed ? 'Your review has been updated.' : 'Your review has been submitted.',
                icon: 'success',
                confirmButtonText: 'OK'
            }).then(() => {
                window.location.reload();
            });
        }
    });
}

/**
 * higlight stars up to the selected rating value
 * @param {number} rating - rating value to highlight up to
 */
function highlightStars(rating) {
    const stars = document.querySelectorAll('.star');
    stars.forEach(star => {
        const value = parseInt(star.getAttribute('data-value'));
        if (value <= rating) {
            star.style.color = '#fbbf24'; // yellow for selected stars
        } else {
            star.style.color = '#d1d5db'; // grey for unselected stars
        }
    });
    document.getElementById('rating').value = rating;
}

/**
 *  event listeners for star clicks
 */
function setupStarListeners() {
    const stars = document.querySelectorAll('.star');
    stars.forEach(star => {
        star.addEventListener('click', function() {
            const rating = parseInt(this.getAttribute('data-value'));
            highlightStars(rating);
        });
        star.addEventListener('mouseover', function() {
            const rating = parseInt(this.getAttribute('data-value'));
            highlightStars(rating);
        });
        star.addEventListener('mouseout', function() {
            const currentRating = parseInt(document.getElementById('rating').value);
            highlightStars(currentRating);
        });
    });
}
