package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.AssignmentRequestDao;
import es.uji.ei1027.proyectoOvi.dao.ListOfProposedCandidatesDao;
import es.uji.ei1027.proyectoOvi.dao.PapPatiDao;
import es.uji.ei1027.proyectoOvi.models.AssignmentRequest;
import es.uji.ei1027.proyectoOvi.models.ListOfProposedCandidates;
import es.uji.ei1027.proyectoOvi.models.Pap_Pati;
import es.uji.ei1027.proyectoOvi.models.UserDetails;


import jakarta.servlet.http.HttpSession;
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
@RequestMapping("/assignmentRequest")
public class AssignmentRequestController {
    private AssignmentRequestDao assignmentRequestDao;
    private PapPatiDao papPatiDao;
    private ListOfProposedCandidatesDao listOfProposedCandidatesDao;

    @Autowired
    public void setAssignmentRequestDao(AssignmentRequestDao assignmentRequestDao){
        this.assignmentRequestDao=assignmentRequestDao;
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

//    @RequestMapping("/list")
//    public String listAssignmentRequests(Model model){
//        model.addAttribute("assignmentRequests", assignmentRequestDao.getAssignmentRequests());
//        return "assignmentRequest/list";
//    }
    //List depediendo del rol
    @RequestMapping("/list")
    public String list(Model model, HttpSession session) {
        // 1. Obtenemos el usuario de la sesión (ajusta el nombre del atributo "user" si es otro)
        // Suponiendo que tu objeto de sesión tiene un método getDni() y getRole()
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login"; // Si no hay sesión, al login
        }

        if (user.getRole().equals("technician")) {
            // El técnico ve tofo
            model.addAttribute("assignmentRequests", assignmentRequestDao.getAssignmentRequests());
            return "technician/assignmentRequest/list";
        } else {
            // Cargamos la lista correspondiente según quién sea
            if (user.getRole().equals("oviuser")) {
                model.addAttribute("assignmentRequests", assignmentRequestDao.getRequestsByOviUser(user.getDni()));
            } else if (user.getRole().equals("tutor")) {
                model.addAttribute("assignmentRequests", assignmentRequestDao.getRequestsByTutor(user.getDni()));
            }

            // Ambos van al mismo archivo HTML a mostrar su lista
            return "assignmentRequest/list";
        }
    }

    @RequestMapping(value="/add")
    public String addAssignmentRequest(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("assignmentRequest", new AssignmentRequest());

        // Ambos roles van al mismo formulario
        return "assignmentRequest/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("assignmentRequest") AssignmentRequest assignmentRequest,
                                   BindingResult bindingResult, HttpSession session) {

        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user != null) {
            if ("oviuser".equals(user.getRole())) {
                assignmentRequest.setOviuser_id(user.getDni());
                // Aseguramos que tutor_id esté nulo por la regla CHECK de la BD
                assignmentRequest.setTutor_id(null);
            } else if ("tutor".equals(user.getRole())) {
                assignmentRequest.setTutor_id(user.getDni());
                // Aseguramos que oviuser_id esté nulo
                assignmentRequest.setOviuser_id(null);
            }
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
        assignmentRequest.setRequestDate(new java.util.Date());

        // 4. VALIDAR Y GUARDAR
        AssignmentRequestValidator assignmentRequestValidator = new AssignmentRequestValidator();
        assignmentRequestValidator.validate(assignmentRequest, bindingResult);

        if (bindingResult.hasErrors()) {
            return "assignmentRequest/add";
        }

        try {
            assignmentRequestDao.addAssignmentRequest(assignmentRequest);
        } catch (DuplicateKeyException e) {
            bindingResult.rejectValue("request_Id", "duplicat", "Ya existe una solicitud con este ID");
            return "assignmentRequest/add";
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("oviuser_id", "no_existe", "El oviUser no es vàlid");
            return "assignmentRequest/add";
        }

        return "redirect:list";
    }

    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editAssignmentRequest(Model model, @PathVariable String id) {
        model.addAttribute("assignmentRequest", assignmentRequestDao.getAssignmentRequest(id));
        return "assignmentRequest/update";
    }

    @RequestMapping(value="/update", method=RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("assignmentRequest") AssignmentRequest assignmentRequest,
                                      BindingResult bindingResult, Model model) {
        // Se recupera la fecha original de la base de datos usando el ID
        AssignmentRequest original = assignmentRequestDao.getAssignmentRequest(assignmentRequest.getRequest_Id());
        assignmentRequest.setRequestDate(original.getRequestDate());
        //Validar después de poner la fecha para que el validador no se queje
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

    //Nuevo metodo para reciba el ID de la solicitud y pase al modelo tanto los datos de la solicitud como la lista de candidatos propuestos.
//    @RequestMapping("/proposals/{id}")
//    public String listProposals(Model model, @PathVariable String id) {
//        // 1. Recuperamos la solicitud para mostrar sus detalles arriba
//        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(id);
//        model.addAttribute("assignmentRequest", request);
//
//        // 2. Busca los candidatos (PAP/PATI)
//        List<Pap_Pati> listaCandidatos = papPatiDao.getProposalsForRequest(id);
//        model.addAttribute("candidates", listaCandidatos);
//
//        return "assignmentRequest/proposals";
//    }
    @RequestMapping("/proposals/{id}")
    public String listProposals(Model model, @PathVariable String id) {
        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(id);
        model.addAttribute("assignmentRequest", request);

        // Ahora cargamos desde ListOfProposedCandidates, no desde PapPati directamente
        List<ListOfProposedCandidates> proposals = listOfProposedCandidatesDao.getProposalsByRequestId(id);
        model.addAttribute("proposals", proposals);

        return "assignmentRequest/proposals";
    }
    // 1. Solo muestra la pantalla de confirmación
    @RequestMapping(value="/accept/{id}", method = RequestMethod.GET)
    public String confirmAccept(Model model, @PathVariable String id) {
        model.addAttribute("assignmentRequest", assignmentRequestDao.getAssignmentRequest(id));
        return "technician/assignmentRequest/accept";
    }
//    @RequestMapping(value="/accept/execute/{id}")
//    public String acceptAndMatch(@PathVariable String id) {
//        // 1. Recuperamos la solicitud
//        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(id);
//        // OPCIONAL: Solo ejecutamos si la solicitud NO estaba aceptada ya
//        if (request != null && !"accepted".equals(request.getStatus())) {
//            // 2. Cambiamos estado
//            request.setStatus("accepted");
//            assignmentRequestDao.updateAssignmentRequest(request);
//            // 3. Ejecutamos el Match automático
//            List<Pap_Pati> compatibles = papPatiDao.getProposalsForRequest(id);
//            for (Pap_Pati pap : compatibles) {
//                try {
//                    ListOfProposedCandidates proposal = new ListOfProposedCandidates();
//                    proposal.setList_id("L-" + id + "-" + pap.getDni());
//                    proposal.setRequest_id(id);
//                    proposal.setPappati_id(pap.getDni());
//                    proposal.setProposalDate(new java.util.Date());
//                    proposal.setSuitabilityScore(100.0f);
//
//                    listOfProposedCandidatesDao.addListOfProposedCandidates(proposal);
//                } catch (DuplicateKeyException e) {
//                    // Si ya existía esta propuesta, simplemente la saltamos y seguimos con el siguiente
//                    continue;
//                }
//            }
//        }
//        return "redirect:/assignmentRequest/list";
//    }
//    @RequestMapping(value="/accept/execute/{id}")
//    public String acceptAndMatch(@PathVariable String id) {
//        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(id);
//
//        if (request != null && !"accepted".equals(request.getStatus())) {
//            request.setStatus("accepted");
//            assignmentRequestDao.updateAssignmentRequest(request);
//
//            List<Pap_Pati> compatibles = papPatiDao.getProposalsForRequest(id);
//            int counter = 1;
//            for (Pap_Pati pap : compatibles) {
//                try {
//                    ListOfProposedCandidates proposal = new ListOfProposedCandidates();
//
//                    // list_id único usando contador
//                    proposal.setList_id("L-" + id + "-" + counter);
//                    proposal.setRequest_id(id);
//                    proposal.setPappati_id(pap.getDni());
//                    proposal.setProposalDate(new java.util.Date());
//
//                    // Calcular suitabilityScore dinámicamente (5 condiciones = 20% cada una)
//                    float score = 0f;
//                    if (pap.getStartDate() != null &&
//                            !pap.getStartDate().after(request.getRequiredStartAvailability())) score += 20f;
//                    if (pap.getEndDate() != null &&
//                            !pap.getEndDate().before(request.getRequiredEndAvailability())) score += 20f;
//                    if (pap.getAddress() != null && request.getServiceLocation() != null &&
//                            (pap.getAddress().toLowerCase()
//                                    .contains(request.getServiceLocation().toLowerCase()) ||
//                                    (pap.getGeographicMobility() != null &&
//                                            pap.getGeographicMobility().toLowerCase()
//                                                    .contains(request.getServiceLocation().toLowerCase())))) score += 20f;
//                    if (pap.getSkills() != null && request.getRequiredSkills() != null &&
//                            pap.getSkills().toLowerCase()
//                                    .contains(request.getRequiredSkills().toLowerCase())) score += 20f;
//                    if (pap.getSpecificTraining() != null && request.getRequiredTraining() != null &&
//                            pap.getSpecificTraining().toLowerCase()
//                                    .contains(request.getRequiredTraining().toLowerCase())) score += 20f;
//
//                    proposal.setSuitabilityScore(score);
//
//                    listOfProposedCandidatesDao.addListOfProposedCandidates(proposal);
//                    counter++;
//                } catch (DuplicateKeyException e) {
//                    counter++;
//                    continue;
//                }
//            }
//        }
//        return "redirect:/assignmentRequest/list";
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


    // Muestra la pantalla de confirmación de rechazo
    @RequestMapping(value="/reject/{id}", method = RequestMethod.GET)
    public String confirmReject(Model model, @PathVariable String id) {
        // Recupera la solicitud y la envía a la vista para que pueda mostrar el ID
        model.addAttribute("assignmentRequest", assignmentRequestDao.getAssignmentRequest(id));
        return "technician/assignmentRequest/reject";
    }

    // Ejecuta el rechazo real y vuelve a la lista
    @RequestMapping(value="/reject/execute/{id}", method = RequestMethod.POST)
    public String executeReject(@PathVariable String id, @RequestParam("rejectReason") String rejectReason) {

        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(id);

        if (request != null && !"refused".equals(request.getStatus())) {
            request.setStatus("refused");

            assignmentRequestDao.updateAssignmentRequest(request);

            // Opcional: Podrías hacer un print para comprobar que llega bien el texto
            System.out.println("Solicitud " + id + " rechazada por: " + rejectReason);
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
}
