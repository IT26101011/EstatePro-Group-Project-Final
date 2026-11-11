// Shared helper functions for all Admin pages

// Show a message at the top of the page
function showMessage(text, isSuccess) {

    const message = document.getElementById("message");

    message.textContent = text;
    message.style.color = isSuccess ? "#087443" : "#b42318";
}

// Send a POST request with form data and return the text response
async function postForm(url, values) {

    const data = new URLSearchParams();

    for (const key in values) {
        data.append(key, values[key]);
    }

    const response = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: data
    });

    return await response.text();
}

// Get JSON data. Throws an error when the response is not a list
async function getList(url) {

    const response = await fetch(url);

    const text = await response.text();

    let result;

    try {
        result = JSON.parse(text);
    } catch (error) {
        throw new Error("Unable to load data. Please login as Admin.");
    }

    if (!Array.isArray(result)) {
        throw new Error("Unable to load data. Please login as Admin.");
    }

    return result;
}

// Create a table cell with plain text
function makeCell(row, text) {

    const cell = document.createElement("td");

    cell.textContent = (text === undefined || text === null || text === "")
        ? "-"
        : text;

    row.appendChild(cell);
}

// Create a coloured status badge cell
function makeBadgeCell(row, status) {

    const cell = document.createElement("td");
    const badge = document.createElement("span");

    badge.className = "badge " + status;
    badge.textContent = status;

    cell.appendChild(badge);
    row.appendChild(cell);
}

// Create a button
function makeButton(text, className, onClick) {

    const button = document.createElement("button");

    button.className = className;
    button.textContent = text;
    button.onclick = onClick;

    return button;
}

// Show "no data" text inside the table box
function showEmpty(box, text) {

    box.innerHTML = "";

    const div = document.createElement("div");
    div.className = "empty";
    div.textContent = text;

    box.appendChild(div);
}

// Build the whole table: headers + one row per item
function buildTable(box, headers, items, fillRow) {

    box.innerHTML = "";

    const table = document.createElement("table");
    const headRow = document.createElement("tr");

    for (const title of headers) {
        const th = document.createElement("th");
        th.textContent = title;
        headRow.appendChild(th);
    }

    table.appendChild(headRow);

    for (const item of items) {
        const row = document.createElement("tr");
        fillRow(row, item);
        table.appendChild(row);
    }

    box.appendChild(table);
}
