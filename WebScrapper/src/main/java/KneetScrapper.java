import com.koshik.pojo.Book;
import com.koshik.pojo.Comparison;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * This class represents a web scraper for the KneetBook website to extract book information.
 * It extends the Thread class to run the scraping process concurrently.
 */
public class KneetScrapper extends Thread{
    // Specifies the interval between HTTP requests to the server in seconds.
    private final int crawlDelay = 1;
    // Allows us to shut down our application cleanly
    volatile private boolean runThread = false;


    /**
     * The main entry point for the thread. Initiates the scraping process.
     */
    @Override
    public void run() {
        runThread = true;

        // While loop will keep running until runThread is set to false;
        while (runThread) {
            System.out.println("KneetScrapper thread is scraping data");

            // Web scrapper here
            scrapeBooks();

            // Sleep for the crawl delay
            try {
                sleep(1000 * crawlDelay);
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }


    /**
     * Stops the thread when called by other classes.
     */
    public void stopThread() {
        runThread = false;
    }

    /**
     * Helper method to check if a Comparison with the same title, author, and website URL already exists in the database.
     *
     * @param session      The Hibernate session.
     * @param title        The title of the book.
     * @param author       The author of the book.
     * @param websiteUrl   The website URL of the book.
     * @return             A Comparison object if a duplicate exists, null otherwise.
     */
    protected Comparison getExistingComparison(Session session, String title, String author, String websiteUrl) {
        return session.createQuery(
                        "FROM Comparison c " +
                                "JOIN FETCH c.book b " +
                                "WHERE b.title = :title AND b.author = :author AND c.websiteUrl = :websiteUrl", Comparison.class)
                .setParameter("title", title)
                .setParameter("author", author)
                .setParameter("websiteUrl", websiteUrl)
                .setMaxResults(1)
                .uniqueResult();
    }
    /**
     * Main method to scrape books from the AbeBooks website.
     *
     */
    private void scrapeBooks() {
        //  Creating the link of the website to scrape
        String urlTemp = "https://www.knetbooks.com/search-results?terms=fantasy&page=";

        // Load Hibernate configuration
        Configuration config = new Configuration();
        config.configure("hibernate.cfg.xml"); // Provide your Hibernate file

        try (SessionFactory factory = config.buildSessionFactory();
             Session session = factory.openSession()) {

            for (int i = 1; i <= 12 && runThread; i++) {
                String url = urlTemp + i;

                System.out.println("Generated URL: " + url);

                try {
                    // Connect to the website and retrieve the document
                    Document document = Jsoup.connect(url).get();
                    // Select all book items from the document
                    Elements books = document.select("ul.results li");

                    // Iterate through each book element
                    for (Element bk : books) {
                        // Extract book details from the HTML elements
                        String title = bk.select("div.info a.title h2").text().trim();
                        String author = bk.select("div.info div.author span a").text();

                        //book with no title or authors will not be added to database after comparison
                        title = title.isEmpty() ? "Book" : title;
                        author = author.isEmpty() ? "Unknown" : author;

                        String isbn = bk.select("div.info div.caption:contains(ISBN 13) + div.caption span.value").text().trim();

                        //Removing the currency symbol before the price
                        Element priceNewElement = bk.select("div.pricing div#pricing-box ul li a:contains(Buy New) div.price span").first();
                        String price = "";
                        if (priceNewElement != null) {
                            String fullPrice = priceNewElement.text();
                            price = fullPrice.substring(1);
                        }

                        String imageUrl = "https:" + bk.select("div.image a img").attr("src").trim();
                        String websiteUrl = "https://www.knetbooks.com" + bk.select("div.info a.title").attr("href").trim();

                        // Check if the book with the same title, author, and website URL already exists in the database
                        Comparison existingComparison = getExistingComparison(session, title, author, websiteUrl);

                        // If no duplicate, print details and persist in the database
                        if (existingComparison == null) {
                            System.out.println("Title: " + title);
                            System.out.println("Author: " + author);
                            System.out.println("ISBN: " + isbn);
                            System.out.println("Price for New Book: " + price);
                            System.out.println("Image URL: " + imageUrl);
                            System.out.println("Website URL: " + websiteUrl);
                            System.out.println("------------------------------");

                            // Start a database transaction
                            Transaction transaction = session.beginTransaction();
                            // Create a new Book object and set its properties
                            Book book = new Book();
                            book.setTitle(title);
                            book.setAuthor(author);
                            book.setIsbn(isbn);
                            session.persist(book);
                            // Create a new Comparison object and set its properties
                            Comparison comparison = new Comparison();
                            comparison.setBook(book);
                            comparison.setWebsiteName("Knet");
                            comparison.setImageUrl(imageUrl);
                            comparison.setWebsiteUrl(websiteUrl);
                            comparison.setPrice(price);
                            session.persist(comparison);

                            // Commit the transaction
                            transaction.commit();
                        } else {
                            System.out.println("Book with the same title, author, and website URL already exists in the database.");
                        }
                    }
                } catch (IOException e) {
                    // Handle IOException (e.g., network issues)
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        runThread = false;
    }
}
