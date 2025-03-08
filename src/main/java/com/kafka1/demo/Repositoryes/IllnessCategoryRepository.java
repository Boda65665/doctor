package com.kafka1.demo.Repositoryes;

import com.kafka1.demo.Entity.IllnessCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IllnessCategoryRepository extends JpaRepository<IllnessCategory,Integer> {
    IllnessCategory findByName(String name);
    IllnessCategory findById(int id);
}
