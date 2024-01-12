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

public class EcampusScrapperTest {

    private SessionFactory factory;
    private Session session;
    private EcampusScrapper ecampusScrapper;

    @Before
    public void setup() {
        Configuration config = new Configuration();
        config.configure("hibernate.cfg.xml");
        factory = config.buildSessionFactory();
        session = factory.openSession();

        ecampusScrapper = new EcampusScrapper();
    }

    @After
    public void tearDown() {
        session.close();
        factory.close();
    }

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
        Comparison existingComparison = ecampusScrapper.getExistingComparison(session, "Test Book", "Test Author", "https://example.com");

        // Assert that the existingComparison is not null and has the correct values
        assertEquals("Test Book", existingComparison.getBook().getTitle());
        assertEquals("Test Author", existingComparison.getBook().getAuthor());
        assertEquals("https://example.com", existingComparison.getWebsiteUrl());

        // Test with non-existing comparison
        existingComparison = ecampusScrapper.getExistingComparison(session, "Non-existing Book", "Non-existing Author", "https://nonexisting.com");

        // Assert that the existingComparison is null
        assertNull(existingComparison);
    }
}
//    @Test
//    public void testDuplicateBookInsertion() {
//        Transaction transaction = session.beginTransaction();
//
//        // Create a Book and Comparison in the database
//        Book book = new Book();
//        book.setTitle("Test Book");
//        book.setAuthor("Test Author");
//        book.setIsbn("1234567890");
//        session.persist(book);
//
//        Comparison comparison = new Comparison();
//        comparison.setBook(book);
//        comparison.setWebsiteName("Test Website");
//        comparison.setWebsiteUrl("https://example.com");
//        session.persist(comparison);
//
//        transaction.commit();
//
//        // Run scrapeBooks method (it should not insert duplicate)
//        ecampusScrapper.scrapeBooks(session);
//
//        // Verify that only one book and one comparison exist in the database
//        long bookCount = (long) session.createQuery("select count(*) from Book").uniqueResult();
//        long comparisonCount = (long) session.createQuery("select count(*) from Comparison").uniqueResult();
//
//        assertEquals(1, bookCount);
//        assertEquals(1, comparisonCount);
//    }
//
//    @Test
//    public void testDifferentBookInsertion() {
//        Transaction transaction = session.beginTransaction();
//
//        // Create a Book and Comparison in the database
//        Book existingBook = new Book();
//        existingBook.setTitle("Existing Book");
//        existingBook.setAuthor("Existing Author");
//        existingBook.setIsbn("9876543210");
//        session.persist(existingBook);
//
//        transaction.commit();
//
//        // Run scrapeBooks method to insert a different book
//        ecampusScrapper.scrapeBooks(session);
//
//        // Verify that two books exist in the database
//        long bookCount = (long) session.createQuery("select count(*) from Book").uniqueResult();
//        assertEquals(2, bookCount);
//    }
//}
