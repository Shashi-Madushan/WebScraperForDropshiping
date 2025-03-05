package com.shashimadushan.aliscapper.service;

import com.shashimadushan.aliscapper.dto.ProductDTO;
import com.shashimadushan.aliscapper.model.Product;
import com.shashimadushan.aliscapper.repo.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
}