package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.ContractDao;
import es.uji.ei1027.proyectoOvi.models.Contract;
import es.uji.ei1027.proyectoOvi.models.UserDetails;
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

    @RequestMapping("/list")
    public String listContracts(Model model){
        model.addAttribute("contracts", contractDao.getContracts());
        return "contract/list";
    }

//    @RequestMapping(value="/add")
//    public String addContract(Model model) {
//        model.addAttribute("contract", new Contract());
//        return "contract/add";
//    }
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

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("contract") Contract contract,
                                   BindingResult bindingResult) {
        ContractValidator contractValidator = new ContractValidator();
        contractValidator.validate(contract, bindingResult);

        if (bindingResult.hasErrors()) {
            return "contract/add";
        }

        try {
            contractDao.addContract(contract);
        } catch (DuplicateKeyException e) {
            bindingResult.rejectValue("contract_Id", "duplicat",
                    "Ya existe un contrato con este ID");
            return "contract/add";
        }

        return "redirect:list";
    }

//    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
//    public String editContract(Model model, @PathVariable String id) {
//        model.addAttribute("contract", contractDao.getContract(id));
//        List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
//        model.addAttribute("statusList", statusList);
//        return "contract/update";
//    }

    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editContract(Model model, @PathVariable String id, jakarta.servlet.http.HttpSession session) {
        // 1. Recuperamos el usuario de la sesión
        UserDetails user = (UserDetails) session.getAttribute("user");

        // 2. Si no hay usuario logueado, lo mandamos al login por seguridad
        if (user == null) {
            return "redirect:/login";
        }

        // 3. Pasamos el usuario al modelo (vital para que no dé error el HTML)
        model.addAttribute("user", user);

        // 4. Pasamos los datos del contrato y la lista de estados
        model.addAttribute("contract", contractDao.getContract(id));
        List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
        model.addAttribute("statusList", statusList);

        return "contract/update";
    }

    @RequestMapping(value="/update", method=RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("contract") Contract contract,
                                      BindingResult bindingResult, Model model) {
        ContractValidator contractValidator = new ContractValidator();
        contractValidator.validate(contract, bindingResult);

        if (bindingResult.hasErrors()) {
            return "contract/update";
        }
        contractDao.updateContract(contract);

        return "redirect:list";
    }

    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable String id) {
        contractDao.deleteContract(id);
        return "redirect:../list";
    }
    //
    @RequestMapping("/user/{dni}")
    public String listContractsByUser(Model model, @PathVariable String dni) {
        // Necesitas tener un método en contractDao que filtre por DNI
        model.addAttribute("contracts", contractDao.getContractsByUser(dni));
        return "contract/list"; // Deberás crear este HTML
    }
    //
    @RequestMapping(value="/search", method = RequestMethod.GET)
    public String searchContractsByDni(@RequestParam("dni") String dni) {
        return "redirect:/contract/user/" + dni;
    }
}
