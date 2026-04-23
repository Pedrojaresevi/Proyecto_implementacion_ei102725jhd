package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.Pap_Pati;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

public class Pap_PatiValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return Pap_Pati.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Pap_Pati papPati = (Pap_Pati) obj;
        if (papPati.getDni() == null || papPati.getDni().trim().equals(""))
            errors.rejectValue("dni", "obligatori", "Cal introduir un valor");
        //
        List<String> valors = Arrays.asList("accepted", "refused", "in progress");
        if (!valors.contains(papPati.getStatus()))
            errors.rejectValue("status", "valor incorrecte",
                    "Deu ser: accepted,refused o in progress");
    }
}
