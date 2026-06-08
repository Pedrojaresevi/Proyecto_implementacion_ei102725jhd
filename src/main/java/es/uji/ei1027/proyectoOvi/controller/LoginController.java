package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.UserDetails;
import es.uji.ei1027.proyectoOvi.models.OviUser;
import es.uji.ei1027.proyectoOvi.models.Tutor;
import es.uji.ei1027.proyectoOvi.models.Pap_Pati; // Ajusta el import si tu modelo se llama distinto

import es.uji.ei1027.proyectoOvi.dao.OviUserDao;
import es.uji.ei1027.proyectoOvi.dao.TutorDao;
import es.uji.ei1027.proyectoOvi.dao.PapPatiDao; // Ajusta el import según tu DAO

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.jasypt.util.password.BasicPasswordEncryptor;

@Controller
public class LoginController {

    private OviUserDao oviUserDao;
    private TutorDao tutorDao;
    private PapPatiDao papPatiDao;

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }

    @Autowired
    public void setTutorDao(TutorDao tutorDao) {
        this.tutorDao = tutorDao;
    }

    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {
        this.papPatiDao = papPatiDao;
    }

    @RequestMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String checkLogin(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             HttpSession session, Model model) {

        // Creamos la instancia del encriptador de Jasypt
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

        // 1. COMPROBAR TÉCNICO OVI (Se queda igual porque está estático en texto plano)
        if (username.equals("admin") && password.equals("admin")) {
            UserDetails user = new UserDetails("00000000T", "Administrador Técnico", "technician");
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        }

        // 2. COMPROBAR OVI USER EN BASE DE DATOS (MODIFICADO CON JASYPT)
        OviUser oviUser = oviUserDao.getOviUser(username);
        // Usamos passwordEncryptor.checkPassword para verificar la contraseña introducida contra el hash de la BD
        if (oviUser != null && passwordEncryptor.checkPassword(password, oviUser.getPassword())) {
            UserDetails user = new UserDetails(oviUser.getDni(), oviUser.getName(), "oviuser");
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        }

        // 3. COMPROBAR TUTOR EN BASE DE DATOS
        Tutor tutor = tutorDao.getTutor(username);
        if (tutor != null && passwordEncryptor.checkPassword(password, tutor.getPassword())) {
            UserDetails user = new UserDetails(tutor.getDni(), tutor.getName(), "tutor");
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        }

        // 4. COMPROBAR PAP/PATI EN BASE DE DATOS
        Pap_Pati papPati = papPatiDao.getPap_Pati(username);
        if (papPati != null && passwordEncryptor.checkPassword(password, papPati.getPassword())) {
            UserDetails user = new UserDetails(papPati.getDni(), papPati.getName(), "pap_pati");
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        }

        // Si el código llega hasta aquí, significa que ningún usuario coincidió
        model.addAttribute("error", "DNI o contraseña incorrectos");
        return "login";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Limpiamos la sesión al salir
        return "redirect:/login";
    }
}