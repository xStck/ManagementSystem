package com.project.rest;

import com.project.DTO.ProductDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "product")
public interface ProductRest {
    @PostMapping(path = "/add")
    ResponseEntity<String> add(@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/update")
    ResponseEntity<String> update(@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/get")
    ResponseEntity<List<ProductDTO>> getAll();

    @DeleteMapping(path = "/delete/{id}")
    ResponseEntity<String> delete(@PathVariable Long id);

    @GetMapping(path = "/getByCategory/{categoryId}")
    ResponseEntity<List<ProductDTO>> getByCategory(@PathVariable Long categoryId);

    @GetMapping(path = "/getById/{id}")
    ResponseEntity<ProductDTO> getById(@PathVariable Long id);
}
