package es.uji.ei1027.proyectoOvi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    // Esta ruta cargará tu NUEVO index.html (la Landing Page con info de OVI)
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Esta ruta cargará tu HTML ANTIGUO (que has renombrado a dashboard.html)
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}