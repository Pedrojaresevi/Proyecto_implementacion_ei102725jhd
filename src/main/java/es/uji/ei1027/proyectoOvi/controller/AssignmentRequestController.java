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
        } else if(user.getRole().equals("oviuser")) {
            // El OVI User solo ve lo SUYO usando el nuevo método
            model.addAttribute("assignmentRequests", assignmentRequestDao.getRequestsByOviUser(user.getDni()));
            return "assignmentRequest/list";
        }else{
            model.addAttribute("assignmentRequests", assignmentRequestDao.getRequestsByTutor(user.getDni()));
            return "tutor/assignmentRequest/list";
        }
    }

    @RequestMapping(value="/add")
    public String addAssignmentRequest(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("assignmentRequest", new AssignmentRequest());

        if(user.getRole().equals("oviuser"))
            return "assignmentRequest/add";
        else
            return "tutor/assignmentRequest/add";
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

        System.out.println("=== DEBUG ANTES DE VALIDAR ===");
        System.out.println("Rol del usuario en sesión: " + (user != null ? user.getRole() : "NULO"));
        System.out.println("Valor de oviuser_id: " + assignmentRequest.getOviuser_id());
        System.out.println("Valor de tutor_id: " + assignmentRequest.getTutor_id());
        System.out.println("==============================");

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
            if (user != null && "tutor".equals(user.getRole())) {
                return "tutor/assignmentRequest/add";
            }
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
    @RequestMapping("/proposals/{id}")
    public String listProposals(Model model, @PathVariable String id) {
        // 1. Recuperamos la solicitud para mostrar sus detalles arriba
        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(id);
        model.addAttribute("assignmentRequest", request);

        // 2. Busca los candidatos (PAP/PATI)
        List<Pap_Pati> listaCandidatos = papPatiDao.getProposalsForRequest(id);
        model.addAttribute("candidates", listaCandidatos);

        return "assignmentRequest/proposals";
    }
    // 1. Solo muestra la pantalla de confirmación
    @RequestMapping(value="/accept/{id}", method = RequestMethod.GET)
    public String confirmAccept(Model model, @PathVariable String id) {
        model.addAttribute("assignmentRequest", assignmentRequestDao.getAssignmentRequest(id));
        return "technician/assignmentRequest/accept";
    }
    @RequestMapping(value="/accept/execute/{id}")
    public String acceptAndMatch(@PathVariable String id) {
        // 1. Recuperamos la solicitud
        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(id);
        // OPCIONAL: Solo ejecutamos si la solicitud NO estaba aceptada ya
        if (request != null && !"accepted".equals(request.getStatus())) {
            // 2. Cambiamos estado
            request.setStatus("accepted");
            assignmentRequestDao.updateAssignmentRequest(request);
            // 3. Ejecutamos el Match automático
            List<Pap_Pati> compatibles = papPatiDao.getProposalsForRequest(id);
            for (Pap_Pati pap : compatibles) {
                try {
                    ListOfProposedCandidates proposal = new ListOfProposedCandidates();
                    proposal.setList_id("L-" + id + "-" + pap.getDni());
                    proposal.setRequest_id(id);
                    proposal.setPappati_id(pap.getDni());
                    proposal.setProposalDate(new java.util.Date());
                    proposal.setSuitabilityScore(100.0f);

                    listOfProposedCandidatesDao.addListOfProposedCandidates(proposal);
                } catch (DuplicateKeyException e) {
                    // Si ya existía esta propuesta, simplemente la saltamos y seguimos con el siguiente
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

            // ⚠️ ATENCIÓN AQUÍ ⚠️
            // Si tienes un campo en tu clase AssignmentRequest para guardar el motivo, sería algo así:
            // request.setRejectReason(rejectReason);

            assignmentRequestDao.updateAssignmentRequest(request);

            // Opcional: Podrías hacer un print para comprobar que llega bien el texto
            System.out.println("Solicitud " + id + " rechazada por: " + rejectReason);
        }

        return "redirect:/assignmentRequest/list";
    }
}
