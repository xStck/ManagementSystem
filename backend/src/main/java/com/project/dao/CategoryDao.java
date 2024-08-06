package com.project.dao;

import com.project.POJO.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface CategoryDao extends CrudRepository<Category, Long> {
    List<Category> findAll();

    @Query("select c from Category c where c.id in (select p.category from Product p where p.status='true')")
    List<Category> getAllCategoriesWithProducts();

    Category findByName(String name);

    @Modifying
    @Transactional
    @Query("update Category c set c.name = :name where c.id = :id")
    void updateName(@Param("name") String name, @Param("id") Long id);
}
