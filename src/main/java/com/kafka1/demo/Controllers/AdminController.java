package com.kafka1.demo.Controllers;

import com.kafka1.demo.Entity.*;
import com.kafka1.demo.Form.ArticleForm;
import com.kafka1.demo.Form.DoctorForm;
import com.kafka1.demo.Models.Role;
import com.kafka1.demo.Repositoryes.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RequestMapping("/admin")
@Controller
public class AdminController {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final IllnessesInfoRepository illnessesInfoRepository;
    private final IllnessCategoryRepository illnessCategoryRepository;


    public AdminController(ArticleRepository articleRepository, UserRepository userRepository, DoctorRepository doctorRepository, IllnessesInfoRepository illnessesInfoRepository, IllnessCategoryRepository illnessCategoryRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.illnessesInfoRepository = illnessesInfoRepository;
        this.illnessCategoryRepository = illnessCategoryRepository;
    }

    @GetMapping("/")
    public String adminPanel(){
        return "/admin/panel";
    }

    @GetMapping("/article")
    public String createArticle(Model model){
        model.addAttribute("article", new ArticleForm());
        return "admin/article";
    }

    @PostMapping("/article")
    String createArticle(@ModelAttribute("article") @Valid ArticleForm articleForm, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "admin/article";
        }
        Article article = new Article(articleForm.getName(),articleForm.getText());
        articleRepository.save(article);
        return "redirect:/blog/articles";
    }

    @GetMapping("/doctor")
    public String createDoctor(Model model){
        model.addAttribute("doctor", new DoctorForm());
        return "/admin/doctor";
    }

    @PostMapping("/doctor")
    public String createDoctor(@ModelAttribute("doctor") @Valid DoctorForm doctorForm, BindingResult bindingResult, Model model){
        if (!bindingResult.hasErrors()) {
            User user = userRepository.findByEmail(doctorForm.getEmail());
            if (user == null) {
                model.addAttribute("err", "не найдено пользователей с таким email");
            }
            else {
                if (user.getRole()!=Role.USER){
                    model.addAttribute("err", "пользователь должен иметь role=user");
                }
                else {

                    user.setRole(Role.DOCTOR);
                    userRepository.save(user);
                    Doctor doctor = new Doctor(doctorForm.getSpeciality(), doctorForm.getPricePerMinute(), user, 5, new ArrayList<>());
                    doctorRepository.save(doctor);
                    model.addAttribute("notification", "Успешно");
                }
            }
        }
        return "/admin/doctor";
    }

    @GetMapping("/illnesses/add_article")
    public String goTest(Model model){
        model.addAttribute("illnesses_info",new IllnessesInfo());
        model.addAttribute("all_categories",illnessCategoryRepository.findAll());
        return "Illness/create";
    }

    @GetMapping("/illnesses/{id}")
    public String getInfo(Model model, @PathVariable("id") String idString){
        int id = convertId(idString);
        if (id==-1) return "redirect:/add";
        model.addAttribute("all_categories",illnessCategoryRepository.findAll());
        model.addAttribute("illnesses_info",illnessesInfoRepository.findById(id));
        return "Illness/update";
    }

    private int convertId(String idString){
        try {
            return Integer.parseInt(idString);
        }
        catch (NumberFormatException err){
            return -1;
        }
    }

    @GetMapping("/illnesses/add_category")
    public String addCategory(Model model){
        model.addAttribute("illness_category", new IllnessCategory());
        return "Illness/category";
    }

    @PostMapping("/illnesses/add_category")
    public String addCategory(@ModelAttribute("illness_category") IllnessCategory illnessCategory,Model model){
        if (illnessCategory.getName().isEmpty()){
            model.addAttribute("err","Введите название категории");
            return "Illness/category";
        }
        if (illnessCategoryRepository.findByName(illnessCategory.getName())!=null){
            model.addAttribute("err","Уже существует");
            return "Illness/category";
        }
        illnessCategoryRepository.save(illnessCategory);
        return "redirect:/admin/illnesses/add_category";
    }

    @PostMapping("/illnesses/del_category")
    public String delCategory(HttpServletRequest request){
        System.out.println(request.getParameter("id_category"));
        int id = convertId(request.getParameter("id_category"));
        if (id==-1){
            return "redirect:/admin/illnesses/add_category";
        }
        illnessCategoryRepository.deleteById(id);
        return "redirect:/admin/illnesses/add_category";
    }
}
