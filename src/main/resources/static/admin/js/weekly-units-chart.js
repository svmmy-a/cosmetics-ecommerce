document.addEventListener('DOMContentLoaded', function() {
    // canvas
    const ctx = document.getElementById('weeklyUnitsChart').getContext('2d');
    
    // initialise bar
    const weeklyUnitsChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: [],
            datasets: [{
                label: 'Units Sold',
                data: [],
                backgroundColor: 'rgba(54, 162, 235, 0.7)',
                borderColor: 'rgba(54, 162, 235, 1)',
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
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Units Sold'
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Week'
                    }
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

    // fetch weekly sales data
    async function fetchWeeklySalesData(range = 'last28days') {
        try {
            const response = await fetch(`/admin/sales-data?range=${range}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            
            // update chart data
            weeklyUnitsChart.data.labels = data.dates;
            weeklyUnitsChart.data.datasets[0].data = data.itemsOrdered;
            weeklyUnitsChart.update();
        } catch (error) {
            console.error('Error fetching weekly sales data:', error);
        }
    }

    // fetch data on page load
    fetchWeeklySalesData();

    // listen for date range changes
    const dateRangeSelect = document.getElementById('dateRangeSelect');
    if (dateRangeSelect) {
        dateRangeSelect.addEventListener('change', function() {
            fetchWeeklySalesData(this.value);
        });
    }
});
