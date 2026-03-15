package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.TutorDao;

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
