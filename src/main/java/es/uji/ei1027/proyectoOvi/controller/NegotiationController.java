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

import java.util.*;

@Controller
@RequestMapping("/negotiation")
public class NegotiationController {
    private NegotiationDao negotiationDao;
    private OviUserDao oviUserDao;
    private PapPatiDao papPatiDao;
    private TutorDao tutorDao; // <-- AÑADIDO: Atributo para el DAO del Tutor
    private AssignmentRequestDao assignmentRequestDao;
    private ListOfProposedCandidatesDao listOfProposedCandidatesDao;

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao){
        this.oviUserDao = oviUserDao;
    }

    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao){
        this.papPatiDao = papPatiDao;
    }

    @Autowired
    public void setTutorDao(TutorDao tutorDao){ // <-- AÑADIDO: Inyección de TutorDao
        this.tutorDao = tutorDao;
    }

    @Autowired
    public void setNegotiationDao(NegotiationDao negotiationDao){
        this.negotiationDao = negotiationDao;
    }
    @Autowired
    public void setAssignmentRequestDao(AssignmentRequestDao assignmentRequestDao) {
        this.assignmentRequestDao = assignmentRequestDao;
    }

    @Autowired
    public void setListOfProposedCandidatesDao(ListOfProposedCandidatesDao listOfProposedCandidatesDao) {
        this.listOfProposedCandidatesDao = listOfProposedCandidatesDao;
    }

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

//    @RequestMapping("/user/{dni}")
//    public String listNegotiationsByUser(Model model, @PathVariable String dni, @RequestParam(defaultValue = "1") int page) {
//        int pageSize = 4;
//        int offset = (page - 1) * pageSize;
//
//        model.addAttribute("negotiations", negotiationDao.getNegotiationsByUserPaginated(dni, pageSize, offset));
//
//        int totalItems = negotiationDao.countNegotiationsByUser(dni);
//        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
//        if (totalPages == 0) totalPages = 1;
//
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", totalPages);
//        model.addAttribute("dniOwner", dni);
//        model.addAttribute("rolePath", "user");
//
//        return "negotiation/list";
//    }

//    @RequestMapping("/user/{dni}")
//    public String listNegotiationsByUser(Model model, @PathVariable String dni, @RequestParam(defaultValue = "1") int page) {
//        // 1. Comprobamos si el usuario es menor (si tiene tutor_id asignado)
//        boolean isMinor = false;
//        OviUser oviUser = oviUserDao.getOviUser(dni);
//        if (oviUser != null && oviUser.getTutor_id() != null && !oviUser.getTutor_id().isEmpty()) {
//            isMinor = true;
//        }
//
//        // 2. Pasamos la bandera a la vista
//        model.addAttribute("isMinor", isMinor);
//
//        // 3. Lógica para mayores de edad (o si por algún error no detecta al menor)
//        int pageSize = 4;
//        int offset = (page - 1) * pageSize;
//
//        if (isMinor) {
//            // Si es menor, no le enviamos negociaciones para evitar accesos indebidos
//            model.addAttribute("negotiations", new ArrayList<Negotiation>());
//            model.addAttribute("currentPage", 1);
//            model.addAttribute("totalPages", 1);
//        } else {
//            // Si es mayor, lógica normal que tú tenías
//            model.addAttribute("negotiations", negotiationDao.getNegotiationsByUserPaginated(dni, pageSize, offset));
//            int totalItems = negotiationDao.countNegotiationsByUser(dni);
//            int totalPages = (int) Math.ceil((double) totalItems / pageSize);
//            if (totalPages == 0) totalPages = 1;
//
//            model.addAttribute("currentPage", page);
//            model.addAttribute("totalPages", totalPages);
//        }
//
//        model.addAttribute("dniOwner", dni);
//        model.addAttribute("rolePath", "user");
//
//        return "negotiation/list";
//    }

//    @RequestMapping("/tutor/{dni}")
//    public String listNegotiationsByTutor(Model model, @PathVariable String dni, @RequestParam(defaultValue = "1") int page) {
//        int pageSize = 4;
//        int offset = (page - 1) * pageSize;
//
//        model.addAttribute("negotiations", negotiationDao.getNegotiationsByTutorPaginated(dni, pageSize, offset));
//
//        int totalItems = negotiationDao.countNegotiationsByTutor(dni);
//        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
//        if (totalPages == 0) totalPages = 1;
//
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", totalPages);
//        model.addAttribute("dniOwner", dni);
//        model.addAttribute("rolePath", "tutor");
//
//        return "negotiation/list";
//    }

//    @RequestMapping(value="/pappati/{dni}", method = RequestMethod.GET)
//    public String listNegotiationsByPapPati(Model model, @PathVariable String dni, HttpSession session) {
//        UserDetails user = (UserDetails) session.getAttribute("user");
//
//        // Verificamos que esté logueado y sea él mismo
//        if (user == null || !user.getDni().equals(dni)) {
//            return "redirect:/login";
//        }
//
//        // Buscamos sus chats específicos
//        List<Negotiation> chats = negotiationDao.getNegotiationsByPapPati(dni);
//        model.addAttribute("negotiations", chats);
//
//        return "negotiation/list";
//    }

    @RequestMapping("/user/{dni}")
    public String listNegotiationsByUser(Model model, @PathVariable String dni,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(value = "statusFilter", required = false) String statusFilter) {
        boolean isMinor = false;
        OviUser oviUser = oviUserDao.getOviUser(dni);
        if (oviUser != null && oviUser.getTutor_id() != null && !oviUser.getTutor_id().isEmpty()) {
            isMinor = true;
        }

        model.addAttribute("isMinor", isMinor);

        int pageSize = 4;
        int offset = (page - 1) * pageSize;

        if (isMinor) {
            model.addAttribute("negotiations", new ArrayList<Negotiation>());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
        } else {
            // Nota: Aquí pasamos statusFilter al DAO
            model.addAttribute("negotiations", negotiationDao.getNegotiationsByUserPaginated(dni, pageSize, offset, statusFilter));
            int totalItems = negotiationDao.countNegotiationsByUser(dni, statusFilter);
            int totalPages = (int) Math.ceil((double) totalItems / pageSize);
            if (totalPages == 0) totalPages = 1;

            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
        }

        model.addAttribute("dniOwner", dni);
        model.addAttribute("rolePath", "user");
        model.addAttribute("statusFilter", statusFilter); // Pasamos el filtro activo al HTML

        return "negotiation/list";
    }

    @RequestMapping("/tutor/{dni}")
    public String listNegotiationsByTutor(Model model, @PathVariable String dni,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(value = "statusFilter", required = false) String statusFilter) {
        int pageSize = 4;
        int offset = (page - 1) * pageSize;

        // Nota: Aquí pasamos statusFilter al DAO
        model.addAttribute("negotiations", negotiationDao.getNegotiationsByTutorPaginated(dni, pageSize, offset, statusFilter));

        int totalItems = negotiationDao.countNegotiationsByTutor(dni, statusFilter);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("dniOwner", dni);
        model.addAttribute("rolePath", "tutor");
        model.addAttribute("statusFilter", statusFilter);

        return "negotiation/list";
    }

    @RequestMapping(value="/pappati/{dni}", method = RequestMethod.GET)
    public String listNegotiationsByPapPati(Model model, @PathVariable String dni,
                                            @RequestParam(value = "statusFilter", required = false) String statusFilter,
                                            HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null || !user.getDni().equals(dni)) {
            return "redirect:/login";
        }

        // Nota: Aquí pasamos statusFilter al DAO
        List<Negotiation> chats = negotiationDao.getNegotiationsByPapPati(dni, statusFilter);
        model.addAttribute("negotiations", chats);

        // Es necesario pasar estos tres atributos para que funcione la URL de filtrado dinámica en el HTML
        model.addAttribute("rolePath", "pappati");
        model.addAttribute("dniOwner", dni);
        model.addAttribute("statusFilter", statusFilter);

        return "negotiation/list";
    }

//    @RequestMapping("/chat/{negotiationId}")
//    public String openChat(@PathVariable("negotiationId") String negotiationId,
//                           Model model,
//                           HttpSession session) {
//
//        // 1. Validar si el usuario está logeado por seguridad
//        UserDetails userLogeado = (UserDetails) session.getAttribute("user");
//        if (userLogeado == null) {
//            return "redirect:/login";
//        }
//
//        // 2. Obtenemos el registro base (el último mensaje) para los metadatos del chat
//        Negotiation negotiationBase = negotiationDao.getNegotiation(negotiationId);
//
//        if (negotiationBase == null) {
//            return "redirect:/assignmentRequest/list";
//        }
//
//        // 3. Recuperamos la LISTA completa de mensajes ordenados para el th:each
//        List<Negotiation> historialMensajes = negotiationDao.getMessagesByNegotiationId(negotiationId);
//
//        // 4. Bucle para buscar y rellenar el nombre real de cada emisor en base a su DNI
//        for (Negotiation msg : historialMensajes) {
//            if (msg.getEmisorDni() != null) {
//
//                // Primero intentamos buscar si el DNI pertenece a un OviUser
//                OviUser oviUser = oviUserDao.getOviUser(msg.getEmisorDni());
//                if (oviUser != null) {
//                    msg.setEmisorNombre(oviUser.getName());
//                    continue;
//                }
//
//                // Si no era un OviUser, miramos si el DNI pertenece a un Asistente (Pap_Pati)
//                Pap_Pati papPati = papPatiDao.getPap_Pati(msg.getEmisorDni());
//                if (papPati != null) {
//                    String nombreCompleto = papPati.getName() + " " + (papPati.getSurname() != null ? papPati.getSurname() : "");
//                    msg.setEmisorNombre(nombreCompleto.trim());
//                    continue;
//                }
//
//                // --- MODIFICADO Y ACTIVADO AQUÍ ---
//                // Si no era ninguno de los anteriores, miramos si el DNI pertenece a un Tutor
//                Tutor tutor = tutorDao.getTutor(msg.getEmisorDni());
//                if (tutor != null) {
//                    msg.setEmisorNombre(tutor.getName());
//                    continue;
//                }
//
//                // Por si acaso hubiera un DNI que no se encuentra en el sistema
//                if (msg.getEmisorNombre() == null) {
//                    msg.setEmisorNombre("Usuario (" + msg.getEmisorDni() + ")");
//                }
//            }
//        }
//
//        // 5. Enviamos todo al modelo para alimentar las etiquetas de Thymeleaf en el HTML
//        model.addAttribute("negotiationId", negotiationId);
//        model.addAttribute("status", negotiationBase.getStatus());
//        model.addAttribute("listId", negotiationBase.getListId());
//        model.addAttribute("messages", historialMensajes);
//
//        return "negotiation/chat";
//    }

    @RequestMapping("/chat/{negotiationId}")
    public String openChat(@PathVariable("negotiationId") String negotiationId,
                           @RequestParam(defaultValue = "false") boolean readOnly,
                           Model model,
                           HttpSession session) {

        // 1. Validar si el usuario está logeado por seguridad
        UserDetails userLogeado = (UserDetails) session.getAttribute("user");
        if (userLogeado == null) {
            return "redirect:/login";
        }

        // 2. Obtenemos el registro base (el último mensaje) para los metadatos del chat
        Negotiation negotiationBase = negotiationDao.getNegotiation(negotiationId);

        if (negotiationBase == null) {
            return "redirect:/assignmentRequest/list";
        }

        // 3. Recuperamos la LISTA completa de mensajes ordenados para el th:each
        List<Negotiation> historialMensajes = negotiationDao.getMessagesByNegotiationId(negotiationId);

        // 4. Bucle para buscar y rellenar el nombre real de cada emisor en base a su DNI
        for (Negotiation msg : historialMensajes) {
            if (msg.getEmisorDni() != null) {

                // Primero intentamos buscar si el DNI pertenece a un OviUser
                OviUser oviUser = oviUserDao.getOviUser(msg.getEmisorDni());
                if (oviUser != null) {
                    msg.setEmisorNombre(oviUser.getName());
                    continue;
                }

                // Si no era un OviUser, miramos si el DNI pertenece a un Asistente (Pap_Pati)
                Pap_Pati papPati = papPatiDao.getPap_Pati(msg.getEmisorDni());
                if (papPati != null) {
                    String nombreCompleto = papPati.getName() + " " + (papPati.getSurname() != null ? papPati.getSurname() : "");
                    msg.setEmisorNombre(nombreCompleto.trim());
                    continue;
                }

                // Si no era ninguno de los anteriores, miramos si el DNI pertenece a un Tutor
                Tutor tutor = tutorDao.getTutor(msg.getEmisorDni());
                if (tutor != null) {
                    msg.setEmisorNombre(tutor.getName());
                    continue;
                }

                // Por si acaso hubiera un DNI que no se encuentra en el sistema
                if (msg.getEmisorNombre() == null) {
                    msg.setEmisorNombre("Usuario (" + msg.getEmisorDni() + ")");
                }
            }
        }

        // 5. Enviamos todo al modelo para alimentar las etiquetas de Thymeleaf en el HTML
        model.addAttribute("negotiationId", negotiationId);
        model.addAttribute("status", negotiationBase.getStatus());
        model.addAttribute("listId", negotiationBase.getListId());
        model.addAttribute("messages", historialMensajes);

        // --- NUEVAS LÍNEAS PARA EL CONTROL DE ESTADO Y VOLVER DINÁMICO ---
        // Pasamos el estado con el nombre exacto que requiere el chat.html
        model.addAttribute("negotiationStatus", negotiationBase.getStatus());

        // Si es modo solo lectura (admin), forzamos un backUrl distinto
        if (readOnly) {
            model.addAttribute("readOnly", true);
            model.addAttribute("backUrl", "/assignmentRequest/adminNegotiations");
        } else {
            // Construimos la URL de retorno inteligente según el rol del usuario logeado
            String rolePath = userLogeado.getRole().toLowerCase();
            if ("pappati".equals(rolePath)) {
                rolePath = "pap_pati";
            }
            model.addAttribute("backUrl", "/negotiation/" + rolePath + "/" + userLogeado.getDni());
        }

        return "negotiation/chat";
    }

    @RequestMapping(value="/sendMessage", method=RequestMethod.POST)
    public String sendMessage(@RequestParam("negotiationId") String negotiationId,
                              @RequestParam("listId") String listId,
                              @RequestParam("messageText") String messageText,
                              HttpSession session) {

        // 1. Recuperar el usuario de la sesión
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Negotiation baseNegotiation = negotiationDao.getNegotiation(negotiationId);
        Negotiation newMessage = new Negotiation();

        newMessage.setNegotiation_Id(negotiationId);
        newMessage.setListId(listId);
        newMessage.setRecordOfComunications(messageText);
        newMessage.setStatus("in progress");

        // Guardamos la fecha de HOY real
        newMessage.setStartDate(new java.util.Date());

        newMessage.setEndDate(null);
        newMessage.setHora(java.time.LocalTime.now());

        // Guardamos el DNI de quien lo envía
        newMessage.setEmisorDni(user.getDni());

        negotiationDao.addNegotiation(newMessage);

        return "redirect:/negotiation/chat/" + negotiationId;
    }

    @RequestMapping(value="/cancel/{negotiationId}", method=RequestMethod.GET)
    public String showCancelConfirmation(@PathVariable("negotiationId") String negotiationId,
                                         @RequestParam("listId") String listId,
                                         Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("negotiationId", negotiationId);
        model.addAttribute("listId", listId);
        return "negotiation/cancel";
    }

//    @RequestMapping(value="/cancel/execute/{negotiationId}", method=RequestMethod.POST)
//    public String executeCancelNegotiation(@PathVariable("negotiationId") String negotiationId,
//                                           @RequestParam("listId") String listId,
//                                           HttpSession session) {
//        UserDetails user = (UserDetails) session.getAttribute("user");
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        negotiationDao.deleteNegotiation(negotiationId);
//
//        ListOfProposedCandidates proposal = listOfProposedCandidatesDao.getListOfProposedCandidates(listId);
//        if (proposal != null) {
//            String requestId = proposal.getRequest_id();
//            AssignmentRequest req = assignmentRequestDao.getAssignmentRequest(requestId);
//            if (req != null) {
//                req.setStatus("accepted");
//                assignmentRequestDao.updateAssignmentRequest(req);
//            }
//            return "redirect:/assignmentRequest/proposals/" + requestId;
//        }
//        return "redirect:/assignmentRequest/list";
//    }

//    @RequestMapping(value="/cancel/execute/{negotiationId}", method = RequestMethod.POST)
//    public String executeCancelNegotiation(@PathVariable("negotiationId") String negotiationId,
//                                           @RequestParam("listId") String listId,
//                                           HttpSession session) {
//        UserDetails user = (UserDetails) session.getAttribute("user");
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        // 1. En lugar de borrar, recuperamos la negociación existente
//        Negotiation negotiation = negotiationDao.getNegotiation(negotiationId);
//        if (negotiation != null) {
//            // 2. Le cambiamos el estado a 'refused'
//            negotiation.setStatus("refused");
//            // 3. Actualizamos en la base de datos
//            negotiationDao.updateNegotiation(negotiation);
//        }
//
//        // El resto de tu lógica se mantiene igual para liberar la solicitud de candidatos
//        ListOfProposedCandidates proposal = listOfProposedCandidatesDao.getListOfProposedCandidates(listId);
//        if (proposal != null) {
//            String requestId = proposal.getRequest_id();
//            AssignmentRequest req = assignmentRequestDao.getAssignmentRequest(requestId);
//            if (req != null) {
//                req.setStatus("accepted"); // Esto vuelve a poner la solicitud disponible
//                assignmentRequestDao.updateAssignmentRequest(req);
//            }
//            return "redirect:/assignmentRequest/proposals/" + requestId;
//        }
//        return "redirect:/assignmentRequest/list";
//    }

    @RequestMapping(value="/cancel/execute/{negotiationId}", method = RequestMethod.POST)
    public String executeCancelNegotiation(@PathVariable("negotiationId") String negotiationId,
                                           @RequestParam("listId") String listId,
                                           HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 1. Recuperamos la negociación existente y cambiamos estado a 'refused'
        Negotiation negotiation = negotiationDao.getNegotiation(negotiationId);
        if (negotiation != null) {
            negotiation.setStatus("refused");
            negotiationDao.updateNegotiation(negotiation);
        }

        // 2. ¡REDIRECCIÓN DINÁMICA A LA LISTA DE CHATS!
        // Determinamos el rolePath basándonos en el tipo de usuario en sesión
        String rolePath = user.getRole().toLowerCase();
        if ("pappati".equals(rolePath)) {
            rolePath = "pap_pati"; // Ajuste por si en BBDD es pappati pero tu ruta usa pap_pati
        }

        // Redirige a la lista filtrando por las rechazadas para ver el cambio, o quita el parámetro si prefieres la lista general
        return "redirect:/negotiation/" + rolePath + "/" + user.getDni();
    }

    @RequestMapping("/list")
    public String listChats(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Negotiation> chatsUnificados = negotiationDao.getNegotiations();

        model.addAttribute("negotiations", chatsUnificados);
        return "negotiation/list";
    }
}