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
        return "index"; // Tu landing page actual
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Recuperamos el usuario que guardamos en el LoginController
        UserDetails user = (UserDetails) session.getAttribute("user");

        // Si alguien intenta entrar a /dashboard sin loguearse, lo mandamos al login
        if (user == null) {
            return "redirect:/login";
        }

        // Pasamos el usuario al modelo para que Thymeleaf pueda leer su nombre y rol
        model.addAttribute("user", user);
        return "dashboard";
    }
}