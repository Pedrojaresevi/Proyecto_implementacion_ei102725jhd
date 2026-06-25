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
        cargarListasDesplegables(model); // Cargamos las listas para el registro
        return "oviUser/add";
    }

//    @PostMapping("/add")
//    public String addOviUser(@ModelAttribute("oviUser") OviUser oviUser,
//                             BindingResult bindingResult,
//                             RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            return "oviUser/add";
//        }
//        oviUserDao.addOviUser(oviUser);
//
//        // Guardamos los datos identificativos en el "Flash" para la redirección
//        redirectAttributes.addFlashAttribute("tipoPerfil", "Usuario OVI");
//        redirectAttributes.addFlashAttribute("nombreUsuario", oviUser.getName());
//        redirectAttributes.addFlashAttribute("dniUsuario", oviUser.getDni());
//
//        return "redirect:/oviUser/success";
//    }

    @GetMapping("/success")
    public String registrationSuccess() {
        // Spring pasa automáticamente los atributos flash al modelo aquí
        return "success";
    }


//    @RequestMapping(value="/add", method= RequestMethod.POST)
//    public String processAddSubmit(@ModelAttribute("oviUser") OviUser oviUser,
//                                   BindingResult bindingResult) {
//        if (bindingResult.hasErrors())
//            return "oviUser/add";
//        oviUserDao.addOviUser(oviUser);
//        return "redirect:list";
//    }

//    @RequestMapping(value="/add", method=RequestMethod.POST)
//    public String processAddSubmit(@ModelAttribute("oviUser") OviUser oviUser,
//                                   BindingResult bindingResult, Model model) {
//
//        if (oviUser.getTutor_id() != null && oviUser.getTutor_id().trim().isEmpty()) {
//            oviUser.setTutor_id(null);
//        }
//
//        OviUserValidator oviUserValidator = new OviUserValidator();
//        oviUserValidator.validate(oviUser, bindingResult);
//
//        if (bindingResult.hasErrors()) {
//            cargarListasDesplegables(model); // Recargamos si hay error
//            return "oviUser/add";
//        }
//
//        // 3. TERCERO: Lógica de negocio (Contraseña y guardado en BD)
//        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
//        String contrasenyaEncriptada = passwordEncryptor.encryptPassword(oviUser.getPassword());
//        oviUser.setPassword(contrasenyaEncriptada);
//
//        oviUser.setStatus("in progress");
//
//        try {
//            oviUserDao.addOviUser(oviUser);
//        } catch (DuplicateKeyException e) {
//            bindingResult.rejectValue("dni", "duplicat",
//                    "Ya existe un usuario con este DNI");
//            return "oviUser/add";
//        } catch (DataIntegrityViolationException e) {
//            // AÑADE ESTA LÍNEA PARA VER EL ERROR REAL EN LA CONSOLA DE SPRING BOOT
//            System.out.println("ERROR REAL EN LA BD: " + e.getMessage());
//            e.printStackTrace();
//
//            bindingResult.rejectValue("tutor_id", "no_existe",
//                    "El tutor introducido no existe en el sistema");
//            return "oviUser/add";
//        }
//
//        return "redirect:success";
//    }

    @RequestMapping(value="/add", method=RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("oviUser") OviUser oviUser,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) { // <-- 1. AÑADIDO AQUÍ

        if (oviUser.getTutor_id() != null && oviUser.getTutor_id().trim().isEmpty()) {
            oviUser.setTutor_id(null);
        }

        OviUserValidator oviUserValidator = new OviUserValidator();
        oviUserValidator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            cargarListasDesplegables(model); // Recargamos si hay error
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
            cargarListasDesplegables(model); // Sugerencia: recargar también aquí las listas para evitar errores visuales
            return "oviUser/add";
        } catch (DataIntegrityViolationException e) {
            System.out.println("ERROR REAL EN LA BD: " + e.getMessage());
            e.printStackTrace();

            bindingResult.rejectValue("tutor_id", "no_existe",
                    "El tutor introducido no existe en el sistema");
            cargarListasDesplegables(model); // Sugerencia: recargar también aquí las listas
            return "oviUser/add";
        }

        // <-- 2. AÑADIDO AQUÍ: Guardamos los datos antes de redirigir
        redirectAttributes.addFlashAttribute("tipoPerfil", "Usuario OVI");
        redirectAttributes.addFlashAttribute("nombreUsuario", oviUser.getName());
        redirectAttributes.addFlashAttribute("dniUsuario", oviUser.getDni());

        return "redirect:success";
    }

//    @RequestMapping("/success")
//    public String registrationSuccess() {
//        return "success";
//    }
//    @RequestMapping("/success")
//    public String registrationSuccess(Model model) {
//        model.addAttribute("tipoPerfil", "Usuario OVI");
//        return "success";
//    }

//    //    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
////    public String editOviUser(Model model, @PathVariable String id) {
////        model.addAttribute("oviUser", oviUserDao.getOviUser(id));
////
////        return "oviUser/update";
////    }
    // --- MÉTODO AUXILIAR PARA CARGAR LOS DESPLEGABLES ---
    private void cargarListasDesplegables(Model model) {
        // Pon aquí EXACTAMENTE las mismas opciones que quieres en tu sistema
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

        cargarListasDesplegables(model); // Cargamos las mismas listas para edición

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
            List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
            model.addAttribute("statusList", statusList);
            cargarListasDesplegables(model); // Recargamos si hay error al editar

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
            cargarListasDesplegables(model); // Recargamos si falla la base de datos

            bindingResult.rejectValue("tutor_id", "noExiste",
                    "El ID de tutor introducido no existe en el sistema");
            return "oviUser/update";
        }

        return "redirect:/oviUser/accepted";
    }

    // 1. Muestra la pantalla de confirmación (GET)
    @RequestMapping(value="/delete/{dni}", method = RequestMethod.GET)
    public String showDeleteConfirmation(Model model, @PathVariable String dni) {
        // Le pasamos el DNI al HTML para que lo muestre en el mensaje
        model.addAttribute("dni", dni);
        return "technician/oviUser/confirmarborrado"; // Asegúrate de guardar el HTML anterior con este nombre
    }
    // 2. Ejecuta el borrado real al enviar el formulario (POST)
    @RequestMapping(value="/delete/{dni}", method = RequestMethod.POST)
    public String processDelete(@PathVariable String dni) {
        oviUserDao.deleteOviUser(dni);
        return "redirect:/oviUser/accepted"; // Vuelve a la lista tras borrar
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

//    @RequestMapping(value="/accept/execute/{dni}", method = RequestMethod.GET)
//    public String executeAcceptOviUser(@PathVariable String dni) {
//        OviUser oviUser = oviUserDao.getOviUser(dni);
//        if (oviUser != null) {
//            oviUser.setStatus("accepted");
//            oviUserDao.updateOviUser(oviUser);
//        }
//        return "redirect:/oviUser/pending";
//    }

    @RequestMapping(value="/accept/execute/{dni}", method = RequestMethod.GET)
    public String executeAcceptOviUser(@PathVariable String dni, Model model) { // <--- Usamos Model
        OviUser oviUser = oviUserDao.getOviUser(dni);
        if (oviUser != null) {
            oviUser.setStatus("accepted");
            oviUserDao.updateOviUser(oviUser);

            // Guardamos los datos directamente en el Model
            model.addAttribute("oviUser", oviUser);
            model.addAttribute("actionType", "accepted");
        }

        // CARGAMOS EL HTML DIRECTAMENTE (Sin hacer redirect)
        return "technician/oviUser/simulacion_email";
    }

    @RequestMapping(value="/reject/{dni}", method = RequestMethod.GET)
    public String confirmRejectOviUser(Model model, @PathVariable String dni) {
        model.addAttribute("oviUser", oviUserDao.getOviUser(dni));
        return "technician/oviUser/reject";
    }

//    // 1. MODIFICAR: Cambiar a POST y recibir el 'rejectReason'
//    @RequestMapping(value="/reject/execute/{dni}", method = RequestMethod.POST)
//    public String executeRejectOviUser(@PathVariable String dni, @RequestParam("rejectReason") String rejectReason) {
//        OviUser oviUser = oviUserDao.getOviUser(dni);
//        if (oviUser != null) {
//            oviUser.setStatus("refused");
//            oviUser.setRejectReason(rejectReason); // Asignamos el motivo
//            oviUserDao.updateOviUser(oviUser);
//        }
//        return "redirect:/oviUser/pending"; // Redirige a la lista de rechazados
//    }

    @RequestMapping(value="/reject/execute/{dni}", method = RequestMethod.POST)
    public String executeRejectOviUser(@PathVariable String dni,
                                       @RequestParam("rejectReason") String rejectReason,
                                       Model model) { // <--- Usamos Model
        OviUser oviUser = oviUserDao.getOviUser(dni);
        if (oviUser != null) {
            oviUser.setStatus("refused");
            oviUser.setRejectReason(rejectReason);
            oviUserDao.updateOviUser(oviUser);

            // Guardamos los datos directamente en el Model
            model.addAttribute("oviUser", oviUser);
            model.addAttribute("actionType", "refused");
            model.addAttribute("rejectReason", rejectReason);
        }

        // CARGAMOS EL HTML DIRECTAMENTE (Sin hacer redirect)
        return "technician/oviUser/simulacion_email";
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

//    @RequestMapping(value="/masdetalle/{dni}", method = RequestMethod.GET)
//    public String verMasDetalle(Model model,
//                                @PathVariable String dni,
//                                @RequestParam(value = "from", required = false) String from,
//                                @RequestParam(value = "tutorDni", required = false) String tutorDni,
//                                HttpSession session) {
//
//        UserDetails user = (UserDetails) session.getAttribute("user");
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        OviUser oviUser = oviUserDao.getOviUser(dni);
//
//        if (oviUser == null) {
//            return "redirect:/oviUser/accepted";
//        }
//
//        // LÓGICA DEL BOTÓN VOLVER DINÁMICO
//        String volverUrl = "/dashboard"; // Ruta por defecto (fallback)
//
//        if ("accepted".equals(from)) {
//            volverUrl = "/oviUser/accepted";
//        } else if ("refused".equals(from)) { // <--- AÑADIR ESTA LÍNEA
//            volverUrl = "/oviUser/refused";  // <--- AÑADIR ESTA LÍNEA
//        } else if ("list_minors".equals(from) && tutorDni != null) {
//            volverUrl = "/tutor/users/" + tutorDni;
//        }
//
//        // Pasamos los atributos al modelo
//        model.addAttribute("oviUser", oviUser);
//        model.addAttribute("volverUrl", volverUrl); // Enviamos la URL a la vista
//
//        return "oviUser/masdetalle";
//    }

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

        // LÓGICA DEL BOTÓN VOLVER DINÁMICO
        String volverUrl = "/dashboard"; // Ruta por defecto (fallback)

        if ("accepted".equals(from)) {
            volverUrl = "/oviUser/accepted";
        } else if ("refused".equals(from)) {
            volverUrl = "/oviUser/refused";
        } else if ("pending".equals(from)) { // <--- AÑADIDA ESTA NUEVA CONDICIÓN
            volverUrl = "/oviUser/pending";  // <--- AÑADIDA ESTA NUEVA CONDICIÓN
        } else if ("list_minors".equals(from) && tutorDni != null) {
            volverUrl = "/tutor/users/" + tutorDni;
        }

        // Pasamos los atributos al modelo
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("volverUrl", volverUrl);

        return "oviUser/masdetalle";
    }
}