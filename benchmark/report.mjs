const charts = document.querySelector('.charts');
const res = await fetch('./tests/report.json');
const report = await res.json();
let bad = 0;
let warn = 0;
let scoreFenrir = 0;
let scoreQuarkus = 0;
let scoreSpring = 0;
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
    if (chart.labels.length === 3) {
        const idxSpring = chart.labels.indexOf('Spring');
        const idxQuarkus = chart.labels.indexOf('Quarkus');
        const idxFenrir = chart.labels.indexOf('Fenrir');
        if (chart.datasets[0].data[idxFenrir] > chart.datasets[0].data[idxSpring]
            && chart.datasets[0].data[idxFenrir] > chart.datasets[0].data[idxQuarkus]) {
            ctx.classList.add('bad');
            bad++;
        } else if (chart.datasets[0].data[idxFenrir] > chart.datasets[0].data[idxSpring]
            || chart.datasets[0].data[idxFenrir] > chart.datasets[0].data[idxQuarkus]) {
            ctx.classList.add('warn');
            warn++;
        }
        if (chart.datasets[0].data[idxFenrir] < chart.datasets[0].data[idxSpring]
            && chart.datasets[0].data[idxFenrir] < chart.datasets[0].data[idxQuarkus]) {
            ctx.classList.add('great');
            scoreFenrir++;
        }
        if (chart.datasets[0].data[idxSpring] < chart.datasets[0].data[idxFenrir]
            && chart.datasets[0].data[idxSpring] < chart.datasets[0].data[idxQuarkus]) {
            scoreSpring++;
        }
        if (chart.datasets[0].data[idxQuarkus] < chart.datasets[0].data[idxSpring]
            && chart.datasets[0].data[idxQuarkus] < chart.datasets[0].data[idxFenrir]) {
            scoreQuarkus++;
        }
    }
    div.appendChild(ctx);
    new Chart(ctx, {
        type: chart.type,
        data: {
            labels: chart.labels,
            datasets: chart.datasets
        }
    });
});

document.querySelector('#scoreSpring').innerText = scoreSpring;
document.querySelector('#scoreQuarkus').innerText = scoreQuarkus;
document.querySelector('#scoreFenrir').innerText = scoreFenrir;

const compareElement = document.querySelector('.compare');
const bads = document.createElement('div');
bads.classList.add('bad');
bads.innerText = `${bad} bad requests (> Spring and > Quarkus)`;
compareElement.parentNode.insertBefore(bads, compareElement.nextSibling);
const warns = document.createElement('div');
warns.classList.add('warn');
warns.innerText = `${warn} warnings (> Spring or > Quarkus)`;
compareElement.parentNode.insertBefore(warns, compareElement.nextSibling);
