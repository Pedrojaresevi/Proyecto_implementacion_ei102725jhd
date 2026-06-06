package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.OviUserDao;
import es.uji.ei1027.proyectoOvi.models.OviUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/oviUser")
public class OviUserController {
    private OviUserDao oviUserDao;

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao){
        this.oviUserDao = oviUserDao;
    }

    @RequestMapping("/list")
    public String listOviUsers(Model model, @RequestParam(defaultValue = "1") int page){
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        model.addAttribute("oviUsers", oviUserDao.getOviUsersPaginated(pageSize, offset));

        int totalItems = oviUserDao.countOviUsers();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "oviUser/list";
    }

    @RequestMapping(value="/add")
    public String addOviUser(Model model) {
        model.addAttribute("oviUser", new OviUser());
        return "oviUser/add";
    }

//    @RequestMapping(value="/add", method= RequestMethod.POST)
//    public String processAddSubmit(@ModelAttribute("oviUser") OviUser oviUser,
//                                   BindingResult bindingResult) {
//        if (bindingResult.hasErrors())
//            return "oviUser/add";
//        oviUserDao.addOviUser(oviUser);
//        return "redirect:list";
//    }

    @RequestMapping(value="/add", method=RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("oviUser") OviUser oviUser,
                                   BindingResult bindingResult) {
        OviUserValidator oviUserValidator = new OviUserValidator();
        oviUserValidator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            return "oviUser/add";
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

//    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
//    public String editOviUser(Model model, @PathVariable String id) {
//        model.addAttribute("oviUser", oviUserDao.getOviUser(id));
//
//        return "oviUser/update";
//    }
    @RequestMapping(value="/update/{dni}", method = RequestMethod.GET)
    public String editOviUser(Model model, @PathVariable String dni) {
        model.addAttribute("oviUser", oviUserDao.getOviUser(dni));
        List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
        model.addAttribute("statusList", statusList);
        return "oviUser/update";
    }

//    @RequestMapping(value="/update", method = RequestMethod.POST)
//    public String processUpdateSubmit(
//            @ModelAttribute("oviUser") OviUser oviUser,
//            BindingResult bindingResult) {
//        if (bindingResult.hasErrors())
//            return "oviUser/update";
//
//        oviUserDao.updateOviUser(oviUser);
//
//        return "redirect:list";
//    }
    @RequestMapping(value="/update", method=RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("oviUser") OviUser oviUser,
                                      BindingResult bindingResult, Model model) {
        OviUserValidator oviUserValidator = new OviUserValidator();
        oviUserValidator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            return "oviUser/update";
        }

        if (oviUser.getTutor_id() != null && oviUser.getTutor_id().trim().isEmpty()) {
            oviUser.setTutor_id(null);
        }

        try {
            oviUserDao.updateOviUser(oviUser);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("tutor_id", "noExiste",
                    "El ID de tutor introducido no existe en el sistema");
            return "oviUser/update";
        }

        return "redirect:/oviUser/accepted";    }

    @RequestMapping(value="/delete/{dni}")
    public String processDelete(@PathVariable String dni) {
        oviUserDao.deleteOviUser(dni);
        return "redirect:../list";
    }
    //
    @RequestMapping("/pending")
    public String listPendingOviUsers(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        model.addAttribute("oviUsers", oviUserDao.getOviUsersByStatusPaginated("in progress", pageSize, offset));

        int totalItems = oviUserDao.countOviUsersByStatus("in progress");
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "technician/oviUser/pending";
    }

    @RequestMapping("/accepted")
    public String listAcceptedOviUsers(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        model.addAttribute("oviUsers", oviUserDao.getOviUsersByStatusPaginated("accepted", pageSize, offset));

        int totalItems = oviUserDao.countOviUsersByStatus("accepted");
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "technician/oviUser/accepted";
    }

    @RequestMapping(value="/accept/{dni}", method = RequestMethod.GET)
    public String confirmAcceptOviUser(Model model, @PathVariable String dni) {
        model.addAttribute("oviUser", oviUserDao.getOviUser(dni));
        return "technician/oviUser/accept";
    }

    @RequestMapping(value="/accept/execute/{dni}", method = RequestMethod.GET)
    public String executeAcceptOviUser(@PathVariable String dni) {
        OviUser oviUser = oviUserDao.getOviUser(dni);
        if (oviUser != null) {
            oviUser.setStatus("accepted");
            oviUserDao.updateOviUser(oviUser);
        }
        return "redirect:/oviUser/pending";
    }

    @RequestMapping(value="/reject/{dni}", method = RequestMethod.GET)
    public String confirmRejectOviUser(Model model, @PathVariable String dni) {
        model.addAttribute("oviUser", oviUserDao.getOviUser(dni));
        return "technician/oviUser/reject";
    }

    @RequestMapping(value="/reject/execute/{dni}", method = RequestMethod.GET)
    public String executeRejectOviUser(@PathVariable String dni) {
        OviUser oviUser = oviUserDao.getOviUser(dni);
        if (oviUser != null) {
            oviUser.setStatus("refused");
            oviUserDao.updateOviUser(oviUser);
        }
        return "redirect:/oviUser/pending";
    }

}
