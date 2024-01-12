/**
 * The Main class for initiating web scraping on multiple bookstores.
 */
public class Main {

    /**
     * The main method that serves as the entry point for the program.
     *
     * @param args Command line arguments (not used in this program).
     */
    public static void main(String[] args) {
        // Read input from the user for search query
//        Scanner scanner = new Scanner(System.in);
//        System.out.print("Enter your search query: ");
//        String searchQuery = scanner.nextLine();

        // Create instances of bookstore scrapers
        AbeBooksScrapper abeBooksScrapper = new AbeBooksScrapper();
        EcampusScrapper ecampusScrapper = new EcampusScrapper();

        // Start the scrapers as separate threads
        abeBooksScrapper.start();
        ecampusScrapper.start();

        // Stop the scrapers
        abeBooksScrapper.stopThread();
        ecampusScrapper.stopThread();

        // Wait for the scrapers to finish
        try {
            abeBooksScrapper.join();
        } catch (InterruptedException ex) {
            System.out.println("Interrupted exception thrown: " + ex.getMessage());
        }

        System.out.println();
        System.out.println("Web scraping complete");
    }
}
