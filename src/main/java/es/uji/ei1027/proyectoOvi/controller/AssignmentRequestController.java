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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
            // El OVI User solo ve lo SUYO usando el nuevo método
            model.addAttribute("assignmentRequests", assignmentRequestDao.getRequestsByOviUser(user.getDni()));
            return "assignmentRequest/list";
        }
    }

    @RequestMapping(value="/add")
    public String addAssignmentRequest(Model model) {
        model.addAttribute("assignmentRequest", new AssignmentRequest());
        return "assignmentRequest/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("assignmentRequest") AssignmentRequest assignmentRequest,
                                   BindingResult bindingResult) {
        AssignmentRequestValidator assignmentRequestValidator = new AssignmentRequestValidator();
        assignmentRequestValidator.validate(assignmentRequest, bindingResult);

        if (bindingResult.hasErrors()) {
            return "assignmentRequest/add";
        }

        try {
            // Asignamos la fecha actual automáticamente antes de guardar
            assignmentRequest.setRequestDate(new java.util.Date());
            assignmentRequestDao.addAssignmentRequest(assignmentRequest);
        } catch (DuplicateKeyException e) {
            bindingResult.rejectValue("request_Id", "duplicat",
                    "Ya existe una solicitud con este ID");
            return "assignmentRequest/add";
        }catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("oviuser_id", "no_existe",
                    "El oviUser no es vàlid");
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
    // METODO 1: ACEPTAR Y GENERAR MATCH
    @RequestMapping(value="/accept/{id}")
    public String acceptAndMatch(@PathVariable String id) {
        // 1. Cambiamos estado de la solicitud
        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(id);
        request.setStatus("accepted");
        assignmentRequestDao.updateAssignmentRequest(request);

        // 2. Ejecutamos el Match automático (lo que hablábamos antes)
        List<Pap_Pati> compatibles = papPatiDao.getProposalsForRequest(id);

        for (Pap_Pati pap : compatibles) {
            ListOfProposedCandidates proposal = new ListOfProposedCandidates();
            proposal.setList_id("L-" + id + "-" + pap.getDni());
            proposal.setRequest_id(id);
            proposal.setPappati_id(pap.getDni());
            proposal.setProposalDate(new java.util.Date());
            proposal.setSuitabilityScore(100.0f);

            listOfProposedCandidatesDao.addListOfProposedCandidates(proposal);
        }
        return "redirect:/assignmentRequest/list";
    }

    // METODO 2: RECHAZAR (SIN MATCH)
    @RequestMapping(value="/reject/{id}")
    public String rejectRequest(@PathVariable String id) {
        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(id);
        request.setStatus("refused"); // O el estado que uses en tu DB
        assignmentRequestDao.updateAssignmentRequest(request);

        return "redirect:/assignmentRequest/list";
    }

}
