package com.project.service;

import com.project.POJO.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    ResponseEntity<String> add(Map<String, String> requestMap);

    ResponseEntity<List<Category>> getAll(String filterValue);

    ResponseEntity<String> update(Map<String, String> requestMap);

    ResponseEntity<String> delete(Long id);
}
