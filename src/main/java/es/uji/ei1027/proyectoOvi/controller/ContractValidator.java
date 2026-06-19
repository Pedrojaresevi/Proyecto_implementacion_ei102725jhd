package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.Contract;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ContractValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return Contract.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Contract contract = (Contract) obj;

        // 1. Validar campos de vinculación (se autocompletan en el HTML como readonly)
        if (contract.getPappati_id() == null || contract.getPappati_id().trim().isEmpty()) {
            errors.rejectValue("pappati_id", "obligatori", "Error interno: El ID del PAP/PATI se ha perdido.");
        }

        if (contract.getRequest_Id() == null || contract.getRequest_Id().trim().isEmpty()) {
            errors.rejectValue("request_Id", "obligatori", "Error interno: El ID de la solicitud se ha perdido.");
        }

        // 2. Validar fechas (las introduce el usuario)
        if (contract.getStartDate() == null) {
            errors.rejectValue("startDate", "obligatori", "Hay que introducir una fecha de inicio.");
        }

        if (contract.getEndDate() == null) {
            errors.rejectValue("endDate", "obligatori", "Hay que introducir una fecha de fin.");
        }

        // 3. Validar coherencia de fechas: el fin no puede ser anterior al inicio
        if (contract.getStartDate() != null && contract.getEndDate() != null) {
            if (contract.getEndDate().before(contract.getStartDate())) {
                errors.rejectValue("endDate", "incoherente", "La fecha de fin no puede ser anterior a la fecha de inicio.");
            }
        }
    }
}