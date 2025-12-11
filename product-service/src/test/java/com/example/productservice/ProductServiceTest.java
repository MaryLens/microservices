package com.example.productservice;

import com.example.productservice.dto.ProductDto;
import com.example.productservice.model.Category;
import com.example.productservice.model.Product;
import com.example.productservice.repository.CategoryRepository;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServiceTest {
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        productService = new ProductService(productRepository, categoryRepository);
    }

    @Test
    void getProducts_filtersByTitleCategoryAndPrice() {
        // given
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setName("Pens");

        Product p1 = new Product();
        p1.setId(1L);
        p1.setTitle("Blue Pen");
        p1.setPrice(100);
        p1.setCategory(cat1);

        Product p2 = new Product();
        p2.setId(2L);
        p2.setTitle("Red Pencil");
        p2.setPrice(200);
        p2.setCategory(cat1);

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<ProductDto> result = productService.getProducts("Pen", 1L, 150);

        assertEquals(1, result.size());
        assertEquals("Blue Pen", result.get(0).getTitle());
    }

    @Test
    void createProduct_savesWithCategory() {
        Category cat = new Category();
        cat.setId(5L);
        cat.setName("Notebooks");

        when(categoryRepository.findById(5L)).thenReturn(Optional.of(cat));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        ProductDto dto = productService.createProduct("Notebook A5", "Good one", 300, 5L, null);

        // then
        verify(productRepository, atLeastOnce()).save(captor.capture());
        Product saved = captor.getValue();
        assertEquals("Notebook A5", saved.getTitle());
        assertEquals(300, saved.getPrice());
        assertNotNull(saved.getCategory());
        assertEquals(5L, saved.getCategory().getId());
        assertEquals("Notebooks", dto.getCategoryName());
    }

    @Test
    void getProduct_throwsWhenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.getProduct(99L));
    }
}
