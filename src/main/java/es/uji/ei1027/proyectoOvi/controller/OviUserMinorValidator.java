package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.OviUser;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

public class OviUserMinorValidator implements Validator {

    private static final String ID_PATTERN = "^([0-9]{8}[a-zA-Z]|[a-zA-Z][0-9]{7}[a-zA-Z]|[a-zA-Z]{3}[0-9]{6})$";

    @Override
    public boolean supports(Class<?> cls) {
        return OviUser.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        OviUser oviUser = (OviUser) obj;

        if (oviUser.getDni() == null || oviUser.getDni().trim().isEmpty()) {
            errors.rejectValue("dni", "obligatorio", "Este campo es obligatorio.");
        } else if (!oviUser.getDni().trim().matches(ID_PATTERN)) {
            errors.rejectValue("dni", "formato_incorrecto", "Formato incorrecto.");
        }

        if (oviUser.getName() == null || oviUser.getName().trim().isEmpty()) {
            errors.rejectValue("name", "obligatorio", "Este campo es obligatorio.");
        }

        if (oviUser.getAddress() == null || oviUser.getAddress().trim().isEmpty())
            errors.rejectValue("address", "obligatorio", "Este campo es obligatorio.");

        if(oviUser.getEmail() == null || oviUser.getEmail().trim().isEmpty())
            errors.rejectValue("email","obligatorio","Este campo es obligatorio.");

        if (oviUser.getTutor_id() == null || oviUser.getTutor_id().trim().isEmpty()) {
            errors.rejectValue("tutor_id", "obligatorio", "Este campo es obligatorio.");
        } else if (!oviUser.getTutor_id().trim().matches(ID_PATTERN)) {
            errors.rejectValue("tutor_id", "formato_incorrecto", "Debe ser un DNI, NIE o Pasaporte válido.");
        }

        if (oviUser.getEntityThatIsInvolved() == null || oviUser.getEntityThatIsInvolved().trim().isEmpty())
            errors.rejectValue("entityThatIsInvolved", "obligatorio", "Este campo es obligatorio.");

        if (oviUser.getTypeOfFunctionalDiversity() == null || oviUser.getTypeOfFunctionalDiversity().trim().isEmpty())
            errors.rejectValue("typeOfFunctionalDiversity", "obligatorio", "Este campo es obligatorio.");

        if (oviUser.getPassword() == null || oviUser.getPassword().trim().isEmpty())
            errors.rejectValue("password", "obligatorio", "Este campo es obligatorio.");

        if (oviUser.getLifePlan() == null || oviUser.getLifePlan().trim().isEmpty())
            errors.rejectValue("lifePlan", "obligatorio", "Este campo es obligatorio.");

        if (oviUser.getDateOfBirth() == null) {
            errors.rejectValue("dateOfBirth", "obligatorio", "Este campo es obligatorio.");
        } else {
            LocalDate today = LocalDate.now();
            LocalDate minDate = today.minusYears(3);
            LocalDate eighteenYearsAgo = today.minusYears(18);

            if (oviUser.getDateOfBirth().isAfter(today)) {
                errors.rejectValue("dateOfBirth", "data_futura", "La fecha no puede ser posterior al día de hoy.");
            } else if (oviUser.getDateOfBirth().isAfter(minDate)) {
                errors.rejectValue("dateOfBirth", "edad_minima", "El usuario debe tener al menos 3 años.");
            } else if (!oviUser.getDateOfBirth().isAfter(eighteenYearsAgo)) {
                errors.rejectValue("dateOfBirth", "edad_maxima", "El usuario debe ser menor de 18 años.");
            }
        }
    }
}