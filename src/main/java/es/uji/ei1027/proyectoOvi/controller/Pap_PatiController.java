package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.PapPatiDao;
import es.uji.ei1027.proyectoOvi.models.Pap_Pati;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/pap_pati")
public class Pap_PatiController {
    private PapPatiDao pap_patiDao;

    @Autowired
    public void setPap_patiDao(PapPatiDao pap_patiDao){
        this.pap_patiDao = pap_patiDao;
    }

    @RequestMapping("/list")
    public String listAllPap_Pati(Model model){
        model.addAttribute("allpap_pati", pap_patiDao.getAllPap_Pati());
        return "pap_pati/list";
    }

    @RequestMapping(value="/add")
    public String addPapPati(Model model) {
        model.addAttribute("pap_pati", new Pap_Pati());
        return "pap_pati/registrarPapPati";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("pap_pati") Pap_Pati papPati,
                                   BindingResult bindingResult) {

        papPati.setStatus("in progress");

        Pap_PatiValidator pap_patiValidator = new Pap_PatiValidator();
        pap_patiValidator.validate(papPati, bindingResult);

        if (bindingResult.hasErrors()) {
            return "pap_pati/registrarPapPati";
        }

        try {
            pap_patiDao.addPap_Pati(papPati);
        } catch (DuplicateKeyException e) {
            bindingResult.rejectValue("dni", "duplicat", "Ya existe un Pap/Pati con este DNI");
            return "pap_pati/registrarPapPati";
        }
        return "redirect:/";
    }

    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editPapPati(Model model, @PathVariable String id) {
        model.addAttribute("pap_pati", pap_patiDao.getPap_Pati(id));
        List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
        model.addAttribute("statusList", statusList);
        return "pap_pati/update";
    }

    @RequestMapping(value="/update", method=RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("pap_pati") Pap_Pati papPati,
                                      BindingResult bindingResult, Model model) {
        Pap_PatiValidator pap_patiValidator = new Pap_PatiValidator();
        pap_patiValidator.validate(papPati, bindingResult);

        if (bindingResult.hasErrors()) {
            return "pap_pati/update";
        }
        pap_patiDao.updatePap_Pati(papPati);

        return "redirect:list";
    }

    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable String id) {
        pap_patiDao.deletePap_Pati(id);
        return "redirect:../list";
    }
    @ModelAttribute("provincias")
    public List<String> getProvincias() {
        return Arrays.asList(
                "Albacete", "Alicante", "Castellón", "Valencia", "Madrid", "Barcelona", "Tarragona"
        );
    }

    @ModelAttribute("anyosExperiencia")
    public List<String> getRequiredExperience() {
        return Arrays.asList(
                "Sin experiencia", "1 año", "2 años", "3 años", "4 años", "5 años o más"
        );
    }

    @ModelAttribute("formacionesDisponibles")
    public List<String> getRequiredTraining() {
        return Arrays.asList(
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
    }

    @ModelAttribute("skillsDisponibles")
    public List<String> getSkillsDisponibles() {
        return Arrays.asList(
                "Lenguaje de signos", "Primeros auxilios", "Manejo de silla de ruedas", "Conducción", "Cocina", "Acompañamiento"
        );
    }

//    // --- RUTAS DEL TÉCNICO ---
//
//    @RequestMapping(value="/accept/{dni}", method = RequestMethod.GET)
//    public String confirmAccept(Model model, @PathVariable String dni) {
//        model.addAttribute("pap_pati", pap_patiDao.getPap_Pati(dni));
//        return "technician/pap_pati/accept";
//    }
//
//    @RequestMapping(value="/accept/execute/{dni}")
//    public String executeAccept(@PathVariable String dni) {
//        Pap_Pati papPati = pap_patiDao.getPap_Pati(dni);
//        if (papPati != null && "in progress".equals(papPati.getStatus())) {
//            papPati.setStatus("accepted");
//            pap_patiDao.updatePap_Pati(papPati);
//        }
//        return "redirect:/pap_pati/list";
//    }
//
//    @RequestMapping(value="/reject/{dni}", method = RequestMethod.GET)
//    public String confirmReject(Model model, @PathVariable String dni) {
//        model.addAttribute("pap_pati", pap_patiDao.getPap_Pati(dni));
//        return "technician/pap_pati/reject";
//    }
//
//    @RequestMapping(value="/reject/execute/{dni}", method = RequestMethod.POST)
//    public String executeReject(@PathVariable String dni, @RequestParam("rejectReason") String rejectReason) {
//        Pap_Pati papPati = pap_patiDao.getPap_Pati(dni);
//        if (papPati != null && "in progress".equals(papPati.getStatus())) {
//            papPati.setStatus("refused");
//            pap_patiDao.updatePap_Pati(papPati);
//            // Aquí podríais guardar el rejectReason en la BD si tuvierais un campo para ello
//            System.out.println("Candidato " + dni + " rechazado. Motivo: " + rejectReason);
//        }
//        return "redirect:/pap_pati/list";
//    }

    @RequestMapping(value="/manage/{dni}", method = RequestMethod.GET)
    public String managePapPati(Model model, @PathVariable String dni) {
        model.addAttribute("pap_pati", pap_patiDao.getPap_Pati(dni));
        return "technician/pap_pati/manage";
    }
}
