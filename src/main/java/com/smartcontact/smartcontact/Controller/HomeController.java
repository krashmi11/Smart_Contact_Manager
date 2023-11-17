package com.smartcontact.smartcontact.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.smartcontact.Helper.helper;
import com.smartcontact.smartcontact.Model.User;
import com.smartcontact.smartcontact.Repository.userrepo;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private userrepo repo;

    @Autowired
    public BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home-Smart Contact Manager");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About-Smart Contact Manager");
        return "about";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Register-Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login-Smart Contact Manager");
        return "login";
    }

    @PostMapping("/do_register")
    public String registration(@ModelAttribute("user") User user,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
            HttpSession session) {
        try {
            if (!agreement) {
                System.out.println("You have not agreed to the the terms and conditons");
                throw new Exception("You have not agreed to the the terms and conditons");
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageurl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User inserted = repo.save(user);

            System.out.println("Agreement:" + agreement);
            System.out.println("USER:" + inserted);

            model.addAttribute("user", new User());
            session.setAttribute("message", new helper("Successfully Registered !!", "alert-success"));

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new helper("Something went wrong!! " + e.getMessage(), "alert-danger"));
        }

        return "signup";
    }
}
