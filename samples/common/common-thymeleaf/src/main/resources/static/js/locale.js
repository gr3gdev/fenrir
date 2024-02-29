document.addEventListener('DOMContentLoaded', (event) => {
    document.querySelector('#select-locale').addEventListener('change', () => {
        document.querySelector('form').submit();
    });
});