document.addEventListener('DOMContentLoaded', function() {
    // canvas
    const ctx = document.getElementById('productSalesChart').getContext('2d');
    
    // initialise pie chart
    const productSalesChart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: [],
            datasets: [{
                label: 'Units Sold',
                data: [],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.7)',
                    'rgba(54, 162, 235, 0.7)',
                    'rgba(255, 206, 86, 0.7)',
                    'rgba(75, 192, 192, 0.7)',
                    'rgba(153, 102, 255, 0.7)',
                    'rgba(255, 159, 64, 0.7)'
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                    'rgba(153, 102, 255, 1)',
                    'rgba(255, 159, 64, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            layout: {
                padding: {
                    left: 10,
                    right: 10,
                    top: 10,
                    bottom: 10
                }
            },
            plugins: {
                legend: {
                    position: 'bottom'
                },
                tooltip: {
                    mode: 'index',
                    intersect: true
                }
            }
        }
    });

    // to fetch product sales data
    async function fetchProductSalesData(range = 'last28days') {
        try {
            const response = await fetch(`/admin/product-sales-data?range=${range}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            
            // update chart data
            productSalesChart.data.labels = data.productNames;
            productSalesChart.data.datasets[0].data = data.unitsSold;
            productSalesChart.update();
        } catch (error) {
            console.error('Error fetching product sales data:', error);
        }
    }

    // fetch data on page load
    fetchProductSalesData();

    // listen for date range changes
    const dateRangeSelect = document.getElementById('dateRangeSelect');
    if (dateRangeSelect) {
        dateRangeSelect.addEventListener('change', function() {
            fetchProductSalesData(this.value);
        });
    }
});
