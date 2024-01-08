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

public class AbeBooksScrapper extends Thread {

    // Specifies the interval between HTTP requests to the server in seconds.
    private int crawlDelay = 1;

    // Allows us to shut down our application cleanly
    volatile private boolean runThread = false;

    private String searchQuery;

    public AbeBooksScrapper(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @Override
    public void run() {
        runThread = true;

        // While loop will keep running until runThread is set to false;
        while (runThread) {
            System.out.println("AbeBooksScrapper thread is scraping data for query: " + searchQuery);

            // WEB SCRAPING CODE GOES HERE
            scrapeBooks(searchQuery);

            // Sleep for the crawl delay, which is in seconds
            try {
                sleep(1000 * crawlDelay); // Sleep is in milliseconds, so we need to multiply the crawl delay by 1000
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    // Other classes can use this method to terminate the thread.
    public void stopThread() {
        runThread = false;
    }

    private void scrapeBooks(String searchQuery) {
        String urlTemp = "https://www.abebooks.com/servlet/SearchResults?dsp=100&kn=" + searchQuery;
        urlTemp = urlTemp + "&sts=t&cm_sp=SearchF-_-TopNavISS-_-Results&sortbyp=0";


        String url = urlTemp ;

            // Uncomment the line below if you want to print the generated URL
             System.out.println("Generated URL: " + url);

        Configuration config = new Configuration();
        config.configure("hibernate.cfg.xml"); // Provide your Hibernate configuration file path

        try (SessionFactory factory = config.buildSessionFactory();
             Session session = factory.openSession()) {

            try {
                Document document = Jsoup.connect(url).get();
                Elements books = document.select(".result-item");


                for (Element bk : books) {
                    String title = bk.select(".result-detail .title [data-cy='listing-title']").text();
                    String author = bk.select(".author strong").text();
                    String isbn = bk.select(".isbn a span").text();
                    String bookCondition = bk.select(".item-meta-data .opt-subcondition").text();
                    String quantity = bk.select(".text-secondary .opt-quantity").text();
                    String price = bk.select(".item-price-group .item-price").text();
                    String imageUrl = bk.select(".srp-item-image").attr("src");
                    String bookType = bk.select(".item-meta-data b span").text();
                    String websiteName = "https://www.abebooks.com";
                    String websiteUrl = "https://www.abebooks.com" + bk.select(".title a").attr("href");
                    String description = bk.select(".desc-container .readmore-toggle").text();


                    System.out.println("Title: " + title);
                    System.out.println("Author: " + author);
                    System.out.println("ISBN: " + isbn);
                    System.out.println("Book Condition: " + bookCondition);
                    System.out.println("Quantity: " + quantity);
                    System.out.println("Price: " + price);
                    System.out.println("Image URL: " + imageUrl);
                    System.out.println("Book Type: " + bookType);
                    System.out.println("Website URL: " + websiteUrl);
                    System.out.println("Website Name: " + websiteName);
                    System.out.println("Description: " + description);
                    System.out.println("------------------------------");

                    Transaction transaction = session.beginTransaction();
                    Book book = new Book();
                    book.setTitle(title);
                    book.setAuthor(author); // You can set the author based on your requirements
                    book.setIsbn(isbn);
                    book.setBookCondition(bookCondition);
                    book.setStock(quantity);
                    book.setBookType(bookType);
                    book.setDescription(description);
                    session.persist(book);

                    Comparison comparison = new Comparison();
                    comparison.setBook(book);
                    comparison.setWebsiteName("AbeBooks");
                    comparison.setImageUrl(imageUrl);
                    comparison.setWebsiteUrl(websiteUrl);
                    comparison.setPrice(price);
                    session.persist(comparison);

                    transaction.commit();


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        runThread = false;
    }
}


