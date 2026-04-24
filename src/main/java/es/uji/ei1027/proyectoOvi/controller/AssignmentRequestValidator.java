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
