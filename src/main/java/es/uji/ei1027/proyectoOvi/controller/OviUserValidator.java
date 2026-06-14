package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.OviUser;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class OviUserValidator implements Validator {

    // Definimos el patrón que acepta DNI, NIE o Pasaporte
    private static final String ID_PATTERN = "^([0-9]{8}[a-zA-Z]|[a-zA-Z][0-9]{7}[a-zA-Z]|[a-zA-Z]{3}[0-9]{6})$";

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
        } else if (!oviUser.getDni().trim().matches(ID_PATTERN)) {
            errors.rejectValue("dni", "format_incorrecte", "Formato incorrecto.");
        }

        if (oviUser.getName() == null || oviUser.getName().trim().isEmpty()) {
            errors.rejectValue("name", "obligatori", "Este campo es obligatorio.");
        }

        if (oviUser.getAddress() == null || oviUser.getAddress().trim().isEmpty())
            errors.rejectValue("address", "obligatori", "Este campo es obligatorio.");

        if(oviUser.getEmail() == null || oviUser.getEmail().trim().isEmpty())
            errors.rejectValue("email","obligatori","Este campo es obligatorio.");

        // Validación del Tutor ID (AHORA ES OPCIONAL)
        // Validamos con el mismo patrón si el usuario ha introducido datos
        if (oviUser.getTutor_id() != null && !oviUser.getTutor_id().trim().isEmpty()) {
            if (!oviUser.getTutor_id().trim().matches(ID_PATTERN)) {
                errors.rejectValue("tutor_id", "format_incorrecte", "Debe ser un DNI, NIE o Pasaporte válido.");
            }
        }

        if (oviUser.getEntityThatIsInvolved() == null || oviUser.getEntityThatIsInvolved().trim().isEmpty())
            errors.rejectValue("entityThatIsInvolved", "obligatori", "Este campo es obligatorio.");

        if (oviUser.getTypeOfFunctionalDiversity() == null || oviUser.getTypeOfFunctionalDiversity().trim().isEmpty())
            errors.rejectValue("typeOfFunctionalDiversity", "obligatori", "Este campo es obligatorio.");

        if (oviUser.getPassword() == null || oviUser.getPassword().trim().isEmpty())
            errors.rejectValue("password", "obligatori", "Este campo es obligatorio.");

        if (oviUser.getLifePlan() == null || oviUser.getLifePlan().trim().isEmpty())
            errors.rejectValue("lifePlan", "obligatori", "Este campo es obligatorio.");

        if (oviUser.getDateOfBirth() == null)
            errors.rejectValue("dateOfBirth", "obligatori", "Este campo es obligatorio.");

    }
}