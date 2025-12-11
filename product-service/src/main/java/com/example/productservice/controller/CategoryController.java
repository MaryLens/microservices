package com.example.productservice.controller;

import com.example.productservice.dto.CategoryDto;
import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories() {
        return ResponseEntity.ok(productService.getCategories());
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestParam String name, @RequestParam String description) {
        return ResponseEntity.ok(productService.createCategory(name, description));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}
