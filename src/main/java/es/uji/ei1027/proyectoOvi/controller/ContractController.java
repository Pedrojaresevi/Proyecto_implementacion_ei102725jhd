package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.ContractDao;
import es.uji.ei1027.proyectoOvi.models.Contract;
import es.uji.ei1027.proyectoOvi.models.UserDetails;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/contract")
public class ContractController {
    private ContractDao contractDao;

    @Autowired
    public void setContractDao(ContractDao contractDao){
        this.contractDao = contractDao;
    }

    @RequestMapping("/list") // Cambia la ruta si tu endpoint es diferente
    public String listContracts(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        // 1. Obtener los contratos de la página actual
        List<Contract> contracts = contractDao.getContractsPaginated(pageSize, offset);

        // 2. Calcular el total de páginas
        int totalItems = contractDao.countContracts();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        if (totalPages == 0) {
            totalPages = 1;
        }

        // 3. Pasar las variables a la vista
        model.addAttribute("contracts", contracts); // Asegúrate de usar el mismo nombre que recorre tu th:each
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "contract/list"; // Cambia al nombre exacto de tu HTML
    }

    @RequestMapping(value="/add")
    public String addContract(Model model, jakarta.servlet.http.HttpSession session) {
        // 1. Recuperamos el usuario de la sesión
        UserDetails user = (UserDetails) session.getAttribute("user");

        // 2. Si no hay usuario logueado, lo mandamos al login por seguridad
        if (user == null) {
            return "redirect:/login";
        }

        // 3. Pasamos el usuario al modelo para que el HTML no de error al buscar su DNI
        model.addAttribute("user", user);

        // 4. Pasamos el contrato vacío para el formulario
        model.addAttribute("contract", new Contract());

        return "contract/add";
    }


    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editContract(Model model, @PathVariable String id, jakarta.servlet.http.HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        // PASAMOS EL DNI BUSCADO A LA VISTA (Para arreglar el botón "Volver")
        model.addAttribute("searchedDni", session.getAttribute("searchedDni"));

        model.addAttribute("contract", contractDao.getContract(id));
        List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
        model.addAttribute("statusList", statusList);

        return "contract/update";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("contract") Contract contract,
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
            model.addAttribute("user", user);
            model.addAttribute("searchedDni", session.getAttribute("searchedDni"));
            List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
            model.addAttribute("statusList", statusList);
            return "contract/update";
        }

        contractDao.updateContract(contract);

        // AQUÍ ESTÁ LA MAGIA DE LA REDIRECCIÓN
        if ("technician".equals(user.getRole())) {
            String searchedDni = (String) session.getAttribute("searchedDni");
            // Si el admin hizo una búsqueda previa, le devolvemos a esa búsqueda
            if (searchedDni != null) {
                return "redirect:/contract/user/" + searchedDni;
            } else {
                // Por si acaso entra directamente sin buscar
                return "redirect:/";
            }
        } else {
            // Si es un usuario normal (OVI user o PAP/PATI), vuelve a sus propios contratos
            return "redirect:/contract/user/" + user.getDni();
        }
    }

    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable String id, jakarta.servlet.http.HttpSession session) {

        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        // 1. Eliminamos el contrato
        contractDao.deleteContract(id);

        // 2. Redirección inteligente según el rol
        if ("technician".equals(user.getRole())) {
            String searchedDni = (String) session.getAttribute("searchedDni");
            // Si el técnico estaba viendo la búsqueda de alguien, vuelve ahí
            if (searchedDni != null) {
                return "redirect:/contract/user/" + searchedDni;
            } else {
                return "redirect:/"; // Redirección segura por defecto
            }
        } else {
            // Si es un OVI user o PAP/PATI, vuelve a su propia lista
            return "redirect:/contract/user/" + user.getDni();
        }
    }
    //
    @RequestMapping("/user/{dni}")
    public String listContractsByUser(Model model, @PathVariable String dni, jakarta.servlet.http.HttpSession session) {
        // Necesitas tener un método en contractDao que filtre por DNI
        session.setAttribute("searchedDni", dni);
        model.addAttribute("contracts", contractDao.getContractsByUser(dni));
        return "contract/list";
    }
    //
    @RequestMapping(value="/search", method = RequestMethod.GET)
    public String searchContractsByDni(@RequestParam("dni") String dni) {
        return "redirect:/contract/user/" + dni;
    }
}
