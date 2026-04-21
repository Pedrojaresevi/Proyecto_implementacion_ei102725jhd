package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.AssignmentRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class AssignmentRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return AssignmentRequestController.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        AssignmentRequest assignmentRequest = (AssignmentRequest) obj;
        if (assignmentRequest.getRequest_Id().trim().equals(""))
            errors.rejectValue("request_Id", "obligatori", "Cal introduir un valor");
    }
}
