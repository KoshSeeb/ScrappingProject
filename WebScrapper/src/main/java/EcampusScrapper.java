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

public class EcampusScrapper extends Thread{

    // Specifies the interval between HTTP requests to the server in seconds.
    private int crawlDelay = 1;

    // Allows us to shut down our application cleanly
    volatile private boolean runThread = false;

    private String searchQuery;

    public EcampusScrapper(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @Override
    public void run() {
        runThread = true;

        // While loop will keep running until runThread is set to false;
        while (runThread) {
            System.out.println("EcampusScrapper thread is scraping data for query: " + searchQuery);

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
        String urlTemp = "https://www.ecampus.com/search-results?terms=" + searchQuery;
        urlTemp = urlTemp + "&page=";




            Configuration config = new Configuration();
            config.configure("hibernate.cfg.xml"); // Provide your Hibernate configuration file path

            try (SessionFactory factory = config.buildSessionFactory();
                 Session session = factory.openSession()) {

                for (int i = 1; i <= 10 && runThread; i++) {
                    String url = urlTemp + i;

                    // Uncomment the line below if you want to print the generated URL
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
                                price = priceNewElement.text();
                            }


                            String imageUrl = bk.select("div.image img").attr("src");
                            String websiteUrl = "https://www.ecampus.com/" + bk.select("h1 a").attr("href");

                            // Print extracted information
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
                            book.setAuthor(author); // You can set the author based on your requirements
                            book.setIsbn(isbn);
        //                    book.setBookCondition(bookCondition);
        //                    book.setStock(quantity);
                            book.setBookType(bookType);
        //                    book.setDescription(description);
                            session.persist(book);
        //
                            Comparison comparison = new Comparison();
                            comparison.setBook(book);
                            comparison.setWebsiteName("ecampus");
                            comparison.setImageUrl(imageUrl);
                            comparison.setWebsiteUrl(websiteUrl);
                            comparison.setPrice(price);
                            session.persist(comparison);
        //
                            transaction.commit();

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





