async function displayCodes() {
    var formInput = document.getElementById('formInput').value;
    console.log("HELLO");
    const response = await fetch('/promo-code?formInput=' + formInput);
    const codes = await response.json();
    const tableEl = document.getElementById('promoCodeTable');
    tableEl.innerHTML =
                `<tr>
                    <th id="table-header">Video</th>
                    <th id="table-header">Promo Code or Affiliate Link</th>
                    <th id="table-header">Description Snippet</th>
                </tr>`;
    if (codes == 0) {
        let row = tableEl.insertRow(-1);
        var noCodes = row.insertCell(0);
        noCodes.innerHTML = '<p>Sorry! There are no codes with this Id.</p>'
    } else {
        const numOfCodes = Object.keys(codes).length;
        for (i = 0; i < numOfCodes; i++) {
            let row = tableEl.insertRow(-1);
            var videoUrl = row.insertCell(0);
            var promoCode = row.insertCell(1);
            var descriptionSnippet = row.insertCell(2);
            videoUrl.innerHTML = '<a href="https://www.youtube.com/watch?v='
               + codes[i].videoId +'" target="_blank">'
               + codes[i].videoTitle + '</a>';
            promoCode.innerHTML = codes[i].promoCode;
            descriptionSnippet.innerHTML = boldString(codes[i].snippet, codes[i].promoCode);
        }
    }
}

function boldString(str, substr) {
    var strRegExp = new RegExp(substr, 'g');
    return str.replace(strRegExp, '<b>'+substr+'</b>');
  }
