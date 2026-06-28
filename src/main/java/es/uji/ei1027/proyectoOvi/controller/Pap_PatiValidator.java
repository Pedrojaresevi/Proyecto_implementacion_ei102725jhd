package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.Pap_Pati;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class Pap_PatiValidator implements Validator {

    private static final List<String> FORMACIONES = Arrays.asList(
            "Sin formación requerida",
            "Auxiliar de ayuda a domicilio",
            "Técnico en cuidados auxiliares de enfermería",
            "Grado en Enfermería",
            "Grado en Fisioterapia",
            "Grado en Trabajo Social",
            "Grado en Terapia Ocupacional",
            "Certificado en primeros auxilios",
            "Carnet de conducir"
    );

    private static final List<String> EXPERIENCIAS = Arrays.asList(
            "Sin experiencia", "1 año", "2 años", "3 años", "4 años", "5 años o más"
    );

    private static final List<String> MOVILIDADES = Arrays.asList(
            "Albacete", "Alicante", "Castellón", "Valencia", "Madrid", "Barcelona", "Tarragona"
    );

    private static final List<String> SKILLS = Arrays.asList(
            "Lenguaje de signos", "Primeros auxilios", "Manejo de silla de ruedas",
            "Conducción", "Cocina", "Acompañamiento"
    );

    private static final List<String> TIPOS_ASISTENTES = Arrays.asList(
            "PAP", "PATI"
    );

    

    @Override
    public boolean supports(Class<?> cls) {
        return Pap_Pati.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Pap_Pati papPati = (Pap_Pati) obj;

        if (papPati.getDni() == null || papPati.getDni().trim().isEmpty())
            errors.rejectValue("dni", "obligatorio", "Este campo es obligatorio.");

        if (papPati.getName() == null || papPati.getName().trim().isEmpty())
            errors.rejectValue("name", "obligatorio", "Este campo es obligatorio.");

        if (papPati.getSurname() == null || papPati.getSurname().trim().isEmpty())
            errors.rejectValue("surname", "obligatorio", "Este campo es obligatorio.");

        if (papPati.getDateOfBirth() == null) {
            errors.rejectValue("dateOfBirth", "obligatorio", "Este campo es obligatorio.");
        } else {
            LocalDate today = LocalDate.now();
            if (papPati.getDateOfBirth().isAfter(today.minusYears(18))) {
                errors.rejectValue("dateOfBirth", "menor_edad", "Debe tener al menos 18 años para registrarse.");
            } else if (papPati.getDateOfBirth().isBefore(today.minusYears(130))) {
                errors.rejectValue("dateOfBirth", "edad_maxima", "La edad máxima permitida es de 130 años.");
            }
        }

        if (papPati.getAddress() == null || papPati.getAddress().trim().isEmpty())
            errors.rejectValue("address", "obligatorio", "Este campo es obligatorio.");

        if (papPati.getPhone() == null || papPati.getPhone().trim().isEmpty())
            errors.rejectValue("phone", "obligatorio", "Este campo es obligatorio.");

        if (papPati.getEmail() == null || papPati.getEmail().trim().isEmpty())
            errors.rejectValue("email", "obligatorio", "Este campo es obligatorio.");

        if (papPati.getSpecificTraining() == null || papPati.getSpecificTraining().trim().isEmpty()) {
            errors.rejectValue("specificTraining", "obligatorio", "Este campo es obligatorio.");
        } else if (!FORMACIONES.contains(papPati.getSpecificTraining())) {
            errors.rejectValue("specificTraining", "valorIncorrecto", "La formación seleccionada no es válida");
        }

        if (papPati.getTypeOfExperience() == null || papPati.getTypeOfExperience().trim().isEmpty()) {
            errors.rejectValue("typeOfExperience", "obligatorio", "Este campo es obligatorio.");
        } else if (!EXPERIENCIAS.contains(papPati.getTypeOfExperience())) {
            errors.rejectValue("typeOfExperience", "valorIncorrecto", "Tipo de experiencia no válida.");
        }

        if (papPati.getCurriculumVitae() == null || papPati.getCurriculumVitae().trim().isEmpty())
            errors.rejectValue("curriculumVitae", "obligatorio", "Este campo es obligatorio.");

        if (papPati.getPassword() == null || papPati.getPassword().trim().isEmpty())
            errors.rejectValue("password", "obligatorio", "Este campo es obligatorio.");

        if (papPati.getAssistant_type() == null || papPati.getAssistant_type().trim().isEmpty()) {
            errors.rejectValue("assistant_type", "obligatorio", "Este campo es obligatorio.");
        } else if (!TIPOS_ASISTENTES.contains(papPati.getAssistant_type())) {
            errors.rejectValue("assistant_type", "valorIncorrecto", "Este campo es obligatorio.");
        }

        if (papPati.getStartDate() == null) {
            errors.rejectValue("startDate", "obligatorio", "Este campo es obligatorio.");
        }

        if (papPati.getEndDate() == null) {
            errors.rejectValue("endDate", "obligatorio", "Este campo es obligatorio.");
        } else if (papPati.getStartDate() != null && papPati.getEndDate().isBefore(papPati.getStartDate())) {
            errors.rejectValue("endDate", "incoherencia", "La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        if (papPati.getGeographicMobility() == null || papPati.getGeographicMobility().trim().isEmpty()) {
            errors.rejectValue("geographicMobility", "obligatorio", "Este campo es obligatorio.");
        } else if (!MOVILIDADES.contains(papPati.getGeographicMobility())) {
            errors.rejectValue("geographicMobility", "valorIncorrecto", "Movilidad geográfica no válida.");
        }

        if (papPati.getSkills() == null || papPati.getSkills().trim().isEmpty()) {
            errors.rejectValue("skills", "obligatorio", "Este campo es obligatorio.");
        } else {
            for (String skill : papPati.getSkills().split(",")) {
                if (!SKILLS.contains(skill.trim())) {
                    errors.rejectValue("skills", "valorIncorrecto", "Habilidad seleccionada no válida.");
                    break;
                }
            }
        }
    }
}