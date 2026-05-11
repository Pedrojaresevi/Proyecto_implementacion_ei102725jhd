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
        if (tutor.getDni() == null || tutor.getDni().trim().equals(""))
            errors.rejectValue("dni", "obligatori",
                    "Debe introducir un DNI");
        //

        if (tutor.getName() == null || tutor.getName().trim().equals(""))
            errors.rejectValue("name", "obligatori",
                    "Debe introducir un nombre");

        if (tutor.getEmail() == null || tutor.getEmail().trim().equals(""))
            errors.rejectValue("email", "obligatori",
                    "Debe introducir un email");

        if (tutor.getPassword() == null || tutor.getPassword().trim().equals(""))
            errors.rejectValue("password", "obligatori",
                    "Debe introducir una contraseña");
    }
}
