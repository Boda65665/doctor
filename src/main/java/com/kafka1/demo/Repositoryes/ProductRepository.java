package com.kafka1.demo.Repositoryes;

import com.kafka1.demo.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>
{
    List<Product> findAllBy();
    Product findById(int id);
    Product findByName(String name);
}
