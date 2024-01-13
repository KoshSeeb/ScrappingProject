// Variable to keep track of the current page
let currentPage = 1;

// Extract search query parameter from the URL
const searchQuery = new URLSearchParams(window.location.search).get('q');

// Function to fetch search results from the server
function fetchResults(page) {
    fetch(`http://localhost:3000/search?q=${encodeURIComponent(searchQuery)}&page=${page}`)
      .then(response => response.json())
      .then(data => {
        console.log('Received data:', data);
        displaySearchResults(data);
      })
      .catch(error => console.error('Error:', error));
  }


  // Initial fetch on page load
  window.onload = function() {
    fetchResults(currentPage);
};

// Function to display search results on the page
function displaySearchResults(results) {
    const contentDiv = document.getElementById('content');
    const result = document.getElementById('result');
    result.innerHTML = searchQuery

     // Clear previous results

    if (results.length != 0) {
        contentDiv.innerHTML = ''; 
    } 

    // Loop through each book in the results and create a card for it
  results.forEach(book => {
    const cardDiv = document.createElement('div');
    cardDiv.classList.add('card');

    const cardImageDiv = document.createElement('div');
    cardImageDiv.classList.add('card-image');
    const image = document.createElement('img');
    image.src = book.image_url; 
    image.alt = 'Book Image';
    cardImageDiv.appendChild(image);

    const cardInfoDiv = document.createElement('div');
    cardInfoDiv.classList.add('card-info');

    // Add a click event listener to each card
    cardDiv.addEventListener('click', () => {
 // Add a click event listener to each card to navigate to the comparison page
      window.location.href = `Comparison_page.html?title=${book.title}&author=${book.author}`;
    });

    cardInfoDiv.innerHTML = `
      <h3>${book.title}</h3>
      <p>Author: ${book.author}</p>
    `;

    cardDiv.appendChild(cardImageDiv);
    cardDiv.appendChild(cardInfoDiv);

    contentDiv.appendChild(cardDiv);
  });

    // Show the card content
 $(".card-content").show();

}



// Event listener for the next page button
$(".next-page").on("click", function(){
        currentPage++;
        fetchResults(currentPage);
        console.log("Page:" + currentPage)
    });


// Event listener for the previous page button
$(".previous-page").on("click", function(){
        if (currentPage > 1) {
            currentPage--;
            fetchResults(currentPage);
            console.log(currentPage)

          }
    });

