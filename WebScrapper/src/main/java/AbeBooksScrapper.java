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
        String urlTemp = "https://www.abebooks.com/servlet/SearchResults?kn=" + searchQuery;
        urlTemp = urlTemp + "&sortby=20&view=list&prevpage=1&sp=1";

//        &p=2&spo=30

        for (int i = 1; i <= 3; i++) {
            String url = urlTemp ;

//            &p=2&spo=30

            // Uncomment the line below if you want to print the generated URL
            // System.out.println("Generated URL: " + url);

            try {
                Document document = Jsoup.connect(url).get();
                Elements books = document.select(".result-item");


                for (Element bk : books) {
                    String title = bk.select(".result-detail .title [data-cy='listing-title']").text();
//                    String author = bk.select(".author span").text();
//                    String price = bk.select(".price").text();

                    System.out.println(title);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        runThread = false;
    }
}
