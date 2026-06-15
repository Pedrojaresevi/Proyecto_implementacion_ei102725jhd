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

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("pap_pati") Pap_Pati papPati,
                                   BindingResult bindingResult) {

        papPati.setStatus("in progress");

        Pap_PatiValidator pap_patiValidator = new Pap_PatiValidator();
        pap_patiValidator.validate(papPati, bindingResult);

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
        return "redirect:/";
    }

    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editPapPati(Model model, @PathVariable String id) {
        model.addAttribute("pap_pati", pap_patiDao.getPap_Pati(id));
        List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
        model.addAttribute("statusList", statusList);
        return "pap_pati/update";
    }

    @RequestMapping(value="/update", method=RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("pap_pati") Pap_Pati papPati,
                                      BindingResult bindingResult, Model model) {
        Pap_PatiValidator pap_patiValidator = new Pap_PatiValidator();
        pap_patiValidator.validate(papPati, bindingResult);

        if (bindingResult.hasErrors()) {
            return "pap_pati/update";
        }
        pap_patiDao.updatePap_Pati(papPati);

        return "redirect:/pap_pati/accepted";
    }

    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable String id) {
        pap_patiDao.deletePap_Pati(id);
        return "redirect:../list";
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

    @RequestMapping("/detallesasignacion/{id}")
    public String viewDetail(Model model, @PathVariable String id, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(id);
        model.addAttribute("assignmentRequest", request);

        // Cargamos el oviuser si la solicitud tiene oviuser_id
        if (request.getOviuser_id() != null) {
            model.addAttribute("oviuser", oviUserDao.getOviUser(request.getOviuser_id()));
        }

        // Si está aceptada y el usuario es pappati, buscamos su list_id para el botón de negociación
        if ("accepted".equals(request.getStatus()) && "pap_pati".equals(user.getRole())) {
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
    public String executeAcceptPapPati(@PathVariable String dni) {
        Pap_Pati papPati = pap_patiDao.getPap_Pati(dni);
        if (papPati != null) {
            papPati.setStatus("accepted");
            pap_patiDao.updatePap_Pati(papPati);
        }
        return "redirect:/pap_pati/pending";
    }

    @RequestMapping(value="/reject/{dni}", method = RequestMethod.GET)
    public String confirmRejectPapPati(Model model, @PathVariable String dni) {
        model.addAttribute("pap_pati", pap_patiDao.getPap_Pati(dni));
        return "technician/pap_pati/reject";
    }


    @RequestMapping(value="/reject/execute/{dni}", method = RequestMethod.POST)
    public String executeRejectPapPati(@PathVariable String dni, @RequestParam("rejectReason") String rejectReason) {

        Pap_Pati pap_pati = pap_patiDao.getPap_Pati(dni);

        if (pap_pati != null) {
            pap_pati.setStatus("refused");
            pap_pati.setRejectReason(rejectReason);

            pap_patiDao.updatePap_Pati(pap_pati);
        }
        return "redirect:/pap_pati/pending";
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

    @RequestMapping(value="/masdetalle/{id}", method = RequestMethod.GET)
    public String verMasDetalle(Model model,
                                @PathVariable String id,
                                @RequestParam(required = false) String requestId,
                                HttpSession session) {

        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Pap_Pati candidato = pap_patiDao.getPap_Pati(id);

        if (candidato == null) {
            return "redirect:/pap_pati/list";
        }

        // Pasamos el candidato
        model.addAttribute("pap_pati", candidato);
        // Pasamos el ID de la solicitud (puede ser null si acceden por otra ruta)
        model.addAttribute("requestId", requestId);

        return "pap_pati/masdetalle";
    }

}
