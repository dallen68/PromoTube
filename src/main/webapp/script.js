async function displayCodes() {
    var formInput = document.getElementById('formInput').value;
    const response = await fetch('/promo-code?formInput=' + formInput);
    const codes = await response.json();
    const tableEl = document.getElementById('promoCodeTable');
    tableEl.innerHTML =
                `<tr>
                    <th>VideoURL</th>
                    <th>PROMO CODE</th>
                </tr>`;
    if (codes == 0) {
        var row = tableEl.insertRow(-1);
        var noCodes = row.insertCell(0);
        noCodes.innerHTML = '<p>Sorry! There are no codes with this Id.</p>'
    } else {
        const numOfCodes = Object.keys(codes).length;
        for (i = 0; i < numOfCodes; i++) {
            var row = tableEl.insertRow(-1);
            var videoUrl = row.insertCell(0);
            var promoCode = row.insertCell(1);
            videoUrl.innerHTML = '<a href="https://www.youtube.com/watch?v=' + codes[i].videoId +'">https://www.youtube.com/watch?v=' + codes[i].videoId + '</a>';
            promoCode.innerHTML = codes[i].promoCode;
        }
    }
    
}
