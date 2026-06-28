package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.OviUserDao;
import es.uji.ei1027.proyectoOvi.models.OviUser;
import es.uji.ei1027.proyectoOvi.models.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @RequestMapping(value="/add")
    public String addOviUser(Model model) {
        model.addAttribute("oviUser", new OviUser());
        cargarListasDesplegables(model); 
        return "oviUser/add";
    }

    @GetMapping("/success")
    public String registrationSuccess() {
        
        return "success";
    }

    @RequestMapping(value="/add", method=RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("oviUser") OviUser oviUser,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        if (oviUser.getTutor_id() != null && oviUser.getTutor_id().trim().isEmpty()) {
            oviUser.setTutor_id(null);
        }

        OviUserValidator oviUserValidator = new OviUserValidator();
        oviUserValidator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            cargarListasDesplegables(model);
            return "oviUser/add";
        }

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String contrasenyaEncriptada = passwordEncryptor.encryptPassword(oviUser.getPassword());
        oviUser.setPassword(contrasenyaEncriptada);

        oviUser.setStatus("in progress");

        try {
            oviUserDao.addOviUser(oviUser);
        } catch (DuplicateKeyException e) {
            bindingResult.rejectValue("dni", "duplicat",
                    "Ya existe un usuario con este DNI");
            cargarListasDesplegables(model);
            return "oviUser/add";
        } catch (DataIntegrityViolationException e) {
            System.out.println("ERROR REAL EN LA BD: " + e.getMessage());
            e.printStackTrace();

            bindingResult.rejectValue("tutor_id", "no_existe",
                    "El tutor introducido no existe en el sistema");
            cargarListasDesplegables(model);
            return "oviUser/add";
        }

        redirectAttributes.addFlashAttribute("tipoPerfil", "Usuario OVI");
        redirectAttributes.addFlashAttribute("nombreUsuario", oviUser.getName());
        redirectAttributes.addFlashAttribute("dniUsuario", oviUser.getDni());

        return "redirect:success";
    }

    
    private void cargarListasDesplegables(Model model) {
        
        List<String> entidades = Arrays.asList(
                "Ayuntamiento de Castellón",
                "Cocemfe",
                "ONCE",
                "Fundación Síndrome de Down",
                "Otra"
        );

        List<String> diversidades = Arrays.asList(
                "Física",
                "Visual",
                "Auditiva",
                "Intelectual",
                "Mental",
                "Múltiple"
        );

        model.addAttribute("entidadesDisponibles", entidades);
        model.addAttribute("diversidadesDisponibles", diversidades);
    }
    @RequestMapping(value="/update/{dni}", method = RequestMethod.GET)
    public String editOviUser(Model model, @PathVariable String dni) {
        model.addAttribute("oviUser", oviUserDao.getOviUser(dni));

        List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
        model.addAttribute("statusList", statusList);

        cargarListasDesplegables(model); 

        return "oviUser/update";
    }

    @RequestMapping(value="/update", method=RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("oviUser") OviUser oviUser,
                                      BindingResult bindingResult,
                                      Model model,
                                      HttpSession session) {
        OviUserValidator oviUserValidator = new OviUserValidator();
        oviUserValidator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
            model.addAttribute("statusList", statusList);
            cargarListasDesplegables(model);
            return "oviUser/update";
        }

        if (oviUser.getTutor_id() != null && oviUser.getTutor_id().trim().isEmpty()) {
            oviUser.setTutor_id(null);
        }

        try {
            oviUserDao.updateOviUser(oviUser);
        } catch (DataIntegrityViolationException e) {
            List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
            model.addAttribute("statusList", statusList);
            cargarListasDesplegables(model);

            bindingResult.rejectValue("tutor_id", "noExiste",
                    "El ID de tutor introducido no existe en el sistema");
            return "oviUser/update";
        }

        UserDetails loggedUser = (UserDetails) session.getAttribute("user");
        if (loggedUser != null && "technician".equals(loggedUser.getRole())) {
            return "redirect:/oviUser/accepted";
        }
        return "redirect:/dashboard";
    }

    
    @RequestMapping(value="/delete/{dni}", method = RequestMethod.GET)
    public String showDeleteConfirmation(Model model, @PathVariable String dni) {
        
        model.addAttribute("dni", dni);
        return "technician/oviUser/confirmarborrado"; 
    }
    
    @RequestMapping(value="/delete/{dni}", method = RequestMethod.POST)
    public String processDelete(@PathVariable String dni) {
        oviUserDao.deleteOviUser(dni);
        return "redirect:/oviUser/accepted"; 
    }
    
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
    public String executeAcceptOviUser(@PathVariable String dni, Model model) {
        OviUser oviUser = oviUserDao.getOviUser(dni);
        if (oviUser != null) {
            oviUser.setStatus("accepted");
            oviUserDao.updateOviUser(oviUser);

            model.addAttribute("oviUser", oviUser);
            model.addAttribute("actionType", "accepted");
        }

        return "technician/oviUser/simulacion_email";
    }

    @RequestMapping(value="/reject/{dni}", method = RequestMethod.GET)
    public String confirmRejectOviUser(Model model, @PathVariable String dni) {
        model.addAttribute("oviUser", oviUserDao.getOviUser(dni));
        return "technician/oviUser/reject";
    }

    @RequestMapping(value="/reject/execute/{dni}", method = RequestMethod.POST)
    public String executeRejectOviUser(@PathVariable String dni,
                                       @RequestParam("rejectReason") String rejectReason,
                                       Model model) {
        OviUser oviUser = oviUserDao.getOviUser(dni);
        if (oviUser != null) {
            oviUser.setStatus("refused");
            oviUser.setRejectReason(rejectReason);
            oviUserDao.updateOviUser(oviUser);

            model.addAttribute("oviUser", oviUser);
            model.addAttribute("actionType", "refused");
            model.addAttribute("rejectReason", rejectReason);
        }

        return "technician/oviUser/simulacion_email";
    }

    @RequestMapping("/refused")
    public String listRefusedOviUsers(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        List<OviUser> requests = oviUserDao.getOviUsersByStatusPaginated("refused", pageSize, offset);
        int totalItems = oviUserDao.countOviUsersByStatus("refused");

        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("oviUsers", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "technician/oviUser/refused";
    }

    @RequestMapping(value="/masdetalle/{dni}", method = RequestMethod.GET)
    public String verMasDetalle(Model model,
                                @PathVariable String dni,
                                @RequestParam(value = "from", required = false) String from,
                                @RequestParam(value = "tutorDni", required = false) String tutorDni,
                                HttpSession session) {

        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        OviUser oviUser = oviUserDao.getOviUser(dni);

        if (oviUser == null) {
            return "redirect:/oviUser/accepted";
        }

        
        String volverUrl = "/dashboard"; 

        if ("accepted".equals(from)) {
            volverUrl = "/oviUser/accepted";
        } else if ("refused".equals(from)) {
            volverUrl = "/oviUser/refused";
        } else if ("pending".equals(from)) {
            volverUrl = "/oviUser/pending";
        } else if ("list_minors".equals(from) && tutorDni != null) {
            volverUrl = "/tutor/users/" + tutorDni;
        }

        
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("volverUrl", volverUrl);

        return "oviUser/masdetalle";
    }
}