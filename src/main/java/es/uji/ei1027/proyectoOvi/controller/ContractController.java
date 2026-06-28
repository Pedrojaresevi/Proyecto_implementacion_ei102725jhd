package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.*;
import es.uji.ei1027.proyectoOvi.models.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/contract")
public class ContractController {

    private ContractDao contractDao;
    private NegotiationDao negotiationDao;
    private ListOfProposedCandidatesDao listOfProposedCandidatesDao;
    private PapPatiDao papPatiDao;
    private OviUserDao oviUserDao;
    private AssignmentRequestDao assignmentRequestDao;

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

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) { this.oviUserDao = oviUserDao;}

    @Autowired
    public void setAssignmentRequestDao(AssignmentRequestDao assignmentRequestDao) {
        this.assignmentRequestDao = assignmentRequestDao;
    }

    
    
    
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addContract(@RequestParam("negotiationId") String negotiationId, Model model) {

        
        Negotiation negotiationBase = negotiationDao.getNegotiation(negotiationId);
        if (negotiationBase == null) {
            return "redirect:/negotiation/list";
        }

        
        ListOfProposedCandidates proposal = listOfProposedCandidatesDao.getListOfProposedCandidates(negotiationBase.getListId());

        
        Contract contract = new Contract();
        if (proposal != null) {
            contract.setRequest_Id(proposal.getRequest_id());
            contract.setPappati_id(proposal.getPappati_id());
        }

        
        model.addAttribute("contract", contract);
        model.addAttribute("negotiationId", negotiationId);

        return "contract/add";
    }

    
    
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("contract") Contract contract,
                                   BindingResult bindingResult,
                                   @RequestParam("negotiationId") String negotiationId,
                                   jakarta.servlet.http.HttpSession session,
                                   Model model) {

        ContractValidator contractValidator = new ContractValidator();
        contractValidator.validate(contract, bindingResult);

        
        if (bindingResult.hasErrors()) {
            model.addAttribute("negotiationId", negotiationId);
            return "contract/add";
        }

        
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

        
        contractDao.addContract(contract);

        
        negotiationDao.closeNegotiation(negotiationId, new java.util.Date());

        
        List<String> negotiationIds = negotiationDao.getNegotiationIdsByRequestId(contract.getRequest_Id());
        for (String negId : negotiationIds) {
            if (!negId.equals(negotiationId)) {
                negotiationDao.updateNegotiationStatus(negId, "refused");
            }
        }

        
        AssignmentRequest request = assignmentRequestDao.getAssignmentRequest(contract.getRequest_Id());
        if (request != null) {
            request.setStatus("completed");
            assignmentRequestDao.updateAssignmentRequest(request);
        }

        
        
        
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

        
        return "redirect:/dashboard";
    }

    @RequestMapping("/list")
    public String listContracts(Model model, HttpSession session,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(value = "statusFilter", required = false) String statusFilter) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        contractDao.finalizeExpiredContracts();

        String dniToQuery = user.getDni();
        boolean isMinor = false;

        OviUser oviUser = oviUserDao.getOviUser(user.getDni());
        if (oviUser != null && oviUser.getTutor_id() != null && !oviUser.getTutor_id().isEmpty()) {
            isMinor = true;
        }

        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        List<Contract> contracts;
        int totalItems;

        if (statusFilter != null && !statusFilter.isEmpty()) {
            contracts = contractDao.getContractsByUserAndStatusPaginated(dniToQuery, statusFilter, pageSize, offset);
            totalItems = contractDao.countContractsByUserAndStatus(dniToQuery, statusFilter);
        } else {
            contracts = contractDao.getContractsByUserPaginated(dniToQuery, pageSize, offset);
            totalItems = contractDao.countContractsByUser(dniToQuery);
        }

        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("contracts", contracts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("isMinor", isMinor);
        model.addAttribute("statusFilter", statusFilter);

        return "contract/list";
    }

    
    
    
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchContractsByDni(@RequestParam("dni") String dni, Model model, jakarta.servlet.http.HttpSession session) {

        
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        
        model.addAttribute("contracts", contractDao.getContractsByUser(dni));

        
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1);
        model.addAttribute("dniOwner", dni);

        
        model.addAttribute("rolePath", "technician");

        return "contract/list";
    }

    
    
    
    @RequestMapping(value = "/searchByRequest", method = RequestMethod.GET)
    public String searchContractsByRequest(@RequestParam("requestId") String requestId, Model model, jakarta.servlet.http.HttpSession session) {

        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("contracts", contractDao.getContractsByRequestId(requestId));
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1);
        model.addAttribute("dniOwner", requestId);
        model.addAttribute("rolePath", "technician");

        return "contract/list";
    }

    
    
    
    @RequestMapping(value = "/delete/{contract_Id}", method = RequestMethod.GET)
    public String confirmDeleteContract(@PathVariable("contract_Id") String contractId, Model model, jakarta.servlet.http.HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        
        model.addAttribute("contractId", contractId);

        
        return "contract/confirmarborrado";
    }

    
    
    
    @RequestMapping(value = "/delete/{contract_Id}", method = RequestMethod.POST)
    public String executeDeleteContract(@PathVariable("contract_Id") String contractId, jakarta.servlet.http.HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        
        contractDao.deleteContract(contractId);

        
        return "redirect:/contract/list";
    }

    
    
    
    @RequestMapping(value = "/update/{contract_Id}", method = RequestMethod.GET)
    public String editContract(@PathVariable("contract_Id") String contractId, Model model, jakarta.servlet.http.HttpSession session) {

        
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        
        Contract contract = contractDao.getContract(contractId);
        if (contract == null) {
            return "redirect:/contract/list";
        }

        
        String rolePath = "user";
        if ("tutor".equals(user.getRole())) {
            rolePath = "tutor";
        } else if ("pap_pati".equals(user.getRole())) {
            rolePath = "pap_pati";
        } else if ("technician".equals(user.getRole())) {
            rolePath = "technician";
        }

        
        model.addAttribute("contract", contract);
        model.addAttribute("user", user);                 
        model.addAttribute("dniOwner", user.getDni());
        model.addAttribute("rolePath", rolePath);

        return "contract/update";
    }

    
    
    @RequestMapping(value = "/update/{contract_Id}", method = RequestMethod.POST)
    public String processUpdateSubmit(@PathVariable("contract_Id") String contractId,
                                      @ModelAttribute("contract") Contract contract,
                                      BindingResult bindingResult,
                                      jakarta.servlet.http.HttpSession session,
                                      Model model) {

        
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        
        ContractValidator contractValidator = new ContractValidator();
        contractValidator.validate(contract, bindingResult);

        
        if (bindingResult.hasErrors()) {
            String rolePath = "user";
            if ("tutor".equals(user.getRole())) {
                rolePath = "tutor";
            } else if ("pap_pati".equals(user.getRole())) {
                rolePath = "pap_pati";
            } else if ("technician".equals(user.getRole())) {
                rolePath = "technician";
            }

            model.addAttribute("user", user);
            model.addAttribute("dniOwner", user.getDni());
            model.addAttribute("rolePath", rolePath);

            
            return "contract/update";
        }

        
        contract.setContract_Id(contractId);

        
        Contract original = contractDao.getContract(contractId);
        if (contract.getEndDate() != null) {
            if (contract.getEndDate().before(new java.util.Date())) {
                contract.setStatus("finalized");
            } else if ("finalized".equals(contract.getStatus()) || "finalized".equals(original.getStatus())) {
                contract.setStatus("in progress");
            }
        }

        
        contractDao.updateContract(contract);

        
        return "redirect:/contract/list";
    }
}