package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.OviUser;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

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

        // Validación Nombre (Obligatorio)
        if (oviUser.getName() == null || oviUser.getName().trim().isEmpty()) {
            errors.rejectValue("name", "obligatori", "Este campo es obligatorio.");
        }

        // Validación Dirección (Obligatorio)
        if (oviUser.getAddress() == null || oviUser.getAddress().trim().isEmpty()) {
            errors.rejectValue("address", "obligatori", "Este campo es obligatorio.");
        }

        // Validación Email (Obligatorio)
        if (oviUser.getEmail() == null || oviUser.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "obligatori", "Este campo es obligatorio.");
        }

        // --- VALIDACIÓN DE FECHA DE NACIMIENTO Y CÁLCULO DE MINORÍA DE EDAD ---
        boolean esMenorDeEdad = false;

        if (oviUser.getDateOfBirth() == null) {
            errors.rejectValue("dateOfBirth", "obligatori", "Este campo es obligatorio.");
        } else {
            LocalDate today = LocalDate.now();
            LocalDate minDate = today.minusYears(3);
            LocalDate maxDate = today.minusYears(130);
            LocalDate fechaMayoriaEdad = today.minusYears(18); // Fecha límite exacta para tener 18 años

            if (oviUser.getDateOfBirth().isAfter(today)) {
                errors.rejectValue("dateOfBirth", "data_futura", "La fecha no puede ser posterior al día de hoy.");
            } else if (oviUser.getDateOfBirth().isAfter(minDate)) {
                errors.rejectValue("dateOfBirth", "edat_minima", "El usuario debe tener al menos 3 años.");
            } else if (oviUser.getDateOfBirth().isBefore(maxDate)) {
                errors.rejectValue("dateOfBirth", "edat_maxima", "La edad máxima permitida es de 130 años.");
            }

            // Si la fecha es válida pero posterior a 'fechaMayoriaEdad', el usuario tiene menos de 18 años
            if (oviUser.getDateOfBirth().isAfter(fechaMayoriaEdad) && oviUser.getDateOfBirth().isBefore(today)) {
                esMenorDeEdad = true;
            }
        }

        // --- VALIDACIÓN DEL TUTOR ID (CONDICIONAL) ---
        if (esMenorDeEdad) {
            // Si el sistema detecta que es menor de edad, el Tutor pasa a ser OBLIGATORIO
            if (oviUser.getTutor_id() == null || oviUser.getTutor_id().trim().isEmpty()) {
                errors.rejectValue("tutor_id", "obligatori_menor", "El documento del tutor es obligatorio para menores de edad.");
            } else if (!oviUser.getTutor_id().trim().matches(ID_PATTERN)) {
                errors.rejectValue("tutor_id", "format_incorrecte", "Debe ser un DNI, NIE o Pasaporte válido.");
            }
        } else {
            // Si es mayor de edad, el campo sigue siendo OPCIONAL (solo se valida formato si decide rellenarlo)
            if (oviUser.getTutor_id() != null && !oviUser.getTutor_id().trim().isEmpty()) {
                if (!oviUser.getTutor_id().trim().matches(ID_PATTERN)) {
                    errors.rejectValue("tutor_id", "format_incorrecte", "Debe ser un DNI, NIE o Pasaporte válido.");
                }
            }
        }

        // Validación Entidad Vinculada (Obligatorio)
        if (oviUser.getEntityThatIsInvolved() == null || oviUser.getEntityThatIsInvolved().trim().isEmpty()) {
            errors.rejectValue("entityThatIsInvolved", "obligatori", "Este campo es obligatorio.");
        }

        // Validación Tipo de Diversidad (Obligatorio)
        if (oviUser.getTypeOfFunctionalDiversity() == null || oviUser.getTypeOfFunctionalDiversity().trim().isEmpty()) {
            errors.rejectValue("typeOfFunctionalDiversity", "obligatori", "Este campo es obligatorio.");
        }

        // Validación Contraseña (Obligatorio)
        if (oviUser.getPassword() == null || oviUser.getPassword().trim().isEmpty()) {
            errors.rejectValue("password", "obligatori", "Este campo es obligatorio.");
        }

        // Validación Plan de Vida (Obligatorio)
        if (oviUser.getLifePlan() == null || oviUser.getLifePlan().trim().isEmpty()) {
            errors.rejectValue("lifePlan", "obligatori", "Este campo es obligatorio.");
        }
    }
}