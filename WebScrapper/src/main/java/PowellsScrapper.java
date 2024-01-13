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
 * This class represents a web scraper for the Powells website to extract book information.
 * It extends the Thread class to run the scraping process concurrently.
 */
public class PowellsScrapper extends Thread {

    // Specifies the interval between HTTP requests to the server in seconds.
    private int crawlDelay = 1;
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
            System.out.println("PowellsScrapper thread is scraping data");

            // web scrapper
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
     * Main method to scrape books from the eCampus website.
     */
    private void scrapeBooks() {
        //  Creating the link of the website to scrape
        String urlTemp = "https://www.powells.com/browse-book-genres/science-fiction-and-fantasy?mpp=50&pg=";

        // Load Hibernate configuration
        Configuration config = new Configuration();
        config.configure("hibernate.cfg.xml");

        try (SessionFactory factory = config.buildSessionFactory();
             Session session = factory.openSession()) {

            for (int i = 1; i <= 3 && runThread; i++) {
                String url = urlTemp + i;

                System.out.println("Generated URL: " + url);

                try {
                    // Connect to the website and retrieve the document
                    Document document = Jsoup.connect(url).get();
                    // Select all book items from the document
                    Elements books = document.select(".book-info");

                    // Iterate through each book element
                    for (Element bk : books) {
                        // Extract book details from the HTML elements
                        String title = bk.select(".book-details > .book-title-wrapper > h3 > a").text();
                        String author = bk.select(".book-details > .book-author").text().replaceFirst("^by\\s*", "");
                        String bookType = bk.select(".book-details > .book-type").text().replace("New", "").trim();
                        String price = "";
                        Element priceNewElement = bk.select(".book-details > .book-price > .reg-price").first();

                        //  Remove the currency symbol before price
                        if (priceNewElement != null) {
                            price = priceNewElement.text().substring(1);
                        }

                        String imageUrl = bk.select(".book-image img").attr("src");
                        String websiteUrl = "https://www.powells.com/" + bk.select(".book-title-wrapper > h3 > a").attr("href");

                        // Check if the book with the same title, author, and website URL already exists in the database
                        Comparison existingComparison = getExistingComparison(session, title, author, websiteUrl);

                        // If no duplicate, print details and persist in the database
                        if (existingComparison == null) {
                            System.out.println("Title: " + title);
                            System.out.println("Author: " + author);
                            System.out.println("Book Type: " + bookType);
                            System.out.println("Price: " + price);
                            System.out.println("Image URL: " + imageUrl);
                            System.out.println("Website URL: " + websiteUrl);
                            System.out.println("------------------------------");
                            // Start a database transaction
                            Transaction transaction = session.beginTransaction();
                            // Create a new Book object and set its properties
                            Book book = new Book();
                            book.setTitle(title);
                            book.setAuthor(author);
                            book.setBookType(bookType);
                            session.persist(book);
                            // Create a new Comparison object and set its properties
                            Comparison comparison = new Comparison();
                            comparison.setBook(book);
                            comparison.setWebsiteName("Powells");
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

