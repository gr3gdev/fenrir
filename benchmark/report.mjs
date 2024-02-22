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

    const values = Object.values(chart.dataset).flatMap(d => d).flatMap(v => v.size);
    const min = Math.min(...values);
    const max = Math.max(...values);

    const compare = document.createElement('div');
    compare.classList.add('compare')
    const compareMin = document.createElement('p');
    const compareMax = document.createElement('p');
    compare.appendChild(compareMin);
    compare.appendChild(compareMax);

    const minValues = [];
    const maxValues = [];
    Object.keys(chart.dataset).forEach(key => {
        const dataset = chart.dataset[key];
        const tr = document.createElement('tr');
        const th = document.createElement('th');
        th.textContent = key;
        th.setAttribute('scope', 'row');
        tr.appendChild(th);
        for (let i = 0; i < dataset.length; i++) {
            const data = dataset[i];
            const td = document.createElement('td');
            let style = `--size: calc(${data.size} / ${max});`;
            if (data.size === min) {
                minValues.push(data.legend);
                style += ' background-image: repeating-linear-gradient(45deg, transparent, transparent 2px, rgba(0,255,0,.3) 2px, rgba(0,255,0,.1) 20px);';
            }
            if (data.size === max) {
                maxValues.push(data.legend);
                style += ' background-image: repeating-linear-gradient(45deg, transparent, transparent 2px, rgba(255,0,0,.3) 2px, rgba(255,0,0,.1) 20px);';
            }
            td.setAttribute('style', style);
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
        tbody.appendChild(tr);
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

    compareMin.textContent = `Min value : ${minValues.filter((value, index, array) => array.indexOf(value) === index).join(', ')}`;
    compareMax.textContent = `Max value : ${maxValues.filter((value, index, array) => array.indexOf(value) === index).join(', ')}`;
    legend.appendChild(compare);

    div.appendChild(table);
    div.appendChild(legend);
});
