package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.Pap_PatiDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pap_pati")
public class Pap_PatiController {
    private Pap_PatiDao pap_patiDao;

    @Autowired
    public void setPap_patiDao(Pap_PatiDao pap_patiDao){
        this.pap_patiDao = pap_patiDao;
    }

    @RequestMapping("/list")
    public String listAllPap_Pati(Model model){
        model.addAttribute("allpap_pati", pap_patiDao.getAllPap_Pati());
        return "pap_pati/list";
    }
}
