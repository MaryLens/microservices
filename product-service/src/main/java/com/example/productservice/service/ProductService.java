package com.example.productservice.service;

import com.example.productservice.dto.CategoryDto;
import com.example.productservice.dto.ProductDto;
import com.example.productservice.model.Category;
import com.example.productservice.model.Image;
import com.example.productservice.model.Product;
import com.example.productservice.repository.CategoryRepository;
import com.example.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductDto> getProducts(String title, Long categoryId, Integer maxPrice) {
        List<Product> products = productRepository.findAll();
        if (title != null && !title.isEmpty())
            products = products.stream()
                    .filter(p -> p.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .collect(Collectors.toList());
        if (categoryId != null)
            products = products.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
        if (maxPrice != null)
            products = products.stream()
                    .filter(p -> p.getPrice() <= maxPrice)
                    .collect(Collectors.toList());
        return products.stream().map(this::toDto).collect(Collectors.toList());
    }

    public ProductDto getProduct(Long id) {
        return productRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public ProductDto createProduct(String title, String description, int price, Long categoryId, MultipartFile[] files) {
        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setOnSale(false);
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("No such category: " + categoryId));
            product.setCategory(category);
        }
        productRepository.save(product);
        if (files != null) {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                Image img = new Image();
                img.setProduct(product);
                img.setOriginalFileName(file.getOriginalFilename());
                try {
                    img.setBytes(file.getBytes());
                } catch (Exception ex) {
                    throw new RuntimeException("File error", ex);
                }
                product.getImages().add(img);
            }
        }
        productRepository.save(product);
        return toDto(product);
    }

    public ProductDto updateProduct(Long id, String title, String description, Integer price, Long categoryId, Boolean isOnSale, MultipartFile[] files) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        if (title != null) product.setTitle(title);
        if (description != null) product.setDescription(description);
        if (price != null) product.setPrice(price);
        if (categoryId != null) {
            Category cat = categoryRepository.findById(categoryId).orElse(null);
            product.setCategory(cat);
        }
        if (isOnSale != null) product.setOnSale(isOnSale);
        if (files != null) {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                Image img = new Image();
                img.setProduct(product);
                img.setOriginalFileName(file.getOriginalFilename());
                try {
                    img.setBytes(file.getBytes());
                } catch (Exception ex) {
                    throw new RuntimeException("File error", ex);
                }
                product.getImages().add(img);
            }
        }
        productRepository.save(product);
        return toDto(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<CategoryDto> getCategories() {
        return categoryRepository.findAll().stream().map(this::toCategoryDto).collect(Collectors.toList());
    }

    public CategoryDto createCategory(String name, String description) {
        Category cat = new Category();
        cat.setName(name);
        cat.setDescription(description);
        categoryRepository.save(cat);
        return toCategoryDto(cat);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setOnSale(product.isOnSale());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        if (product.getImages() != null)
            dto.setImagesBase64(product.getImages().stream()
                    .filter(img -> img.getBytes() != null)
                    .map(img -> Base64.getEncoder().encodeToString(img.getBytes()))
                    .collect(Collectors.toList()));
        return dto;
    }

    private CategoryDto toCategoryDto(Category cat) {
        CategoryDto dto = new CategoryDto();
        dto.setId(cat.getId());
        dto.setName(cat.getName());
        dto.setDescription(cat.getDescription());
        return dto;
    }
}
