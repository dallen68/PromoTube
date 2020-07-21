async function displayCodes() {
    var formInput = document.getElementById('formInput').value;
    const selected = document.querySelector('input[name="searchOption"]:checked');
    var response; 
    // TODO: Change name of URI's to appropriate servlet names.
    if(selected.id === "channel"){
        response = await fetch('/promo-code?formInput=' + formInput);
    } else {
       // TODO: Add business URI 
       response = await fetch('/promo-code?formInput=' + formInput);
    }
    const codes = await response.json();
    setTable(codes);
}

function setTable(codes) {
    const tableEl = document.getElementById('promoCodeTable');
    tableEl.innerHTML =
                `<tr>
                    <th>VideoURL</th>
                    <th>PROMO CODE</th>
                </tr>`;
    if (codes.length === 0) {
        let row = tableEl.insertRow(-1);
        var noCodes = row.insertCell(0);
        noCodes.innerHTML = '<p>Sorry! There are no codes with this Id.</p>'
    } else {
        const numOfCodes = Object.keys(codes).length;
        for (i = 0; i < numOfCodes; i++) {
            let row = tableEl.insertRow(-1);
            var videoUrl = row.insertCell(0);
            var promoCode = row.insertCell(1);
            videoUrl.innerHTML = '<a href="https://www.youtube.com/watch?v='
                + codes[i].videoId + '" target="_blank">'
                + codes[i].videoTitle + '</a>';
            promoCode.innerHTML = codes[i].promoCode;
        }
    }
}
