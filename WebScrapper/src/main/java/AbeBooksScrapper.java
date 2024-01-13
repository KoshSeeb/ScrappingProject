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
 * This class represents a web scraper for the AbeBooks website to extract book information.
 * It extends the Thread class to run the scraping process concurrently.
 */
public class AbeBooksScrapper extends Thread {

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
            System.out.println("AbeBooksScrapper thread is scraping data");

            // Web scrapper
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
//        Creating the link of the website to scrape
        String urlTemp = "https://www.abebooks.com/servlet/SearchResults?dsp=25&kn=fantasy";
        urlTemp = urlTemp + "&sts=t&cm_sp=SearchF-_-TopNavISS-_-Results&sortbyp=0";
        String url = urlTemp;

        Configuration config = new Configuration();
        config.configure("hibernate.cfg.xml"); // Hibernate file for configuration

        try (SessionFactory factory = config.buildSessionFactory();
             Session session = factory.openSession()) {

            // Connect to the website and retrieve the document
            Document document = Jsoup.connect(url).get();
            // Select all book items from the document
            Elements books = document.select(".result-item");

            // Iterate through each book element
            for (Element bk : books) {
                // Extract book details from the HTML elements
                String title = bk.select(".result-detail .title [data-cy='listing-title']").text();
                String author = bk.select(".author strong").text();
                String isbn = bk.select(".isbn a span").text();
                String bookCondition = bk.select(".item-meta-data .opt-subcondition").text();
                String quantity = bk.select(".text-secondary .opt-quantity").text();
                String price = bk.select(".item-price-group .item-price").text();
                price = price.substring(3);
                String imageUrl = bk.select(".srp-item-image").attr("src");
                String bookType = bk.select(".item-meta-data b span").text();
                String websiteUrl = "https://www.abebooks.com" + bk.select(".title a").attr("href");
                String description = bk.select(".desc-container .readmore-toggle").text();

                // Check if the book with the same title, author, and website URL already exists in the database
                Comparison existingComparison = getExistingComparison(session, title, author, websiteUrl);

                // If no duplicate, print details and persist in the database
                if (existingComparison == null) {
                    System.out.println("Title: " + title);
                    System.out.println("Author: " + author);
                    System.out.println("ISBN: " + isbn);
                    System.out.println("Book Condition: " + bookCondition);
                    System.out.println("Quantity: " + quantity);
                    System.out.println("Price: " + price);
                    System.out.println("Image URL: " + imageUrl);
                    System.out.println("Book Type: " + bookType);
                    System.out.println("Website URL: " + websiteUrl);
                    System.out.println("Description: " + description);
                    System.out.println("------------------------------");

                    // Start a database transaction
                    Transaction transaction = session.beginTransaction();

                    // Create a new Book object and set its properties
                    Book book = new Book();
                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setIsbn(isbn);
                    book.setBookCondition(bookCondition);
                    book.setStock(quantity);
                    book.setBookType(bookType);
                    book.setDescription(description);

                    // Persist the book in the database
                    session.persist(book);

                    // Create a new Comparison object and set its properties
                    Comparison comparison = new Comparison();
                    comparison.setBook(book);
                    comparison.setWebsiteName("AbeBooks");
                    comparison.setImageUrl(imageUrl);
                    comparison.setWebsiteUrl(websiteUrl);
                    comparison.setPrice(price);

                    // Persist the comparison in the database
                    session.persist(comparison);

                    // Commit the transaction
                    transaction.commit();
                } else {
                    // Print a message if the book is a duplicate
                    System.out.println("Book with the same title, author, and website URL already exists in the database.");
                }
            }
        } catch (IOException e) {
            // Handle IOException (e.g., network issues)
            e.printStackTrace();
        }

        // Set the runThread to false when scraping is complete
        runThread = false;
    }
}
