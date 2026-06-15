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

//    @RequestMapping("/list")
//    public String listNegotiations(Model model){
//        model.addAttribute("negotiations", negotiationDao.getNegotiations());
//        return "negotiation/list";
//    }

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
        int pageSize = 4;
        int offset = (page - 1) * pageSize;

        model.addAttribute("negotiations", negotiationDao.getNegotiationsByUserPaginated(dni, pageSize, offset));

        int totalItems = negotiationDao.countNegotiationsByUser(dni);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("dniOwner", dni);
        model.addAttribute("rolePath", "user"); // <- AÑADIDO: Identifica que es un usuario normal

        return "negotiation/list";
    }

    @RequestMapping("/tutor/{dni}")
    public String listNegotiationsByTutor(Model model, @PathVariable String dni, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 4;
        int offset = (page - 1) * pageSize;

        model.addAttribute("negotiations", negotiationDao.getNegotiationsByTutorPaginated(dni, pageSize, offset));

        int totalItems = negotiationDao.countNegotiationsByTutor(dni);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("dniOwner", dni);
        model.addAttribute("rolePath", "tutor"); // <- AÑADIDO: Identifica que es un tutor

        return "negotiation/list"; // <- CAMBIADO: Ahora el tutor usa el HTML de la bandeja de entrada de chats
    }
    @RequestMapping(value="/pappati/{dni}", method = RequestMethod.GET)
    public String listNegotiationsByPapPati(Model model, @PathVariable String dni, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        // Verificamos que esté logueado y sea él mismo
        if (user == null || !user.getDni().equals(dni)) {
            return "redirect:/login";
        }

        // Buscamos sus chats específicos
        List<Negotiation> chats = negotiationDao.getNegotiationsByPapPati(dni);
        model.addAttribute("negotiations", chats);

        return "negotiation/list"; // Reutilizamos la vista de la lista de chats
    }

    @RequestMapping("/chat/{negotiationId}")
    public String openChat(@PathVariable("negotiationId") String negotiationId,
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
                    continue; // Si lo encuentra, pasa al siguiente mensaje del historial
                }

                // Si no era un OviUser, miramos si el DNI pertenece a un Asistente (Pap_Pati)
                Pap_Pati papPati = papPatiDao.getPap_Pati(msg.getEmisorDni());
                if (papPati != null) {
                    // Concatenamos nombre y apellido si lo prefieres, o solo el nombre
                    String nombreCompleto = papPati.getName() + " " + (papPati.getSurname() != null ? papPati.getSurname() : "");
                    msg.setEmisorNombre(nombreCompleto.trim());
                    continue;
                }

                // (Opcional) Si en tu sistema un Tutor también puede escribir en el chat,
                // descomenta estas líneas e inyecta tutorDao:
                /*
                Tutor tutor = tutorDao.getTutor(msg.getEmisorDni());
                if (tutor != null) {
                    msg.setEmisorNombre(tutor.getName());
                    continue;
                }
                */

                // Por si acaso hubiera un DNI que no se encuentra en el sistema (antiguo o erróneo),
                // dejamos un salvavidas para que no se quede en blanco ni rompa la vista
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

        return "negotiation/chat";
    }

    @RequestMapping(value="/sendMessage", method=RequestMethod.POST)
    public String sendMessage(@RequestParam("negotiationId") String negotiationId,
                              @RequestParam("listId") String listId,
                              @RequestParam("messageText") String messageText,
                              HttpSession session) { // <-- IMPORTANTE: Añadir HttpSession aquí

        // 1. Recuperar el usuario de la sesión
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // Por seguridad
        }

        Negotiation baseNegotiation = negotiationDao.getNegotiation(negotiationId);
        Negotiation newMessage = new Negotiation();

        newMessage.setNegotiation_Id(negotiationId);
        newMessage.setListId(listId);
        newMessage.setRecordOfComunications(messageText);
        newMessage.setStatus("in progress");
        newMessage.setStartDate(baseNegotiation.getStartDate());
        newMessage.setEndDate(null);
        newMessage.setHora(java.time.LocalTime.now());

        // --- NUEVO: Guardamos el DNI de quien lo envía ---
        newMessage.setEmisorDni(user.getDni());

        negotiationDao.addNegotiation(newMessage);

        return "redirect:/negotiation/chat/" + negotiationId;
    }
    // --- NUEVO MÉTODO PARA CANCELAR LA NEGOCIACIÓN ---
    // 1. PASO UNO: Mostrar la vista de confirmación (GET)
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
        return "negotiation/cancel"; // Ruta de tu nuevo HTML
    }

    // Ejecutar la cancelación definitiva (POST) - Versión limpia sin residuos en BBDD
    @RequestMapping(value="/cancel/execute/{negotiationId}", method=RequestMethod.POST)
    public String executeCancelNegotiation(@PathVariable("negotiationId") String negotiationId,
                                           @RequestParam("listId") String listId,
                                           HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        // 1. ELIMINAR físicamente todo el historial de esta negociación de la Base de Datos
        negotiationDao.deleteNegotiation(negotiationId);
        // 2. Buscar la propuesta asociada para saber qué solicitud (AssignmentRequest) reabrir
        ListOfProposedCandidates proposal = listOfProposedCandidatesDao.getListOfProposedCandidates(listId);
        if (proposal != null) {
            String requestId = proposal.getRequest_id();
            // Cambiar estado del AssignmentRequest de "completed" a "accepted" para volver a habilitarla
            AssignmentRequest req = assignmentRequestDao.getAssignmentRequest(requestId);
            if (req != null) {
                req.setStatus("accepted");
                assignmentRequestDao.updateAssignmentRequest(req);
            }
            // Redirigir de vuelta a la pantalla de propuestas (Candidatos posibles)
            return "redirect:/assignmentRequest/proposals/" + requestId;
        }
        return "redirect:/assignmentRequest/list";
    }
    @RequestMapping("/list")
    public String listChats(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 1. Ahora el DAO ya nos devuelve directamente solo 1 fila por chat (el mensaje más reciente)
        // Por tanto, no hace falta procesar la lista ni usar HashSet
        List<Negotiation> chatsUnificados = negotiationDao.getNegotiations();

        // 2. Enviamos al HTML la lista limpia
        model.addAttribute("negotiations", chatsUnificados);
        return "negotiation/list";
    }


}
