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

//    @RequestMapping(value="/login", method=RequestMethod.POST)
//    public String checkLogin(@RequestParam("username") String username,
//                             @RequestParam("password") String password,
//                             HttpSession session, Model model) {
//
//        // Creamos la instancia del encriptador de Jasypt
//        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
//
//        // 1. COMPROBAR TÉCNICO OVI (Se queda igual porque está estático en texto plano)
//        if (username.equals("admin") && password.equals("admin")) {
//            UserDetails user = new UserDetails("00000000T", "Administrador Técnico", "technician");
//            session.setAttribute("user", user);
//            return "redirect:/dashboard";
//        }
//
//        // 2. COMPROBAR OVI USER EN BASE DE DATOS
//        OviUser oviUser = oviUserDao.getOviUser(username);
//        if (oviUser != null && passwordEncryptor.checkPassword(password, oviUser.getPassword())) {
//
//            // Verificamos el estado antes de dejarle entrar
//            if ("in progress".equals(oviUser.getStatus())) {
//                model.addAttribute("error", "Tu solicitud de registro aún está en proceso de evaluación. No puedes acceder todavía.");
//                return "login";
//            } else if ("refused".equals(oviUser.getStatus())) {
//                model.addAttribute("error", "Tu solicitud de acceso ha sido denegada. Por favor, contacta con la administración.");
//                return "login";
//            }
//
//            // Si el estado es 'accepted', creamos la sesión
//            UserDetails user = new UserDetails(oviUser.getDni(), oviUser.getName(), "oviuser");
//            session.setAttribute("user", user);
//            return "redirect:/dashboard";
//        }
//
//        // 3. COMPROBAR TUTOR EN BASE DE DATOS
//        Tutor tutor = tutorDao.getTutor(username);
//        if (tutor != null && passwordEncryptor.checkPassword(password, tutor.getPassword())) {
//
//            // Si Tutor tiene campo status, descomenta esto:
//            /*
//            if ("in progress".equals(tutor.getStatus())) {
//                model.addAttribute("error", "Tu perfil de tutor aún está en proceso de validación.");
//                return "login";
//            } else if ("refused".equals(tutor.getStatus())) {
//                model.addAttribute("error", "Tu perfil de tutor ha sido denegado.");
//                return "login";
//            }
//            */
//
//            UserDetails user = new UserDetails(tutor.getDni(), tutor.getName(), "tutor");
//            session.setAttribute("user", user);
//            return "redirect:/dashboard";
//        }
//
//        // 4. COMPROBAR PAP/PATI EN BASE DE DATOS
//        Pap_Pati papPati = papPatiDao.getPap_Pati(username);
//        if (papPati != null && passwordEncryptor.checkPassword(password, papPati.getPassword())) {
//
//            // Si Pap_Pati tiene campo status, descomenta esto:
//            /*
//            if ("in progress".equals(papPati.getStatus())) {
//                model.addAttribute("error", "Tu perfil de asistente aún está en proceso de validación.");
//                return "login";
//            } else if ("refused".equals(papPati.getStatus())) {
//                model.addAttribute("error", "Tu perfil de asistente ha sido denegado.");
//                return "login";
//            }
//            */
//
//            UserDetails user = new UserDetails(papPati.getDni(), papPati.getName(), "pap_pati");
//            session.setAttribute("user", user);
//            return "redirect:/dashboard";
//        }
//
//        // Si el código llega hasta aquí, significa que ningún usuario coincidió o la contraseña es incorrecta
//        model.addAttribute("error", "DNI o contraseña incorrectos.");
//        return "login";
//    }

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

        // 2. COMPROBAR OVI USER EN BASE DE DATOS
        OviUser oviUser = oviUserDao.getOviUser(username);
        if (oviUser != null && passwordEncryptor.checkPassword(password, oviUser.getPassword())) {

            // Verificamos el estado antes de dejarle entrar
            if ("in progress".equals(oviUser.getStatus())) {
                model.addAttribute("error", "Tu solicitud de registro aún está en proceso de evaluación. No puedes acceder todavía.");
                return "login";
            } else if ("refused".equals(oviUser.getStatus())) {
                model.addAttribute("error", "Tu solicitud de acceso ha sido denegada. Por favor, contacta con la administración.");
                // Pasamos la razón del rechazo guardada en el modelo OviUser
                model.addAttribute("rejectReason", oviUser.getRejectReason() != null ? oviUser.getRejectReason() : "No especificado");
                return "login";
            }

            // Si el estado es 'accepted', creamos la sesión
            UserDetails user = new UserDetails(oviUser.getDni(), oviUser.getName(), "oviuser");
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        }

        // 3. COMPROBAR TUTOR EN BASE DE DATOS
        Tutor tutor = tutorDao.getTutor(username);
        if (tutor != null && passwordEncryptor.checkPassword(password, tutor.getPassword())) {

            // Descomentado y adaptado para incluir la razón del rechazo
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

        // 4. COMPROBAR PAP/PATI EN BASE DE DATOS
        Pap_Pati papPati = papPatiDao.getPap_Pati(username);
        if (papPati != null && passwordEncryptor.checkPassword(password, papPati.getPassword())) {

            // Descomentado y adaptado para incluir la razón del rechazo
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

        // Si el código llega hasta aquí, significa que ningún usuario coincidió o la contraseña es incorrecta
        model.addAttribute("error", "DNI o contraseña incorrectos.");
        return "login";
    }

    @RequestMapping("/migrar-contrasenas")
    public String migrarContrasenas() {
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

        // 1. Encriptar todos los OviUsers
        // (Asegúrate de tener el método getOviUsers() en tu OviUserDao)
        for (OviUser u : oviUserDao.getOviUsers()) {
            // Las contraseñas encriptadas de Jasypt miden unos 28 caracteres.
            // Si mide menos de 20, asumimos que está en texto plano.
            if (u.getPassword() != null && u.getPassword().length() < 20) {
                u.setPassword(passwordEncryptor.encryptPassword(u.getPassword()));
                oviUserDao.updateOviUser(u);
            }
        }

        // 2. Encriptar todos los Tutores
        for (Tutor t : tutorDao.getTutors()) { // Necesitas que exista getTutors() en TutorDao
            if (t.getPassword() != null && t.getPassword().length() < 20) {
                t.setPassword(passwordEncryptor.encryptPassword(t.getPassword()));
                tutorDao.updateTutor(t);
            }
        }

        // 3. Encriptar todos los Pap_Pati
        for (Pap_Pati p : papPatiDao.getAllPap_Pati()) { // Según tu DAO se llama getAllPap_Pati()
            if (p.getPassword() != null && p.getPassword().length() < 20) {
                p.setPassword(passwordEncryptor.encryptPassword(p.getPassword()));
                papPatiDao.updatePap_Pati(p);
            }
        }

        return "redirect:/login"; // Te devuelve al login cuando acabe
    }


    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Limpiamos la sesión al salir
        return "redirect:/login";
    }
}