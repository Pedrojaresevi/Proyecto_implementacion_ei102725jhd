package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.*;
import es.uji.ei1027.proyectoOvi.models.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/pap_pati")
public class Pap_PatiController {
    private PapPatiDao pap_patiDao;
    private AssignmentRequestDao assignmentRequestDao;
    private ListOfProposedCandidatesDao listOfProposedCandidatesDao;
    private OviUserDao oviUserDao;
    private NegotiationDao negotiationDao;
    private TutorDao tutorDao;

    @Autowired
    public void setPap_patiDao(PapPatiDao pap_patiDao){
        this.pap_patiDao = pap_patiDao;
    }
    @Autowired
    public void setAssignmentRequestDao(AssignmentRequestDao assignmentRequestDao){
        this.assignmentRequestDao = assignmentRequestDao;
    }
    @Autowired
    public void setListOfProposedCandidatesDao(ListOfProposedCandidatesDao ListOfProposedCandidatesDao){
        this.listOfProposedCandidatesDao = ListOfProposedCandidatesDao;
    }
    @Autowired
    public void setOviUserDao(OviUserDao OviUserDao){
        this.oviUserDao = OviUserDao;
    }
    @Autowired
    public void setNegotiationDao(NegotiationDao negotiationDao) {
        this.negotiationDao = negotiationDao;
    }
    @Autowired
    public void setTutorDao(TutorDao tutorDao) {
        this.tutorDao = tutorDao;
    }
    @RequestMapping("/list")
    public String listAllPap_Pati(Model model, @RequestParam(defaultValue = "1") int page){
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        model.addAttribute("allpap_pati", pap_patiDao.getAllPap_PatiPaginated(pageSize, offset));

        int totalItems = pap_patiDao.countAllPap_Pati();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "pap_pati/list";
    }

    @RequestMapping(value="/add")
    public String addPapPati(Model model) {
        model.addAttribute("pap_pati", new Pap_Pati());
        return "pap_pati/add";
    }

//    @PostMapping("/add")
//    public String addPapPati(@ModelAttribute("pap_pati") Pap_Pati pap_pati,
//                             BindingResult bindingResult,
//                             RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            return "pap_pati/add";
//        }
//        pap_patiDao.addPap_Pati(pap_pati);
//
//        redirectAttributes.addFlashAttribute("tipoPerfil", "Asistente " + pap_pati.getAssistant_type());
//        redirectAttributes.addFlashAttribute("nombreUsuario", pap_pati.getName() + " " + pap_pati.getSurname());
//        redirectAttributes.addFlashAttribute("dniUsuario", pap_pati.getDni());
//
//        return "redirect:/pap_pati/success";
//    }

    @GetMapping("/success")
    public String registrationSuccess() {
        return "success";
    }

//    @RequestMapping(value="/add", method= RequestMethod.POST)
//    public String processAddSubmit(@ModelAttribute("pap_pati") Pap_Pati papPati,
//                                   BindingResult bindingResult) {
//
//        papPati.setStatus("in progress");
//
//        // 1. Pasa el validador universal (campos nulos, formatos, etc.)
//        Pap_PatiValidator pap_patiValidator = new Pap_PatiValidator();
//        pap_patiValidator.validate(papPati, bindingResult);
//
//        // 2. Reglas EXCLUSIVAS para nuevos registros: Las fechas no pueden ser pasadas
//        if (papPati.getStartDate() != null && papPati.getStartDate().isBefore(LocalDate.now())) {
//            bindingResult.rejectValue("startDate", "fecha_pasada", "La fecha de inicio no puede ser anterior a hoy.");
//        }
//
//        if (papPati.getEndDate() != null && papPati.getEndDate().isBefore(LocalDate.now().plusDays(1))) {
//            bindingResult.rejectValue("endDate", "fecha_invalida", "La fecha de fin debe ser al menos el día de mañana.");
//        }
//
//        // 3. Comprobar si hay algún error (del validador o de nuestras reglas exclusivas)
//        if (bindingResult.hasErrors()) {
//            return "pap_pati/add";
//        }
//
//        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
//        String contrasenaEncriptada = passwordEncryptor.encryptPassword(papPati.getPassword());
//        papPati.setPassword(contrasenaEncriptada);
//
//        try {
//            pap_patiDao.addPap_Pati(papPati);
//        } catch (DuplicateKeyException e) {
//            bindingResult.rejectValue("dni", "duplicat", "Ya existe un Pap/Pati con este DNI");
//            return "pap_pati/add";
//        }
//        return "redirect:/";
//    }

//    @RequestMapping(value="/add", method= RequestMethod.POST)
//    public String processAddSubmit(@ModelAttribute("pap_pati") Pap_Pati papPati,
//                                   BindingResult bindingResult) {
//
//        papPati.setStatus("in progress");
//
//        // 1. Pasa el validador universal (campos nulos, formatos, etc.)
//        Pap_PatiValidator pap_patiValidator = new Pap_PatiValidator();
//        pap_patiValidator.validate(papPati, bindingResult);
//
//        // 2. Reglas EXCLUSIVAS para nuevos registros: Las fechas no pueden ser pasadas
//        if (papPati.getStartDate() != null && papPati.getStartDate().isBefore(LocalDate.now())) {
//            bindingResult.rejectValue("startDate", "fecha_pasada", "La fecha de inicio no puede ser anterior a hoy.");
//        }
//
//        if (papPati.getEndDate() != null && papPati.getEndDate().isBefore(LocalDate.now().plusDays(1))) {
//            bindingResult.rejectValue("endDate", "fecha_invalida", "La fecha de fin debe ser al menos el día de mañana.");
//        }
//
//        // 3. Comprobar si hay algún error (del validador o de nuestras reglas exclusivas)
//        if (bindingResult.hasErrors()) {
//            return "pap_pati/add";
//        }
//
//        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
//        String contrasenaEncriptada = passwordEncryptor.encryptPassword(papPati.getPassword());
//        papPati.setPassword(contrasenaEncriptada);
//
//        try {
//            pap_patiDao.addPap_Pati(papPati);
//        } catch (DuplicateKeyException e) {
//            bindingResult.rejectValue("dni", "duplicat", "Ya existe un Pap/Pati con este DNI");
//            return "pap_pati/add";
//        }
//
//        return "redirect:success";
//    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("pap_pati") Pap_Pati papPati,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) { // <-- 1. AÑADIDO AQUÍ

        papPati.setStatus("in progress");

        // 1. Pasa el validador universal (campos nulos, formatos, etc.)
        Pap_PatiValidator pap_patiValidator = new Pap_PatiValidator();
        pap_patiValidator.validate(papPati, bindingResult);

        // 2. Reglas EXCLUSIVAS para nuevos registros: Las fechas no pueden ser pasadas
        if (papPati.getStartDate() != null && papPati.getStartDate().isBefore(LocalDate.now())) {
            bindingResult.rejectValue("startDate", "fecha_pasada", "La fecha de inicio no puede ser anterior a hoy.");
        }

        if (papPati.getEndDate() != null && papPati.getEndDate().isBefore(LocalDate.now().plusDays(1))) {
            bindingResult.rejectValue("endDate", "fecha_invalida", "La fecha de fin debe ser al menos el día de mañana.");
        }

        // 3. Comprobar si hay algún error (del validador o de nuestras reglas exclusivas)
        if (bindingResult.hasErrors()) {
            return "pap_pati/add";
        }

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String contrasenaEncriptada = passwordEncryptor.encryptPassword(papPati.getPassword());
        papPati.setPassword(contrasenaEncriptada);

        try {
            pap_patiDao.addPap_Pati(papPati);
        } catch (DuplicateKeyException e) {
            bindingResult.rejectValue("dni", "duplicat", "Ya existe un Pap/Pati con este DNI");
            return "pap_pati/add";
        }

        // <-- 2. AÑADIDO AQUÍ: Guardamos los datos efímeros en el "Flash" antes de redirigir
        redirectAttributes.addFlashAttribute("tipoPerfil", "Asistente " + papPati.getAssistant_type());
        redirectAttributes.addFlashAttribute("nombreUsuario", papPati.getName() + " " + papPati.getSurname());
        redirectAttributes.addFlashAttribute("dniUsuario", papPati.getDni());

        return "redirect:success";
    }

//    @RequestMapping("/success")
//    public String registrationSuccess() {
//        return "success";
//    }

//    @RequestMapping("/success")
//    public String registrationSuccess(Model model) {
//        model.addAttribute("tipoPerfil", "Asistente PAP/PATI");
//        return "success";
//    }

    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editPapPati(Model model, @PathVariable String id) {
        model.addAttribute("pap_pati", pap_patiDao.getPap_Pati(id));
        List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
        model.addAttribute("statusList", statusList);
        return "pap_pati/update";
    }

//    @RequestMapping(value="/update", method=RequestMethod.POST)
//    public String processUpdateSubmit(@ModelAttribute("pap_pati") Pap_Pati papPati,
//                                      BindingResult bindingResult, Model model) {
//        Pap_PatiValidator pap_patiValidator = new Pap_PatiValidator();
//        pap_patiValidator.validate(papPati, bindingResult);
//
//        if (bindingResult.hasErrors()) {
//            return "pap_pati/update";
//        }
//        pap_patiDao.updatePap_Pati(papPati);
//
//        return "redirect:/pap_pati/accepted";
//    }

    @RequestMapping(value="/update", method=RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("pap_pati") Pap_Pati papPati,
                                      BindingResult bindingResult,
                                      Model model,
                                      HttpSession session) {
        // 1. Control de sesión: verificar si el usuario está autenticado
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 2. Control de seguridad: un usuario común solo puede editar su propio perfil.
        // Solo el técnico ("technician") puede editar perfiles ajenos.
        if (!"technician".equals(user.getRole()) && !user.getDni().equals(papPati.getDni())) {
            return "redirect:/dashboard"; // Redirigir si intenta saltarse la seguridad
        }

        // 3. Validación de los campos del formulario
        Pap_PatiValidator pap_patiValidator = new Pap_PatiValidator();
        pap_patiValidator.validate(papPati, bindingResult);

        if (bindingResult.hasErrors()) {
            // Si hay errores, volvemos a cargar la lista de estados para el desplegable y recargamos la vista
            List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
            model.addAttribute("statusList", statusList);
            return "pap_pati/update";
        }

        // 4. Actualización en la base de datos
        pap_patiDao.updatePap_Pati(papPati);

        // 5. Redirección dinámica según el rol de quien edita
        if ("technician".equals(user.getRole())) {
            return "redirect:/pap_pati/accepted"; // Si es el técnico, vuelve a la gestión de aprobados
        } else {
            return "redirect:/dashboard";         // Si es el propio PAP/PATI, vuelve a su panel principal
        }
    }

    // 1. Muestra la pantalla roja de confirmación (GET)
    @RequestMapping(value="/delete/{id}", method = RequestMethod.GET)
    public String showDeleteConfirmation(Model model, @PathVariable String id) {
        model.addAttribute("id", id);
        // Importante: ruta apuntando a technician/pap_pati
        return "technician/pap_pati/confirmarborrado";
    }

    // 2. Ejecuta el borrado real al confirmar en el formulario (POST)
    @RequestMapping(value="/delete/{id}", method = RequestMethod.POST)
    public String processDelete(@PathVariable String id) {
        pap_patiDao.deletePap_Pati(id);
        // Redirige a la lista de asistentes aceptados tras borrar
        return "redirect:/pap_pati/accepted";
    }
    @ModelAttribute("provincias")
    public List<String> getProvincias() {
        return Arrays.asList(
                "Albacete", "Alicante", "Castellón", "Valencia", "Madrid", "Barcelona", "Tarragona"
        );
    }

    @ModelAttribute("anyosExperiencia")
    public List<String> getRequiredExperience() {
        return Arrays.asList(
                "Sin experiencia", "1 año", "2 años", "3 años", "4 años", "5 años o más"
        );
    }

    @ModelAttribute("formacionesDisponibles")
    public List<String> getRequiredTraining() {
        return Arrays.asList(
                "Sin formación requerida",
                "Auxiliar de ayuda a domicilio",
                "Técnico en cuidados auxiliares de enfermería",
                "Grado en Enfermería",
                "Grado en Fisioterapia",
                "Grado en Trabajo Social",
                "Grado en Terapia Ocupacional",
                "Certificado en primeros auxilios",
                "Carnet de conducir"
        );
    }

    @ModelAttribute("skillsDisponibles")
    public List<String> getSkillsDisponibles() {
        return Arrays.asList(
                "Lenguaje de signos", "Primeros auxilios", "Manejo de silla de ruedas", "Conducción", "Cocina", "Acompañamiento"
        );
    }

//    @RequestMapping("/detallesasignacion/{id}")
//    public String viewDetail(Model model, @PathVariable String id, HttpSession session) {
//        UserDetails user = (UserDetails) session.getAttribute("user");
//        if (user == null) return "redirect:/login";
//
//        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(id);
//        model.addAttribute("request", request);
//
//        //Cargamos el solicitante dinámicamente (OviUser o Tutor)
//        if (request.getOviuser_id() != null && !request.getOviuser_id().trim().isEmpty()) {
//            OviUser oviUser = oviUserDao.getOviUser(request.getOviuser_id());
//            model.addAttribute("solicitante", oviUser);
//            model.addAttribute("tipoSolicitante", "oviuser");
//
//        } else if (request.getTutor_id() != null && !request.getTutor_id().trim().isEmpty()) {
//            Tutor tutor = tutorDao.getTutor(request.getTutor_id());
//            model.addAttribute("solicitante", tutor);
//            model.addAttribute("tipoSolicitante", "tutor");
//        }
//        // Lógica para cargar el chat si está completada
//        if ("completed".equals(request.getStatus()) && "pap_pati".equals(user.getRole())) {
//            listOfProposedCandidatesDao.getProposalsByPapPati(user.getDni())
//                    .stream()
//                    .filter(p -> p.getRequest_id().equals(id))
//                    .findFirst()
//                    .ifPresent(p -> {
//                        model.addAttribute("listId", p.getList_id());
//                        // Buscamos la negociación asociada
//                        Negotiation neg = negotiationDao.getNegotiationByListId(p.getList_id());
//                        if (neg != null) {
//                            model.addAttribute("negotiationId", neg.getNegotiation_Id());
//                        }
//                    });
//        }
//        return "pap_pati/detallesasignacion";
//    }

    @RequestMapping("/detallesasignacion/{id}")
    public String viewDetail(Model model,
                             @PathVariable String id,
                             @RequestParam(value = "from", required = false) String from, // <-- AÑADIDO: Recibir el parámetro
                             HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(id);
        model.addAttribute("request", request);

        // --- NUEVA LÓGICA DEL BOTÓN VOLVER DINÁMICO ---
        String volverUrl = "/dashboard"; // Ruta por defecto si no viene parámetro

        if ("pending".equals(from)) {
            volverUrl = "/assignmentRequest/pending"; // Vuelve a la tabla del Técnico
        } else if ("list".equals(from)) {
            volverUrl = "/assignmentRequest/list";    // Vuelve a la lista normal del usuario
        } else if ("history".equals(from)) {
            volverUrl = "/assignmentRequest/history"; // Por si acceden desde el historial
        }

        // Pasamos la variable al modelo para que la recoja el HTML
        model.addAttribute("volverUrl", volverUrl);
        // ----------------------------------------------

        // Cargamos el solicitante dinámicamente (OviUser o Tutor)
        if (request.getOviuser_id() != null && !request.getOviuser_id().trim().isEmpty()) {
            OviUser oviUser = oviUserDao.getOviUser(request.getOviuser_id());
            model.addAttribute("solicitante", oviUser);
            model.addAttribute("tipoSolicitante", "oviuser");

        } else if (request.getTutor_id() != null && !request.getTutor_id().trim().isEmpty()) {
            Tutor tutor = tutorDao.getTutor(request.getTutor_id());
            model.addAttribute("solicitante", tutor);
            model.addAttribute("tipoSolicitante", "tutor");
        }

        // Lógica para cargar el chat si está completada o en negociación
        if (("completed".equals(request.getStatus()) || "in negotiation".equals(request.getStatus())) && "pap_pati".equals(user.getRole())) {
            listOfProposedCandidatesDao.getProposalsByPapPati(user.getDni())
                    .stream()
                    .filter(p -> p.getRequest_id().equals(id))
                    .findFirst()
                    .ifPresent(p -> {
                        model.addAttribute("listId", p.getList_id());
                        // Buscamos la negociación asociada
                        Negotiation neg = negotiationDao.getNegotiationByListId(p.getList_id());
                        if (neg != null) {
                            model.addAttribute("negotiationId", neg.getNegotiation_Id());
                        }
                    });
        }

        return "pap_pati/detallesasignacion";
    }

    @RequestMapping(value="/manage/{dni}", method = RequestMethod.GET)
    public String managePapPati(Model model, @PathVariable String dni) {
        model.addAttribute("pap_pati", pap_patiDao.getPap_Pati(dni));
        return "technician/pap_pati/manage";
    }
    @RequestMapping("/pending")
    public String listPendingPapPati(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        model.addAttribute("allpap_pati", pap_patiDao.getPapPatiByStatusPaginated("in progress", pageSize, offset));

        int totalItems = pap_patiDao.countPapPatiByStatus("in progress");
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "technician/pap_pati/pending";
    }

    @RequestMapping("/accepted")
    public String listAcceptedPapPati(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        model.addAttribute("allpap_pati", pap_patiDao.getPapPatiByStatusPaginated("accepted", pageSize, offset));

        int totalItems = pap_patiDao.countPapPatiByStatus("accepted");
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "technician/pap_pati/accepted";
    }

    @RequestMapping(value="/accept/{dni}", method = RequestMethod.GET)
    public String confirmAcceptPapPati(Model model, @PathVariable String dni) {
        model.addAttribute("pap_pati", pap_patiDao.getPap_Pati(dni));
        return "technician/pap_pati/accept";
    }

    @RequestMapping(value="/accept/execute/{dni}", method = RequestMethod.GET)
    public String executeAcceptPapPati(@PathVariable String dni,Model model) {
        Pap_Pati pap_pati = pap_patiDao.getPap_Pati(dni);
        if (pap_pati != null) {
            pap_pati.setStatus("accepted");
            pap_patiDao.updatePap_Pati(pap_pati);

            model.addAttribute("pap_pati", pap_pati);
            model.addAttribute("actionType", "accepted");
        }
        return "technician/pap_pati/simulacion_email";
    }

    @RequestMapping(value="/reject/{dni}", method = RequestMethod.GET)
    public String confirmRejectPapPati(Model model, @PathVariable String dni) {
        model.addAttribute("pap_pati", pap_patiDao.getPap_Pati(dni));
        return "technician/pap_pati/reject";
    }


    @RequestMapping(value="/reject/execute/{dni}", method = RequestMethod.POST)
    public String executeRejectPapPati(@PathVariable String dni, @RequestParam("rejectReason") String rejectReason,Model model) {

        Pap_Pati pap_pati = pap_patiDao.getPap_Pati(dni);

        if (pap_pati != null) {
            pap_pati.setStatus("refused");
            pap_pati.setRejectReason(rejectReason);
            pap_patiDao.updatePap_Pati(pap_pati);

            model.addAttribute("pap_pati", pap_pati);
            model.addAttribute("actionType", "refused");
            model.addAttribute("rejectReason", rejectReason);
        }
        return "technician/pap_pati/simulacion_email";
    }

    @RequestMapping("/refused")
    public String listRefusedPapPatis(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        List<Pap_Pati> refusedList = pap_patiDao.getPapPatiByStatusPaginated("refused", pageSize, offset);
        int totalItems = pap_patiDao.countPapPatiByStatus("refused");

        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) {
            totalPages = 1;
        }

        model.addAttribute("papPatis", refusedList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "technician/pap_pati/refused";
    }

    @RequestMapping("/listpappati")
    public String listMyProposals(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        // Seguridad: Solo permitimos acceso si es pappati
        if (user == null || !"pap_pati".equals(user.getRole())) {
            return "redirect:/login";
        }

        // 1. Obtenemos las solicitudes vinculadas a este Asistente
        List<AssignmentRequest> myRequests = assignmentRequestDao.getRequestsByPappati(user.getDni());

        // 2. Necesitamos pasar las propuestas para tener los list_id de las negociaciones
        // Si prefieres, puedes crear un método en el DAO de ListOfProposedCandidates
        List<ListOfProposedCandidates> proposals = listOfProposedCandidatesDao.getProposalsByPapPati(user.getDni());

        model.addAttribute("assignmentRequests", myRequests);
        model.addAttribute("proposals", proposals); //

        return "pap_pati/listpappati";
    }

//    @RequestMapping(value="/masdetalle/{id}", method = RequestMethod.GET)
//    public String verMasDetalle(Model model,
//                                @PathVariable String id,
//                                @RequestParam(required = false) String requestId,
//                                @RequestParam(value = "from", required = false) String from,
//                                HttpSession session) {
//
//        UserDetails user = (UserDetails) session.getAttribute("user");
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        Pap_Pati candidato = pap_patiDao.getPap_Pati(id);
//
//        if (candidato == null) {
//            return "redirect:/pap_pati/list";
//        }
//
//        // 1. Inicializamos la variable vacía para procesarla con seguridad
//        String volverUrl = "/dashboard";
//
//        // 2. Control estricto de la ruta de retorno limpiando posibles espacios (.trim())
//        if (from != null) {
//            String origen = from.trim();
//
//            if ("proposals".equals(origen)) {
//                if (requestId != null && !requestId.isEmpty()) {
//                    volverUrl = "/assignmentRequest/proposals/" + requestId.trim();
//                }
//            } else if ("accepted".equals(origen)) {
//                // Forzamos explícitamente que si viene de 'accepted', regrese a la lista de registrados
//                volverUrl = "/pap_pati/accepted";
//            }
//        }
//
//        // Pasamos los atributos necesarios a la vista masdetalle.html
//        model.addAttribute("pap_pati", candidato);
//        model.addAttribute("requestId", requestId);
//        model.addAttribute("volverUrl", volverUrl);
//
//        return "pap_pati/masdetalle";
//    }

//    // Busca este método en tu Pap_PatiController.java y añade el bloque "else if"
//    @RequestMapping(value="/masdetalle/{id}", method = RequestMethod.GET)
//    public String verMasDetalle(Model model,
//                                @PathVariable String id,
//                                @RequestParam(required = false) String requestId,
//                                @RequestParam(value = "from", required = false) String from,
//                                HttpSession session) {
//
//        UserDetails user = (UserDetails) session.getAttribute("user");
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        Pap_Pati candidato = pap_patiDao.getPap_Pati(id);
//
//        if (candidato == null) {
//            return "redirect:/pap_pati/list";
//        }
//        String volverUrl = "/dashboard";
//
//        if (from != null) {
//            String origen = from.trim();
//
//            if ("proposals".equals(origen)) {
//                if (requestId != null && !requestId.isEmpty()) {
//                    volverUrl = "/assignmentRequest/proposals/" + requestId.trim();
//                }
//            } else if ("accepted".equals(origen)) {
//                volverUrl = "/pap_pati/accepted";
//            } else if ("refused".equals(origen)) { // <--- AÑADE ESTA CONDICIÓN
//                volverUrl = "/pap_pati/refused";
//            }
//        }
//
//        model.addAttribute("pap_pati", candidato);
//        model.addAttribute("requestId", requestId);
//        model.addAttribute("volverUrl", volverUrl);
//
//        return "pap_pati/masdetalle";
//    }
@RequestMapping(value="/masdetalle/{id}", method = RequestMethod.GET)
public String verMasDetalle(Model model,
                            @PathVariable String id,
                            @RequestParam(required = false) String requestId,
                            @RequestParam(value = "from", required = false) String from,
                            HttpSession session) {

    UserDetails user = (UserDetails) session.getAttribute("user");
    if (user == null) {
        return "redirect:/login";
    }

    Pap_Pati candidato = pap_patiDao.getPap_Pati(id);

    if (candidato == null) {
        return "redirect:/pap_pati/list";
    }

    String volverUrl = "/dashboard";

    if (from != null) {
        String origen = from.trim();

        if ("proposals".equals(origen)) {
            if (requestId != null && !requestId.isEmpty()) {
                volverUrl = "/assignmentRequest/proposals/" + requestId.trim();
            }
        } else if ("accepted".equals(origen)) {
            volverUrl = "/pap_pati/accepted";
        } else if ("refused".equals(origen)) {
            volverUrl = "/pap_pati/refused";
        } else if ("pending".equals(origen)) { // <--- AÑADIDA ESTA NUEVA CONDICIÓN
            volverUrl = "/pap_pati/pending";
        }
    }

    model.addAttribute("pap_pati", candidato);
    model.addAttribute("requestId", requestId);
    model.addAttribute("volverUrl", volverUrl);

    return "pap_pati/masdetalle";
}
}
