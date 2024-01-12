package com.koshik.pojo;

import jakarta.persistence.*;

/**
 * Represents a comparison entry between a book and a specific website.
 */
@Entity
@Table(name = "Comparison")
public class Comparison {

    /**
     * Unique identifier for the comparison entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comparison_id")
    private int comparisonId;

    /**
     * The associated book for this comparison entry.
     */
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /**
     * Name of the website for this comparison entry.
     */
    @Column(name = "website_name", nullable = false)
    private String websiteName;

    /**
     * URL of the website for this comparison entry.
     */
    @Column(name = "website_url", nullable = false)
    private String websiteUrl;

    /**
     * URL of the image associated with this comparison entry.
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * Price information for the book on the associated website.
     */
    @Column(name = "price", nullable = false)
    private String price;

    /**
     * Gets the unique identifier for the comparison entry.
     * @return The comparison ID.
     */
    public int getComparisonId() {
        return comparisonId;
    }

    /**
     * Sets the unique identifier for the comparison entry.
     * @param comparisonId The comparison ID.
     */
    public void setComparisonId(int comparisonId) {
        this.comparisonId = comparisonId;
    }

    /**
     * Gets the associated book for this comparison entry.
     * @return The associated book.
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets the associated book for this comparison entry.
     * @param book The associated book.
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Gets the name of the website for this comparison entry.
     * @return The website name.
     */
    public String getWebsiteName() {
        return websiteName;
    }

    /**
     * Sets the name of the website for this comparison entry.
     * @param websiteName The website name.
     */
    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    /**
     * Gets the URL of the website for this comparison entry.
     * @return The website URL.
     */
    public String getWebsiteUrl() {
        return websiteUrl;
    }

    /**
     * Sets the URL of the website for this comparison entry.
     * @param websiteUrl The website URL.
     */
    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    /**
     * Gets the URL of the image associated with this comparison entry.
     * @return The image URL.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the URL of the image associated with this comparison entry.
     * @param imageUrl The image URL.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the price information for the book on the associated website.
     * @return The price.
     */
    public String getPrice() {
        return price;
    }

    /**
     * Sets the price information for the book on the associated website.
     * @param price The price.
     */
    public void setPrice(String price) {
        this.price = price;
    }

}

