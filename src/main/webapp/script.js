//This function is not currently in use but may be used in further implementations
// function ratePromoCode(tableRow, rating) {
//     var videoUrl = document.getElementById('promoCodeTable').rows[tableRow].cells[0].firstChild.getAttribute("href");
//     console.log(videoUrl);
//     fetch('/promo-code?rating=' + rating + '&videoUrl=' + videoUrl, {method: 'POST'});
//     getCodes();
// }

function getCodes() {
    fetch('/promo-code').then(response => response.json()).then((codes) => {
        const tableEl = document.getElementById('promoCodeTable');
        tableEl.innerHTML =
                    `<tr id="row0">
                        <th>VideoURL</th>
                        <th>PROMO CODE</th>
                    </tr>`;
        const numOfCodes = Object.keys(codes).length;
        for (i = 0; i < numOfCodes; i++) {
            var row = tableEl.insertRow(-1);
            var videoUrl = row.insertCell(0);
            var promoCode = row.insertCell(1);
            videoUrl.innerHTML = '<a href="' + codes[i].videoUrl +'">' + codes[i].videoUrl + '</a>'
            promoCode.innerHTML = codes[i].promoCode;
        }
    });
}
