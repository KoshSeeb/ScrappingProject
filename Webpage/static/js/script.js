function searchProducts() {
    const searchInput = document.getElementById('searchInput').value;

    // Redirect to Result_page.html with the search query as a parameter
    window.location.href = `Result_page.html?q=${encodeURIComponent(searchInput)}`;
}

// Get the search query parameter from the URL
const searchQuery = new URLSearchParams(window.location.search).get('q');

// Make an AJAX request to the /search endpoint with the search query
fetch(`http://localhost:3000/search?q=${encodeURIComponent(searchQuery)}`)
    .then(response => response.json())
    .then(data => {
        console.log('Received data:', data);
        displaySearchResults(data);
    })
    .catch(error => console.error('Error:', error));

function displaySearchResults(results) {
    const contentDiv = document.getElementById('content');
    const result = document.getElementById('result');
    result.innerHTML = searchQuery

     // Clear previous results

    if (results.length != 0) {
        contentDiv.innerHTML = ''; 
    } 

    
  results.forEach(book => {
    const cardDiv = document.createElement('div');
    cardDiv.classList.add('card');

    const cardImageDiv = document.createElement('div');
    cardImageDiv.classList.add('card-image');
    const image = document.createElement('img');
    image.src = book.image_url; // Replace with the actual image source or a placeholder
    image.alt = 'Book Image';
    cardImageDiv.appendChild(image);

    const cardInfoDiv = document.createElement('div');
    cardInfoDiv.classList.add('card-info');

    // Add a click event listener to each card
    cardDiv.addEventListener('click', () => {
      // Redirect to comparison page with title and author parameters
      window.location.href = `Comparison_page.html?title=${book.title}&author=${book.author}`;
    //   window.location.href = `Comparison_page.html?title=${titleParam}&author=${authorParam}`;
    });

    cardInfoDiv.innerHTML = `
      <h3>${book.title}</h3>
      <p>Author: ${book.author}</p>
    `;

    cardDiv.appendChild(cardImageDiv);
    cardDiv.appendChild(cardInfoDiv);

    contentDiv.appendChild(cardDiv);
  });


    pagination()
}

'../static/image/Book4U_logo.png'


function getPageList(totalPages, page, maxLength){


    function range(start, end){
        return Array.from(Array(end - start + 1), (_, i) => i + start);
    }

    var sideWidth = maxLength < 9 ? 1 : 2;
    var leftWidth = (maxLength - sideWidth * 2 - 3) >> 1;
    var rightWidth = (maxLength - sideWidth * 2 - 3) >> 1;

    if(totalPages <= maxLength){
        return range(1, totalPages);
    }

    if(page <= maxLength - sideWidth - 1 - rightWidth){
        return range(1, maxLength - sideWidth -1).concat(0, range(totalPages - sideWidth + 1, totalPages));
    }

    if(page <= totalPages - sideWidth - 1 - rightWidth){
        return range(1, sideWidth).concat(0, range(totalPages - sideWidth - 1 - rightWidth - leftWidth, totalPages))
    }

    return range(1, sideWidth).concat(0, range(page - leftWidth,page + rightWidth), 0 ,range(totalPages - sideWidth + 1, totalPages));
}


function pagination() {
    var numberOfItems = $(".card-content .card").length;
    var limitPerPage = 6; //How many card items visible per a page
    var totalPages = Math.ceil(numberOfItems / limitPerPage);
    var paginationSize = 7; //How many page elements visible in the pagination
    var currentPage;

    function showPage(whichPage){
        if(whichPage < 1 || whichPage > totalPages) return false;
        
        currentPage = whichPage;
        
        $(".card-content .card").hide().slice((currentPage - 1) * limitPerPage, currentPage * limitPerPage).show();
        
        $(".pagination li").slice(1, -1).remove();
        
        getPageList(totalPages, currentPage, paginationSize).forEach(item => {
            $("<li>").addClass("page-item").addClass(item ? "current-page" : "dots")
            .toggleClass("active", item === currentPage).append($("<a>").addClass("page-link")
            .attr({href: "javascript:void(0)"}).text(item || "...")).insertBefore(".next-page");
        });

        $(".previous-page").toggleClass("disable", currentPage === 1);
        $(".next-page").toggleClass("disable", currentPage === totalPages);
        return true;
    }

    $(".pagination").append(
        $("<li>").addClass("page-item").addClass("previous-page").append($("<a>").addClass("page-link").attr({href: "javascript:void(0)"}).text("Prev")),
        $("<li>").addClass("page-item").addClass("next-page").append($("<a>").addClass("page-link").attr({href: "javascript:void(0)"}).text("Next")),
    );

    $(".card-content").show();
    showPage(1);

    $(document).on("click", ".pagination li.current-page:not(.active)", function(){
        return showPage(+$(this).text());
    });

    $(".next-page").on("click", function(){
        return showPage(currentPage + 1);
    });

    $(".previous-page").on("click", function(){
        return showPage(currentPage - 1);
    });
};
