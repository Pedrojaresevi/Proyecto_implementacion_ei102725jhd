package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.UserDetails;
import es.uji.ei1027.proyectoOvi.models.OviUser;
import es.uji.ei1027.proyectoOvi.models.Tutor;
import es.uji.ei1027.proyectoOvi.models.Pap_Pati; 

import es.uji.ei1027.proyectoOvi.dao.OviUserDao;
import es.uji.ei1027.proyectoOvi.dao.TutorDao;
import es.uji.ei1027.proyectoOvi.dao.PapPatiDao; 

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

        
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

        
        if (username.equals("admin") && password.equals("admin")) {
            UserDetails user = new UserDetails("00000000T", "Administrador Técnico", "technician");
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        }

        
        OviUser oviUser = oviUserDao.getOviUser(username);
        if (oviUser != null && passwordEncryptor.checkPassword(password, oviUser.getPassword())) {

            
            if ("in progress".equals(oviUser.getStatus())) {
                model.addAttribute("error", "Tu solicitud de registro aún está en proceso de evaluación. No puedes acceder todavía.");
                return "login";
            } else if ("refused".equals(oviUser.getStatus())) {
                model.addAttribute("error", "Tu solicitud de acceso ha sido denegada. Por favor, contacta con la administración.");
                
                model.addAttribute("rejectReason", oviUser.getRejectReason() != null ? oviUser.getRejectReason() : "No especificado");
                return "login";
            }

            
            UserDetails user = new UserDetails(oviUser.getDni(), oviUser.getName(), "oviuser");
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        }

        
        Tutor tutor = tutorDao.getTutor(username);
        if (tutor != null && passwordEncryptor.checkPassword(password, tutor.getPassword())) {

            
            if ("in progress".equals(tutor.getStatus())) {
                model.addAttribute("error", "Tu perfil de tutor aún está en proceso de validación.");
                return "login";
            } else if ("refused".equals(tutor.getStatus())) {
                model.addAttribute("error", "Tu solicitud de acceso ha sido denegada. Por favor, contacta con la administración.");
                model.addAttribute("rejectReason", tutor.getRejectReason() != null ? tutor.getRejectReason() : "No especificado");
                return "login";
            }

            UserDetails user = new UserDetails(tutor.getDni(), tutor.getName(), "tutor");
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        }

        
        Pap_Pati papPati = papPatiDao.getPap_Pati(username);
        if (papPati != null && passwordEncryptor.checkPassword(password, papPati.getPassword())) {

            
            if ("in progress".equals(papPati.getStatus())) {
                model.addAttribute("error", "Tu perfil de asistente aún está en proceso de validación.");
                return "login";
            } else if ("refused".equals(papPati.getStatus())) {
                model.addAttribute("error", "Tu solicitud de acceso ha sido denegada. Por favor, contacta con la administración.");
                model.addAttribute("rejectReason", papPati.getRejectReason() != null ? papPati.getRejectReason() : "No especificado");
                return "login";
            }

            UserDetails user = new UserDetails(papPati.getDni(), papPati.getName(), "pap_pati");
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        }

        
        model.addAttribute("error", "DNI o contraseña incorrectos.");
        return "login";
    }

    @RequestMapping("/migrar-contrasenas")
    public String migrarContrasenas() {
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

        
        
        for (OviUser u : oviUserDao.getOviUsers()) {
            
            
            if (u.getPassword() != null && u.getPassword().length() < 20) {
                u.setPassword(passwordEncryptor.encryptPassword(u.getPassword()));
                oviUserDao.updateOviUser(u);
            }
        }

        
        for (Tutor t : tutorDao.getTutors()) { 
            if (t.getPassword() != null && t.getPassword().length() < 20) {
                t.setPassword(passwordEncryptor.encryptPassword(t.getPassword()));
                tutorDao.updateTutor(t);
            }
        }

        
        for (Pap_Pati p : papPatiDao.getAllPap_Pati()) { 
            if (p.getPassword() != null && p.getPassword().length() < 20) {
                p.setPassword(passwordEncryptor.encryptPassword(p.getPassword()));
                papPatiDao.updatePap_Pati(p);
            }
        }

        return "redirect:/login"; 
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/login";
    }
}