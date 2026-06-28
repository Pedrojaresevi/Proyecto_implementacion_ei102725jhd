package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.ListOfProposedCandidates;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

public class ListOfProposedCandidatesValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return ListOfProposedCandidates.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        ListOfProposedCandidates listOfProposedCandidates = (ListOfProposedCandidates) obj;
        if (listOfProposedCandidates.getList_id() == null || listOfProposedCandidates.getList_id().trim().equals(""))
            errors.rejectValue("list_id", "obligatorio",
                    "Hay que introducir un valor");
        
        if (listOfProposedCandidates.getSuitabilityScore() < 0 || listOfProposedCandidates.getSuitabilityScore() > 100)
            errors.rejectValue("suitabilityScore", "valor incorrecto",
                    "La puntuación tiene que estar entre 0 y 100");
        
        if (listOfProposedCandidates.getPappati_id() == null || listOfProposedCandidates.getPappati_id().trim().equals(""))
            errors.rejectValue("pappati_id","obligatorio",
                    "Hay que introducir un valor");
        if (listOfProposedCandidates.getRequest_id() == null || listOfProposedCandidates.getRequest_id().trim().equals(""))
            errors.rejectValue("request_id","obligatorio",
                    "Hay que introducir un valor");

        if (listOfProposedCandidates.getProposalDate() == null)
            errors.rejectValue("proposalDate", "obligatorio", "Hay que introducir una fecha de propuesta");

    }
}
