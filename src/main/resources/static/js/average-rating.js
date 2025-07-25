$(document).ready(function() {
    // initialise ratings on page load
    initializeRatings();
    
    // set up a MutationObserver to watch for dynamically added elements
    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            if (mutation.addedNodes && mutation.addedNodes.length > 0) {
                // Check if any new rateyo-rating elements were added
                let hasNewRatings = false;
                mutation.addedNodes.forEach(function(node) {
                    if (node.nodeType === 1 && node.querySelector) { // Element node
                        const newRatings = node.querySelectorAll('.rateyo-rating:not(.initialized)');
                        if (newRatings.length > 0) {
                            hasNewRatings = true;
                        }
                    }
                });
                
                if (hasNewRatings) {
                    // initialise the new ratings
                    initializeRatings();
                }
            }
        });
    });
    
    // start observing the document with the configured parameters
    observer.observe(document.body, { childList: true, subtree: true });
});

// function to initialise all uninitialised ratings
function initializeRatings() {
    $(".rateyo-rating:not(.initialized)").each(function() {
        var rating = $(this).data("rating");
        $(this).rateYo({
            rating: rating || 0,
            readOnly: true,
            starWidth: "16px",
            normalFill: "#d1d5db",
            ratedFill: "#f59e0b",
            spacing: "2px"
        }).addClass("initialized"); // Mark as initialized
    });
}
