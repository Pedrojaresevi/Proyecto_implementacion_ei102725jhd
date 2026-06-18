package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.*;
import es.uji.ei1027.proyectoOvi.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/contract")
public class ContractController {

    private ContractDao contractDao;
    private NegotiationDao negotiationDao;
    private ListOfProposedCandidatesDao listOfProposedCandidatesDao;
    private PapPatiDao papPatiDao;

    @Autowired
    public void setContractDao(ContractDao contractDao) {
        this.contractDao = contractDao;
    }

    @Autowired
    public void setNegotiationDao(NegotiationDao negotiationDao) {
        this.negotiationDao = negotiationDao;
    }

    @Autowired
    public void setListOfProposedCandidatesDao(ListOfProposedCandidatesDao listOfProposedCandidatesDao) {
        this.listOfProposedCandidatesDao = listOfProposedCandidatesDao;
    }

    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {
        this.papPatiDao = papPatiDao;
    }

    // ==========================================
    // 1. GET: Preparar el formulario de contrato
    // ==========================================
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addContract(@RequestParam("negotiationId") String negotiationId, Model model) {

        // 1. Obtener los metadatos de la negociación activa para conocer el listId
        Negotiation negotiationBase = negotiationDao.getNegotiation(negotiationId);
        if (negotiationBase == null) {
            return "redirect:/negotiation/list";
        }

        // 2. Localizar la propuesta de candidatos vinculada para extraer request_id y pappati_id
        ListOfProposedCandidates proposal = listOfProposedCandidatesDao.getListOfProposedCandidates(negotiationBase.getListId());

        // 3. Inicializar el objeto Contract mapeando los campos correspondientes de tu Modelo
        Contract contract = new Contract();
        if (proposal != null) {
            contract.setRequest_Id(proposal.getRequest_id());
            contract.setPappati_id(proposal.getPappati_id());
        }

        // 4. Pasar el objeto y el ID de negociación a la vista Thymeleaf
        model.addAttribute("contract", contract);
        model.addAttribute("negotiationId", negotiationId);

        return "contract/add";
    }

    // ==========================================
    // 2. POST: Procesar, automatizar y redirigir por Rol
    // ==========================================
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("contract") Contract contract,
                                   BindingResult bindingResult,
                                   @RequestParam("negotiationId") String negotiationId,
                                   jakarta.servlet.http.HttpSession session, // <-- AÑADIDO: Inyectamos la sesión
                                   Model model) {

        // Si hay errores de validación en las fechas, volvemos al formulario
        if (bindingResult.hasErrors()) {
            model.addAttribute("negotiationId", negotiationId);
            return "contract/add";
        }

        // --- AUTOMATIZACIÓN DE CAMPOS ---
        String nextContractId = contractDao.generateNextContractId();
        contract.setContract_Id(nextContractId);
        contract.setStatus("accepted");

        int currentYear = java.time.LocalDate.now().getYear();
        String candidateName = "Documento";

        Pap_Pati papPati = papPatiDao.getPap_Pati(contract.getPappati_id());
        if (papPati != null) {
            String fullName = papPati.getName() + " " + (papPati.getSurname() != null ? papPati.getSurname() : "");
            candidateName = fullName.trim().replace(" ", "_");
        }

        String pdfPath = "/docs/contracts/" + currentYear + "/" + nextContractId + "_" + candidateName + ".pdf";
        contract.setPlaceWhereThePDFIsGonnaBeSaved(pdfPath);

        // 4. Guardar contrato en la Base de Datos
        contractDao.addContract(contract);

        // 5. Cerrar la negociación pasando el estado a 'accepted' y fijando el enddate
        negotiationDao.closeNegotiation(negotiationId, new java.util.Date());

        // -------------------------------------------------------------
        // 6. REDIRECCIÓN DINÁMICA SEGÚN EL ROL (Igual que el botón)
        // -------------------------------------------------------------
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user != null) {
            String role = user.getRole();
            String dni = user.getDni();

            if ("oviuser".equals(role)) {
                return "redirect:/negotiation/user/" + dni;
            } else if ("tutor".equals(role)) {
                return "redirect:/negotiation/tutor/" + dni;
            } else if ("pap_pati".equals(role)) {
                return "redirect:/pap_pati/listpappati";
            }
        }

        // Ruta por defecto en caso de que no hubiera un usuario válido en la sesión
        return "redirect:/dashboard";
    }

    // ==========================================
    // 3. GET: Listar contratos de la persona logueada
    // ==========================================
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String listContracts(Model model, jakarta.servlet.http.HttpSession session) {

        // 1. Obtenemos el usuario conectado desde la sesión
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // Redirige al login si la sesión expiró
        }

        String dni = user.getDni();
        String role = user.getRole();

        // 2. Filtrar los contratos en el DAO usando el DNI del usuario actual
        model.addAttribute("contracts", contractDao.getContractsByUser(dni));

        // 3. Enviamos las variables de paginación que tu HTML necesita
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1); // Por defecto 1 (puedes meterle lógica de paginación real más adelante)
        model.addAttribute("dniOwner", dni);

        // 4. Mapeamos el 'rolePath' para que los enlaces de volver/paginación del HTML funcionen
        String rolePath = "user";
        if ("tutor".equals(role)) {
            rolePath = "tutor";
        } else if ("pap_pati".equals(role)) {
            rolePath = "pap_pati";
        }
        model.addAttribute("rolePath", rolePath);

        // 5. Devolvemos la vista HTML (contract/list.html)
        return "contract/list";
    }

    // ==========================================
    // 4. GET: Buscar contratos por DNI (Para el Técnico)
    // ==========================================
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchContractsByDni(@RequestParam("dni") String dni, Model model, jakarta.servlet.http.HttpSession session) {

        // 1. Verificamos que el usuario esté logueado
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 2. Buscamos los contratos usando el DNI que viene del formulario
        model.addAttribute("contracts", contractDao.getContractsByUser(dni));

        // 3. Pasamos las variables por defecto para la vista HTML (para que no falle la paginación)
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1);
        model.addAttribute("dniOwner", dni);

        // Le pasamos el rol de técnico para que sepa de dónde viene
        model.addAttribute("rolePath", "technician");

        // 4. Devolvemos la misma vista de tabla de contratos
        return "contract/list";
    }
}