package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.Pap_Pati;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

public class Pap_PatiValidator implements Validator {
    // ── Listas de valores permitidos (idénticas a las del controlador) ────────

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
            "Local", "Provincial", "Total"
    );

    private static final List<String> SKILLS = Arrays.asList(
            "Lenguaje de signos", "Primeros auxilios", "Manejo de silla de ruedas",
            "Conducción", "Cocina", "Acompañamiento"
    );

    private static final List<String> ESTADOS = Arrays.asList(
            "accepted", "refused", "in progress"
    );

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public boolean supports(Class<?> cls) {
        return Pap_Pati.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Pap_Pati papPati = (Pap_Pati) obj;
        // DNI obligatorio
        if (papPati.getDni() == null || papPati.getDni().trim().isEmpty())
            errors.rejectValue("dni", "obligatori", "Cal introduir un valor");

        // Estado
        if (papPati.getStatus() == null || !ESTADOS.contains(papPati.getStatus()))
            errors.rejectValue("status", "valorIncorrecte",
                    "Deu ser: accepted, refused o in progress");

        // Formación específica
        if (papPati.getSpecificTraining() == null || papPati.getSpecificTraining().trim().isEmpty()) {
            errors.rejectValue("specificTraining", "obligatori",
                    "Cal seleccionar una formació");
        } else if (!FORMACIONES.contains(papPati.getSpecificTraining())) {
            errors.rejectValue("specificTraining", "valorIncorrecte",
                    "La formació seleccionada no és vàlida");
        }

        // Tipo de experiencia
        if (papPati.getTypeOfExperience() == null || papPati.getTypeOfExperience().trim().isEmpty()) {
            errors.rejectValue("typeOfExperience", "obligatori",
                    "Cal seleccionar els anys d'experiència");
        } else if (!EXPERIENCIAS.contains(papPati.getTypeOfExperience())) {
            errors.rejectValue("typeOfExperience", "valorIncorrecte",
                    "El valor d'experiència seleccionat no és vàlid");
        }

        // Movilidad geográfica
        if (papPati.getGeographicMobility() == null || papPati.getGeographicMobility().trim().isEmpty()) {
            errors.rejectValue("geographicMobility", "obligatori",
                    "Cal seleccionar la mobilitat geogràfica");
        } else if (!MOVILIDADES.contains(papPati.getGeographicMobility())) {
            errors.rejectValue("geographicMobility", "valorIncorrecte",
                    "La mobilitat seleccionada no és vàlida");
        }

        // Habilidades (skills) — se almacena como String con los valores separados por coma
        // Basta con que no esté vacío y que cada valor individual sea válido
        if (papPati.getSkills() == null || papPati.getSkills().trim().isEmpty()) {
            errors.rejectValue("skills", "obligatori",
                    "Cal seleccionar almenys una habilitat");
        } else {
            for (String skill : papPati.getSkills().split(",")) {
                if (!SKILLS.contains(skill.trim())) {
                    errors.rejectValue("skills", "valorIncorrecte",
                            "Una de les habilitats seleccionades no és vàlida");
                    break;
                }
            }
        }
    }
}
