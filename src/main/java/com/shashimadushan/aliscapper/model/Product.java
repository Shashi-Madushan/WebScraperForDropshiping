package com.shashimadushan.aliscapper.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Map;

@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String productID;
    private String productName;
    private String currentPrice;
    private String originalPrice;
    private String discount;
    private String rating;
    private String soldCount;
    private List<String> imageLinks;
    private List<String> videoLinks;
    private List<Description> description;
    private Map<String, String> specifications;

    public Product(String id, String productID, String productName, String currentPrice, String originalPrice, String discount, String rating, String soldCount, List<String> imageLinks, List<String> videoLinks, List<Description> description, Map<String, String> specifications) {
        this.id = id;
        this.productID = productID;
        this.productName = productName;
        this.currentPrice = currentPrice;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.rating = rating;
        this.soldCount = soldCount;
        this.imageLinks = imageLinks;
        this.videoLinks = videoLinks;
        this.description = description;
        this.specifications = specifications;
    }

    public Product() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(String soldCount) {
        this.soldCount = soldCount;
    }

    public List<String> getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(List<String> imageLinks) {
        this.imageLinks = imageLinks;
    }

    public List<String> getVideoLinks() {
        return videoLinks;
    }

    public void setVideoLinks(List<String> videoLinks) {
        this.videoLinks = videoLinks;
    }

    public List<Description> getDescription() {
        return description;
    }

    public void setDescription(List<Description> description) {
        this.description = description;
    }

    public Map<String, String> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(Map<String, String> specifications) {
        this.specifications = specifications;
    }
}
