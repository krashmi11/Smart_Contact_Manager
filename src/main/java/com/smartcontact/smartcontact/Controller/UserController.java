package com.smartcontact.smartcontact.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
// import java.util.ArrayList;
// import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartcontact.smartcontact.Helper.helper;
import com.smartcontact.smartcontact.Model.Contact;
import com.smartcontact.smartcontact.Model.User;
import com.smartcontact.smartcontact.Repository.contactrepo;
import com.smartcontact.smartcontact.Repository.userrepo;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private userrepo userrepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private contactrepo cr;

    @GetMapping("/user/index")
    public String dashboard(Model model, Principal p) {
        String username = p.getName();
        User user = userrepo.getUserByEmail(username);
        System.out.println("Username:" + username);
        System.out.println(user);
        model.addAttribute("user", user);
        return "Users/user_dashboard";
    }

    @GetMapping("/user/add-contact")
    public String addContact(Model model, Principal p) {
        String username = p.getName();
        User user = userrepo.getUserByEmail(username);
        System.out.println("Username:" + username);
        System.out.println(user);
        model.addAttribute("title", "Add Contact");
        model.addAttribute("user", user);
        model.addAttribute("contact", new Contact());
        return "Users/addContact";
    }

    @PostMapping("/user/process-contact")
    public String contactProcess(@ModelAttribute("contact") Contact contact, Model m,
            @RequestParam("profileImage") MultipartFile file, Principal p, HttpSession session) {
        try {
            String username = p.getName();
            User user = this.userrepo.getUserByEmail(username);
            m.addAttribute("user", user);

            if (file.isEmpty()) {
                System.out.println("File required");
                contact.setImage("contact.png");
            } else {
                contact.setImage(file.getOriginalFilename());
                File file1 = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(file1.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image uploaded");
            }
            contact.setUser(user);
            user.getContacts().add(contact);
            this.userrepo.save(user);
            System.out.println("DATA:" + contact);
            System.out.println("Added to database");
            session.setAttribute("message", new helper("Successfully added!! ", "alert-success"));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            session.setAttribute("message", new helper("Something went wrong!! " + e.getMessage(), "alert-danger"));

        }

        return "Users/addContact";
    }

    @GetMapping("/user/show-contact/{page}")
    public String showContact(@PathVariable("page") Integer page, Model model, Principal p) {
        String username = p.getName();
        User user = userrepo.getUserByEmail(username);
        // List<Contact> contacts = user.getContacts();

        model.addAttribute("title", "View Contacts");
        model.addAttribute("user", user);
        Pageable pageable = PageRequest.of(page, 5);
        Page<Contact> contacts = cr.findContactByUser(user.getU_id(), pageable);
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        System.out.println("Total pages:" + contacts.getTotalPages());
        return "Users/show_contact";
    }

    // SHOWING Particular contact details
    @RequestMapping("/user/{c_id}/contact")
    public String showContactDetails(@PathVariable("c_id") Integer c_id, Model m, Principal p) {
        String username = p.getName();
        User user = this.userrepo.getUserByEmail(username);
        m.addAttribute("user", user);
        Optional<Contact> optionalContact = this.cr.findById(c_id);
        Contact contact = optionalContact.get();
        if (user.getU_id() == contact.getUser().getU_id())
            m.addAttribute("contact", contact);
        m.addAttribute("title", contact.getC_name());
        return "Users/contact_details";
    }

    // delete contact
    @GetMapping("/user/delete/{c_id}")
    public String deleteContact(@PathVariable("c_id") Integer c_id, Principal p, HttpSession httpSession, Model model) {
        String username = p.getName();
        User user = userrepo.getUserByEmail(username);
        Contact contact = this.cr.findById(c_id).get();
        model.addAttribute("title", "View Contacts");
        model.addAttribute("user", user);
        if (user.getU_id() == contact.getUser().getU_id()) {
            contact.setUser(null);
            this.cr.delete(contact);
            httpSession.setAttribute("message", new helper("Contact deleted successfully", "success"));
        }
        return "redirect:/user/show-contact/0";
    }

    @PostMapping("/user/update-form/{c_id}")
    public String updateContact(Model m, @PathVariable("c_id") Integer c_id, Principal p) {
        String username = p.getName();
        User u = userrepo.getUserByEmail(username);
        Contact contact = this.cr.findById(c_id).get();
        m.addAttribute("contact", contact);
        m.addAttribute("user", u);
        m.addAttribute("title", "update Form");
        return "Users/update_form";
    }

    @PostMapping("/user/update-contact")
    public String updateProcess(@ModelAttribute("contact") Contact contact, Model m,
            @RequestParam("profileImage") MultipartFile file, Principal p, HttpSession session) {
        try {
            String username = p.getName();
            User user = this.userrepo.getUserByEmail(username);
            m.addAttribute("user", user);
            Contact oldContact = this.cr.findById(contact.getC_id()).get();
            if (file.isEmpty()) {
                System.out.println("File required");
                contact.setImage(oldContact.getImage());
            } else {

                // delete old
                File deleteFile = new ClassPathResource("static/img").getFile();
                File f1 = new File(deleteFile, oldContact.getImage());
                f1.delete();

                // update image
                File file1 = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(file1.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());

                System.out.println("Image uploaded");
            }
            contact.setUser(user);
            this.cr.save(contact);
            System.out.println(contact.getC_name());
            System.out.println(contact.getC_id());
            session.setAttribute("message", new helper("Contact Updated Successfully!! ", "alert-success"));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            session.setAttribute("message", new helper("Something went wrong!! " + e.getMessage(), "alert-danger"));

        }

        return "Users/update_form";
    }

    // profile_page
    @GetMapping("/user/profile-page")
    public String profile_page(Model m, Principal p) {
        String username = p.getName();
        User user = this.userrepo.getUserByEmail(username);
        m.addAttribute("user", user);
        return "Users/profile_page";
    }

    @GetMapping("/user/setting")
    public String settings(Principal p, Model m) {
        User user = this.userrepo.getUserByEmail(p.getName());
        m.addAttribute("user", user);
        return "Users/password_page";
    }

    @PostMapping("/user/change-password")
    public String change_Password(@RequestParam("oldpass") String oldpass, @RequestParam("newpass") String newpass,
            Model m, Principal p, HttpSession session) {
        User user = this.userrepo.getUserByEmail(p.getName());
        m.addAttribute("user", user);
        if (bCryptPasswordEncoder.matches(oldpass, user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(newpass));
            this.userrepo.save(user);
            session.setAttribute("message", new helper("Password Changed successfully", "alert-success"));
            return "redirect:/user/index";
        } else {
            session.setAttribute("message", new helper("Please enter correct old Password", "alert-danger"));
            return "redirect:/user/setting";
        }

    }

}
