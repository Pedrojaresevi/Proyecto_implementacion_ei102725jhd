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
        if (oviUser.getDni() == null || oviUser.getDni().trim().isEmpty()) {
            errors.rejectValue("dni", "obligatori", "Este campo es obligatorio.");
        } else if (oviUser.getDni().trim().length() != 9) {
            // Añadimos una pequeña validación de tamaño para que sea más robusto
            errors.rejectValue("dni", "format_incorrecte", "Documento no válido.");
        }

        if (oviUser.getName() == null || oviUser.getName().trim().isEmpty()) {
            errors.rejectValue("name", "obligatori", "Este campo es obligatorio.");
        }

        if (oviUser.getAddress() == null || oviUser.getAddress().trim().isEmpty())
            errors.rejectValue("address", "obligatori", "Este campo es obligatorio.");

        if(oviUser.getEmail() == null || oviUser.getEmail().trim().isEmpty())
            errors.rejectValue("email","obligatori","Este campo es obligatorio.");

        // Validación del Tutor ID (AHORA ES OPCIONAL)
        // Solo validamos si el campo NO está vacío (es decir, el usuario es menor y ha puesto un DNI de tutor)
        if (oviUser.getTutor_id() != null && !oviUser.getTutor_id().trim().isEmpty()) {
            if (oviUser.getTutor_id().trim().length() != 9) {
                errors.rejectValue("tutor_id", "format_incorrecte", "Documento no válido.");
            }
        }

        if (oviUser.getEntityThatIsInvolved() == null || oviUser.getEntityThatIsInvolved().trim().isEmpty())
            errors.rejectValue("entityThatIsInvolved", "obligatori", "Este campo es obligatorio.");

        if (oviUser.getTypeOfFunctionalDiversity() == null || oviUser.getTypeOfFunctionalDiversity().trim().isEmpty())
            errors.rejectValue("typeOfFunctionalDiversity", "obligatori", "Este campo es obligatorio.");

        if (oviUser.getDateOfAcceptance() == null)
            errors.rejectValue("dateOfAcceptance", "obligatori", "Este campo es obligatorio.");

        if (oviUser.getPassword() == null || oviUser.getPassword().trim().isEmpty())
            errors.rejectValue("password", "obligatori", "Este campo es obligatorio.");

        if (oviUser.getLifePlan() == null || oviUser.getLifePlan().trim().isEmpty())
            errors.rejectValue("lifePlan", "obligatori", "Este campo es obligatorio.");

        if (oviUser.getDateOfBirth() == null)
            errors.rejectValue("dateOfBirth", "obligatori", "Este campo es obligatorio.");

    }
}