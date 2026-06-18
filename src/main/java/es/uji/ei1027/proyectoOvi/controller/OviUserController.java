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

        // 1. PRIMERO: Limpiamos los datos conflictivos (transformamos "" en null)
        if (oviUser.getTutor_id() != null && oviUser.getTutor_id().trim().isEmpty()) {
            oviUser.setTutor_id(null);
        }

        // 2. SEGUNDO: Validamos el objeto ya limpio
        OviUserValidator oviUserValidator = new OviUserValidator();
        oviUserValidator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            return "oviUser/add";
        }

        // 3. TERCERO: Lógica de negocio (Contraseña y guardado en BD)
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String contrasenyaEncriptada = passwordEncryptor.encryptPassword(oviUser.getPassword());
        oviUser.setPassword(contrasenyaEncriptada);

        oviUser.setStatus("in progress");

        try {
            oviUserDao.addOviUser(oviUser);
        } catch (DuplicateKeyException e) {
            bindingResult.rejectValue("dni", "duplicat",
                    "Ya existe un usuario con este DNI");
            return "oviUser/add";
        } catch (DataIntegrityViolationException e) {
        // AÑADE ESTA LÍNEA PARA VER EL ERROR REAL EN LA CONSOLA DE SPRING BOOT
        System.out.println("ERROR REAL EN LA BD: " + e.getMessage());
        e.printStackTrace();

        bindingResult.rejectValue("tutor_id", "no_existe",
                "El tutor introducido no existe en el sistema");
        return "oviUser/add";
    }

        return "redirect:/oviUser/success";
    }

    @RequestMapping("/success")
    public String registrationSuccess() {
        return "oviUser/success";
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

    // 1. MODIFICAR: Cambiar a POST y recibir el 'rejectReason'
    @RequestMapping(value="/reject/execute/{dni}", method = RequestMethod.POST)
    public String executeRejectOviUser(@PathVariable String dni, @RequestParam("rejectReason") String rejectReason) {
        OviUser oviUser = oviUserDao.getOviUser(dni);
        if (oviUser != null) {
            oviUser.setStatus("refused");
            oviUser.setRejectReason(rejectReason); // Asignamos el motivo
            oviUserDao.updateOviUser(oviUser);
        }
        return "redirect:/oviUser/pending"; // Redirige a la lista de rechazados
    }

    // 2. AÑADIR: Endpoint para la nueva vista de rechazados
    @RequestMapping("/refused")
    public String listRefusedOviUsers(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        // Aprovechamos los métodos dinámicos que ya tienes en el Dao pasándole "refused"
        List<OviUser> requests = oviUserDao.getOviUsersByStatusPaginated("refused", pageSize, offset);
        int totalItems = oviUserDao.countOviUsersByStatus("refused");

        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("oviUsers", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "technician/oviUser/refused"; // Nombre del nuevo HTML
    }
    @RequestMapping(value="/masdetalle/{dni}", method = RequestMethod.GET)
    public String verMasDetalle(Model model, @PathVariable String dni, HttpSession session) {

        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        OviUser oviUser = oviUserDao.getOviUser(dni);

        if (oviUser == null) {
            return "redirect:/oviUser/accepted";
        }

        // Pasamos el oviUser a la vista (el HTML)
        model.addAttribute("oviUser", oviUser);

        return "oviUser/masdetalle";
    }

}
