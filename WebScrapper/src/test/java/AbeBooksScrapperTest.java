import com.koshik.pojo.Comparison;
import com.koshik.pojo.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * JUnit test class for the AbeBooksScrapper class.
 */
public class AbeBooksScrapperTest {

    private SessionFactory factory;
    private Session session;
    private AbeBooksScrapper abeBooksScrapper;

    /**
     * Setup method executed before each test.
     * It configures Hibernate, opens a session, and initializes the AbeBooksScrapper.
     */
    @Before
    public void setup() {
        Configuration config = new Configuration();
        config.configure("hibernate.cfg.xml");
        factory = config.buildSessionFactory();
        session = factory.openSession();

        abeBooksScrapper = new AbeBooksScrapper();
    }
    /**
     * Teardown method executed after each test.
     * It closes the session and the session factory.
     */
    @After
    public void tearDown() {
        session.close();
        factory.close();
    }
    /**
     * Test case for the getExistingComparison method.
     * It checks if the method correctly retrieves an existing Comparison from the database.
     */
    @Test
    public void testGetExistingComparison() {
        Transaction transaction = session.beginTransaction();

        // Create a Book and Comparison in the database
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("1234567890");
        session.persist(book);

        Comparison comparison = new Comparison();
        comparison.setBook(book);
        comparison.setWebsiteName("Test Website");
        comparison.setWebsiteUrl("https://example.com");
        comparison.setPrice("10.99");  // Set a dummy price for testing purposes
        session.persist(comparison);

        // Test the getExistingComparison method
        Comparison existingComparison = abeBooksScrapper.getExistingComparison(session, "Test Book", "Test Author", "https://example.com");

        // Assert that the existingComparison is not null and has the correct values
        assertEquals("Test Book", existingComparison.getBook().getTitle());
        assertEquals("Test Author", existingComparison.getBook().getAuthor());
        assertEquals("https://example.com", existingComparison.getWebsiteUrl());

        // Test with non-existing comparison
        existingComparison = abeBooksScrapper.getExistingComparison(session, "Non-existing Book", "Non-existing Author", "https://nonexisting.com");

        // Assert that the existingComparison is null
        assertNull(existingComparison);
    }
}
