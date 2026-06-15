package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.AssignmentRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

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
            errors.rejectValue("request_Id", "obligatori", "Cal introduir un valor");
        }

        // 2. Validación del Estado
        List<String> valors = Arrays.asList("accepted", "refused", "in progress");
        if (!valors.contains(assignmentRequest.getStatus())) {
            errors.rejectValue("status", "valor incorrecte", "Deu ser: accepted, refused o in progress");
        }

        // Validación de la ubicación
        if (assignmentRequest.getServiceLocation() == null || assignmentRequest.getServiceLocation().trim().equals("")) {
            errors.rejectValue("serviceLocation", "obligatori", "Cal seleccionar una província");
        }

        // Validación de la experiencia
        if (assignmentRequest.getRequiredExperience() == null || assignmentRequest.getRequiredExperience().trim().equals("")) {
            errors.rejectValue("requiredExperience", "obligatori", "Cal seleccionar els anys d'experiència");
        }

        //Validación formación requerida
        if (assignmentRequest.getRequiredTraining() == null || assignmentRequest.getRequiredTraining().trim().equals("")) {
            errors.rejectValue("requiredTraining", "obligatori", "Hay que seleccionar la formación requerida");
        }

        //Validación de tipo de servicio
        if (assignmentRequest.getTypeOfService() == null || assignmentRequest.getTypeOfService().trim().equals("")) {
            errors.rejectValue("typeOfService", "obligatori", "Hay que seleccionar el tipo de servicio");
        }

        // Validación de la fecha de inicio
        if (assignmentRequest.getRequiredStartAvailability() == null) {
            errors.rejectValue("requiredStartAvailability", "obligatori", "Cal introduir una data d'inici");
        }

        // Validación de la fecha de fin
        if (assignmentRequest.getRequiredEndAvailability() == null) {
            errors.rejectValue("requiredEndAvailability", "obligatori", "Cal introduir una data de fi");
        }

        // Validación de que la fecha de fin sea posterior a la de inicio
        if (assignmentRequest.getRequiredStartAvailability() != null && assignmentRequest.getRequiredEndAvailability() != null) {
            if (assignmentRequest.getRequiredEndAvailability().isBefore(assignmentRequest.getRequiredStartAvailability())) {
                errors.rejectValue("requiredEndAvailability", "dataInvalida", "La data de fi ha de ser posterior a la data d'inici");
            }
        }

        // Validación de las habilidades requeridas
        if (assignmentRequest.getRequiredSkills() == null || assignmentRequest.getTypeOfService().trim().isEmpty()) {
            errors.rejectValue("requiredSkills", "obligatori", "Hay que introducir al menos una skill");
        }

        // 3. Validación exclusiva Oviuser / Tutor
        boolean hasOviuser = assignmentRequest.getOviuser_id() != null && !assignmentRequest.getOviuser_id().trim().equals("");
        boolean hasTutor = assignmentRequest.getTutor_id() != null && !assignmentRequest.getTutor_id().trim().equals("");

        if (!hasOviuser && !hasTutor) {
            // Usamos reject global para que el mensaje no salga dos veces
            errors.reject("obligatori", "Cal assignar la petició a un oviuser o a un tutor");
        } else if (hasOviuser && hasTutor) {
            // Usamos reject global para que el mensaje no salga dos veces
            errors.reject("exclusiu", "La petició no pot ser d'un oviuser i d'un tutor alhora");
        }
    }
}
