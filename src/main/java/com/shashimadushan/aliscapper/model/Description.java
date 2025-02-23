package com.shashimadushan.aliscapper.model;

import java.util.List;
import java.util.Map;

public class Description {
    private Map<String, String> attributes;
    private List<String> images;
    private String text;

    public Description(Map<String, String> attributes, List<String> images, String text) {
        this.attributes = attributes;
        this.images = images;
        this.text = text;
    }

    public Description() {
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}