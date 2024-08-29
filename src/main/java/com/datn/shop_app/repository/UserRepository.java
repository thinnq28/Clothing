package com.datn.shop_app.repository;

import com.datn.shop_app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByPhoneNumberAndActive(String phoneNumber, Boolean active);
    Optional<User> findByEmailOrPhoneNumberAndActive(String email, String phoneNumber, Boolean active);
    Optional<User> findByEmailAndActive(String email, Boolean active);
    Optional<User> findByIdAndActive(Integer id, Boolean active);

    @Query("SELECT u FROM User u WHERE" +
            "(:name IS NULL OR :name = '' OR u.fullName LIKE %:name%) " +
            "AND (:phoneNumber IS NULL OR :phoneNumber = '' OR u.phoneNumber LIKE %:phoneNumber%)" +
            "AND (:email IS NULL OR :email = '' OR u.email LIKE %:email%)" +
            "AND (:active IS NULL OR u.active = :active)")
    Page<User> findAllUsers(@Param("name") String name, @Param("phoneNumber") String phoneNumber,
                            @Param("email") String email, @Param("active") Boolean active, Pageable pageable);

    @Query("UPDATE User u set u.active = false WHERE u.id = :id")
    void updateActiveUser(@Param("id") Integer id);

}