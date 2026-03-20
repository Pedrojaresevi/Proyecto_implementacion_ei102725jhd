package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.OviUserDao;
import es.uji.ei1027.proyectoOvi.models.OviUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    @RequestMapping(value="/add")
    public String addNadador(Model model) {
        model.addAttribute("oviUser", new OviUser());
        return "oviUser/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("oviUser") OviUser oviUser,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "oviUser/add";
        oviUserDao.addOviUser(oviUser);
        return "redirect:list";
    }

    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editOviUser(Model model, @PathVariable String id) {
        model.addAttribute("oviUser", oviUserDao.getOviUser(id));

        return "oviUser/update";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("oviUser") OviUser oviUser,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "oviUser/update";

        oviUserDao.updateOviUser(oviUser);

        return "redirect:list";
    }

    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable String id) {
        oviUserDao.deleteOviUser(id);
        return "redirect:../list";
    }

}
