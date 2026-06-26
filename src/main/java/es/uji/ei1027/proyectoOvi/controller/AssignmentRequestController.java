package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.*;
import es.uji.ei1027.proyectoOvi.models.*;


import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/assignmentRequest")
public class AssignmentRequestController {
    private AssignmentRequestDao assignmentRequestDao;
    private PapPatiDao papPatiDao;
    private ListOfProposedCandidatesDao listOfProposedCandidatesDao;
    private NegotiationDao negotiationDao; // Inyectamos el DAO de Negociaciones
    private OviUserDao oviUserDao;

    @Autowired
    public void setAssignmentRequestDao(AssignmentRequestDao assignmentRequestDao){
        this.assignmentRequestDao = assignmentRequestDao;
    }
    //Setter para inyectar el DAO de asistentes
    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao){
        this.papPatiDao = papPatiDao;
    }
    //Setter para inyectar el DAO de list
    @Autowired
    public void setListOfProposedCandidatesDao(ListOfProposedCandidatesDao listOfProposedCandidatesDao){
        this.listOfProposedCandidatesDao = listOfProposedCandidatesDao;
    }
    //Setter para inyectar el DAO de Negociación
    @Autowired
    public void setNegotiationDao(NegotiationDao negotiationDao){
        this.negotiationDao = negotiationDao;
    }

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) { this.oviUserDao = oviUserDao;}

//    //List depediendo del rol
//    @RequestMapping("/list")
//    public String list(@RequestParam(defaultValue = "0") int page, Model model, HttpSession session) {
//        UserDetails user = (UserDetails) session.getAttribute("user");
//
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        List<AssignmentRequest> allRequests;
//
//        if (user.getRole().equals("technician")) {
//            allRequests = assignmentRequestDao.getAssignmentRequests();
//        } else if (user.getRole().equals("oviuser")) {
//            allRequests = assignmentRequestDao.getRequestsByOviUser(user.getDni());
//        } else if (user.getRole().equals("tutor")) {
//            allRequests = assignmentRequestDao.getRequestsByTutor(user.getDni());
//        } else {
//            allRequests = java.util.Collections.emptyList();
//        }
//
//        // --- FILTRO: EXCLUIR LAS COMPLETADAS ---
//        List<AssignmentRequest> activeRequests = allRequests.stream()
//                .filter(req -> !"completed".equals(req.getStatus()))
//                .collect(java.util.stream.Collectors.toList());
//
//        // 2. LÓGICA DE PAGINACIÓN SOBRE LAS ACTIVAS
//        int pageSize = 6;
//        int totalItems = activeRequests.size();
//        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
//        if (totalPages == 0) totalPages = 1; // Evitar que totalPages sea 0
//
//        if (page < 0) page = 0;
//        if (page >= totalPages) page = totalPages - 1;
//
//        int start = page * pageSize;
//        int end = Math.min(start + pageSize, totalItems);
//
//        List<AssignmentRequest> pagedRequests = java.util.Collections.emptyList();
//        if (start < totalItems) {
//            pagedRequests = activeRequests.subList(start, end);
//        }
//
//        model.addAttribute("assignmentRequests", pagedRequests);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", totalPages);
//
//        if (user.getRole().equals("technician")) {
//            return "redirect:/assignmentRequest/pending";
//        } else {
//            return "assignmentRequest/list";
//        }
//    }

    //List depediendo del rol
//    @RequestMapping("/list")
//    public String list(@RequestParam(defaultValue = "0") int page,
//                       @RequestParam(value = "statusFilter", required = false) String statusFilter, // NUEVO PARÁMETRO
//                       Model model, HttpSession session) {
//        UserDetails user = (UserDetails) session.getAttribute("user");
//
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        List<AssignmentRequest> allRequests;
//
//        if (user.getRole().equals("technician")) {
//            allRequests = assignmentRequestDao.getAssignmentRequests();
//        } else if (user.getRole().equals("oviuser")) {
//            allRequests = assignmentRequestDao.getRequestsByOviUser(user.getDni());
//        } else if (user.getRole().equals("tutor")) {
//            allRequests = assignmentRequestDao.getRequestsByTutor(user.getDni());
//        } else {
//            allRequests = java.util.Collections.emptyList();
//        }
//
//        // --- FILTRO BASE: EXCLUIR LAS COMPLETADAS ---
//        List<AssignmentRequest> activeRequests = allRequests.stream()
//                .filter(req -> !"completed".equals(req.getStatus()))
//                .collect(java.util.stream.Collectors.toList());
//
//        // --- NUEVO: FILTRO POR ESTADO SELECCIONADO EN EL HTML ---
//        if (statusFilter != null && !statusFilter.isEmpty()) {
//            activeRequests = activeRequests.stream()
//                    .filter(req -> statusFilter.equals(req.getStatus()))
//                    .collect(java.util.stream.Collectors.toList());
//        }
//
//        // 2. LÓGICA DE PAGINACIÓN SOBRE LAS ACTIVAS/FILTRADAS
//        int pageSize = 6;
//        int totalItems = activeRequests.size();
//        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
//        if (totalPages == 0) totalPages = 1; // Evitar que totalPages sea 0
//
//        if (page < 0) page = 0;
//        if (page >= totalPages) page = totalPages - 1;
//
//        int start = page * pageSize;
//        int end = Math.min(start + pageSize, totalItems);
//
//        List<AssignmentRequest> pagedRequests = java.util.Collections.emptyList();
//        if (start < totalItems) {
//            pagedRequests = activeRequests.subList(start, end);
//        }
//
//        model.addAttribute("assignmentRequests", pagedRequests);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", totalPages);
//        model.addAttribute("statusFilter", statusFilter); // NUEVO: Para recordar la selección en el HTML y la paginación
//
//        if (user.getRole().equals("technician")) {
//            return "redirect:/assignmentRequest/pending";
//        } else {
//            return "assignmentRequest/list";
//        }
//    }

    @RequestMapping("/list")
    public String listRequests(Model model,
                               @RequestParam(value = "statusFilter", required = false) String statusFilter,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        boolean isMinor = false;
        // Si es un usuario final, comprobamos si realmente tiene un tutor asociado en la BD
        if ("user".equals(user.getRole()) || "oviuser".equals(user.getRole())) {
            OviUser oviUser = oviUserDao.getOviUser(user.getDni());
            if (oviUser != null && oviUser.getTutor_id() != null && !oviUser.getTutor_id().isEmpty()) {
                isMinor = true; // Solo es menor si tiene tutor_id asignado
            }
        }
        model.addAttribute("isMinor", isMinor);
        model.addAttribute("statusFilter", statusFilter);

        int pageSize = 6;
        List<AssignmentRequest> requestsList = new ArrayList<>();
        int totalItems = 0;

        if (statusFilter != null && !statusFilter.isEmpty()) {
            // Si hay un filtro explícito (in progress, accepted, refused) se busca directamente en la BD
            int offset = page * pageSize;
            requestsList = assignmentRequestDao.getAssignmentRequestsByUserAndStatusPaginated(user.getDni(), statusFilter, pageSize, offset);
            totalItems = assignmentRequestDao.countAssignmentRequestsByUserAndStatus(user.getDni(), statusFilter);
        } else {
            // "Todos los activos": Debemos traer TODAS las del usuario y filtrar manualmente para EXCLUIR 'completed'
            // Esto garantiza que 'refused', 'in progress' y 'accepted' salgan juntas, tanto para el tutor como para el menor.
            List<AssignmentRequest> allUserRequests = assignmentRequestDao.getAssignmentRequestsByUserPaginated(user.getDni(), Integer.MAX_VALUE, 0);
            List<AssignmentRequest> activeRequests = new ArrayList<>();

            for (AssignmentRequest req : allUserRequests) {
                if (!req.getStatus().equalsIgnoreCase("completed")) {
                    activeRequests.add(req);
                }
            }

            // Paginación manual segura sobre la lista activa filtrada
            totalItems = activeRequests.size();
            int fromIndex = page * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, totalItems);
            if (fromIndex < totalItems) {
                requestsList = activeRequests.subList(fromIndex, toIndex);
            }
        }

        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("assignmentRequests", requestsList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "assignmentRequest/list";
    }

    @RequestMapping("/history")
    public String historyRequests(Model model,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        boolean isMinor = "user".equals(user.getRole()) || "oviuser".equals(user.getRole());
        model.addAttribute("isMinor", isMinor);

        int pageSize = 6;
        int offset = page * pageSize;

        // El historial usa tu método existente filtrando estrictamente por "completed"
        List<AssignmentRequest> completedRequests = assignmentRequestDao.getAssignmentRequestsByUserAndStatusPaginated(user.getDni(), "completed", pageSize, offset);
        int totalItems = assignmentRequestDao.countAssignmentRequestsByUserAndStatus(user.getDni(), "completed");

        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("assignmentRequests", completedRequests);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "assignmentRequest/history";
    }

    @RequestMapping(value="/add")
    public String addAssignmentRequest(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        // ¡ESTA ES LA LÍNEA QUE FALTABA! Le pasamos el usuario a la vista
        model.addAttribute("user", user);

        model.addAttribute("assignmentRequest", new AssignmentRequest());

        // Ambos roles van al mismo formulario
        return "assignmentRequest/add";
    }

//    @RequestMapping(value="/add", method= RequestMethod.POST)
//    public String processAddSubmit(@ModelAttribute("assignmentRequest") AssignmentRequest assignmentRequest,
//                                   BindingResult bindingResult, HttpSession session, Model model) {
//
//        UserDetails user = (UserDetails) session.getAttribute("user");
//
//        // 1. COMPROBAR SESIÓN (Si se ha caducado, mandarlo al login en lugar de romper)
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        if ("oviuser".equals(user.getRole())) {
//            assignmentRequest.setOviuser_id(user.getDni());
//            // Aseguramos que tutor_id esté nulo por la regla CHECK de la BD
//            assignmentRequest.setTutor_id(null);
//        } else if ("tutor".equals(user.getRole())) {
//            assignmentRequest.setTutor_id(user.getDni());
//            // Aseguramos que oviuser_id esté nulo
//            assignmentRequest.setOviuser_id(null);
//        }
//
//        // 2. GENERACIÓN DEL ID SECUENCIAL
//        String ultimoId = assignmentRequestDao.getLastRequestId();
//        String nuevoId;
//
//        if (ultimoId == null || ultimoId.isEmpty()) {
//            nuevoId = "REQ1";
//        } else {
//            String numeroStr = ultimoId.substring(3);
//            int numero = Integer.parseInt(numeroStr);
//            numero++;
//            nuevoId = "REQ" + numero;
//        }
//
//        // 3. ASIGNAR VALORES AUTOMÁTICOS
//        assignmentRequest.setRequest_Id(nuevoId);
//        assignmentRequest.setStatus("in progress");
//        assignmentRequest.setRequestDate(LocalDate.now());
//
//        // 4. VALIDAR Y GUARDAR
//        AssignmentRequestValidator assignmentRequestValidator = new AssignmentRequestValidator();
//        assignmentRequestValidator.validate(assignmentRequest, bindingResult);
//
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("user", user);
//            return "assignmentRequest/add";
//        }
//
//        try {
//            assignmentRequestDao.addAssignmentRequest(assignmentRequest);
//        } catch (DuplicateKeyException e) {
//            model.addAttribute("user", user);
//            bindingResult.rejectValue("request_Id", "duplicat", "Ya existe una solicitud con este ID");
//            return "assignmentRequest/add";
//        } catch (DataIntegrityViolationException e) {
//            model.addAttribute("user", user);
//            bindingResult.rejectValue("oviuser_id", "no_existe", "El oviUser no es vàlid");
//            return "assignmentRequest/add";
//        }
//
//        return "redirect:list";
//    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("assignmentRequest") AssignmentRequest assignmentRequest,
                                   BindingResult bindingResult, HttpSession session, Model model,
                                   RedirectAttributes redirectAttributes) {

        UserDetails user = (UserDetails) session.getAttribute("user");

        // 1. COMPROBAR SESIÓN (Si se ha caducado, mandarlo al login en lugar de romper)
        if (user == null) {
            return "redirect:/login";
        }

        if ("oviuser".equals(user.getRole())) {
            assignmentRequest.setOviuser_id(user.getDni());
            // Aseguramos que tutor_id esté nulo por la regla CHECK de la BD
            assignmentRequest.setTutor_id(null);
        } else if ("tutor".equals(user.getRole())) {
            assignmentRequest.setTutor_id(user.getDni());
            // Aseguramos que oviuser_id esté nulo
            assignmentRequest.setOviuser_id(null);
        }

        // 2. GENERACIÓN DEL ID SECUENCIAL
        String ultimoId = assignmentRequestDao.getLastRequestId();
        String nuevoId;

        if (ultimoId == null || ultimoId.isEmpty()) {
            nuevoId = "REQ1";
        } else {
            String numeroStr = ultimoId.substring(3);
            int numero = Integer.parseInt(numeroStr);
            numero++;
            nuevoId = "REQ" + numero;
        }

        // 3. ASIGNAR VALORES AUTOMÁTICOS
        assignmentRequest.setRequest_Id(nuevoId);
        assignmentRequest.setStatus("in progress");
        assignmentRequest.setRequestDate(LocalDate.now());

        // 4. VALIDAR Y GUARDAR
        AssignmentRequestValidator assignmentRequestValidator = new AssignmentRequestValidator();
        assignmentRequestValidator.validate(assignmentRequest, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "assignmentRequest/add";
        }

        try {
            assignmentRequestDao.addAssignmentRequest(assignmentRequest);
            redirectAttributes.addFlashAttribute("registeredRequest", assignmentRequest);
        } catch (DuplicateKeyException e) {
            model.addAttribute("user", user);
            bindingResult.rejectValue("request_Id", "duplicat", "Ya existe una solicitud con este ID");
            return "assignmentRequest/add";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("user", user);
            bindingResult.rejectValue("oviuser_id", "no_existe", "El oviUser no es vàlid");
            return "assignmentRequest/add";
        }

        return "redirect:/assignmentRequest/success";
    }

    @RequestMapping("/success")
    public String requestSuccess() {
        return "assignmentRequest/success";
    }

    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editAssignmentRequest(Model model, @PathVariable String id) {
        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(id);
        model.addAttribute("assignmentRequest", request);

        // Novedad: Convertimos el String de habilidades ("Cocina,Conducción") en una Lista para el HTML
        List<String> habilidadesSeleccionadas = new ArrayList<>();
        if (request.getRequiredSkills() != null && !request.getRequiredSkills().trim().isEmpty()) {
            // Dividimos por coma y eliminamos los espacios en blanco alrededor de cada palabra
            habilidadesSeleccionadas = Arrays.stream(request.getRequiredSkills().split(","))
                    .map(String::trim)
                    .collect(java.util.stream.Collectors.toList());
        }
        model.addAttribute("habilidadesSeleccionadas", habilidadesSeleccionadas);

        return "assignmentRequest/update";
    }

//    @RequestMapping(value="/update", method=RequestMethod.POST)
//    public String processUpdateSubmit(@ModelAttribute("assignmentRequest") AssignmentRequest assignmentRequest,
//                                      BindingResult bindingResult, Model model) {
//        // Se recupera la fecha original de la base de datos usando el ID
//        AssignmentRequest original = assignmentRequestDao.getAssignmentRequest(assignmentRequest.getRequest_Id());
//        assignmentRequest.setRequestDate(original.getRequestDate());
//        //Validar después de poner la fecha para que el validador no se queje
//        AssignmentRequestValidator assignmentRequestValidator = new AssignmentRequestValidator();
//        assignmentRequestValidator.validate(assignmentRequest, bindingResult);
//
//        if (bindingResult.hasErrors()) {
//            return "assignmentRequest/update";
//        }
//
//        assignmentRequestDao.updateAssignmentRequest(assignmentRequest);
//
//        return "redirect:list";
//    }

    @RequestMapping(value="/update", method=RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("assignmentRequest") AssignmentRequest assignmentRequest,
                                      BindingResult bindingResult, Model model) {

        // 1. CORRECCIÓN: Convertir cadenas vacías en null para la Base de Datos
        if (assignmentRequest.getOviuser_id() != null && assignmentRequest.getOviuser_id().trim().isEmpty()) {
            assignmentRequest.setOviuser_id(null);
        }
        if (assignmentRequest.getTutor_id() != null && assignmentRequest.getTutor_id().trim().isEmpty()) {
            assignmentRequest.setTutor_id(null);
        }

        // Se recupera la fecha original de la base de datos usando el ID
        AssignmentRequest original = assignmentRequestDao.getAssignmentRequest(assignmentRequest.getRequest_Id());
        assignmentRequest.setRequestDate(original.getRequestDate());

        // Validar después de normalizar los IDs para que el validador funcione sobre seguro
        AssignmentRequestValidator assignmentRequestValidator = new AssignmentRequestValidator();
        assignmentRequestValidator.validate(assignmentRequest, bindingResult);

        if (bindingResult.hasErrors()) {
            return "assignmentRequest/update";
        }

        assignmentRequestDao.updateAssignmentRequest(assignmentRequest);

        return "redirect:list";
    }

    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable String id) {
        listOfProposedCandidatesDao.deleteCandidatesByRequestId(id);
        assignmentRequestDao.deleteAssignmentRequest(id);
        return "redirect:../list";
    }

//    @RequestMapping("/proposals/{id}")
//    public String listProposals(Model model, @PathVariable String id) {
//        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(id);
//        model.addAttribute("assignmentRequest", request);
//
//        // Ahora cargamos desde ListOfProposedCandidates, no desde PapPati directamente
//        List<ListOfProposedCandidates> proposals = listOfProposedCandidatesDao.getProposalsByRequestId(id);
//        model.addAttribute("proposals", proposals);
//
//        return "assignmentRequest/proposals";
//    }

    // Metodo auxiliar para convertir experiencia a número
    private int experienciaANumero(String exp) {
        if (exp == null) return 0;
        switch (exp.trim()) {
            case "Sin experiencia": return 0;
            case "1 año":           return 1;
            case "2 años":          return 2;
            case "3 años":          return 3;
            case "4 años":          return 4;
            case "5 años o más":    return 5;
            default:                return 0;
        }
    }

    @RequestMapping(value="/accept/execute/{id}")
    public String acceptAndMatch(@PathVariable String id) {
        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(id);

        if (request != null && !"accepted".equals(request.getStatus())) {
            request.setStatus("accepted");
            assignmentRequestDao.updateAssignmentRequest(request);

            // Primero borramos propuestas anteriores si las hubiera
            listOfProposedCandidatesDao.deleteCandidatesByRequestId(id);

            List<Pap_Pati> compatibles = papPatiDao.getProposalsForRequest(id);
            int counter = 1;

            for (Pap_Pati pap : compatibles) {
                // Calcular score: 5 criterios opcionales = 20% cada uno
                float score = 0f;

                // 1. Ubicación / movilidad
                if (request.getServiceLocation() != null) {
                    boolean ciudadOk = pap.getAddress() != null &&
                            pap.getAddress().toLowerCase()
                                    .contains(request.getServiceLocation().toLowerCase());
                    boolean movilidadOk = pap.getGeographicMobility() != null &&
                            pap.getGeographicMobility().toLowerCase()
                                    .contains(request.getServiceLocation().toLowerCase());
                    if (ciudadOk || movilidadOk) score += 20f;
                }

                // 2. Habilidades
                if (pap.getSkills() != null && request.getRequiredSkills() != null &&
                        pap.getSkills().toLowerCase()
                                .contains(request.getRequiredSkills().toLowerCase())) {
                    score += 20f;
                }

                // 3. Formación específica
                if (pap.getSpecificTraining() != null && request.getRequiredTraining() != null &&
                        pap.getSpecificTraining().toLowerCase()
                                .contains(request.getRequiredTraining().toLowerCase())) {
                    score += 20f;
                }

                // 4. Experiencia: el candidato cumple si tiene >= experiencia requerida
                int expCandidato = experienciaANumero(pap.getTypeOfExperience());
                int expRequerida = experienciaANumero(request.getRequiredExperience());
                if (expCandidato >= expRequerida) {
                    score += 20f;
                }

                // 5. Disponibilidad (ya filtrada en SQL, pero sumamos puntos)
                score += 20f;

                try {
                    ListOfProposedCandidates proposal = new ListOfProposedCandidates();
                    proposal.setList_id("L-" + id + "-" + counter);
                    proposal.setRequest_id(id);
                    proposal.setPappati_id(pap.getDni());
                    proposal.setProposalDate(new java.util.Date());
                    proposal.setSuitabilityScore(score);

                    listOfProposedCandidatesDao.addListOfProposedCandidates(proposal);
                    counter++;
                } catch (DuplicateKeyException e) {
                    counter++;
                    continue;
                }
            }
        }
        return "redirect:/assignmentRequest/list";
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
    private void generateAndSaveCandidates(AssignmentRequest req) {
        // Primero borramos propuestas anteriores si las hubiera
        listOfProposedCandidatesDao.deleteCandidatesByRequestId(req.getRequest_Id());

        List<Pap_Pati> candidates = papPatiDao.getProposalsForRequest(req.getRequest_Id());

        int counter = 1;
        for (Pap_Pati candidate : candidates) {
            float score = calculateScore(candidate, req);

            ListOfProposedCandidates proposal = new ListOfProposedCandidates();
            proposal.setList_id("L-" + req.getRequest_Id() + "-" + counter++);
            proposal.setPappati_id(candidate.getDni());
            proposal.setRequest_id(req.getRequest_Id());
            proposal.setProposalDate(new java.util.Date());
            proposal.setSuitabilityScore(score);

            listOfProposedCandidatesDao.addListOfProposedCandidates(proposal);
        }
    }

    private float calculateScore(Pap_Pati candidate, AssignmentRequest req) {
        float score = 0;

        // Disponibilidad (condición base, 20 puntos)
        if (candidate.getStartDate() != null && candidate.getEndDate() != null
                && req.getRequiredStartAvailability() != null && req.getRequiredEndAvailability() != null
                && !candidate.getStartDate().isAfter(req.getRequiredStartAvailability())
                && !candidate.getEndDate().isBefore(req.getRequiredEndAvailability())) {
            score += 20;
        }

        // Proximidad geográfica (20 puntos)
        if (candidate.getAddress() != null && req.getServiceLocation() != null
                && (candidate.getAddress().toLowerCase().contains(req.getServiceLocation().toLowerCase())
                || (candidate.getGeographicMobility() != null
                && candidate.getGeographicMobility().toLowerCase()
                .contains(req.getServiceLocation().toLowerCase())))) {
            score += 20;
        }

        // Formación específica (20 puntos)
        if (candidate.getSpecificTraining() != null && req.getRequiredTraining() != null
                && candidate.getSpecificTraining().toLowerCase()
                .contains(req.getRequiredTraining().toLowerCase())) {
            score += 20;
        }

        // Tipo de experiencia (20 puntos)
        if (candidate.getTypeOfExperience() != null && req.getRequiredExperience() != null
                && candidate.getTypeOfExperience().equals(req.getRequiredExperience())) {
            score += 20;
        }

        // Habilidades (20 puntos)
        if (candidate.getSkills() != null && req.getRequiredSkills() != null
                && candidate.getSkills().toLowerCase()
                .contains(req.getRequiredSkills().toLowerCase())) {
            score += 20;
        }

        return score; // Máximo 100, mínimo 20 (por el filtro OR del DAO)
    }

    // --- ACEPTAR ---
    @RequestMapping(value="/accept/{id}", method = RequestMethod.GET)
    public String confirmAccept(Model model, @PathVariable String id) {
        model.addAttribute("assignmentRequest", assignmentRequestDao.getAssignmentRequest(id));
        return "technician/assignmentRequest/accept";
    }

//    @RequestMapping(value="/accept/execute/{id}", method = RequestMethod.GET)
//    public String executeAccept(@PathVariable String id) {
//        AssignmentRequest req = assignmentRequestDao.getAssignmentRequest(id);
//        if (req != null && "in progress".equals(req.getStatus())) {
//            req.setStatus("accepted");
//            assignmentRequestDao.updateAssignmentRequest(req);
//            generateAndSaveCandidates(req); // ← genera la lista
//        }
//        return "redirect:/assignmentRequest/list";
//    }

    @RequestMapping(value="/accept/execute/{id}", method = RequestMethod.GET)
    public String executeAcceptAssignmentRequest(@PathVariable String id, Model model) {
        AssignmentRequest assignmentRequest = assignmentRequestDao.getAssignmentRequest(id);

        if (assignmentRequest != null) {
            // 1. Cambiamos el estado a aceptado y actualizamos en la BD
            assignmentRequest.setStatus("accepted");
            assignmentRequestDao.updateAssignmentRequest(assignmentRequest);

            generateAndSaveCandidates(assignmentRequest);

            // 2. Buscamos el nombre del OviUser para que el email quede más realista
            if (assignmentRequest.getOviuser_id() != null) {
                OviUser oviUser = oviUserDao.getOviUser(assignmentRequest.getOviuser_id());
                if (oviUser != null) {
                    model.addAttribute("oviUserName", oviUser.getName());
                }
            }

            // 3. Cargamos los datos necesarios para el HTML de simulación
            model.addAttribute("assignmentRequest", assignmentRequest);
            model.addAttribute("actionType", "accepted");
        }

        // Devolvemos directamente el nuevo HTML de simulación sin hacer redirect
        return "technician/assignmentRequest/simulacion_email";
    }

    // --- RECHAZAR ---
    @RequestMapping(value="/reject/{id}", method = RequestMethod.GET)
    public String confirmReject(Model model, @PathVariable String id) {
        model.addAttribute("assignmentRequest", assignmentRequestDao.getAssignmentRequest(id));
        return "technician/assignmentRequest/reject";
    }

//    @RequestMapping(value="/reject/execute/{id}", method = RequestMethod.POST)
//    public String executeReject(@PathVariable String id,
//                                @RequestParam("rejectReason") String rejectReason) {
//        AssignmentRequest req = assignmentRequestDao.getAssignmentRequest(id);
//        if (req != null && "in progress".equals(req.getStatus())) {
//            req.setStatus("refused");
//            req.setRejectReason(rejectReason); // ← se guarda en BD
//            assignmentRequestDao.updateAssignmentRequest(req);
//        }
//        return "redirect:/assignmentRequest/list";
//    }

    @RequestMapping(value="/reject/execute/{id}", method = RequestMethod.POST)
    public String executeRejectAssignmentRequest(@PathVariable String id,
                                                 @RequestParam("rejectReason") String rejectReason,
                                                 Model model) {
        AssignmentRequest assignmentRequest = assignmentRequestDao.getAssignmentRequest(id);

        if (assignmentRequest != null) {
            // 1. Cambiamos el estado a rechazado (refused) y guardamos en la BD
            assignmentRequest.setStatus("refused");
            // Nota: Si tu tabla de solicitudes guarda el motivo de rechazo descomenta la línea de abajo,
            // si no la guarda no pasa nada, se enviará igualmente al email simulado a través del Model.
            // assignmentRequest.setRejectReason(rejectReason);
            assignmentRequestDao.updateAssignmentRequest(assignmentRequest);

            // 2. Buscamos el nombre del OviUser para el email
            if (assignmentRequest.getOviuser_id() != null) {
                OviUser oviUser = oviUserDao.getOviUser(assignmentRequest.getOviuser_id());
                if (oviUser != null) {
                    model.addAttribute("oviUserName", oviUser.getName());
                }
            }

            // 3. Cargamos los datos en el Model
            model.addAttribute("assignmentRequest", assignmentRequest);
            model.addAttribute("actionType", "refused");
            model.addAttribute("rejectReason", rejectReason);
        }

        // Devolvemos directamente el nuevo HTML de simulación sin hacer redirect
        return "technician/assignmentRequest/simulacion_email";
    }

    // --- VER CANDIDATOS PROPUESTOS ---
//    @RequestMapping(value="/proposals/{id}", method = RequestMethod.GET)
//    public String viewProposals(Model model, @PathVariable String id, @RequestParam(defaultValue = "1") int page) {
//        // 1. Datos de la solicitud original
//        model.addAttribute("assignmentRequest", assignmentRequestDao.getAssignmentRequest(id));
//
//        // 2. Lógica de paginación
//        int pageSize = 6;
//        int offset = (page - 1) * pageSize;
//
//        // 3. Obtenemos solo los candidatos de la página actual
//        List<ListOfProposedCandidates> proposals = listOfProposedCandidatesDao.getProposalsByRequestIdPaginated(id, pageSize, offset);
//
//        // 4. Calculamos total de páginas
//        int totalItems = listOfProposedCandidatesDao.countProposalsByRequestId(id);
//        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
//        if (totalPages == 0) totalPages = 1;
//
//        // 5. Mandamos todo a la vista
//        model.addAttribute("proposals", proposals);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", totalPages);
//
//        return "assignmentRequest/proposals";
//    }

    @RequestMapping("/proposals/{id}")
    public String showProposals(@PathVariable("id") String requestId, Model model, HttpSession session,
                                @RequestParam(defaultValue = "1") int page) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // Comprobamos si el usuario actual es menor usando getTutor_id()
        boolean isMinor = false;
        OviUser oviUser = oviUserDao.getOviUser(user.getDni());
        if (oviUser != null && oviUser.getTutor_id() != null && !oviUser.getTutor_id().isEmpty()) {
            isMinor = true;
        }

        // Obtenemos la solicitud de asignación concreta
        AssignmentRequest assignmentRequest = assignmentRequestDao.getAssignmentRequest(requestId);

        // Configuración de la paginación (Como en proposals.html empieza en 1: offset = (page - 1) * pageSize)
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        // Buscamos las propuestas de candidatos asociadas a la solicitud
        List<ListOfProposedCandidates> proposals = listOfProposedCandidatesDao.getProposalsByRequestIdPaginated(requestId, pageSize, offset);
        int totalItems = listOfProposedCandidatesDao.countProposalsByRequestId(requestId);

        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        // Determinar qué propuestas ya tienen una negociación iniciada
        Set<String> listIdsWithNegotiation = new HashSet<>();
        for (ListOfProposedCandidates proposal : proposals) {
            Negotiation negotiation = negotiationDao.getNegotiationByListId(proposal.getList_id());
            if (negotiation != null && "in progress".equals(negotiation.getStatus())) {
                listIdsWithNegotiation.add(proposal.getList_id());
            }
        }

        // Enviamos todos los datos requeridos por proposals.html
        model.addAttribute("assignmentRequest", assignmentRequest);
        model.addAttribute("proposals", proposals);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("isMinor", isMinor); // <-- Pasamos la bandera a proposals.html
        model.addAttribute("listIdsWithNegotiation", listIdsWithNegotiation);

        return "assignmentRequest/proposals";
    }
    //
    @RequestMapping("/user/{dni}")
    public String listRequestsByUser(Model model, @PathVariable String dni) {
        // Necesitas tener un método en assignmentRequestDao que filtre por DNI
        model.addAttribute("assignmentRequests", assignmentRequestDao.getRequestsByOviUser(dni));
        return "assignmentRequest/list"; // Deberás crear este HTML
    }
    //
    @RequestMapping("/tutor/{dni}")
    public String listRequestsByTutor(Model model, @PathVariable String dni) {
        model.addAttribute("assignmentRequests", assignmentRequestDao.getRequestsByTutor(dni));
        return "assignmentRequest/list";
    }

    @RequestMapping("/pending")
    public String listPendingAssignmentRequests(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 6; // Tamaño de página
        int offset = (page - 1) * pageSize;

        // 1. Obtenemos solo los registros que corresponden a la página actual
        List<AssignmentRequest> requests = assignmentRequestDao.getAssignmentRequestsByStatusPaginated("in progress", pageSize, offset);

        // 2. Calculamos el total de páginas necesarias
        int totalItems = assignmentRequestDao.countAssignmentRequestsByStatus("in progress");
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        // Evitamos problemas de división o conteos vacíos asegurando un mínimo de 1 página
        if (totalPages == 0) {
            totalPages = 1;
        }

        // 3. Pasamos todos los atributos a Thymeleaf
        model.addAttribute("assignmentRequests", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "technician/assignmentRequest/pending";
    }

    // --- NUEVA LÓGICA DE NEGOCIACIÓN ---
//    @RequestMapping(value="/startNegotiation", method = RequestMethod.POST)
//    public String startNegotiation(@RequestParam("list_id") String listId,
//                                   @RequestParam("requestId") String requestId,
//                                   @RequestParam("candidateId") String candidateId) {
//
//        Negotiation negotiation = new Negotiation();
//
//        // 1. Crear la negociación inicial
//        String negotiationId = "NEG-" + listId;
//        negotiation.setNegotiation_Id(negotiationId);
//        negotiation.setListId(listId);
//        negotiation.setStatus("In progress");
//        negotiation.setStartDate(new java.util.Date());
//        negotiation.setHora(java.time.LocalTime.now());
//        negotiation.setRecordOfComunications("Inicio de negociaciones con el candidato " + candidateId);
//
//        negotiationDao.addNegotiation(negotiation);
//
//        // 2. NUEVO: Pasar la solicitud a estado 'completed'
//        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(requestId);
//        if (request != null) {
//            request.setStatus("completed");
//            assignmentRequestDao.updateAssignmentRequest(request);
//        }
//
//        // 3. NUEVO: Redirigir a la vista de la conversación (al NegotiationController)
//        return "redirect:/negotiation/chat/" + negotiationId;
//    }
// --- NUEVA LÓGICA DE NEGOCIACIÓN CON CONFIRMACIÓN INTERMEDIA ---

    // Paso 1: Muestra la pantalla intermedia 'confirmarcandidato.html'
    @RequestMapping(value="/confirmCandidate", method = RequestMethod.POST)
    public String confirmCandidate(@RequestParam("list_id") String listId,
                                   @RequestParam("requestId") String requestId,
                                   @RequestParam("candidateId") String candidateId,
                                   Model model) {
        // Buscamos los datos del Pap_Pati para poder pintar su nombre y DNI en la confirmación
        Pap_Pati papPati = papPatiDao.getPap_Pati(candidateId);

        model.addAttribute("pappati", papPati);
        model.addAttribute("requestId", requestId);
        model.addAttribute("listId", listId); // Lo pasamos para no perderlo de vista

        // Dependiendo de cómo tengas estructuradas tus carpetas de templates,
        // si está en la raíz de templates usa "confirmarcandidato", si está dentro de la carpeta usa "assignmentRequest/confirmarcandidato"
        return "assignmentRequest/confirmarcandidato";
    }

    // Paso 2: Ejecuta la acción real tras pulsar "Confirmar"
    @RequestMapping(value="/finalizeAssignment", method = RequestMethod.POST)
    public String finalizeAssignment(@RequestParam("listId") String listId,
                                     @RequestParam("requestId") String requestId,
                                     @RequestParam("candidateId") String candidateId) {

        Negotiation negotiation = new Negotiation();

        // 1. Crear la negociación inicial utilizando el listId que recuperamos
        String negotiationId = "NEG-" + listId;
        negotiation.setNegotiation_Id(negotiationId);
        negotiation.setListId(listId);
        negotiation.setStatus("in progress");
        negotiation.setStartDate(new java.util.Date());
        negotiation.setHora(java.time.LocalTime.now());
        negotiation.setRecordOfComunications("Inicio de negociaciones con el candidato " + candidateId);

        negotiationDao.addNegotiation(negotiation);

        // 2. Pasar la solicitud a estado 'in negotiation'
        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(requestId);
        if (request != null) {
            request.setStatus("in negotiation");
            assignmentRequestDao.updateAssignmentRequest(request);
        }

        // 3. Redirigir a la vista del Chat
        return "redirect:/negotiation/chat/" + negotiationId;
    }

}