package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.PapPatiDao;
import es.uji.ei1027.proyectoOvi.models.Pap_Pati;
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
@RequestMapping("/pap_pati")
public class Pap_PatiController {
    private PapPatiDao pap_patiDao;

    @Autowired
    public void setPap_patiDao(PapPatiDao pap_patiDao){
        this.pap_patiDao = pap_patiDao;
    }

    @RequestMapping("/list")
    public String listAllPap_Pati(Model model){
        model.addAttribute("allpap_pati", pap_patiDao.getAllPap_Pati());
        return "pap_pati/list";
    }

    @RequestMapping(value="/add")
    public String addPapPati(Model model) {
        model.addAttribute("pap_pati", new Pap_Pati());
        return "pap_pati/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("pap_pati") Pap_Pati papPati,
                                   BindingResult bindingResult) {
        Pap_PatiValidator pap_patiValidator = new Pap_PatiValidator();
        pap_patiValidator.validate(papPati, bindingResult);

        if (bindingResult.hasErrors()) {
            return "pap_pati/add";
        }

        try {
            pap_patiDao.addPap_Pati(papPati);
        } catch (DuplicateKeyException e) {
            bindingResult.rejectValue("dni", "duplicat",
                    "Ya existe un Pap/Pati con este DNI");
            return "pap_pati/add";
        }

        return "redirect:list";
    }

    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editPapPati(Model model, @PathVariable String id) {
        model.addAttribute("pap_pati", pap_patiDao.getPap_Pati(id));
        List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
        model.addAttribute("statusList", statusList);
        return "pap_pati/update";
    }

    @RequestMapping(value="/update", method=RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("pap_pati") Pap_Pati papPati,
                                      BindingResult bindingResult, Model model) {
        Pap_PatiValidator pap_patiValidator = new Pap_PatiValidator();
        pap_patiValidator.validate(papPati, bindingResult);

        if (bindingResult.hasErrors()) {
            return "pap_pati/update";
        }
        pap_patiDao.updatePap_Pati(papPati);

        return "redirect:list";
    }

    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable String id) {
        pap_patiDao.deletePap_Pati(id);
        return "redirect:../list";
    }
}
