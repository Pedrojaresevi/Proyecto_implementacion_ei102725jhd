package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.ListOfProposedCandidatesDao;
import es.uji.ei1027.proyectoOvi.models.ListOfProposedCandidates;
import es.uji.ei1027.proyectoOvi.models.OviUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/ListOfProposedCandidatesController")
public class ListOfProposedCandidatesController {
    private ListOfProposedCandidatesDao listOfProposedCandidatesDao;

    @Autowired
    public void setListOfProposedCandidatesDao(ListOfProposedCandidatesDao listOfProposedCandidatesDao){
        this.listOfProposedCandidatesDao = listOfProposedCandidatesDao;
    }

    @RequestMapping("/list")
    public String listListOfProposedCandidates(Model model){
        model.addAttribute("ListOfProposedCandidates", listOfProposedCandidatesDao.getListOfProposedCandidates());
        return "ListOfProposedCandidates/list";
    }

    @RequestMapping(value="/add")
    public String addListOfProposedCandidates(Model model) {
        model.addAttribute("ListOfProposedCandidates", new ListOfProposedCandidates());
        return "ListOfProposedCandidates/add";
    }
    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("ListOfProposedCandidates") ListOfProposedCandidates listOfProposedCandidates,
                                   BindingResult bindingResult) {
        ListOfProposedCandidatesValidator listOfProposedCandidatesValidator = new ListOfProposedCandidatesValidator();
        listOfProposedCandidatesValidator.validate(listOfProposedCandidates, bindingResult);

        if (bindingResult.hasErrors()) {
            return "ListOfProposedCandidates/add";
        }

        if (oviUser.getTutor_id() != null && oviUser.getTutor_id().trim().isEmpty()) {
            oviUser.setTutor_id(null);
        }

        try {
            oviUserDao.addOviUser(oviUser);
        } catch (DuplicateKeyException e) {
            bindingResult.rejectValue("dni", "duplicat",
                    "Ya existe un usuario con este DNI");
            return "oviUser/add";
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("tutor_id", "no_existe",
                    "El tutor introducido no existe en el sistema");
            return "oviUser/add";
        }

        return "redirect:list";
    }
}
