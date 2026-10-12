const API = "http://localhost:8080/api/links";

let currentShortCode = "";

async function createLink() {

    const originalUrl = document.getElementById("originalUrl").value;
    const customAlias = document.getElementById("customAlias").value;
    const category = document.getElementById("category").value;

    const url =
        `${API}/shorten?originalUrl=${encodeURIComponent(originalUrl)}&customAlias=${encodeURIComponent(customAlias)}&category=${encodeURIComponent(category)}`;

    const response = await fetch(url, {
        method: "POST"
    });

    const data = await response.json();

    console.log(data);
    alert(JSON.stringify(data));

    currentShortCode = data.shortCode;

    if (!currentShortCode) {
        alert("shortCode was not received from backend.");
        return;
    }

    const shortLink =
        `http://localhost:8080/api/links/${currentShortCode}`;

    document.getElementById("resultSection").innerHTML = `
        <a id="shortLink"
           href="${shortLink}"
           target="_blank">
           ${shortLink}
        </a>

        <br><br>

        <button onclick="copyLink()">
            Copy Link
        </button>
    `;

    document.getElementById("qrImage").src =
        `${API}/${currentShortCode}/qr`;

    loadStatistics();
}

function copyLink() {

    const link = document.getElementById("shortLink").href;

    navigator.clipboard.writeText(link);

    alert("Link copied!");
}

async function loadStatistics() {

    const response =
        await fetch(`${API}/${currentShortCode}/stats`);

    const data = await response.json();

    document.getElementById("stats").innerHTML = `
        <p><strong>Original URL:</strong></p>
        <p>${data.originalUrl}</p>

        <br>

        <p><strong>Category:</strong> ${data.category}</p>

        <p><strong>Total Clicks:</strong> ${data.totalClicks}</p>
    `;
}

async function searchCategory() {

    const category =
        document.getElementById("searchCategory").value;

    const response =
        await fetch(`${API}/category/${category}`);

    const links =
        await response.json();

    let html = "";

    if (links.length === 0) {

        html = "<p>No links found.</p>";

    } else {

        links.forEach(link => {

            html += `
                <div class="card">

                    <h3>${link.shortCode}</h3>

                    <p>${link.originalUrl}</p>

                    <p>Clicks : ${link.totalClicks}</p>

                </div>
            `;

        });

    }

    document.getElementById("searchResults").innerHTML = html;
}