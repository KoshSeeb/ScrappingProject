// Get the title and author parameters from the URL
const titleParam = new URLSearchParams(window.location.search).get('title');
const authorParam = new URLSearchParams(window.location.search).get('author');

// Make an AJAX request to the /bookComparison endpoint with title and author parameters
fetch(`http://localhost:3000/bookComparison?title=${titleParam}&author=${authorParam}`)
  .then(response => response.json())
  .then(bookDetailsArray => {
    console.log('Received book details:', bookDetailsArray);
    displayBookDetails(bookDetailsArray);
  })
  .catch(error => console.error('Error:', error));

function displayBookDetails(bookDetailsArray) {
  const bookDetailsContainer = document.getElementById('bookDetailsContainer');

  
  if (bookDetailsArray && bookDetailsArray.length > 0) {
    // Display book details for each book in the array
    bookDetailsArray.forEach(bookDetails => {
      const detailsDiv = document.createElement('div');
      detailsDiv.classList.add('book-details');

      detailsDiv.innerHTML = `
        <h3>${bookDetails.title}</h3>
        <p>Author: ${bookDetails.author}</p>
        <p>ISBN: ${bookDetails.isbn}</p>
        <p>Description: ${bookDetails.description}</p>
        <p>Book Condition: ${bookDetails.book_condition}</p>
        <p>Stock: ${bookDetails.stock}</p>
        <p>Book Type: ${bookDetails.book_type}</p>
        <!-- Add more details as needed -->
      `;

      bookDetailsContainer.appendChild(detailsDiv);
    });
  } else {
    // No books found
    bookDetailsContainer.innerHTML = '<p>No books found.</p>';
  }
}
