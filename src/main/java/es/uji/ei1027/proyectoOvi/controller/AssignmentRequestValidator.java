package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.AssignmentRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class AssignmentRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return AssignmentRequest.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        AssignmentRequest assignmentRequest = (AssignmentRequest) obj;


        // 1. Validación del ID
        if (assignmentRequest.getRequest_Id() == null || assignmentRequest.getRequest_Id().trim().equals("")) {
            errors.rejectValue("request_Id", "obligatori", "Este campo es obligatorio.");
        }

        // Validación de la ubicación
        if (assignmentRequest.getServiceLocation() == null || assignmentRequest.getServiceLocation().trim().equals("")) {
            errors.rejectValue("serviceLocation", "obligatori", "Este campo es obligatorio.");
        }

        // Validación de la experiencia
        if (assignmentRequest.getRequiredExperience() == null || assignmentRequest.getRequiredExperience().trim().equals("")) {
            errors.rejectValue("requiredExperience", "obligatori", "Este campo es obligatorio.");
        }

        //Validación formación requerida
        if (assignmentRequest.getRequiredTraining() == null || assignmentRequest.getRequiredTraining().trim().equals("")) {
            errors.rejectValue("requiredTraining", "obligatori", "Este campo es obligatorio.");
        }

        //Validación de tipo de servicio
        if (assignmentRequest.getTypeOfService() == null || assignmentRequest.getTypeOfService().trim().equals("")) {
            errors.rejectValue("typeOfService", "obligatori", "Este campo es obligatorio.");
        }

        // Validación de la fecha de inicio
        if (assignmentRequest.getRequiredStartAvailability() == null) {
            errors.rejectValue("requiredStartAvailability", "obligatori", "Este campo es obligatorio.");
        }

        // Validación de la fecha de fin
        if (assignmentRequest.getRequiredEndAvailability() == null) {
            errors.rejectValue("requiredEndAvailability", "obligatori", "Este campo es obligatorio.");
        }

        // Validación de que la fecha de inicio no sea anterior a hoy
        if (assignmentRequest.getRequiredStartAvailability() != null) {
            if (assignmentRequest.getRequiredStartAvailability().isBefore(LocalDate.now())) {
                errors.rejectValue("requiredStartAvailability", "dataPasada", "La fecha de inicio no puede ser anterior a hoy.");
            }
        }

        // Validación de que la fecha de fin sea al menos mañana
        if (assignmentRequest.getRequiredEndAvailability() != null) {
            if (assignmentRequest.getRequiredEndAvailability().isBefore(LocalDate.now().plusDays(1))) {
                errors.rejectValue("requiredEndAvailability", "dataPasada", "La fecha de fin debe de ser como mínimo mañana.");
            }
        }

        // Validación de que la fecha de fin sea posterior a la de inicio
        if (assignmentRequest.getRequiredStartAvailability() != null && assignmentRequest.getRequiredEndAvailability() != null) {
            if (assignmentRequest.getRequiredEndAvailability().isBefore(assignmentRequest.getRequiredStartAvailability())) {
                errors.rejectValue("requiredEndAvailability", "dataInvalida", "La fecha de fin debe de ser posterior a la de inicio.");
            }
        }

        // Validación de las habilidades requeridas
        if (assignmentRequest.getRequiredSkills() == null || assignmentRequest.getRequiredSkills().trim().isEmpty()) {
            errors.rejectValue("requiredSkills", "obligatori", "Este campo es obligatorio.");
        }

        // 3. Validación: debe haber al menos oviuser o tutor
        boolean hasOviuser = assignmentRequest.getOviuser_id() != null && !assignmentRequest.getOviuser_id().trim().equals("");
        boolean hasTutor = assignmentRequest.getTutor_id() != null && !assignmentRequest.getTutor_id().trim().equals("");

        if (!hasOviuser && !hasTutor) {
            errors.reject("obligatori", "Este campo es obligatorio.");
        }
    }
}
