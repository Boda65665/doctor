package com.kafka1.demo.Repositoryes;

import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    Doctor findById(int id);
    Doctor findByUser(User user);
}
