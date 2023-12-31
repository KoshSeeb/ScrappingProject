// document.addEventListener('DOMContentLoaded', function() {
//     document.getElementById('searchBtn').addEventListener('click', function() {
//         const searchTerm = document.getElementById('searchInput').value.trim();
//         // Check if the search term is not empty
//         if (searchTerm) {
//             // Redirect to the search page with the search term as a query parameter
//             window.location.href = `Result_page.html?search=${encodeURIComponent(searchTerm)}`;
  
//         } else {
//             // Optionally, alert the user that they need to enter a search term
//             alert('Please enter a search term.');
//         }
//     });
  
  
//   });
    // Optional: Trigger search when the user presses "Enter" key in the search input
    // document.getElementById('searchInput').addEventListener('keypress', function(e) {
    //     if (e.key === 'Enter') {
    //         document.getElementById('searchBtn').click();
    //     }
    // });



    // function searchProducts() {
    //     const searchInput = document.getElementById('searchInput').value;
  
    //     // Make an AJAX request to the /search endpoint
    //     fetch(`http://localhost:3000/search?q=${searchInput}`)
    //       .then(response => response.json())
    //       .then(data => displaySearchResults(data))
    //       .catch(error => console.error('Error:', error));
    //   }

    function searchProducts() {
        const searchInput = document.getElementById('searchInput').value;
  
        // Make an AJAX request to the /search endpoint
        fetch(`http://localhost:3000/search?q=${searchInput}`)
          .then(response => response.json())
          .then(data => {
            console.log('Received data:', data);
            displaySearchResults(data);
          })
          .catch(error => console.error('Error:', error));
      }