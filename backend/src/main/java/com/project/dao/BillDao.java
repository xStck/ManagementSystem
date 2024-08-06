package com.project.dao;

import com.project.POJO.Bill;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillDao extends CrudRepository<Bill, Long> {

    List<Bill> findAllByOrderByIdDesc();

    @Query("select b from Bill b where b.createdBy = :userName order by b.id desc")
    List<Bill> getBillByUserName(@Param("userName") String userName);
}
