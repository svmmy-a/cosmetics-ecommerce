document.addEventListener('DOMContentLoaded', function() {
    // canvas
    const ctx = document.getElementById('salesChart').getContext('2d');
    
    //  initialise chart
    const salesChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                label: 'Sales (£)',
                data: [],
                borderColor: 'rgb(239, 68, 68)', // Orange line
                backgroundColor: 'rgba(239, 68, 68, 0.1)',
                fill: false,
                tension: 0.3,
                yAxisID: 'sales'
            }, {
                label: 'Items Ordered',
                data: [],
                borderColor: 'rgb(59, 130, 246)', // Blue line
                backgroundColor: 'rgba(59, 130, 246, 0.2)',
                fill: true, // Area chart effect
                tension: 0.3,
                yAxisID: 'items'
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
                sales: {
                    type: 'linear',
                    position: 'left',
                    title: {
                        display: true,
                        text: 'Sales (£)'
                    }
                },
                items: {
                    type: 'linear',
                    position: 'right',
                    title: {
                        display: true,
                        text: 'Items Ordered'
                    },
                    grid: {
                        drawOnChartArea: false 
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Date'
                    }
                }
            },
            plugins: {
                legend: {
                    position: 'bottom'
                },
                tooltip: {
                    mode: 'index',
                    intersect: false
                }
            },
            hover: {
                mode: 'nearest',
                intersect: true
            }
        }
    });

    // to fetch sales data
    async function fetchSalesData(range = 'last28days') {
        try {
            const response = await fetch(`/admin/sales-data?range=${range}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            
            // update chart data
            salesChart.data.labels = data.dates;
            salesChart.data.datasets[0].data = data.sales;
            salesChart.data.datasets[1].data = data.itemsOrdered;
            salesChart.update();
        } catch (error) {
            console.error('Error fetching sales data:', error);
            // display a message to the user
            // log the error to console for now
        }
    }

    // fetch data on page load
    fetchSalesData();

    // listen for date range chnages
    const dateRangeSelect = document.getElementById('dateRangeSelect');
    if (dateRangeSelect) {
        dateRangeSelect.addEventListener('change', function() {
            fetchSalesData(this.value);
        });
    }
});
