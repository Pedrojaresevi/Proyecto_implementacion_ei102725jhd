package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.NegotiationDao;
import es.uji.ei1027.proyectoOvi.models.Negotiation;
import es.uji.ei1027.proyectoOvi.models.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/negotiation")
public class NegotiationController {
    private NegotiationDao negotiationDao;

    @Autowired
    public void setNegotiationDao(NegotiationDao negotiationDao){
        this.negotiationDao = negotiationDao;
    }

    @RequestMapping("/list")
    public String listNegotiations(Model model){
        model.addAttribute("negotiations", negotiationDao.getNegotiations());
        return "negotiation/list";
    }

//    @RequestMapping(value="/add")
//    public String addNegotiation(Model model) {
//        model.addAttribute("negotiation", new Negotiation());
//        return "negotiation/add";
//    }
    @RequestMapping(value="/add")
    public String addNegotiation(Model model, jakarta.servlet.http.HttpSession session) {
        // 1. Recuperamos el usuario de la sesión
        UserDetails user = (UserDetails) session.getAttribute("user");

        // 2. Si no hay usuario, lo mandamos al login
        if (user == null) {
            return "redirect:/login";
        }

        // 3. Le pasamos el usuario al modelo para que la vista pueda usar user.dni
        model.addAttribute("user", user);

        // 4. Pasamos el objeto Negotiation vacío para el formulario
        model.addAttribute("negotiation", new Negotiation());

        return "negotiation/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("negotiation") Negotiation negotiation,
                                   BindingResult bindingResult) {
        NegotiationValidator negotiationValidator = new NegotiationValidator();
        negotiationValidator.validate(negotiation, bindingResult);

        if (bindingResult.hasErrors()) {
            return "negotiation/add";
        }

        try {
            negotiationDao.addNegotiation(negotiation);
        } catch (DuplicateKeyException e) {
            bindingResult.rejectValue("negotiation_Id", "duplicat",
                    "Ya existe una negociación con este ID");
            return "negotiation/add";
        }

        return "redirect:list";
    }

    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editNegotiation(Model model, @PathVariable("id") String id) {
        model.addAttribute("negotiation", negotiationDao.getNegotiation(id));
        List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
        model.addAttribute("statusList", statusList);
        return "negotiation/update";
    }

    @RequestMapping(value="/update", method=RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("negotiation") Negotiation negotiation,
                                      BindingResult bindingResult, Model model, HttpSession session) {
        NegotiationValidator negotiationValidator = new NegotiationValidator();
        negotiationValidator.validate(negotiation, bindingResult);

        if (bindingResult.hasErrors()) {
            return "negotiation/update";
        }
        negotiationDao.updateNegotiation(negotiation);

        UserDetails user = (UserDetails) session.getAttribute("user");
        return "redirect:/negotiation/user/" + user.getDni();
    }

    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable("id") String id) {
        negotiationDao.deleteNegotiation(id);
        return "redirect:../list";
    }
    //
    @RequestMapping("/user/{dni}")
    public String listNegotiationsByUser(Model model, @PathVariable String dni, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        model.addAttribute("negotiations", negotiationDao.getNegotiationsByUserPaginated(dni, pageSize, offset));

        int totalItems = negotiationDao.countNegotiationsByUser(dni);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("dniOwner", dni); // Necesario para los enlaces de paginación

        return "negotiation/list";
    }

    @RequestMapping("/tutor/{dni}")
    public String listNegotiationsByTutor(Model model, @PathVariable String dni, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        model.addAttribute("negotiations", negotiationDao.getNegotiationsByTutorPaginated(dni, pageSize, offset));

        int totalItems = negotiationDao.countNegotiationsByTutor(dni);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("dniOwner", dni); // Necesario para los enlaces de paginación

        return "negotiation/tutor/list";
    }

    //
    @RequestMapping("/chat/{id}")
    public String openChat(@PathVariable("id") String id, Model model, jakarta.servlet.http.HttpSession session) {
        // 1. Recuperamos el usuario de la sesión
        UserDetails user = (UserDetails) session.getAttribute("user");

        // 2. Si no hay usuario, lo mandamos al login por seguridad
        if (user == null) {
            return "redirect:/login";
        }

        // 3. Pasamos el usuario al modelo (vital para el botón de volver)
        model.addAttribute("user", user);

        // 4. Pasamos los datos de la negociación
        model.addAttribute("negotiation", negotiationDao.getNegotiation(id));

        return "negotiation/chat";
    }
}
