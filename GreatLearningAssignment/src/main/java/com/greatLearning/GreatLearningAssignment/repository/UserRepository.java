package com.greatLearning.GreatLearningAssignment.repository;

import com.greatLearning.GreatLearningAssignment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String username);

    boolean existsByEmail(String email);
}
