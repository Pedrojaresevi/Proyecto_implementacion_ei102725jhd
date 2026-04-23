package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.UserDetails;
import es.uji.ei1027.proyectoOvi.models.OviUser;
// Necesitarás un Dao para buscar al OviUser por DNI y password
// import es.uji.ei1027.proyectoOvi.dao.OviUserDao;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @RequestMapping("/login")
    public String login(Model model) {
        return "login"; // Devuelve el HTML del formulario
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String checkLogin(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             HttpSession session, Model model) {

        // 1. LÓGICA PARA EL TÉCNICO (Ejemplo rápido)
        if (username.equals("admin") && password.equals("admin")) {
            UserDetails user = new UserDetails();
            user.setDni("00000000T");
            user.setUsername("Administrador");
            user.setRole("technician");

            session.setAttribute("user", user); // ¡Aquí se guarda en la sesión!
            return "redirect:/assignmentRequest/list";
        }

        // 2. LÓGICA PARA EL OVI USER
        // Aquí deberías usar tu OviUserDao para buscar en la base de datos:
        // OviUser ovi = oviUserDao.getOviUser(username); // username suele ser el DNI
        // if (ovi != null && ovi.getPassword().equals(password)) { ... }

        // Ejemplo rápido para que puedas probar con un OVI User (DNI: 12345678A)
        if (username.equals("10000001A") && password.equals("1234")) {
            UserDetails user = new UserDetails();
            user.setDni("10000001A");
            user.setUsername("Usuario Prueba");
            user.setRole("oviuser");

            session.setAttribute("user", user);
            return "redirect:/assignmentRequest/list";
        }

        // Si falla, volvemos al login con error
        model.addAttribute("error", "Usuario o contraseña incorrectos");
        return "login";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Limpiamos la sesión
        return "redirect:/login";
    }

}