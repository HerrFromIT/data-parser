let currentDisplayed = 20;
const loadIncrement = 10;
let totalItems = 0;
let isLoading = false;
let isSorted = false;
let originalOrder = [];
let sortedData = [];

// Initial setup
document.addEventListener('DOMContentLoaded', function () {
    totalItems = document.querySelectorAll('#tableBody tr').length;
    updateLoadMoreButton();
    initializeSorting();
    
    // Prüfe beim Start, ob Loading-Spinner versteckt werden muss
    checkAndHideLoadingIfNoData();

    // Infinite scroll - nur wenn noch versteckte Zeilen verfügbar sind
    window.addEventListener('scroll', function () {
        if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 1000) {
            const hiddenRows = document.querySelectorAll('.hidden-row');
            const loadingSpinner = document.getElementById('loadingSpinner');
            
            // Verstecke Loading-Spinner wenn keine Daten mehr verfügbar sind
            if (hiddenRows.length === 0) {
                loadingSpinner.style.display = 'none';
                return;
            }
            
            if (!isLoading && hiddenRows.length > 0) {
                loadMoreData();
            }
        }
    });
});

function initializeSorting() {
    // Store original order
    const rows = document.querySelectorAll('#tableBody tr');
    originalOrder = Array.from(rows);

    // Create sorted data array
    sortedData = Array.from(rows).map((row, index) => {
        const nameCell = row.querySelector('td:nth-child(2) strong');
        const name = nameCell ? nameCell.textContent.trim() : '';
        return {
            row: row,
            name: name,
            originalIndex: index
        };
    });
}

function sortByName() {
    const table = document.getElementById('landsTable');
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));

    rows.sort((a, b) => {
        const nameA = a.cells[1].textContent.trim();
        const nameB = b.cells[1].textContent.trim();
        return nameA.localeCompare(nameB, 'de');
    });

    rows.forEach(row => tbody.appendChild(row));

    const sortBtn = document.getElementById('sortBtn');
    sortBtn.classList.add('sort-active');
    setTimeout(() => sortBtn.classList.remove('sort-active'), 2000);
}

function resetLazyLoading() {
    // Hide all rows except first 20
    const rows = document.querySelectorAll('#tableBody tr');
    rows.forEach((row, index) => {
        if (index < 20) {
            row.classList.remove('hidden-row');
            row.classList.add('fade-in');
        } else {
            row.classList.add('hidden-row');
            row.classList.remove('fade-in');
        }
    });

    currentDisplayed = 20;
    updateLoadMoreButton();
}

function loadMoreData() {
    if (isLoading) {
        return; // Verhindere mehrfache Ausführung
    }
    
    const hiddenRows = document.querySelectorAll('.hidden-row');
    const loadMoreBtn = document.getElementById('loadMoreBtn');
    const loadingSpinner = document.getElementById('loadingSpinner');

    if (hiddenRows.length === 0) {
        loadMoreBtn.style.display = 'none';
        return;
    }

    isLoading = true;
    loadingSpinner.style.display = 'block';
    loadMoreBtn.style.display = 'none';

    setTimeout(() => {
        const rowsToShow = Math.min(10, hiddenRows.length);
        for (let i = 0; i < rowsToShow; i++) {
            hiddenRows[i].classList.remove('hidden-row');
            hiddenRows[i].classList.add('fade-in');
        }

        currentDisplayed += rowsToShow;
        loadingSpinner.style.display = 'none';
        isLoading = false;

        // Prüfe, ob noch mehr Daten zu laden sind
        const remainingHiddenRows = document.querySelectorAll('.hidden-row');
        if (remainingHiddenRows.length > 0) {
            loadMoreBtn.style.display = 'block';
            updateLoadMoreButton();
        } else {
            // Keine Daten mehr verfügbar - verstecke alle Loading-Elemente
            loadMoreBtn.style.display = 'none';
            loadingSpinner.style.display = 'none';
            console.log('Alle Daten geladen - Loading gestoppt');
        }
    }, 500);
}

function checkAndHideLoadingIfNoData() {
    const hiddenRows = document.querySelectorAll('.hidden-row');
    const loadingSpinner = document.getElementById('loadingSpinner');
    const loadMoreBtn = document.getElementById('loadMoreBtn');
    
    if (hiddenRows.length === 0) {
        loadingSpinner.style.display = 'none';
        loadMoreBtn.style.display = 'none';
        console.log('Keine versteckten Daten - Loading-Elemente versteckt');
    }
}

function updateLoadMoreButton() {
    const loadMoreBtn = document.getElementById('loadMoreBtn');
    const loadMoreContainer = document.getElementById('loadMoreContainer');

    if (currentDisplayed >= totalItems) {
        loadMoreContainer.style.display = 'none';
    } else {
        loadMoreBtn.style.display = 'block';
        const remaining = totalItems - currentDisplayed;
        const loadAmount = Math.min(loadIncrement, remaining);
        loadMoreBtn.innerHTML = `<i class="bi bi-arrow-down"></i> Weitere ${loadAmount} laden (${remaining} verbleibend)`;
    }
}

function exportToCSV() {
    const table = document.getElementById('landsTable');
    const rows = table.querySelectorAll('tbody tr');
    let csv = 'Land,Datenquelle,Datensätze,Ausführungszeit,Status\n';

    rows.forEach(row => {
        const land = row.cells[1].textContent.trim();
        const dataSource = row.cells[2].textContent.trim();
        const dataCount = row.cells[3].textContent.trim();
        const executionTime = row.cells[4].textContent.trim();
        const status = row.cells[6].textContent.trim();

        csv += `"${land}","${dataSource}","${dataCount}","${executionTime}","${status}"\n`;
    });

    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'laender_daten.csv';
    a.click();
    window.URL.revokeObjectURL(url);
} 