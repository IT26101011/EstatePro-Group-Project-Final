document.addEventListener("DOMContentLoaded", function () {

    const searchButton = document.getElementById("searchButton");
    const resultsSection = document.getElementById("searchResultsSection");
    const resultsContainer = document.getElementById("searchResults");
    const searchMessage = document.getElementById("searchMessage");

    if (!searchButton) {
        console.error("Search button not found!");
        return;
    }

    searchButton.addEventListener("click", searchProperties);

    async function searchProperties() {

        const location = document.getElementById("searchLocation").value;
        const propertyType = document.getElementById("searchPropertyType").value;
        const minPrice = document.getElementById("minPrice").value;
        const maxPrice = document.getElementById("maxPrice").value;

        // Validate price range
        if (minPrice && maxPrice && Number(minPrice) > Number(maxPrice)) {
            resultsSection.hidden = false;
            searchMessage.textContent =
                "Minimum price cannot be greater than maximum price.";

            resultsContainer.replaceChildren();
            return;
        }

        // Build search parameters
        const params = new URLSearchParams();

        if (location) params.append("location", location);
        if (propertyType) params.append("propertyType", propertyType);
        if (minPrice) params.append("minPrice", minPrice);
        if (maxPrice) params.append("maxPrice", maxPrice);

        const url = "/property/filter?" + params.toString();

        // Show loading message
        resultsSection.hidden = false;
        resultsContainer.replaceChildren();
        searchMessage.textContent = "Searching properties...";

        searchButton.disabled = true;

        try {

            const response = await fetch(url);

            if (!response.ok) {
                throw new Error("Unable to search properties.");
            }

            const properties = await response.json();

            resultsContainer.replaceChildren();

            if (!Array.isArray(properties)) {
                throw new Error("Invalid response from server.");
            }

            if (properties.length === 0) {
                searchMessage.textContent =
                    "No properties found. Please try different search criteria.";
                return;
            }

            searchMessage.textContent =
                properties.length + " properties found.";

            properties.forEach(function (property) {
                displayProperty(property);
            });

            resultsSection.scrollIntoView({
                behavior: "smooth",
                block: "start"
            });

        } catch (error) {

            console.error("Property search error:", error);

            searchMessage.textContent =
                "Something went wrong. Please try again.";

        } finally {

            searchButton.disabled = false;

        }
    }

    function displayProperty(property) {

        const card = document.createElement("div");
        card.className = "property-card";

        const imageArea = document.createElement("div");
        imageArea.className = "property-image";

        const badge = document.createElement("span");
        badge.className = "badge";
        badge.textContent = property.listingType || "Property";

        imageArea.appendChild(badge);

        const info = document.createElement("div");
        info.className = "property-info";

        const title = document.createElement("h3");
        title.textContent = property.title || "Untitled Property";

        const type = document.createElement("p");
        type.textContent = property.propertyType || "Property";

        const location = document.createElement("p");
        location.className = "location";
        location.textContent = property.location || "Location unavailable";

        const price = document.createElement("h3");
        price.className = "price";

        const priceValue = Number(property.price);

        price.textContent = Number.isFinite(priceValue)
            ? "Rs. " + priceValue.toLocaleString("en-LK")
            : "Price unavailable";

        const details = document.createElement("div");
        details.className = "property-details";

        const bedrooms = document.createElement("span");
        bedrooms.textContent = "Bedrooms: " + (property.bedrooms ?? "N/A");

        const bathrooms = document.createElement("span");
        bathrooms.textContent = "Bathrooms: " + (property.bathrooms ?? "N/A");

        const area = document.createElement("span");
        area.textContent = "Area: " + (property.area ?? "N/A");

        details.appendChild(bedrooms);
        details.appendChild(bathrooms);
        details.appendChild(area);

        info.appendChild(title);
        info.appendChild(type);
        info.appendChild(location);
        info.appendChild(price);
        info.appendChild(details);

        card.appendChild(imageArea);
        card.appendChild(info);

        resultsContainer.appendChild(card);
    }

});