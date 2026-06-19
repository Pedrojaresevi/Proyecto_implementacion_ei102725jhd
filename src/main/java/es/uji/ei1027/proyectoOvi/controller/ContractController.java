package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.*;
import es.uji.ei1027.proyectoOvi.models.*;
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

    // =========================================================================
    // 1. GET: Preparar el formulario de creación de contrato (Desde Negociación)
    // =========================================================================
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

    // =========================================================================
    // 2. POST: Procesar, automatizar y registrar el nuevo contrato en BBDD
    // =========================================================================
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("contract") Contract contract,
                                   BindingResult bindingResult,
                                   @RequestParam("negotiationId") String negotiationId,
                                   jakarta.servlet.http.HttpSession session,
                                   Model model) {

        ContractValidator contractValidator = new ContractValidator();
        contractValidator.validate(contract, bindingResult);

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

        // Guardar contrato en la Base de Datos
        contractDao.addContract(contract);

        // Cerrar la negociación pasando el estado a 'accepted' y fijando el enddate
        negotiationDao.closeNegotiation(negotiationId, new java.util.Date());

        // -------------------------------------------------------------
        // REDIRECCIÓN DINÁMICA SEGÚN EL ROL
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

        // Ruta por defecto en caso de no detectar un rol específico
        return "redirect:/dashboard";
    }

    // =========================================================================
    // 3. GET: Listar los contratos filtrados por la persona logueada (CON PAGINACIÓN)
    // =========================================================================
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String listContracts(Model model,
                                @RequestParam(defaultValue = "1") int page,
                                jakarta.servlet.http.HttpSession session) {

        // 1. Obtenemos el usuario conectado desde la sesión
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        String dni = user.getDni();
        String role = user.getRole();

        // 2. Configuración de la paginación (6 items por página)
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        // 3. Filtrar los contratos en el DAO usando el DNI y la paginación
        List<Contract> contracts = contractDao.getContractsByUserPaginated(dni, pageSize, offset);

        // 4. Calcular el total de páginas
        int totalItems = contractDao.countContractsByUser(dni);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) {
            totalPages = 1;
        }

        // 5. Enviamos las variables necesarias para la vista
        model.addAttribute("contracts", contracts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("dniOwner", dni);

        // 6. Mapeamos el 'rolePath' para la construcción de rutas de navegación
        String rolePath = "user";
        if ("tutor".equals(role)) {
            rolePath = "tutor";
        } else if ("pap_pati".equals(role)) {
            rolePath = "pap_pati";
        } else if ("technician".equals(role)) {
            rolePath = "technician";
        }
        model.addAttribute("rolePath", rolePath);

        return "contract/list";
    }

    // =========================================================================
    // 4. GET: Buscar contratos por DNI (Exclusivo del Técnico/Administrador)
    // =========================================================================
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchContractsByDni(@RequestParam("dni") String dni, Model model, jakarta.servlet.http.HttpSession session) {

        // 1. Verificamos que el usuario esté logueado
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 2. Buscamos los contratos usando el DNI introducido en el formulario
        model.addAttribute("contracts", contractDao.getContractsByUser(dni));

        // 3. Pasamos las variables para renderizar la tabla correctamente sin fallar
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1);
        model.addAttribute("dniOwner", dni);

        // Fijamos el rol a técnico para que el HTML sepa de qué panel procede
        model.addAttribute("rolePath", "technician");

        return "contract/list";
    }

    // =========================================================================
    // 5. GET: Mostrar la pantalla de confirmación antes de eliminar
    // =========================================================================
    @RequestMapping(value = "/delete/{contract_Id}", method = RequestMethod.GET)
    public String confirmDeleteContract(@PathVariable("contract_Id") String contractId, Model model, jakarta.servlet.http.HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        // Enviamos el ID del contrato que se quiere borrar para mostrarlo en el texto
        model.addAttribute("contractId", contractId);

        // Carga la plantilla 'confirmarborrado.html' dentro de la carpeta templates/contract
        return "contract/confirmarborrado";
    }

    // =========================================================================
    // 6. POST: Ejecutar el borrado definitivo en la base de datos
    // =========================================================================
    @RequestMapping(value = "/delete/{contract_Id}", method = RequestMethod.POST)
    public String executeDeleteContract(@PathVariable("contract_Id") String contractId, jakarta.servlet.http.HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        // Borramos el contrato mediante la sentencia SQL del DAO
        contractDao.deleteContract(contractId);

        // Redirigimos limpiamente de vuelta al listado general
        return "redirect:/contract/list";
    }

    // =========================================================================
    // 7. GET: Cargar y mostrar el formulario para actualizar el contrato
    // =========================================================================
    @RequestMapping(value = "/update/{contract_Id}", method = RequestMethod.GET)
    public String editContract(@PathVariable("contract_Id") String contractId, Model model, jakarta.servlet.http.HttpSession session) {

        // 1. Validamos que exista sesión activa
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 2. Extraemos los datos del contrato de la base de datos
        Contract contract = contractDao.getContract(contractId);
        if (contract == null) {
            return "redirect:/contract/list";
        }

        // 3. Calculamos el rolePath correspondiente para evitar errores en la barra o migas de pan
        String rolePath = "user";
        if ("tutor".equals(user.getRole())) {
            rolePath = "tutor";
        } else if ("pap_pati".equals(user.getRole())) {
            rolePath = "pap_pati";
        } else if ("technician".equals(user.getRole())) {
            rolePath = "technician";
        }

        // 4. Inyectamos TODAS las variables que el HTML 'update.html' espera evaluar
        model.addAttribute("contract", contract);
        model.addAttribute("user", user);                 // Soluciona el error de: user.dni
        model.addAttribute("dniOwner", user.getDni());
        model.addAttribute("rolePath", rolePath);

        return "contract/update";
    }

    // =========================================================================
    // 8. POST: Recibir las modificaciones y actualizar los datos en BBDD
    // =========================================================================
    @RequestMapping(value = "/update/{contract_Id}", method = RequestMethod.POST)
    public String processUpdateSubmit(@PathVariable("contract_Id") String contractId,
                                      @ModelAttribute("contract") Contract contract,
                                      BindingResult bindingResult,
                                      jakarta.servlet.http.HttpSession session,
                                      Model model) {

        // 1. Validamos sesión
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 2. Si el formulario contiene campos incorrectos, recargamos el modelo y la vista
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

        // Forzamos que el ID del objeto coincida fielmente con el parámetro de la URL
        contract.setContract_Id(contractId);

        // Mandamos la orden UPDATE a tu base de datos
        contractDao.updateContract(contract);

        // Retornamos de forma fluida a la lista
        return "redirect:/contract/list";
    }
}