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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/assignmentRequest")
public class AssignmentRequestController {
    private AssignmentRequestDao assignmentRequestDao;
    private PapPatiDao papPatiDao;
    private ListOfProposedCandidatesDao listOfProposedCandidatesDao;
    private NegotiationDao negotiationDao; // Inyectamos el DAO de Negociaciones
    private OviUserDao oviUserDao;
    private TutorDao tutorDao;

    @Autowired
    public void setAssignmentRequestDao(AssignmentRequestDao assignmentRequestDao){
        this.assignmentRequestDao = assignmentRequestDao;
    }
    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao){
        this.papPatiDao = papPatiDao;
    }
    @Autowired
    public void setListOfProposedCandidatesDao(ListOfProposedCandidatesDao listOfProposedCandidatesDao){
        this.listOfProposedCandidatesDao = listOfProposedCandidatesDao;
    }
    @Autowired
    public void setNegotiationDao(NegotiationDao negotiationDao){
        this.negotiationDao = negotiationDao;
    }
    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }
    @Autowired
    public void setTutorDao(TutorDao tutorDao) {
        this.tutorDao = tutorDao;
    }

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
        if ("user".equals(user.getRole()) || "oviuser".equals(user.getRole())) {
            OviUser oviUser = oviUserDao.getOviUser(user.getDni());
            if (oviUser != null && oviUser.getTutor_id() != null && !oviUser.getTutor_id().isEmpty()) {
                isMinor = true;
            }
        }
        model.addAttribute("isMinor", isMinor);
        model.addAttribute("statusFilter", statusFilter);

        int pageSize = 6;
        List<AssignmentRequest> requestsList = new ArrayList<>();
        int totalItems = 0;

        if (statusFilter != null && !statusFilter.isEmpty()) {
            int offset = page * pageSize;
            requestsList = assignmentRequestDao.getAssignmentRequestsByUserAndStatusPaginated(user.getDni(), statusFilter, pageSize, offset);
            totalItems = assignmentRequestDao.countAssignmentRequestsByUserAndStatus(user.getDni(), statusFilter);
        } else {
            List<AssignmentRequest> allUserRequests = assignmentRequestDao.getAssignmentRequestsByUserPaginated(user.getDni(), Integer.MAX_VALUE, 0);
            List<AssignmentRequest> activeRequests = new ArrayList<>();

            for (AssignmentRequest req : allUserRequests) {
                if (!req.getStatus().equalsIgnoreCase("completed")) {
                    activeRequests.add(req);
                }
            }
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

        List<AssignmentRequest> completedRequests = assignmentRequestDao.getAssignmentRequestsByUserAndStatusPaginated(user.getDni(), "completed", pageSize, offset);
        int totalItems = assignmentRequestDao.countAssignmentRequestsByUserAndStatus(user.getDni(), "completed");

        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("assignmentRequests", completedRequests);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "assignmentRequest/history";
    }

    @RequestMapping(value="/add", method=RequestMethod.GET)
    public String addAssignmentRequest(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("assignmentRequest", new AssignmentRequest());
        model.addAttribute("todayMin", LocalDate.now());
        model.addAttribute("tomorrowMin", LocalDate.now().plusDays(1));

        if ("tutor".equals(user.getRole())) {
            List<OviUser> oviUsers = oviUserDao.getOviUsersByTutor(user.getDni());
            model.addAttribute("oviUsers", oviUsers);
        }
        return "assignmentRequest/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("assignmentRequest") AssignmentRequest assignmentRequest,
                                   BindingResult bindingResult, HttpSession session, Model model,
                                   RedirectAttributes redirectAttributes,
                                   @RequestParam(value = "requiredSkills", required = false) String[] requiredSkillsValues) {

        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if ("oviuser".equals(user.getRole()) || "user".equals(user.getRole())) {
            assignmentRequest.setOviuser_id(user.getDni());
            assignmentRequest.setTutor_id(null);
        } else if ("tutor".equals(user.getRole())) {
            assignmentRequest.setTutor_id(user.getDni());
        }

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

        assignmentRequest.setRequest_Id(nuevoId);
        assignmentRequest.setStatus("in progress");
        assignmentRequest.setRequestDate(LocalDate.now());

        if (requiredSkillsValues != null && requiredSkillsValues.length > 0) {
            assignmentRequest.setRequiredSkills(String.join(", ", requiredSkillsValues));
        } else {
            assignmentRequest.setRequiredSkills(null);
        }

        AssignmentRequestValidator assignmentRequestValidator = new AssignmentRequestValidator();
        assignmentRequestValidator.validate(assignmentRequest, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("todayMin", LocalDate.now());
            model.addAttribute("tomorrowMin", LocalDate.now().plusDays(1));
            if ("tutor".equals(user.getRole())) {
                model.addAttribute("oviUsers", oviUserDao.getOviUsersByTutor(user.getDni()));
            }
            return "assignmentRequest/add";
        }

        try {
            assignmentRequestDao.addAssignmentRequest(assignmentRequest);
            redirectAttributes.addFlashAttribute("registeredRequest", assignmentRequest);
        } catch (DuplicateKeyException e) {
            model.addAttribute("user", user);
            model.addAttribute("todayMin", LocalDate.now());
            model.addAttribute("tomorrowMin", LocalDate.now().plusDays(1));
            bindingResult.rejectValue("request_Id", "duplicat", "Ya existe una solicitud con este ID");
            return "assignmentRequest/add";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("user", user);
            model.addAttribute("todayMin", LocalDate.now());
            model.addAttribute("tomorrowMin", LocalDate.now().plusDays(1));
            bindingResult.rejectValue("oviuser_id", "no_existe", "El usuario especificado no es válido");
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

        List<String> habilidadesSeleccionadas = new ArrayList<>();
        if (request.getRequiredSkills() != null && !request.getRequiredSkills().trim().isEmpty()) {
            habilidadesSeleccionadas = Arrays.stream(request.getRequiredSkills().split(","))
                    .map(String::trim)
                    .collect(java.util.stream.Collectors.toList());
        }
        model.addAttribute("habilidadesSeleccionadas", habilidadesSeleccionadas);
        model.addAttribute("todayMin", LocalDate.now());
        model.addAttribute("tomorrowMin", LocalDate.now().plusDays(1));

        return "assignmentRequest/update";
    }

    @RequestMapping(value="/update", method=RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("assignmentRequest") AssignmentRequest assignmentRequest,
                                      BindingResult bindingResult, Model model,
                                      @RequestParam(value = "requiredSkills", required = false) String[] requiredSkillsValues) {

        if (requiredSkillsValues != null && requiredSkillsValues.length > 0) {
            assignmentRequest.setRequiredSkills(String.join(", ", requiredSkillsValues));
        } else {
            assignmentRequest.setRequiredSkills(null);
        }

        if (assignmentRequest.getOviuser_id() != null && assignmentRequest.getOviuser_id().trim().isEmpty()) {
            assignmentRequest.setOviuser_id(null);
        }
        if (assignmentRequest.getTutor_id() != null && assignmentRequest.getTutor_id().trim().isEmpty()) {
            assignmentRequest.setTutor_id(null);
        }

        AssignmentRequest original = assignmentRequestDao.getAssignmentRequest(assignmentRequest.getRequest_Id());
        assignmentRequest.setRequestDate(original.getRequestDate());

        AssignmentRequestValidator assignmentRequestValidator = new AssignmentRequestValidator();
        assignmentRequestValidator.validate(assignmentRequest, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("todayMin", LocalDate.now());
            model.addAttribute("tomorrowMin", LocalDate.now().plusDays(1));
            return "assignmentRequest/update";
        }
        assignmentRequestDao.updateAssignmentRequest(assignmentRequest);
        return "redirect:list";
    }

//    @RequestMapping(value="/delete/{id}")
//    public String processDelete(@PathVariable String id) {
//        listOfProposedCandidatesDao.deleteCandidatesByRequestId(id);
//        assignmentRequestDao.deleteAssignmentRequest(id);
//        return "redirect:../list";
//    }


    // 1. PETICIÓN GET: Muestra la pantalla de confirmación compartiendo el ID
    @RequestMapping(value="/delete/{id}", method = RequestMethod.GET)
    public String confirmDelete(@PathVariable String id, Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Pasamos el ID de la solicitud a la vista de confirmación
        model.addAttribute("requestId", id);

        // IMPORTANTE: Asegúrate de guardar el HTML en esta ruta de carpetas (templates/assignmentRequest/...)
        return "assignmentRequest/confirmarborrado";
    }

    // 2. PETICIÓN POST: Ejecuta el borrado definitivo cuando se confirma en el formulario
    @RequestMapping(value="/delete/{id}", method = RequestMethod.POST)
    public String processDelete(@PathVariable String id) {
        listOfProposedCandidatesDao.deleteCandidatesByRequestId(id);
        assignmentRequestDao.deleteAssignmentRequest(id);
        return "redirect:../list";
    }

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

            listOfProposedCandidatesDao.deleteCandidatesByRequestId(id);

            List<Pap_Pati> compatibles = papPatiDao.getProposalsForRequest(id);
            int counter = 1;

            for (Pap_Pati pap : compatibles) {
                float score = 0f;
                if (request.getServiceLocation() != null) {
                    String location = request.getServiceLocation().trim().toLowerCase();
                    boolean ciudadOk = pap.getAddress() != null &&
                            pap.getAddress().toLowerCase().contains(location);
                    boolean movilidadOk = pap.getGeographicMobility() != null &&
                            pap.getGeographicMobility().toLowerCase().contains(location);
                    if (ciudadOk || movilidadOk) score += 20f;
                }

                if (pap.getSkills() != null && request.getRequiredSkills() != null) {
                    String candSkills = pap.getSkills().toLowerCase();
                    String[] reqSkills = request.getRequiredSkills().split(",");

                    for (String reqSkill : reqSkills) {
                        if (candSkills.contains(reqSkill.trim().toLowerCase())) {
                            score += 20f;
                            break;
                        }
                    }
                }

                if (pap.getSpecificTraining() != null && request.getRequiredTraining() != null) {
                    String reqTraining = request.getRequiredTraining().trim().toLowerCase();
                    if (pap.getSpecificTraining().toLowerCase().contains(reqTraining)) {
                        score += 20f;
                    }
                }

                int expCandidato = experienciaANumero(pap.getTypeOfExperience());
                int expRequerida = experienciaANumero(request.getRequiredExperience());
                if (expCandidato >= expRequerida) {
                    score += 20f;
                }
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
        if (candidate.getStartDate() != null && candidate.getEndDate() != null
                && req.getRequiredStartAvailability() != null && req.getRequiredEndAvailability() != null
                && !candidate.getStartDate().isAfter(req.getRequiredStartAvailability())
                && !candidate.getEndDate().isBefore(req.getRequiredEndAvailability())) {
            score += 20;
        }

        if (candidate.getAddress() != null && req.getServiceLocation() != null) {
            String location = req.getServiceLocation().trim().toLowerCase();
            if (candidate.getAddress().toLowerCase().contains(location) ||
                    (candidate.getGeographicMobility() != null && candidate.getGeographicMobility().toLowerCase().contains(location))) {
                score += 20;
            }
        }

        if (candidate.getSpecificTraining() != null && req.getRequiredTraining() != null) {
            String reqTraining = req.getRequiredTraining().trim().toLowerCase();
            if (candidate.getSpecificTraining().toLowerCase().contains(reqTraining)) {
                score += 20;
            }
        }

        if (candidate.getTypeOfExperience() != null && req.getRequiredExperience() != null) {
            if (candidate.getTypeOfExperience().trim().equals(req.getRequiredExperience().trim())) {
                score += 20;
            }
        }

        if (candidate.getSkills() != null && req.getRequiredSkills() != null) {
            String candSkills = candidate.getSkills().toLowerCase();
            String[] reqSkills = req.getRequiredSkills().split(",");

            for (String reqSkill : reqSkills) {
                if (candSkills.contains(reqSkill.trim().toLowerCase())) {
                    score += 20;
                    break;
                }
            }
        }
        return score;
    }

    @RequestMapping(value="/accept/{id}", method = RequestMethod.GET)
    public String confirmAccept(Model model, @PathVariable String id) {
        model.addAttribute("assignmentRequest", assignmentRequestDao.getAssignmentRequest(id));
        return "technician/assignmentRequest/accept";
    }

    @RequestMapping(value="/accept/execute/{id}", method = RequestMethod.GET)
    public String executeAcceptAssignmentRequest(@PathVariable String id, Model model) {
        AssignmentRequest assignmentRequest = assignmentRequestDao.getAssignmentRequest(id);

        if (assignmentRequest != null) {
            assignmentRequest.setStatus("accepted");
            assignmentRequestDao.updateAssignmentRequest(assignmentRequest);

            generateAndSaveCandidates(assignmentRequest);

            if (assignmentRequest.getOviuser_id() != null) {
                OviUser oviUser = oviUserDao.getOviUser(assignmentRequest.getOviuser_id());
                if (oviUser != null) {
                    model.addAttribute("oviUserName", oviUser.getName());
                }
            }
            model.addAttribute("assignmentRequest", assignmentRequest);
            model.addAttribute("actionType", "accepted");
        }
        return "technician/assignmentRequest/simulacion_email";
    }

    @RequestMapping(value="/reject/{id}", method = RequestMethod.GET)
    public String confirmReject(Model model, @PathVariable String id) {
        model.addAttribute("assignmentRequest", assignmentRequestDao.getAssignmentRequest(id));
        return "technician/assignmentRequest/reject";
    }

    @RequestMapping(value="/reject/execute/{id}", method = RequestMethod.POST)
    public String executeRejectAssignmentRequest(@PathVariable String id,
                                                 @RequestParam("rejectReason") String rejectReason,
                                                 Model model) {
        AssignmentRequest assignmentRequest = assignmentRequestDao.getAssignmentRequest(id);

        if (assignmentRequest != null) {
            assignmentRequest.setStatus("refused");
            assignmentRequestDao.updateAssignmentRequest(assignmentRequest);

            if (assignmentRequest.getOviuser_id() != null) {
                OviUser oviUser = oviUserDao.getOviUser(assignmentRequest.getOviuser_id());
                if (oviUser != null) {
                    model.addAttribute("oviUserName", oviUser.getName());
                }
            }
            model.addAttribute("assignmentRequest", assignmentRequest);
            model.addAttribute("actionType", "refused");
            model.addAttribute("rejectReason", rejectReason);
        }
        return "technician/assignmentRequest/simulacion_email";
    }

    @RequestMapping("/proposals/{id}")
    public String showProposals(@PathVariable("id") String requestId, Model model, HttpSession session,
                                @RequestParam(defaultValue = "1") int page) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        boolean isMinor = false;
        OviUser oviUser = oviUserDao.getOviUser(user.getDni());
        if (oviUser != null && oviUser.getTutor_id() != null && !oviUser.getTutor_id().isEmpty()) {
            isMinor = true;
        }
        AssignmentRequest assignmentRequest = assignmentRequestDao.getAssignmentRequest(requestId);

        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        List<ListOfProposedCandidates> proposals = listOfProposedCandidatesDao.getProposalsByRequestIdPaginated(requestId, pageSize, offset);
        int totalItems = listOfProposedCandidatesDao.countProposalsByRequestId(requestId);

        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        Set<String> listIdsWithNegotiation = new HashSet<>();
        for (ListOfProposedCandidates proposal : proposals) {
            Negotiation negotiation = negotiationDao.getNegotiationByListId(proposal.getList_id());
            if (negotiation != null && "in progress".equals(negotiation.getStatus())) {
                listIdsWithNegotiation.add(proposal.getList_id());
            }
        }
        model.addAttribute("assignmentRequest", assignmentRequest);
        model.addAttribute("proposals", proposals);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("isMinor", isMinor);
        model.addAttribute("listIdsWithNegotiation", listIdsWithNegotiation);
        return "assignmentRequest/proposals";
    }

    @RequestMapping("/user/{dni}")
    public String listRequestsByUser(Model model, @PathVariable String dni) {
        model.addAttribute("assignmentRequests", assignmentRequestDao.getRequestsByOviUser(dni));
        return "assignmentRequest/list";
    }

    @RequestMapping("/tutor/{dni}")
    public String listRequestsByTutor(Model model, @PathVariable String dni) {
        model.addAttribute("assignmentRequests", assignmentRequestDao.getRequestsByTutor(dni));
        return "assignmentRequest/list";
    }

    @RequestMapping("/pending")
    public String listPendingAssignmentRequests(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        List<AssignmentRequest> requests = assignmentRequestDao.getAssignmentRequestsByStatusPaginated("in progress", pageSize, offset);

        int totalItems = assignmentRequestDao.countAssignmentRequestsByStatus("in progress");
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        if (totalPages == 0) {
            totalPages = 1;
        }
        model.addAttribute("assignmentRequests", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "technician/assignmentRequest/pending";
    }

    @RequestMapping(value="/confirmCandidate", method = RequestMethod.POST)
    public String confirmCandidate(@RequestParam("list_id") String listId,
                                   @RequestParam("requestId") String requestId,
                                   @RequestParam("candidateId") String candidateId,
                                   Model model) {
        Pap_Pati papPati = papPatiDao.getPap_Pati(candidateId);

        model.addAttribute("pappati", papPati);
        model.addAttribute("requestId", requestId);
        model.addAttribute("listId", listId);
        return "assignmentRequest/confirmarcandidato";
    }

    @RequestMapping(value="/finalizeAssignment", method = RequestMethod.POST)
    public String finalizeAssignment(@RequestParam("listId") String listId,
                                     @RequestParam("requestId") String requestId,
                                     @RequestParam("candidateId") String candidateId) {

        Negotiation negotiation = new Negotiation();

        String negotiationId = "NEG-" + listId;
        negotiation.setNegotiation_Id(negotiationId);
        negotiation.setListId(listId);
        negotiation.setStatus("in progress");
        negotiation.setStartDate(new java.util.Date());
        negotiation.setHora(java.time.LocalTime.now());
        negotiation.setRecordOfComunications("Inicio de negociaciones con el candidato " + candidateId);

        negotiationDao.addNegotiation(negotiation);
        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(requestId);
        if (request != null) {
            request.setStatus("in negotiation");
            assignmentRequestDao.updateAssignmentRequest(request);
        }
        return "redirect:/negotiation/chat/" + negotiationId;
    }

    @RequestMapping("/adminNegotiations")
    public String listAdminNegotiations(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || !"technician".equals(user.getRole())) {
            return "redirect:/login";
        }

        List<AssignmentRequest> requests = assignmentRequestDao.getAssignmentRequestsByStatus("in negotiation");

        Map<String, List<String>> requestNegotiations = new HashMap<>();
        Map<String, String> negotiationStatuses = new HashMap<>();
        Map<String, String> firstNegotiationId = new HashMap<>();

        for (AssignmentRequest req : requests) {
            List<String> negIds = negotiationDao.getNegotiationIdsByRequestId(req.getRequest_Id());
            requestNegotiations.put(req.getRequest_Id(), negIds);

            if (!negIds.isEmpty()) {
                firstNegotiationId.put(req.getRequest_Id(), negIds.get(0));
                Negotiation neg = negotiationDao.getNegotiation(negIds.get(0));
                if (neg != null) {
                    negotiationStatuses.put(req.getRequest_Id(), neg.getStatus());
                }
            }
        }
        model.addAttribute("requests", requests);
        model.addAttribute("requestNegotiations", requestNegotiations);
        model.addAttribute("negotiationStatuses", negotiationStatuses);
        model.addAttribute("firstNegotiationId", firstNegotiationId);
        return "assignmentRequest/adminNegotiations";
    }

    @RequestMapping("/detail/{id}")
    public String viewDetail(@PathVariable("id") String requestId, Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(requestId);
        if (request == null) {
            return "redirect:/assignmentRequest/adminNegotiations";
        }

        model.addAttribute("request", request);

        if (request.getOviuser_id() != null) {
            OviUser oviUser = oviUserDao.getOviUser(request.getOviuser_id());
            model.addAttribute("oviUserName", oviUser != null ? oviUser.getName() : null);
        }
        if (request.getTutor_id() != null) {
            Tutor tutor = tutorDao.getTutor(request.getTutor_id());
            model.addAttribute("tutorName", tutor != null ? tutor.getName() : null);
        }
        return "assignmentRequest/detail";
    }

}