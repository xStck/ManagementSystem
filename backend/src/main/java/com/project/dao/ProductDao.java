package com.project.dao;

import com.project.DTO.ProductDTO;
import com.project.POJO.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ProductDao extends CrudRepository<Product, Long> {
    @Modifying
    @Transactional
    @Query("update Product p " +
            "set p.name = :#{#product.name}, " +
            "p.category = :#{#product.category}, " +
            "p.description = :#{#product.description}, " +
            "p.price = :#{#product.price}, " +
            "p.status = :#{#product.status} " +
            "where p.id = :#{#product.id}")
    void update(@Param("product") Product product);

    @Query("select new com.project.DTO.ProductDTO(p.id, p.name, p.description, p.price, p.status, p.category.id, p.category.name) from Product p")
    List<ProductDTO> getAll();

    @Query("select new com.project.DTO.ProductDTO(p.id, p.name, p.description, p.price, p.status, p.category.id, p.category.name) from Product p where p.category.id = :categoryId and p.status = 'true'")
    List<ProductDTO> getByCategory(Long categoryId);

    @Query("select new com.project.DTO.ProductDTO(p.id, p.name, p.description, p.price, p.status, p.category.id, p.category.name) from Product p where p.id = :id")
    ProductDTO getById(Long id);

}