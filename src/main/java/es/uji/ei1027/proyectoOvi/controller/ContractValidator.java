package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.Contract;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

public class ContractValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return Contract.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Contract contract = (Contract) obj;
        if (contract.getContractId() == null || contract.getContractId().trim().equals(""))
            errors.rejectValue("contractId", "obligatori", "Cal introduir un valor");
        //
        List<String> valors = Arrays.asList("accepted", "refused", "in progress");
        if (!valors.contains(contract.getStatus()))
            errors.rejectValue("status", "valor incorrecte",
                    "Deu ser: accepted,refused o in progress");
    }
}