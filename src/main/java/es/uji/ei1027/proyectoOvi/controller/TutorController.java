package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.OviUserDao;
import es.uji.ei1027.proyectoOvi.dao.TutorDao;
import es.uji.ei1027.proyectoOvi.models.OviUser;
import es.uji.ei1027.proyectoOvi.models.Tutor;
import es.uji.ei1027.proyectoOvi.models.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/tutor")
public class TutorController {
    private TutorDao tutorDao;
    private OviUserDao oviUserDao;

    @Autowired
    public void setTutorDao(TutorDao tutorDao){
        this.tutorDao = tutorDao;
    }

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao){
        this.oviUserDao = oviUserDao;
    }

    @RequestMapping("/list")
    public String listTutors(Model model){
        model.addAttribute("tutors", tutorDao.getTutors());
        return "tutor/list";
    }
    //
    @RequestMapping(value="/add")
    public String addTutor(Model model) {
        model.addAttribute("tutor", new Tutor());
        return "add_minor";
    }
    //
    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("tutor") Tutor tutor,
                                   BindingResult bindingResult) {
        TutorValidator tutorValidator = new TutorValidator();
        tutorValidator.validate(tutor, bindingResult);

        if (bindingResult.hasErrors()) {
            return "add_minor";
        }

        try {
            tutorDao.addTutor(tutor);
        } catch (DuplicateKeyException e) {
            bindingResult.rejectValue("dni", "duplicat",
                    "Ya existe un tutor con este DNI");
            return "add_minor";
        }

        return "redirect:list";
    }
    //
    @RequestMapping(value="/update/{dni}", method = RequestMethod.GET)
    public String editTutor(Model model, @PathVariable String dni) {
        model.addAttribute("tutor", tutorDao.getTutor(dni));
        List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
        model.addAttribute("statusList", statusList);
        return "tutor/update";
    }
    //
    @RequestMapping(value="/update", method=RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("tutor") Tutor tutor,
                                      BindingResult bindingResult, Model model) {
        TutorValidator tutorValidator = new TutorValidator();
        tutorValidator.validate(tutor, bindingResult);

        if (bindingResult.hasErrors()) {
            return "tutor/update";
        }
        tutorDao.updateTutor(tutor);

        return "redirect:list";
    }
    //
    @RequestMapping(value="/delete/{dni}")
    public String processDelete(@PathVariable String dni) {
        tutorDao.deleteTutor(dni);
        return "redirect:../list";
    }

    @RequestMapping("/users/{dni}")
    public String listUsersByTutor(Model model, @PathVariable String dni) {
        // Obtenemos el tutor para mostrar su nombre en el título
        model.addAttribute("tutor", tutorDao.getTutor(dni));

        // Obtenemos la lista de personas a su cargo
        model.addAttribute("oviUsers", oviUserDao.getOviUsersByTutor(dni));

        return "tutor/list_minors";
    }
    // MÉTODOS PARA AÑADIR MENORES DESDE EL TUTOR

    @RequestMapping(value="/add-minor", method = RequestMethod.GET)
    public String addMinor(Model model, HttpSession session) {
        // Recuperamos el usuario conectado de la sesión
        UserDetails user = (UserDetails) session.getAttribute("user");

        // Si no hay usuario o no tiene permisos, lo mandamos al login
        if (user == null) {
            return "redirect:/login";
        }

        OviUser oviUser = new OviUser();
        // Pre-asignamos el DNI del tutor que ha iniciado sesión
        oviUser.setTutor_id(user.getDni());

        model.addAttribute("oviUser", oviUser);
        return "tutor/add_minor";
    }

    @RequestMapping(value="/add-minor", method = RequestMethod.POST)
    public String processAddMinorSubmit(@ModelAttribute("oviUser") OviUser oviUser,
                                        BindingResult bindingResult,
                                        HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Por seguridad, forzamos que el tutor_id sea el del usuario conectado
        // evitando que alguien manipule el HTML e intente poner otro DNI
        oviUser.setTutor_id(user.getDni());

        // IMPORTANTE: Aquí necesitarías validar al OviUser (si tienes creado el OviUserValidator)
        // OviUserValidator oviUserValidator = new OviUserValidator();
        // oviUserValidator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            return "tutor/add_minor";
        }

        try {
            // Utilizamos el oviUserDao que ya tienes inyectado en este controlador
            oviUserDao.addOviUser(oviUser);
        } catch (DuplicateKeyException e) {
            bindingResult.rejectValue("dni", "duplicat", "Ya existe un usuario con este DNI");
            return "tutor/add_minor";
        }

        // Redirigimos a la lista de menores de este tutor concreto
        return "redirect:/tutor/users/" + user.getDni();
    }
}
