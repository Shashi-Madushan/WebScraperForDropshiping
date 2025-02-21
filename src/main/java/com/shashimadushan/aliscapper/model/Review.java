package com.shashimadushan.aliscapper.model;

import java.util.List;

public class Review {
    private String buyerName;
    private String buyerCountry;
    private String feedback;
    private String evalDate;
    private List<String> images;

    // Getters and Setters
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getBuyerCountry() { return buyerCountry; }
    public void setBuyerCountry(String buyerCountry) { this.buyerCountry = buyerCountry; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getEvalDate() { return evalDate; }
    public void setEvalDate(String evalDate) { this.evalDate = evalDate; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}
