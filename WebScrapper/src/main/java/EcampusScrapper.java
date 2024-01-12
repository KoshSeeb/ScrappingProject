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
 * This class represents a web scraper for the eCampus website to extract book information.
 * It extends the Thread class to run the scraping process concurrently.
 */
public class EcampusScrapper extends Thread {

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
            System.out.println("EcampusScrapper thread is scraping data");

            // WEB SCRAPING CODE GOES HERE
            scrapeBooks();

            // Sleep for the crawl delay, which is in seconds
            try {
                sleep(1000 * crawlDelay); // Sleep is in milliseconds, so we need to multiply the crawl delay by 1000
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
    protected void scrapeBooks( ) {
        String urlTemp = "https://www.ecampus.com/search-results?terms=fantasy";
        urlTemp = urlTemp + "&page=";

        Configuration config = new Configuration();
        config.configure("hibernate.cfg.xml");

        try (SessionFactory factory = config.buildSessionFactory();
             Session session = factory.openSession()) {

            for (int i = 1; i <= 10 && runThread; i++) {
                String url = urlTemp + i;

                System.out.println("Generated URL: " + url);

                try {
                    Document document = Jsoup.connect(url).get();
                    Elements books = document.select("ul.results > li.row");

                    for (Element bk : books) {
                        String title = bk.select("h1 a").text();
                        String author = bk.select("p.author").text().replaceFirst("^by\\s*", "");
                        String isbn = bk.select("ul li:contains(ISBN13)").text();
                        String bookType = bk.select("ul li:contains(Format)").text().replace("Format:", "").trim();
                        String price = "";
                        Element priceNewElement = bk.select("li.row:contains(Buy New) div.price").first();

                        if (priceNewElement != null) {
                            String fullPrice = priceNewElement.text();
                            price = fullPrice.substring(1);
                        }

                        String imageUrl = bk.select("div.image img").attr("src");
                        String websiteUrl = "https://www.ecampus.com/" + bk.select("h1 a").attr("href");

                        // Check if the book with the same title, author, and website URL already exists in the database
                        Comparison existingComparison = getExistingComparison(session, title, author, websiteUrl);

                        if (existingComparison == null) {
                            System.out.println("Title: " + title);
                            System.out.println("Author: " + author);
                            System.out.println("ISBN: " + isbn);
                            System.out.println("Book Type: " + bookType);
                            System.out.println("Price for New Book: " + price);
                            System.out.println("Image URL: " + imageUrl);
                            System.out.println("Website URL: " + websiteUrl);
                            System.out.println("------------------------------");

                            Transaction transaction = session.beginTransaction();

                            Book book = new Book();
                            book.setTitle(title);
                            book.setAuthor(author);
                            book.setIsbn(isbn);
                            book.setBookType(bookType);
                            session.persist(book);

                            Comparison comparison = new Comparison();
                            comparison.setBook(book);
                            comparison.setWebsiteName("ecampus");
                            comparison.setImageUrl(imageUrl);
                            comparison.setWebsiteUrl(websiteUrl);
                            comparison.setPrice(price);
                            session.persist(comparison);

                            transaction.commit();
                        } else {
                            System.out.println("Book with the same title, author, and website URL already exists in the database.");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        runThread = false;
    }
}
