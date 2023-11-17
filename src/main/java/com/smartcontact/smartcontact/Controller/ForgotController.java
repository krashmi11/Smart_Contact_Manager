package com.smartcontact.smartcontact.Controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.smartcontact.Helper.helper;
import com.smartcontact.smartcontact.Model.User;
import com.smartcontact.smartcontact.Repository.userrepo;
import com.smartcontact.smartcontact.services.EmailSenderService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private userrepo ur;

    @RequestMapping("/forgot")
    public String forgot_pass() {
        return "forgot_pass";
    }

    @PostMapping("/send-OTP")
    public String Enter_OTP(@RequestParam("regEmail") String email, HttpSession session) {
        Random random = new Random();
        int OTP = random.nextInt(999999);
        System.out.println("otp:" + OTP);
        String body = "OTP=" + OTP;
        String subject = "OTP from Smart Contact Manager";
        this.emailSenderService.sendEmail(email, subject, body);
        System.out.println("Mail sent");
        session.setAttribute("myemail", email);
        session.setAttribute("myotp", OTP);
        return "OTP";
    }

    @PostMapping("/verify-OTP")
    public String Verify(@RequestParam("OTP") int otp, HttpSession session) {
        int otpp = (int) session.getAttribute("myotp");
        if (otp == otpp) {
            return "New_pass";
        } else {
            session.setAttribute("message", new helper("OTP didn't match!! Try Again", "alert-danger"));
            return "redirect:/forgot";
        }
    }

    @PostMapping("/change-pass")
    public String new_pass(@RequestParam("newp") String pass, HttpSession session) {
        User u = this.ur.getUserByEmail((String) session.getAttribute("myemail"));
        u.setPassword(bCryptPasswordEncoder.encode(pass));
        this.ur.save(u);
        return "redirect:/login?change=password changed successfully!!";
    }
}
