package com.koshik.pojo;

import jakarta.persistence.*;

/**
 * Entity class representing a book.
 */
@Entity
@Table(name = "Book")
public class Book {

    /**
     * Unique identifier for the book.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private int bookId;

    /**
     * Title of the book.
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Author of the book.
     */
    @Column(name = "author", nullable = false)
    private String author;

    /**
     * ISBN (International Standard Book Number) of the book.
     */
    @Column(name = "isbn", length = 13)
    private String isbn;

    /**
     * Description or summary of the book.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Condition of the book (e.g., new, used).
     */
    @Column(name = "book_condition")
    private String bookCondition;

    /**
     * Available stock of the book.
     */
    @Column(name = "stock")
    private String stock;

    /**
     * Type or genre of the book.
     */
    @Column(name = "book_type", length = 50)
    private String bookType;

    /**
     * Gets the unique identifier for the book.
     *
     * @return The book ID.
     */
    public int getBookId() {
        return bookId;
    }

    /**
     * Sets the unique identifier for the book.
     *
     * @param bookId The book ID.
     */
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    /**
     * Gets the title of the book.
     *
     * @return The book title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     *
     * @param title The book title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the author of the book.
     *
     * @return The author's name.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of the book.
     *
     * @param author The author's name.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets the ISBN of the book.
     *
     * @return The ISBN.
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN of the book.
     *
     * @param isbn The ISBN.
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the description or summary of the book.
     *
     * @return The book description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description or summary of the book.
     *
     * @param description The book description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the condition of the book.
     *
     * @return The book condition.
     */
    public String getBookCondition() {
        return bookCondition;
    }

    /**
     * Sets the condition of the book.
     *
     * @param bookCondition The book condition.
     */
    public void setBookCondition(String bookCondition) {
        this.bookCondition = bookCondition;
    }

    /**
     * Gets the type or genre of the book.
     *
     * @return The book type.
     */
    public String getBookType() {
        return bookType;
    }

    /**
     * Sets the type or genre of the book.
     *
     * @param bookType The book type.
     */
    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    /**
     * Gets the available stock of the book.
     *
     * @return The stock information.
     */
    public String getStock() {
        return stock;
    }

    /**
     * Sets the available stock of the book.
     *
     * @param stock The stock information.
     */
    public void setStock(String stock) {
        this.stock = stock;
    }
}
