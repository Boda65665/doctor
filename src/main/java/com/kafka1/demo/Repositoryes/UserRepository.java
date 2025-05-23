package com.kafka1.demo.Repositoryes;

import com.kafka1.demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    User findById(int id);
    User findBySecretKey(String key);
    boolean existsByEmail(String email);
    boolean existsById(int id);
}