package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index"; 
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "dashboard";
    }
}