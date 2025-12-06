package com.taskforge.taskforge_core_service.repository;

import com.taskforge.taskforge_core_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long > {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    List<User> findByIsActiveTrue() ;

    List<User> findByRole(String role);

    List<User> findByAuthProvider(String authProvider);

    @Query("SELECT u FROM User u WHERE u.emailVerified = true")
    List<User> findVerifiedUsers();




}
