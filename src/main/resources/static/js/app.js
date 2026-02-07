const API_BASE = 'http://localhost:8080/api';

// --- Dashboard ---
async function loadDashboardStats() {
    try {
        const stats = await fetch(`${API_BASE}/reports/dashboard-stats`).then(res => res.json());

        document.getElementById('totalCustomersCount').innerText = stats.totalCustomers;
        document.getElementById('totalCottonWeight').innerText = stats.totalCottonKg.toFixed(2);
        document.getElementById('totalValue').innerText = '₹ ' + stats.totalAmount.toFixed(2);

        const villageList = document.getElementById('topVillagesList');
        villageList.innerHTML = '';
        stats.topVillages.forEach(v => {
            villageList.innerHTML += `<li class="list-group-item d-flex justify-content-between align-items-center">
                ${v.village}
                <span class="badge bg-primary rounded-pill">${v.qty.toFixed(1)} Kg</span>
            </li>`;
        });

        const customerList = document.getElementById('topCustomersList');
        customerList.innerHTML = '';
        stats.topCustomers.forEach(c => {
            customerList.innerHTML += `<li class="list-group-item d-flex justify-content-between align-items-center">
                ${c.name}
                <span class="badge bg-success rounded-pill">₹ ${c.amount.toFixed(0)}</span>
            </li>`;
        });
    } catch (e) {
        console.error("Error loading dashboard stats", e);
    }
}

// --- Customers ---
async function loadCustomers() {
    const customers = await fetch(`${API_BASE}/customers`).then(res => res.json());
    const tbody = document.getElementById('customersTableBody');
    tbody.innerHTML = '';
    customers.forEach(c => {
        const row = `<tr>
            <td>${c.customer.customerId}</td>
            <td><a href="customer-details.html?id=${c.customer.customerId}">${c.customer.name}</a></td>
            <td>${c.customer.contactNumber}</td>
            <td>${c.customer.village || '-'}</td>
            <td>${c.customer.address}</td>
            <td class="${c.currentBalance >= 0 ? 'text-success' : 'text-danger'}">₹${c.currentBalance.toFixed(2)}</td>
        </tr>`;
        tbody.innerHTML += row;
    });
}

async function createCustomer() {
    const name = document.getElementById('custName').value;
    const contact = document.getElementById('custContact').value;
    const village = document.getElementById('custVillage').value;
    const address = document.getElementById('custAddress').value;

    const res = await fetch(`${API_BASE}/customers`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            name,
            contactNumber: contact,
            village: village,
            address
        })
    });

    if (res.ok) {
        alert('Customer Added!');
        window.location.reload();
    } else {
        alert('Failed to add customer');
    }
}

// --- Customer Details ---
async function loadCustomerDetails() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');
    if (!id) return;

    const c = await fetch(`${API_BASE}/customers/${id}`).then(res => res.json());

    document.getElementById('cName').innerText = c.customer.name;
    document.getElementById('cContact').innerText = c.customer.contactNumber;
    document.getElementById('cVillage').innerText = c.customer.village || '-';
    document.getElementById('cAddress').innerText = c.customer.address;
    document.getElementById('cBalance').innerText = '₹ ' + c.currentBalance.toFixed(2);
    document.getElementById('cBalance').className = c.currentBalance >= 0 ? "h4 text-success" : "h4 text-danger";

    // Load Cotton Entries
    const entries = await fetch(`${API_BASE}/entries/customer/${id}`).then(res => res.json());
    const entriesTable = document.getElementById('entriesTable');
    entriesTable.innerHTML = '';
    entries.forEach(e => {
        entriesTable.innerHTML += `<tr>
            <td>${e.date}</td>
            <td>${e.cottonType || '-'}</td>
            <td>${e.cottonQuantityKg}</td>
            <td>${e.pricePerKg}</td>
            <td>${e.totalAmount.toFixed(2)}</td>
            <td>${e.notes || ''}</td>
        </tr>`;
    });

    // Load Transactions
    const txs = await fetch(`${API_BASE}/transactions/customer/${id}`).then(res => res.json());
    const txTable = document.getElementById('txTable');
    txTable.innerHTML = '';
    txs.forEach(t => {
        txTable.innerHTML += `<tr>
            <td>${t.transactionDate}</td>
            <td><span class="badge bg-${t.type === 'TAKE' ? 'success' : 'danger'}">${t.type}</span></td>
            <td>${t.amount}</td>
            <td>${t.paymentMode}</td>
            <td>${t.remarks || ''}</td>
        </tr>`;
    });
}

async function addEntry() {
    const params = new URLSearchParams(window.location.search);
    const customerId = params.get('id');

    const type = document.getElementById('eType').value;
    const qty = document.getElementById('eQty').value;
    const price = document.getElementById('ePrice').value;
    const notes = document.getElementById('eNotes').value;

    const res = await fetch(`${API_BASE}/entries`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            customer: { customerId: customerId },
            cottonType: type,
            cottonQuantityKg: qty,
            pricePerKg: price,
            notes: notes
        })
    });

    if (res.ok) {
        window.location.reload();
    } else {
        alert('Error adding entry');
    }
}

async function addTransaction() {
    const params = new URLSearchParams(window.location.search);
    const customerId = params.get('id');

    const type = document.getElementById('tType').value;
    const amount = document.getElementById('tAmount').value;
    const mode = document.getElementById('tMode').value;
    const remarks = document.getElementById('tRemarks').value;

    const res = await fetch(`${API_BASE}/transactions`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            customer: { customerId: customerId },
            type: type,
            amount: amount,
            paymentMode: mode,
            remarks: remarks
        })
    });

    if (res.ok) {
        window.location.reload();
    } else {
        alert('Error adding transaction');
    }
}

// --- Cotton Records (Sorting & Filtering) ---
let currentSortBy = 'date';
let currentSortDir = 'desc';

function setSort(field) {
    if (currentSortBy === field) {
        currentSortDir = currentSortDir === 'asc' ? 'desc' : 'asc';
    } else {
        currentSortBy = field;
        currentSortDir = 'asc';
    }
    loadCottonRecords();
}

function resetFilters() {
    document.getElementById('filterBy').value = '';
    document.getElementById('filterValue').value = '';
    document.getElementById('startDate').value = '';
    document.getElementById('endDate').value = '';
    loadCottonRecords();
}

async function loadCottonRecords() {
    const filterBy = document.getElementById('filterBy').value;
    const filterValue = document.getElementById('filterValue').value;
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    let url = `${API_BASE}/entries/all?sortBy=${currentSortBy}&direction=${currentSortDir}`;

    if (filterBy && filterValue) {
        url += `&filterBy=${filterBy}&filterValue=${filterValue}`;
    }
    if (startDate && endDate) {
        url += `&startDate=${startDate}&endDate=${endDate}`;
    }

    try {
        const entries = await fetch(url).then(res => res.json());
        const tbody = document.getElementById('recordsTable');
        tbody.innerHTML = '';

        if (entries.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center">No records found</td></tr>';
            return;
        }

        entries.forEach(e => {
            tbody.innerHTML += `<tr>
                <td>${e.date}</td>
                <td>${e.customer.name}</td>
                <td>${e.customer.village || '-'}</td>
                <td>${e.cottonType || '-'}</td>
                <td>${e.cottonQuantityKg}</td>
                <td>${e.pricePerKg}</td>
                <td class="fw-bold">₹${e.totalAmount.toFixed(2)}</td>
            </tr>`;
        });
    } catch (e) {
        console.error("Error loading records", e);
    }
}
