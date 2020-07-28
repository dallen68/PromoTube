
const channelPlaceholder = "Enter a Channel's URL";
const businessPlaceholder = "Enter a Business's Name";
const loadingId = "loadingIcon";
const channelId = "channel";
const noCodesNode = document.createTextNode("Sorry! We didn\'t find any codes.");

async function displayCodes() {
    let formInput = document.getElementById('formInput').value;
    const selected = document.querySelector('input[name="searchOption"]:checked');
    let response;
    // Display loading icon.
    triggerLoading();
    if (selected.id === "channel") {
        response = await fetch('/channel/promo-codes?formInput=' + formInput);
    } else { 
       response = await fetch('/business/promo-codes?formInput=' + formInput);
    }
    const codes = await response.json();
    // Hide loading icon.
    triggerLoading();
    setTable(codes);
}

function setTable(codes) {
    const tableEl = document.getElementById('promoCodeTable');
    tableEl.innerHTML =
        `<tr>
                    <th id="table-header">Video</th>
                    <th id="table-header">Promo Code or Affiliate Link</th>
                    <th id="table-header">Description Snippet</th>
                </tr>`;
    if (codes.length === 0) {
        tableEl.appendChild(noCodesNode);
    } else {
        noCodesNode.remove();
        const numOfCodes = Object.keys(codes).length;
        for (i = 0; i < numOfCodes; i++) {
            let row = tableEl.insertRow(-1);
            let videoUrl = row.insertCell(0);
            let promoCode = row.insertCell(1);
            let descriptionSnippet = row.insertCell(2);
            videoUrl.innerHTML = '<a href="https://www.youtube.com/watch?v='
                + codes[i].videoId + '" target="_blank">'
                + codes[i].videoTitle + '</a>';
            promoCode.innerHTML = codes[i].promoCode;
            descriptionSnippet.innerHTML = boldSubstring(codes[i].snippet, codes[i].promoCode);
        }
    }
}

function boldSubstring(str, substr) {
    let strRegExp = new RegExp(substr, 'g');
    return str.replace(strRegExp, '<b>' + substr + '</b>');
}

function resetForm(id) {
    $("#formInput").val("");
    const placeholder = id === channelId ? channelPlaceholder : businessPlaceholder;
    $("#formInput").attr("placeholder", placeholder);
}

/**
 *  Display loading icon if it is not being displayed. Or hide the loading icon if it is displayed.
 */
function triggerLoading() {
    let loadingState = document.getElementById(loadingId).style.display;
    document.getElementById(loadingId).style.display = loadingState === "none" ? "inline-block" : "none";
}
