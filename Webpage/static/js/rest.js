const express = require('express');
const mysql = require('mysql2');
const cors = require('cors');

const app = express();
const port = 3000;

app.use(cors());

// Create a MySQL connection pool
const pool = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: '1234',
    database: 'book_compare',
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

// Endpoint to search for products
app.get('/search', (req, res) => {
  const query = req.query.q;
  const numItems = parseInt(req.query.numitems) || 12;
  const offset = parseInt(req.query.offset) || 0;

  // Use a MySQL query to fetch products based on the search query
  pool.query(
    'SELECT b.title, b.author, c.image_url ' +
    'FROM Book b ' +
    'LEFT JOIN Comparison c ON b.book_id = c.book_id ' +
    'WHERE b.title LIKE ? LIMIT ?, ?',
    [`%${query}%`, offset, numItems],
    (error, results) => {
      if (error) {
        console.error('Error executing MySQL query:', error);
        res.status(500).json({ error: 'Internal Server Error' });
      } else {
        res.json(results);
      }
    }
  );
});

app.listen(port, () => {
  console.log(`Server is running at http://localhost:${port}`);
});


app.get('/bookComparison', (req, res) => {
    // Extract title and author from the query parameters
    const titleParam = req.query.title;
    const authorParam = req.query.author;
  
    // Use the first three words in the title
    const titleWords = titleParam.split(' ').slice(0, 3).join(' ');
  
    // Use only the first name in the author
    const authorName = authorParam.split(' ')[0];
  
 
    // Use a MySQL query to fetch detailed information from both tables
    pool.query(
        'SELECT b.*, c.* FROM Book b ' +
        'LEFT JOIN Comparison c ON b.book_id = c.book_id ' +
        'WHERE b.title LIKE ? AND b.author LIKE ? ' +
        'ORDER BY c.price ASC',
      [`%${titleWords}%`, `%${authorName}%`],
      (error, results) => {
        if (error) {
          console.error('Error executing MySQL query:', error);
          res.status(500).json({ error: 'Internal Server Error' });
        } else {
          if (results.length > 0) {
            // Data found, send detailed information
            res.json(results);
          } else {
            // Data not found
            res.status(404).json({ error: 'Data not found' });
          }
        }
      }
    );
  });
  
  



// const express = require('express');
// const path = require('path');
// const mysql = require('mysql2/promise');
// // const mysql = require('mysql')

// const app = express();
// const port = 3000;

// const pool = mysql.createPool({
//     host: 'localhost',
//     user: 'root',
//     password: '1234',
//     database: 'book_compare',
//     waitForConnections: true,
//     connectionLimit: 10,
//     queueLimit: 0
// });


// // app.use(express.static(path.join(__dirname, 'public')));

// // app.get('/', async (req, res) => {
// //     try {
// //         const [books] = await pool.query('SELECT * FROM Book');
// //         const [comparisons] = await pool.query('SELECT * FROM Comparison');

// //         const responseData = {
// //             books: books,
// //             comparisons: comparisons
// //         };

// //         res.json(responseData);
// //     } catch (error) {
// //         console.error(error);
// //         res.status(500).json({ error: 'Error fetching data' });
// //     }
// // });

// // app.listen(port, () => {
// //     console.log(`Server running at http://localhost:${port}/`);
// // });

// function getEmployees(){
//     //Build query
//     let sql = "SELECT * FROM Book";

//     //Execute query and output results
//     pool.query(sql, (err, result) => {
//         if (err){//Check for errors
//             console.error("Error executing query: " + JSON.stringify(err));
//         }
//         else{//Output results in JSON format - a web service would return this string.
//             console.log(JSON.stringify(result));
//         }
//     });
// }

// //Call function to output employees
// getEmployees();
