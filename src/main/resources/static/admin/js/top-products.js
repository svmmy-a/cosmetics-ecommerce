document.addEventListener('DOMContentLoaded', function() {
    // to fetch top products data
    async function fetchTopProducts(range = 'monthly') {
        try {
            const response = await fetch(`/admin/top-products-data?range=${range}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            
            // update table content
            const tbody = document.querySelector('#topProductsTimeRange').closest('section').querySelector('tbody');
            tbody.innerHTML = '';
            
            if (data.length === 0) {
                tbody.innerHTML = `<tr class="text-gray-600"><td class="py-3 px-3" colspan="5">No top selling products data available for the selected period.</td></tr>`;
            } else {
                data.forEach(product => {
                    const row = document.createElement('tr');
                    row.className = 'border-b border-gray-200 text-gray-700 hover:bg-gray-100';
                    
                    row.innerHTML = `
                        <td class="py-3 px-3">${product.productName}</td>
                        <td class="py-3 px-3">
                            <span class="px-2 py-1 text-xs font-medium rounded-full ${
                                product.category === 'Skincare' ? 'bg-blue-100 text-blue-800' :
                                product.category === 'Haircare' ? 'bg-green-100 text-green-800' :
                                product.category === 'Makeup' ? 'bg-purple-100 text-purple-800' :
                                'bg-gray-100 text-gray-800'
                            }">${product.category}</span>
                        </td>
                        <td class="py-3 px-3">${product.inventoryQty !== null ? product.inventoryQty : 0}</td>
                        <td class="py-3 px-3">${product.unitsSold}</td>
                        <td class="py-3 px-3">£${product.earnings !== null ? product.earnings.toFixed(2) : '0.00'}</td>
                    `;
                    
                    tbody.appendChild(row);
                });
            }
        } catch (error) {
            console.error('Error fetching top products data:', error);
        }
    }

    // fetch data on page load
    fetchTopProducts();

    // listen for time range changes
    const timeRangeSelect = document.getElementById('topProductsTimeRange');
    if (timeRangeSelect) {
        timeRangeSelect.addEventListener('change', function() {
            fetchTopProducts(this.value);
        });
    }
});
