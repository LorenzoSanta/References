package com.security.SecurityProj.repositories;

import com.security.SecurityProj.dtos.AddressDTO;
import com.security.SecurityProj.entities.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {

    @Query("SELECT a.civNumber, a.street FROM Address a WHERE a.user = :userId")
    List<Address> findByUserId(@Param("userId") Long userId);

    List<Address> findAll();

}
