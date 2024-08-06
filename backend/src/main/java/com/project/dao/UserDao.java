package com.project.dao;

import com.project.DTO.UserDTO;
import com.project.POJO.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserDao extends CrudRepository<User, Long> {
    User findByEmail(String email);

    @Query("select new com.project.DTO.UserDTO(u.id, u.name, u.email, u.contactNumber, u.status) from User u where u.role = 'user'")
    List<UserDTO> getAll();

    @Modifying
    @Transactional
    @Query("update User u set u.status = :status where u.id = :id")
    void updateStatus(@Param("status") String status, @Param("id") Long id);

    @Query("select u.email from User u where u.role = 'admin'")
    List<String> getAllAdminsEmails();
}
