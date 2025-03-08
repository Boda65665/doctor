package com.kafka1.demo.Repositoryes;

import com.kafka1.demo.Entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    Article findById(int id);
    @Query("SELECT a FROM Article a ORDER BY a.id")
    List<Article> findAllOrderById();
}
