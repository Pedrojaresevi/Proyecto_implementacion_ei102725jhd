package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.OviUser;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

public class OviUserValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return OviUser.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        OviUser oviUser = (OviUser) obj;
        if (oviUser.getId().trim().equals(""))
            errors.rejectValue("id", "obligatori",
                    "Cal introduir un valor");
        //
        List<String> valors = Arrays.asList("accepted", "refused", "in progress");
        if (!valors.contains(oviUser.getStatus()))
            errors.rejectValue("status", "valor incorrecte",
                    "Deu ser: accepted,refused o in progress");

    }
}
