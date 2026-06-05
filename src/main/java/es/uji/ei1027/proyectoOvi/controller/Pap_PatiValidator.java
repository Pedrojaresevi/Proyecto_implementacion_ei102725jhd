package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.models.Pap_Pati;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

public class Pap_PatiValidator implements Validator {
    // ── Listas de valores permitidos ────────

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
            errors.rejectValue("dni", "obligatori", "Este campo es obligatorio.");

        // CORRECCIÓN: "nombre" -> "name"
        if (papPati.getName() == null || papPati.getName().trim().isEmpty())
            errors.rejectValue("name", "obligatori", "Este campo es obligatorio.");

        // CORRECCIÓN: "apellidos" -> "surname"
        if (papPati.getSurname() == null || papPati.getSurname().trim().isEmpty())
            errors.rejectValue("surname", "obligatori", "Este campo es obligatorio.");

        // CORRECCIÓN: "fecha_nacimiento" -> "dateOfBirth"
        if (papPati.getDateOfBirth() == null)
            errors.rejectValue("dateOfBirth", "obligatori", "Este campo es obligatorio.");

        // CORRECCIÓN: "dirección" -> "address"
        if (papPati.getAddress() == null || papPati.getAddress().trim().isEmpty())
            errors.rejectValue("address", "obligatori", "Este campo es obligatorio.");

        // CORRECCIÓN: Se cambió getAddress() por getPhone() en la validación, y "telefono" -> "phone"
        if (papPati.getPhone() == null || papPati.getPhone().trim().isEmpty())
            errors.rejectValue("phone", "obligatori", "Este campo es obligatorio.");

        if (papPati.getEmail() == null || papPati.getEmail().trim().isEmpty())
            errors.rejectValue("email", "obligatori", "Este campo es obligatorio.");

        // Formación específica
        if (papPati.getSpecificTraining() == null || papPati.getSpecificTraining().trim().isEmpty()) {
            errors.rejectValue("specificTraining", "obligatori", "Este campo es obligatorio.");
        } else if (!FORMACIONES.contains(papPati.getSpecificTraining())) {
            errors.rejectValue("specificTraining", "valorIncorrecte", "La formación seleccionada no es válida");
        }

        // Tipo de experiencia
        if (papPati.getTypeOfExperience() == null || papPati.getTypeOfExperience().trim().isEmpty()) {
            errors.rejectValue("typeOfExperience", "obligatori", "Este campo es obligatorio.");
        } else if (!EXPERIENCIAS.contains(papPati.getTypeOfExperience())) {
            errors.rejectValue("typeOfExperience", "valorIncorrecte", "Tipo de experiencia no válida.");
        }

        // CORRECCIÓN: El atributo se llama "curriculumVitae" no "email"
        if (papPati.getCurriculumVitae() == null || papPati.getCurriculumVitae().trim().isEmpty())
            errors.rejectValue("curriculumVitae", "obligatori", "Este campo es obligatorio.");

        // CORRECCIÓN: "contraseña" -> "password"
        if (papPati.getPassword() == null || papPati.getPassword().trim().isEmpty())
            errors.rejectValue("password", "obligatori", "Este campo es obligatorio.");

        // Tipo de asistente (CORRECCIÓN: "tipo_asistente" -> "assistant_type")
        if (papPati.getAssistant_type() == null || papPati.getAssistant_type().trim().isEmpty()) {
            errors.rejectValue("assistant_type", "obligatori", "Este campo es obligatorio.");
        } else if (!TIPOS_ASISTENTES.contains(papPati.getAssistant_type())) {
            errors.rejectValue("assistant_type", "valorIncorrecte", "Este campo es obligatorio.");
        }

        // CORRECCIÓN: "fecha_inicio" -> "startDate"
        if (papPati.getStartDate() == null)
            errors.rejectValue("startDate", "obligatori", "Este campo es obligatorio.");

        // CORRECCIÓN: "fecha_fin" -> "endDate"
        if (papPati.getEndDate() == null)
            errors.rejectValue("endDate", "obligatori", "Este campo es obligatorio.");

        // Movilidad geográfica
        if (papPati.getGeographicMobility() == null || papPati.getGeographicMobility().trim().isEmpty()) {
            errors.rejectValue("geographicMobility", "obligatori", "Este campo es obligatorio.");
        } else if (!MOVILIDADES.contains(papPati.getGeographicMobility())) {
            errors.rejectValue("geographicMobility", "valorIncorrecte", "Movilidad geográfica no válida.");
        }

        // Habilidades (skills)
        if (papPati.getSkills() == null || papPati.getSkills().trim().isEmpty()) {
            errors.rejectValue("skills", "obligatori", "Este campo es obligatorio.");
        } else {
            for (String skill : papPati.getSkills().split(",")) {
                if (!SKILLS.contains(skill.trim())) {
                    errors.rejectValue("skills", "valorIncorrecte", "Habilidad seleccionada no válida.");
                    break;
                }
            }
        }
    }
}