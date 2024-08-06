package com.project.serviceImpl;

import com.google.common.base.Strings;
import com.project.DTO.ProductDTO;
import com.project.JWT.JwtFilter;
import com.project.POJO.Category;
import com.project.POJO.Product;
import com.project.constants.Constants;
import com.project.dao.CategoryDao;
import com.project.dao.ProductDao;
import com.project.service.ProductService;
import com.project.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductDao productDao;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    CategoryDao categoryDao;

    @Override
    public ResponseEntity<String> add(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateAdd(requestMap)) {
                    if (!checkIfCategoryExists(requestMap.get("categoryId")))
                        return Utils.getResponseEntity("Category with given ID does not exists.", HttpStatus.BAD_REQUEST);
                    productDao.save(getProductFromMap(requestMap));
                    return Utils.getResponseEntity("Product successfully added.", HttpStatus.OK);
                }
                return Utils.getResponseEntity(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            } else {
                return Utils.getResponseEntity(Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (requestMap.containsKey("id") && !Strings.isNullOrEmpty(requestMap.get("id"))) {
                    Optional<Product> product = productDao.findById(Long.parseLong(requestMap.get("id")));
                    if (product.isPresent()) {
                        if (requestMap.containsKey("categoryId") &&
                                !Strings.isNullOrEmpty("categoryId") &&
                                !checkIfCategoryExists(requestMap.get("categoryId"))) {
                            return Utils.getResponseEntity("Category with given ID does not exists.", HttpStatus.BAD_REQUEST);
                        }
                        setProductVariables(product.get(), requestMap);
                        productDao.update(product.get());
                        return Utils.getResponseEntity("Product successfully updated.", HttpStatus.OK);
                    }
                    return Utils.getResponseEntity("Product with given ID does not exists.", HttpStatus.BAD_REQUEST);
                }
                return Utils.getResponseEntity(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            } else {
                return Utils.getResponseEntity(Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductDTO>> getAll() {
        try {
            return new ResponseEntity<>(productDao.getAll(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> delete(Long id) {
        try {
            if (jwtFilter.isAdmin()) {
                if (!Objects.isNull(id)) {
                    Optional<Product> product = productDao.findById(id);
                    if (!product.isEmpty()) {
                        productDao.deleteById(id);
                        return Utils.getResponseEntity("Product deleted successfully.", HttpStatus.OK);
                    } else {
                        return Utils.getResponseEntity("Product Id does not exist.", HttpStatus.OK);
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
    public ResponseEntity<List<ProductDTO>> getByCategory(Long categoryId) {
        try {
            return new ResponseEntity<>(productDao.getByCategory(categoryId), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductDTO> getById(Long id) {
        try {
            return new ResponseEntity<>(productDao.getById(id), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ProductDTO(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void setProductVariables(Product product, Map<String, String> requestMap) {
        if (requestMap.containsKey("name")) {
            product.setName(requestMap.get("name"));
        }

        if (requestMap.containsKey("description")) {
            product.setDescription(requestMap.get("description"));
        }

        if (requestMap.containsKey("categoryId")) {
            Category category = new Category();
            category.setId(Long.parseLong(requestMap.get("categoryId")));
            product.setCategory(category);
        }

        if (requestMap.containsKey("price")) {
            product.setPrice(Double.parseDouble(requestMap.get("price")));
        }

        if (requestMap.containsKey("status")) {
            product.setStatus(requestMap.get("status"));
        }
    }


    private Product getProductFromMap(Map<String, String> requestMap) {
        Category category = new Category();
        category.setId(Long.parseLong(requestMap.get("categoryId")));
        Product product = new Product();
        product.setCategory(category);
        product.setDescription(requestMap.get("description"));
        product.setPrice(Double.parseDouble(requestMap.get("price")));
        product.setName(requestMap.get("name"));
        product.setStatus("true");
        return product;
    }

    private boolean checkIfCategoryExists(String categoryId) {
        return categoryDao.findById(Long.parseLong(categoryId)).isPresent();
    }

    private boolean validateAdd(Map<String, String> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("description") &&
                requestMap.containsKey("categoryId") &&
                requestMap.containsKey("price") &&
                requestMap.containsKey("status");
    }
}
