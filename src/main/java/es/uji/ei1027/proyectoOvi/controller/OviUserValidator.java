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

        // Validación DNI del usuario (Obligatorio)
        if (oviUser.getDni() == null || oviUser.getDni().trim().equals("")) {
            errors.rejectValue("dni", "obligatori", "Cal introduir un valor");
        } else if (oviUser.getDni().trim().length() != 9) {
            // Añadimos una pequeña validación de tamaño para que sea más robusto
            errors.rejectValue("dni", "format_incorrecte", "El DNI/Id debe tener 9 caracteres");
        }

        // Validación del Estatus
        List<String> valors = Arrays.asList("accepted", "refused", "in progress");
        if (!valors.contains(oviUser.getStatus())) {
            errors.rejectValue("status", "valor incorrecte", "Deu ser: accepted, refused o in progress");
        }

        // Validación del Tutor ID (AHORA ES OPCIONAL)
        // Solo validamos si el campo NO está vacío (es decir, el usuario es menor y ha puesto un DNI de tutor)
        if (oviUser.getTutor_id() != null && !oviUser.getTutor_id().trim().isEmpty()) {
            if (oviUser.getTutor_id().trim().length() != 9) {
                errors.rejectValue("tutor_id", "format_incorrecte", "El DNI del tutor debe tener 9 caracteres");
            }
        }
    }
}