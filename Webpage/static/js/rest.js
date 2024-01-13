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

app.listen(port, () => {
  console.log(`Server is running at http://localhost:${port}`);
});


// Endpoint to search for products
app.get('/search', (req, res) => {
  const query = req.query.q;
  const numItems = parseInt(req.query.numitems) || 6;
  const page = parseInt(req.query.page) || 1; // New: Get page number

  // Calculate offset based on page number and items per page
  const offset = (page - 1) * numItems;

   // Use a MySQL query to fetch detailed information from both tables
  pool.query(
    'SELECT b.title, b.author, c.image_url ' +
    'FROM Book b ' +
    'LEFT JOIN Comparison c ON b.book_id = c.book_id ' +
    'WHERE b.title LIKE ? LIMIT ?, ?',
    [`%${query}%`, offset, numItems],
    (error, results) => {
      if (error) {
        // if there was an error, return error
        console.error('Error executing MySQL query:', error);
        res.status(500).json({ error: 'Internal Server Error' });
      } else {
        res.json(results);
      }
    }
  );
});




app.get('/getSelected', (req, res) => {
  // Extract title and author from the query parameters
  const titleParam = req.query.title;
  const authorParam = req.query.author;

  // Use a MySQL query to fetch detailed information from both tables
  pool.query(
    'SELECT b.*, c.* FROM Book b ' +
    'LEFT JOIN Comparison c ON b.book_id = c.book_id ' +
    'WHERE b.title = ? AND b.author = ?',
    [titleParam, authorParam],
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


  app.get('/bookComparison', (req, res) => {
    // Extract title and author from the query parameters
    const titleParam = req.query.title;
    const authorParam = req.query.author;
  
    // Use the first three words in the title
    const titleWords = titleParam.split(' ').slice(0, 5).join(' ');
  
    // Use only the first name in the author
    const authorName = authorParam.split(' ')[0];
  
 
    // Use a MySQL query to fetch detailed information from both tables
    pool.query(
      'SELECT b.*, c.* FROM Book b ' +
      'LEFT JOIN Comparison c ON b.book_id = c.book_id ' +
      'WHERE b.title LIKE ? AND b.author LIKE ? ' +
      'ORDER BY CAST(c.price AS DECIMAL(10, 2)) ASC',
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
  
  