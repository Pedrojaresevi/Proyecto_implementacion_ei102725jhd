package es.uji.ei1027.proyectoOvi.controller;


import es.uji.ei1027.proyectoOvi.models.Tutor;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

public class TutorValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return Tutor.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Tutor tutor = (Tutor) obj;
        if (tutor.getDni() == null || tutor.getDni().trim().isEmpty())
            errors.rejectValue("dni", "obligatori", "Este campo es obligatorio.");

        if (tutor.getName() == null || tutor.getName().trim().isEmpty())
            errors.rejectValue("name", "obligatori", "Este campo es obligatorio.");

        if (tutor.getEmail() == null || tutor.getEmail().trim().isEmpty())
            errors.rejectValue("email", "obligatori", "Este campo es obligatorio.");

        if (tutor.getPassword() == null || tutor.getPassword().trim().isEmpty())
            errors.rejectValue("password", "obligatori", "Este campo es obligatorio.");
    }
}
