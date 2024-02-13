const charts = document.querySelector('.charts');
const res = await fetch('./tests/report.json');
const report = await res.json();

Object.keys(report.charts).forEach(async id => {
    const chart = report.charts[id];
    const key = chart.key;
    let div = document.querySelector(`.${key}`);
    if (!div) {
        div = document.createElement('div');
        div.classList.add('group');
        div.classList.add(key);
        charts.appendChild(div);
    }
    const ctx = document.createElement('canvas');
    ctx.id = id;
    div.appendChild(ctx);
    new Chart(ctx, {
        type: chart.type,
        data: {
            labels: chart.labels,
            datasets: chart.datasets
        }
    });
});
