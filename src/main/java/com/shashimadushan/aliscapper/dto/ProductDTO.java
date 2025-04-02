package com.shashimadushan.aliscapper.dto;

import com.shashimadushan.aliscapper.model.Description;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductDTO {
    private String id;

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
