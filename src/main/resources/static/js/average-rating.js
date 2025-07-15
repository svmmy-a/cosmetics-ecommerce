$(document).ready(function() {
    $(".rateyo-rating").each(function() {
        var rating = $(this).data("rating");
        $(this).rateYo({
            rating: rating,
            readOnly: true,
            starWidth: "16px",
            normalFill: "#d1d5db",
            ratedFill: "#f59e0b",
            spacing: "2px"
        });
    });
});
