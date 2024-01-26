const charts = document.querySelector('.charts');
const res = await fetch('./tests/report.json');
const report = await res.json();
Object.keys(report.charts).forEach(async id => {
    const chart = report.charts[id];
    const ctx = document.createElement('canvas');
    ctx.id = id;
    charts.appendChild(ctx);
    new Chart(ctx, {
        type: chart.type,
        data: {
            labels: chart.labels,
            datasets: chart.datasets
        }
    });
});
