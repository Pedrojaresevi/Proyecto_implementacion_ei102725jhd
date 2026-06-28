package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.Negotiation;
import es.uji.ei1027.proyectoOvi.models.Pap_Pati;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

public class NegotiationValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return Negotiation.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Negotiation negotiation = (Negotiation) obj;

        if (negotiation.getNegotiation_Id() == null || negotiation.getNegotiation_Id().trim().equals(""))
            errors.rejectValue("negotiation_Id", "obligatorio", "Cal introduir un valor");
        
        List<String> valors = Arrays.asList("accepted", "refused", "in progress");
        if (!valors.contains(negotiation.getStatus()))
            errors.rejectValue("status", "valor incorrecte",
                    "Debe ser: accepted, refused o in progress");
        
        if (negotiation.getListId() == null || negotiation.getListId().trim().equals(""))
            errors.rejectValue("list_id","obligatori",
                    "Hay que introducir un valor");

        if (negotiation.getStartDate() == null)
            errors.rejectValue("startDate", "obligatorio", "Hay que introducir una fecha de inicio");

        if (negotiation.getEndDate() == null)
            errors.rejectValue("endDate", "obligatorio", "Hay que introducir una fecha de fin");

        if (negotiation.getRecordOfComunications() == null || negotiation.getRecordOfComunications().trim().equals(""))
            errors.rejectValue("recordOfComunications","obligatorio",
                    "Hay que introducir el registro de comunicaciones");
    }
}

