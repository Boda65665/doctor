package com.kafka1.demo.Repositoryes;

import com.kafka1.demo.Entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<Medication,Integer> {
    @Query("select Medication FROM Medication m order by m.id")
    List<Medication> findAllOrderById();
    Medication findById(int id);
}
