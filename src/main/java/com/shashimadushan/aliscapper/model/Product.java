package com.shashimadushan.aliscapper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Document(collection = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
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
    private String userName;
    private LocalDate creationDate;


}
