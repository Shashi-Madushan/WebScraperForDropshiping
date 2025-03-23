package com.shashimadushan.aliscapper.service;

import com.shashimadushan.aliscapper.dto.DailyProductCountDTO;
import com.shashimadushan.aliscapper.dto.ProductDTO;
import com.shashimadushan.aliscapper.model.Product;
import com.shashimadushan.aliscapper.repo.ProductRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public List<ProductDTO> getUserProducts(String username) {
        List<Product> products = productRepository.findByUserName(username);
        return products.stream()
                       .map(this::convertToDto)
                       .collect(Collectors.toList());
    }

    public Optional<ProductDTO> getProductById(String id, String userId) {
        return productRepository.findById(id)
                .filter(product -> product.getUserName().equals(userId))
                .map(this::convertToDto);
    }

    public Product saveProduct(ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        product.setCreationDate(LocalDate.now());
        return productRepository.save(product);
    }

    public void deleteProduct(String id, String userNsme) {
        productRepository.findById(id).ifPresent(product -> {
            if (product.getUserName().equals(userNsme)) {
                productRepository.deleteById(id);
            }
        });
    }

    private ProductDTO convertToDto(Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }

    private Product convertToEntity(ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return modelMapper.map(products, new TypeToken<List<ProductDTO>>() {}.getType());
    }

    public void deleteProduct(String productId) {
        productRepository.deleteById(productId);
    }
    public long getUserProductCount(String userName) {
        return productRepository.countByUserName(userName);
    }
    public List<DailyProductCountDTO> getUserDailyProductCount(String userName) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29); // Last 30 days including today

        // Create a list of all dates in the range
        List<LocalDate> dateRange = IntStream.rangeClosed(0, 29)
                .mapToObj(endDate::minusDays)
                .sorted()
                .toList();

        // Get all products in the date range
        List<Product> products = productRepository.findByUserNameAndCreationDateBetween(userName, startDate, endDate);

        // Group products by creation date and count
        Map<LocalDate, Long> productCountByDate = products.stream()
                .collect(Collectors.groupingBy(Product::getCreationDate, Collectors.counting()));

        // Create DTOs for each date, even if there are no products
        return dateRange.stream()
                .map(date -> new DailyProductCountDTO(date, productCountByDate.getOrDefault(date, 0L)))
                .collect(Collectors.toList());
    }

    public Map<String, List<DailyProductCountDTO>> getAllUsersDailyProductCount() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29); // Last 30 days including today

        // Get all products in the date range
        List<Product> products = productRepository.findByCreationDateBetween(startDate, endDate);

        // Group products by user and then by date
        Map<String, Map<LocalDate, Long>> userProductCountsByDate = products.stream()
                .collect(Collectors.groupingBy(
                        Product::getUserName,
                        Collectors.groupingBy(Product::getCreationDate, Collectors.counting())
                ));

        // Create a list of all dates in the range
        List<LocalDate> dateRange = IntStream.rangeClosed(0, 29)
                .mapToObj(endDate::minusDays)
                .sorted()
                .toList();

        // Create the final result map
        Map<String, List<DailyProductCountDTO>> result = new HashMap<>();

        userProductCountsByDate.forEach((userName, countsByDate) -> {
            List<DailyProductCountDTO> dailyCounts = dateRange.stream()
                    .map(date -> new DailyProductCountDTO(date, countsByDate.getOrDefault(date, 0L)))
                    .collect(Collectors.toList());
            result.put(userName, dailyCounts);
        });

        return result;
    }
}