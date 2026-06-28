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


        if (assignmentRequest.getRequest_Id() == null || assignmentRequest.getRequest_Id().trim().equals("")) {
            errors.rejectValue("request_Id", "obligatorio", "Este campo es obligatorio.");
        }

        if (assignmentRequest.getServiceLocation() == null || assignmentRequest.getServiceLocation().trim().equals("")) {
            errors.rejectValue("serviceLocation", "obligatorio", "Este campo es obligatorio.");
        }

        if (assignmentRequest.getRequiredExperience() == null || assignmentRequest.getRequiredExperience().trim().equals("")) {
            errors.rejectValue("requiredExperience", "obligatorio", "Este campo es obligatorio.");
        }

        if (assignmentRequest.getRequiredTraining() == null || assignmentRequest.getRequiredTraining().trim().equals("")) {
            errors.rejectValue("requiredTraining", "obligatorio", "Este campo es obligatorio.");
        }

        if (assignmentRequest.getTypeOfService() == null || assignmentRequest.getTypeOfService().trim().equals("")) {
            errors.rejectValue("typeOfService", "obligatorio", "Este campo es obligatorio.");
        }

        if (assignmentRequest.getRequiredStartAvailability() == null) {
            errors.rejectValue("requiredStartAvailability", "obligatorio", "Este campo es obligatorio.");
        }

        if (assignmentRequest.getRequiredEndAvailability() == null) {
            errors.rejectValue("requiredEndAvailability", "obligatorio", "Este campo es obligatorio.");
        }

        if (assignmentRequest.getRequiredStartAvailability() != null) {
            if (assignmentRequest.getRequiredStartAvailability().isBefore(LocalDate.now())) {
                errors.rejectValue("requiredStartAvailability", "dataPasada", "La fecha de inicio no puede ser anterior a hoy.");
            }
        }

        if (assignmentRequest.getRequiredEndAvailability() != null) {
            if (assignmentRequest.getRequiredEndAvailability().isBefore(LocalDate.now().plusDays(1))) {
                errors.rejectValue("requiredEndAvailability", "dataPasada", "La fecha de fin debe de ser como mínimo mañana.");
            }
        }

        if (assignmentRequest.getRequiredStartAvailability() != null && assignmentRequest.getRequiredEndAvailability() != null) {
            if (assignmentRequest.getRequiredEndAvailability().isBefore(assignmentRequest.getRequiredStartAvailability())) {
                errors.rejectValue("requiredEndAvailability", "dataInvalida", "La fecha de fin debe de ser posterior a la de inicio.");
            }
        }

        if (assignmentRequest.getRequiredSkills() == null || assignmentRequest.getRequiredSkills().trim().isEmpty()) {
            errors.rejectValue("requiredSkills", "obligatorio", "Este campo es obligatorio.");
        }

        boolean hasOviuser = assignmentRequest.getOviuser_id() != null && !assignmentRequest.getOviuser_id().trim().equals("");
        boolean hasTutor = assignmentRequest.getTutor_id() != null && !assignmentRequest.getTutor_id().trim().equals("");

        if (!hasOviuser && !hasTutor) {
            errors.reject("obligatorio", "Este campo es obligatorio.");
        }
    }
}
