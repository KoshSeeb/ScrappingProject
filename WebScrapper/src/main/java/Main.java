import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Read input from the user for search query
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your search query: ");
        String searchQuery = scanner.nextLine();

        // Create the PowellsScrapper instance
        PowellsScrapper powellsScrapper = new PowellsScrapper(searchQuery);
        AbeBooksScrapper abeBooksScrapper = new AbeBooksScrapper(searchQuery);

        // Start the thread running
        powellsScrapper.start();
        abeBooksScrapper.start();

        // Read input from the user until they type 'stop'
//        String userInput = scanner.nextLine();
//        while (!userInput.equals("stop")) {
//            userInput = scanner.nextLine();
//        }

        // Stop the thread
        powellsScrapper.stopThread();
        abeBooksScrapper.stopThread();

        // Wait for the thread to finish - join can throw an InterruptedException
        try {
            powellsScrapper.join();
            abeBooksScrapper.join();
        } catch (InterruptedException ex) {
            System.out.println("Interrupted exception thrown: " + ex.getMessage());
        }

        System.out.println("");
        System.out.println("Web scraping complete");
    }
}
