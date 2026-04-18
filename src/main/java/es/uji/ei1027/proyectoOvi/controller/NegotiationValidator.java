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
        return NegotiationController.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Negotiation negotiation = (Negotiation) obj;
        if (negotiation.getNegotiation_Id().trim().equals(""))
            errors.rejectValue("negotiation_Id", "obligatori", "Cal introduir un valor");

        List<String> valors = Arrays.asList("accepted", "refused", "in progress");
        if (!valors.contains(negotiation.getStatus()))
            errors.rejectValue("status", "valor incorrecte",
                    "Deu ser: accepted,refused o in progress");
    }
}

