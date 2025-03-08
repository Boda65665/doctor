package com.kafka1.demo.Repositoryes;

import com.kafka1.demo.Entity.IllnessesInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IllnessesInfoRepository extends JpaRepository<IllnessesInfo,Integer> {
    IllnessesInfo findById(int id);
}
