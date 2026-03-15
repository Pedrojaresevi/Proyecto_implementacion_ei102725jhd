package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.TutorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tutor")
public class TutorController {
    private TutorDao tutorDao;

    @Autowired
    public void setTutorDao(TutorDao tutorDao){
        this.tutorDao = tutorDao;
    }

    @RequestMapping("/list")
    public String listTutors(Model model){
        model.addAttribute("tutors", tutorDao.getTutors());
        return "tutor/list";
    }
}
