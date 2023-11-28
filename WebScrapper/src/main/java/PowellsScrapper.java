import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class PowellsScrapper extends Thread {

    // Specifies the interval between HTTP requests to the server in seconds.
    private int crawlDelay = 1;

    // Allows us to shut down our application cleanly
    volatile private boolean runThread = false;

    private String searchQuery;

    public PowellsScrapper(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @Override
    public void run() {
        runThread = true;

        // While loop will keep running until runThread is set to false;
        while (runThread) {
            System.out.println("PowellsScrapper thread is scraping data for query: " + searchQuery);

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
        String urlTemp = "https://www.powells.com/searchresults?keyword=" + searchQuery;

        for (int i = 1; i <= 3; i++) {
            String url = urlTemp + "&pg=" + i;
//            System.out.println("Generated URL: " + url);

            try {
                Document document = Jsoup.connect(url).get();
                Elements books = document.select(".book-info");

//                System.out.println();
//                System.out.println("Books - Price");
//                System.out.println();

//                int j = 1;

                for (Element bk : books) {

                    String title = bk.select(".book-details > .book-title-wrapper > h3 > a").text();
                    String price = bk.select(".book-details > .book-price > .reg-price").text();

//                    System.out.println(j+ ". " + title + " - " + price);
                    System.out.println(title + " - " + price);
//                    j++;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        runThread = false;
    }
}
