package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.OviUserDao;

@Controller
@RequestMapping("/oviUser")
public class OviUserController {
    private OviUserDao oviUserDao;

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao){
        this.oviUserDao = oviUserDao;
    }

    @RequestMapping("/list")
    public String listOviUsers(Model model){
        model.addAttribute("oviUsers", oviUserDao.getOviUsers());
        return "oviUser/list";
    }
}
