package com.kafka1.demo.Controllers;

import com.kafka1.demo.Entity.Medication;
import com.kafka1.demo.Repositoryes.ArticleRepository;
import com.kafka1.demo.Repositoryes.MedicationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/blog")
public class BlogController {
    private final MedicationRepository medicationRepository;
    public final ArticleRepository articleRepository;

    public BlogController(MedicationRepository medicationRepository, ArticleRepository articleRepository) {
        this.medicationRepository = medicationRepository;
        this.articleRepository = articleRepository;
    }

    @GetMapping("/supplements")
    public String supplements(Model model){
        model.addAttribute("medications", medicationRepository.findAllOrderById());
        return "/blog/supplements";
    }

    @GetMapping("/supplements/{id}")
    public String supplement(@PathVariable int id, Model model){
        Medication medication = medicationRepository.findById(id);
        if (medication==null) return "redirect:/blog/supplements/";
        model.addAttribute("medication", medication);
        return "/blog/supplement";
    }

    @GetMapping("/articles")
    public String articles(Model model){
        model.addAttribute("articles" ,articleRepository.findAllOrderById());
        return "/blog/articles";
    }

    @GetMapping("/article/{id}")
    public String article(@PathVariable int id,Model model){
        model.addAttribute("article" ,articleRepository.findById(id));
        return "/blog/article";
    }
}
