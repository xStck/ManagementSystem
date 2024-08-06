package com.project.service;

import com.project.DTO.ProductDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ProductService {
    ResponseEntity<String> add(Map<String, String> requestMap);

    ResponseEntity<String> update(Map<String, String> requestMap);

    ResponseEntity<List<ProductDTO>> getAll();

    ResponseEntity<String> delete(Long id);

    ResponseEntity<List<ProductDTO>> getByCategory(Long categoryId);

    ResponseEntity<ProductDTO> getById(Long id);
}
