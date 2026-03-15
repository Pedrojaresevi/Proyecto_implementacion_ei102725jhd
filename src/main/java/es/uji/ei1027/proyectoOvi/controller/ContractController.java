package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.ContractDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
