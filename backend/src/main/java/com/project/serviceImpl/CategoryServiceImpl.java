package com.project.serviceImpl;

import com.google.common.base.Strings;
import com.project.JWT.JwtFilter;
import com.project.POJO.Category;
import com.project.constants.Constants;
import com.project.dao.CategoryDao;
import com.project.service.CategoryService;
import com.project.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryDao categoryDao;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> add(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (requestMap.containsKey("name") && !Strings.isNullOrEmpty(requestMap.get("name"))) {
                    Category category = categoryDao.findByName(requestMap.get("name"));
                    if (Objects.isNull(category)) {
                        categoryDao.save(getCategoryFromMap(requestMap));
                        return Utils.getResponseEntity("Category added successfully.", HttpStatus.OK);
                    }
                    return Utils.getResponseEntity("Category with given name already exists.", HttpStatus.BAD_REQUEST);
                } else {
                    return Utils.getResponseEntity(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            } else {
                return Utils.getResponseEntity(Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Utils.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Category>> getAll(String filterValue) {
        try {
            if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")) {
                List<Category> categories = categoryDao.getAllCategoriesWithProducts();
                return new ResponseEntity<>(categories, HttpStatus.OK);
            }
            return new ResponseEntity<>(categoryDao.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (requestMap.containsKey("id") && requestMap.containsKey("name") && !Strings.isNullOrEmpty(requestMap.get("id")) && !Strings.isNullOrEmpty(requestMap.get("name"))) {
                    Optional<Category> category = categoryDao.findById(Long.parseLong(requestMap.get("id")));
                    if (category.isPresent()) {
                        String categoryName = requestMap.get("name");
                        if (!Objects.isNull(categoryDao.findByName(categoryName)) && !category.get().getName().equalsIgnoreCase(categoryName))
                            return Utils.getResponseEntity("Category with given name already exists.", HttpStatus.BAD_REQUEST);
                        categoryDao.updateName(categoryName, Long.parseLong(requestMap.get("id")));
                        return Utils.getResponseEntity("Category updated successfully.", HttpStatus.OK);
                    } else {
                        return Utils.getResponseEntity("Category Id does not exist.", HttpStatus.OK);
                    }
                }
                return Utils.getResponseEntity(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            } else {
                return Utils.getResponseEntity(Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Utils.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> delete(Long id) {
        try {
            if (jwtFilter.isAdmin()) {
                if (!Objects.isNull(id)) {
                    Optional<Category> category = categoryDao.findById(id);
                    if (category.isPresent()) {
                        categoryDao.deleteById(id);
                        return Utils.getResponseEntity("Category deleted successfully.", HttpStatus.OK);
                    } else {
                        return Utils.getResponseEntity("Category Id does not exist.", HttpStatus.OK);
                    }
                }
                return Utils.getResponseEntity(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            } else {
                return Utils.getResponseEntity(Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Utils.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Category getCategoryFromMap(Map<String, String> requestMap) {
        Category category = new Category();
        category.setName(requestMap.get("name"));
        return category;
    }
}
