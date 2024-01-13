/**
 * The Main class for initiating web scraping on multiple bookstores.
 */
public class Main {

    /**
     * The main method that runs all the scrapper and stops them
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {

        // Create instances of bookstore scrapers
        EcampusScrapper ecampusScrapper = new EcampusScrapper();
        PowellsScrapper powellsScrapper = new PowellsScrapper();
        KneetScrapper kneetScrapper = new KneetScrapper();
        HiveScrapper hiveScrapper = new HiveScrapper();
        AbeBooksScrapper abeBooksScrapper = new AbeBooksScrapper();

        // Start the scrapers as separate threads
        abeBooksScrapper.start();
        ecampusScrapper.start();
        powellsScrapper.start();
        kneetScrapper.start();
        hiveScrapper.start();

        // Stop the scrapers
        abeBooksScrapper.stopThread();
        ecampusScrapper.stopThread();
        powellsScrapper.stopThread();
        kneetScrapper.stopThread();
        hiveScrapper.stopThread();


        // Wait for the scrapers to finish
        try {
            abeBooksScrapper.join();
            ecampusScrapper.join();
            powellsScrapper.join();
            kneetScrapper.join();
            hiveScrapper.join();

        } catch (InterruptedException ex) {
            System.out.println("Interrupted exception thrown: " + ex.getMessage());
        }

        System.out.println();
        System.out.println("Web scraping complete");
    }
}
