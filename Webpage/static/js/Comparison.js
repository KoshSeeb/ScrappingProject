// Get the title and author parameters from the URL
const titleParam = new URLSearchParams(window.location.search).get('title');
const authorParam = new URLSearchParams(window.location.search).get('author');

// Make an AJAX request to fetch detailed information about the selected book
fetch(`http://localhost:3000/getSelected?title=${titleParam}&author=${authorParam}`)
  .then(response => response.json())
  .then(Selected => {
    console.log('Received selected book details:', Selected);
    displaySelectedBook(Selected);
  })
  .catch(error => console.error('Error:', error));

// Make an AJAX request to fetch comparison details for related books
fetch(`http://localhost:3000/bookComparison?title=${titleParam}&author=${authorParam}`)
  .then(response => response.json())
  .then(bookDetailsArray => {
    console.log('Received book details:', bookDetailsArray);
        // Display the details of related books in a table
    displayBookDetails(bookDetailsArray);
  })
  .catch(error => console.error('Error:', error));

  // Function to display details of the selected book
function displaySelectedBook(Selected) {
  const bookDetailsContainer = document.getElementById('bookDetailsContainer');
  const result = document.getElementById('result');

  if (Selected && Selected.length > 0) {
    // Display the first book details in the left container
    const firstBookDetails = Selected[0];
    result.innerHTML = firstBookDetails.title;

    // Create the required format
    const leftContainer = document.createElement('div');
    leftContainer.classList.add('left-container');

    const top = document.createElement('div');
    top.id = 'top';
    top.innerHTML = `
      <img src="${firstBookDetails.image_url}" alt="Product Image" style="max-width: 100%; height: auto;">
      <div class="top-right">
        <span> Price:</span>
        <span id="price">${firstBookDetails.price}</span>
      </div>
    `;
    leftContainer.appendChild(top);

    const middleCenter = document.createElement('div');
    middleCenter.classList.add('middle-center');
    middleCenter.innerHTML = `
      <b>Website URL: </b> <a href="${firstBookDetails.website_url}" target="_blank">${firstBookDetails.website_url}</a>
    `;
    leftContainer.appendChild(middleCenter);

    const bottomCenter = document.createElement('div');
    bottomCenter.classList.add('bottom-center');
    bottomCenter.innerHTML = `
      <b>Description:</b><br> ${firstBookDetails.description}
    `;
    leftContainer.appendChild(bottomCenter);

    bookDetailsContainer.appendChild(leftContainer);
  } else {
    // No books found
    bookDetailsContainer.innerHTML = '<p>No books found.</p>';
  }
}

// Function to display details of related books in a table
function displayBookDetails(bookDetailsArray) {
  const tableBody = document.querySelector('.table_body tbody');

  if (bookDetailsArray && bookDetailsArray.length > 0) {
    for (let i = 1; i < bookDetailsArray.length; i++) {
      const bookDetails = bookDetailsArray[i];
      const tableRow = document.createElement('tr');

      tableRow.innerHTML = `
        <td>${bookDetails.title}</td>
        <td>${bookDetails.website_name}</td>
        <td><a href="${bookDetails.website_url}" target="_blank">${bookDetails.website_url}</a></td>
        <td>${bookDetails.price}</td>
        <td>
          ${bookDetails.isbn ? `${bookDetails.isbn}, ` : ''}
          ${bookDetails.book_condition ? `${bookDetails.book_condition}, ` : ''}
          ${bookDetails.stock ? `${bookDetails.stock}, ` : ''}
          ${bookDetails.book_type ? `${bookDetails.book_type}` : ''}
          <!-- Add more details as needed -->
        </td>
      `;

      tableBody.appendChild(tableRow);
    }
  } else {
    // No books found
    tableBody.innerHTML = '<tr><td colspan="5">No books found.</td></tr>';
  }
}

  