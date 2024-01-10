import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Read input from the user for search query
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your search query: ");
        String searchQuery = scanner.nextLine();

        // Create the PowellsScrapper instance

//        AbeBooks ecampus done

//        PowellsScrapper powellsScrapper = new PowellsScrapper(searchQuery);
        AbeBooksScrapper abeBooksScrapper = new AbeBooksScrapper(searchQuery);
//        EcampusScrapper ecampusScrapper = new EcampusScrapper(searchQuery);

        // Start the thread running
//        powellsScrapper.start();
        abeBooksScrapper.start();
//        ecampusScrapper.start();

        // Read input from the user until they type 'stop'
//        String userInput = scanner.nextLine();
//        while (!userInput.equals("stop")) {
//            userInput = scanner.nextLine();
//        }

        // Stop the thread
//        powellsScrapper.stopThread();
        abeBooksScrapper.stopThread();
//        ecampusScrapper.stopThread();

        // Wait for the thread to finish
        try {
//            powellsScrapper.join();
            abeBooksScrapper.join();
//            ecampusScrapper.join();
        } catch (InterruptedException ex) {
            System.out.println("Interrupted exception thrown: " + ex.getMessage());
        }

        System.out.println();
        System.out.println("Web scraping complete");
    }
}
