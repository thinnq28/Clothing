package com.datn.shop_app.repository;

import com.datn.shop_app.entity.Option;
import com.datn.shop_app.entity.Supplier;
import com.datn.shop_app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    Optional<Supplier> findByPhoneNumberAndActive(String phoneNumber, Boolean active);


    @Query("SELECT s FROM Supplier s WHERE" +
            "(:name IS NULL OR :name = '' OR s.supplierName LIKE %:name%) " +
            "AND (:phoneNumber IS NULL OR :phoneNumber = '' OR s.phoneNumber LIKE %:phoneNumber%)" +
            "AND (:email IS NULL OR :email = '' OR s.email LIKE %:email%)" +
            "AND (:active IS NULL OR s.active = :active)")
    Page<Supplier> findAllSuppliers(@Param("name") String name, @Param("phoneNumber") String phoneNumber,
                            @Param("email") String email, @Param("active") Boolean active, Pageable pageable);

    @Query("SELECT s FROM Supplier s WHERE " +
            "(:name IS NULL OR :name = '' OR s.supplierName LIKE %:name%) " +
            "AND (:active IS NULL OR s.active = :active)")
    List<Supplier> findAllSuppliers(@Param("name") String name, @Param("active") Boolean active);

    @Query("UPDATE Supplier s set s.active = false WHERE s.id = :id")
    void updateActiveSupplier(@Param("id") Integer id);

    Optional<Supplier> findByIdAndActive(Integer id, Boolean active);
}