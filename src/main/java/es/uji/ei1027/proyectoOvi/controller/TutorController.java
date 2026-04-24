package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.OviUserDao;
import es.uji.ei1027.proyectoOvi.dao.TutorDao;
import es.uji.ei1027.proyectoOvi.models.Tutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    @RequestMapping("/list")
    public String listTutors(Model model){
        model.addAttribute("tutors", tutorDao.getTutors());
        return "tutor/list";
    }
    //
    @RequestMapping(value="/add")
    public String addTutor(Model model) {
        model.addAttribute("tutor", new Tutor());
        return "tutor/add";
    }
    //
    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("tutor") Tutor tutor,
                                   BindingResult bindingResult) {
        TutorValidator tutorValidator = new TutorValidator();
        tutorValidator.validate(tutor, bindingResult);

        if (bindingResult.hasErrors()) {
            return "tutor/add";
        }

        try {
            tutorDao.addTutor(tutor);
        } catch (DuplicateKeyException e) {
            bindingResult.rejectValue("dni", "duplicat",
                    "Ya existe un tutor con este DNI");
            return "tutor/add";
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
}
