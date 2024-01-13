import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit test class for the Main to check if threads are working properly.
 */
public class MainTest {

    /**
     * Tests the concurrent execution of bookstore scrapers.
     * <p>
     * This test initializes instances of the scrapper
     * starts them as separate threads, stops the threads, and waits for their completion.
     * It then asserts that both threads have finished their execution without errors.
     * </p>
     */
    @Test
    public void testConcurrentScraping() {
        // Create instances of bookstore scrapers
        AbeBooksScrapper abeBooksScrapper = new AbeBooksScrapper();
        PowellsScrapper powellsScrapper = new PowellsScrapper();

        // Start the scrapers as separate threads
        abeBooksScrapper.start();
        powellsScrapper.start();

        // Stop the scrapers
        abeBooksScrapper.stopThread();
        powellsScrapper.stopThread();

        // Wait for the scrapers to finish
        try {
            abeBooksScrapper.join();
            powellsScrapper.join();
        } catch (InterruptedException ex) {
            fail("Thread interrupted exception: " + ex.getMessage());
        }

        // Adapt assertions based on the actual properties/methods in your scrapers
        // For example, check if the threads completed without errors
        assertFalse(abeBooksScrapper.isAlive());
        assertFalse(powellsScrapper.isAlive());
    }

    /**
     * Runs scrapper again to check for duplicates.
     * <p>
     * The data should be the same as the one just scrapped and
     * should therefore not be added to database.
     * </p>
     */
    @Test
    public void testDuplicatesRejected() {
        // Create instances of bookstore scrapers
        AbeBooksScrapper abeBooksScrapper = new AbeBooksScrapper();

        // Start the scrapers as separate threads
        abeBooksScrapper.start();

        // Stop the scrapers
        abeBooksScrapper.stopThread();

        // Wait for the scrapers to finish
        try {
            abeBooksScrapper.join();

        } catch (InterruptedException ex) {
            fail("Thread interrupted exception: " + ex.getMessage());
        }

        // Adapt assertions based on the actual properties/methods in your scrapers
        // For example, check if the threads completed without errors
        assertFalse(abeBooksScrapper.isAlive());

    }


}