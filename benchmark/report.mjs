const charts = document.querySelector('.charts');
const res = await fetch('./tests/report.json');
const report = await res.json();

Object.keys(report.charts).forEach(id => {
    const chart = report.charts[id];
    const key = chart.key;
    let div = document.querySelector(`.${key}`);
    if (!div) {
        div = document.createElement('div');
        div.classList.add('group');
        div.classList.add(key);
        charts.appendChild(div);
    }
    const table = document.createElement('table');
    table.id = key;
    table.setAttribute('class', chart.cssClass);
    const caption = document.createElement('caption');
    caption.textContent = chart.title;
    table.appendChild(caption);
    const tbody = document.createElement('tbody');

    const max = Math.max(...Object.values(chart.dataset).flatMap(d => d).flatMap(v => v.size)) + 1;
    let style;
    if (chart.cssClass.indexOf('column') > -1) {
        style = (v) => `--size: calc(${v} / ${max});`;
    } else if (chart.cssClass.indexOf('line') > -1) {
        style = (v, prev) => {
            return `--start: ${prev / max}; --end: ${v / max};`;
        };
    }
    let prev = [];
    Object.keys(chart.dataset).forEach(key => {
        const dataset = chart.dataset[key];
        const tr = document.createElement('tr');
        const th = document.createElement('th');
        th.textContent = key;
        th.setAttribute('scope', 'row');
        tr.appendChild(th);
        let add = false;
        for (let i = 0; i < dataset.length; i++) {
            const data = dataset[i];
            if (chart.cssClass.indexOf('line') > -1 && prev[i] || chart.cssClass.indexOf('line') < 0) {
                add = true;
                const td = document.createElement('td');
                td.setAttribute('style', style(data.size, prev[i]));
                const spanData = document.createElement('span');
                spanData.classList.add('data');
                spanData.textContent = data.value;
                td.appendChild(spanData);
                const spanTooltip = document.createElement('span');
                spanTooltip.classList.add('tooltip');
                spanTooltip.textContent = data.tooltip;
                td.appendChild(spanTooltip);
                tr.appendChild(td);
            }
            prev[i] = data.size;
        }
        if (add === true) {
            tbody.appendChild(tr);
        }
    });
    table.appendChild(tbody);

    const legend = document.createElement('ul');
    legend.setAttribute('class', 'charts-css legend legend-square')
    Object.values(chart.dataset).flatMap(d => d).flatMap(d => d.legend)
        .filter((value, index, array) => array.indexOf(value) === index).sort()
        .forEach(l => {
            const li = document.createElement('li');
            li.textContent = l;
            legend.appendChild(li);
        });

    div.appendChild(table);
    div.appendChild(legend);
});
