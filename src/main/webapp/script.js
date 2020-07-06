function ratePromoCode(tableRow, rating) {
    var videoUrl = document.getElementById('promoCodeTable').rows[tableRow].cells[0].firstChild.getAttribute("href");
    console.log(videoUrl);
    fetch('/promo-code?rating=' + rating + '&videoUrl=' + videoUrl, {method: 'POST'});
    getCodes();
}

async function getCodes() {
    const response = await fetch('/promo-code');
    const codes = await response.json();
    const tableEl = document.getElementById('promoCodeTable');
    tableEl.innerHTML =
                `<tr id="row0">
                    <th>VideoURL</th>
                    <th>PROMO CODE</th>
                    <th>Rating</th>
                    <th><button>Like</button></th>
                    <th><button>Dislike</button></th>
                </tr>`;
    const numOfCodes = Object.keys(codes).length;
    for (i = 0; i < numOfCodes; i++) {
        var row = tableEl.insertRow(-1);
        var videoUrl = row.insertCell(0);
        var promoCode = row.insertCell(1);
        var rating = row.insertCell(2);
        var likeButton = row.insertCell(3);
        var dislikeButton = row.insertCell(4);
        videoUrl.innerHTML = '<a href="' + codes[i].videoUrl +'">' + codes[i].videoUrl + '</a>'
        promoCode.innerHTML = codes[i].promoCode;
        rating.innerHTML = codes[i].rating;
        likeButton.innerHTML = '<button onClick="ratePromoCode(' + i + ', 1)">Like</button>'
        dislikeButton.innerHTML = '<button onClick="ratePromoCode(' + i + ', -1)">Dislike</button>'
    }
}
