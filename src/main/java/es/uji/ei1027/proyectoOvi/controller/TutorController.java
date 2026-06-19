package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.OviUserDao;
import es.uji.ei1027.proyectoOvi.dao.TutorDao;
import es.uji.ei1027.proyectoOvi.models.OviUser;
import es.uji.ei1027.proyectoOvi.models.Tutor;
import es.uji.ei1027.proyectoOvi.models.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.jasypt.util.password.BasicPasswordEncryptor;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/tutor")
public class TutorController {
    private TutorDao tutorDao;
    private OviUserDao oviUserDao;

    @Autowired
    public void setTutorDao(TutorDao tutorDao){
        this.tutorDao = tutorDao;
    }

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao){
        this.oviUserDao = oviUserDao;
    }

    @RequestMapping("/list")
    public String listTutors(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        model.addAttribute("tutors", tutorDao.getTutorsPaginated(pageSize, offset));

        int totalItems = tutorDao.countTutors();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "tutor/list";
    }
    //
    @RequestMapping(value="/add")
    public String addTutor(Model model) {
        model.addAttribute("tutor", new Tutor());
        return "tutor/add";
    }
    //

    //
    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("tutor") Tutor tutor,
                                   BindingResult bindingResult) {
        tutor.setStatus("in progress");
        TutorValidator tutorValidator = new TutorValidator();
        tutorValidator.validate(tutor, bindingResult);

        if (bindingResult.hasErrors()) {
            return "tutor/add";
        }

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String contrasenaEncriptada = passwordEncryptor.encryptPassword(tutor.getPassword());
        tutor.setPassword(contrasenaEncriptada);

        try {
            tutorDao.addTutor(tutor);
        } catch (DuplicateKeyException e) {
            bindingResult.rejectValue("dni", "duplicat",
                    "Ya existe un tutor con este DNI");
            return "tutor/add";
        }

        return "redirect:/";
    }
    //
    @RequestMapping(value="/update/{dni}", method = RequestMethod.GET)
    public String editTutor(Model model, @PathVariable String dni) {
        model.addAttribute("tutor", tutorDao.getTutor(dni));
        List<String> statusList = Arrays.asList("accepted", "refused", "in progress");
        model.addAttribute("statusList", statusList);
        return "tutor/update";
    }
    //
    @RequestMapping(value="/update", method=RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("tutor") Tutor tutor,
                                      BindingResult bindingResult, Model model) {
        TutorValidator tutorValidator = new TutorValidator();
        tutorValidator.validate(tutor, bindingResult);

        if (bindingResult.hasErrors()) {
            return "tutor/update";
        }
        tutorDao.updateTutor(tutor);

        return "redirect:/tutor/accepted";
    }

    // 1. Muestra la pantalla roja de confirmación (GET)
    @RequestMapping(value="/delete/{dni}", method = RequestMethod.GET)
    public String showDeleteConfirmation(Model model, @PathVariable String dni) {
        model.addAttribute("dni", dni);
        // Importante: ruta apuntando a technician/tutor
        return "technician/tutor/confirmarborrado";
    }

    // 2. Ejecuta el borrado real al confirmar en el formulario (POST)
    @RequestMapping(value="/delete/{dni}", method = RequestMethod.POST)
    public String processDelete(@PathVariable String dni) {
        tutorDao.deleteTutor(dni);
        // Redirige a la lista de tutores aceptados tras borrar
        return "redirect:/tutor/accepted";
    }

    @RequestMapping("/users/{dni}")
    public String listMinors(Model model, @PathVariable String dni, @RequestParam(defaultValue = "1") int page) {
        model.addAttribute("tutor", tutorDao.getTutor(dni));

        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        model.addAttribute("oviUsers", oviUserDao.getOviUsersByTutorPaginated(dni, pageSize, offset));

        int totalItems = oviUserDao.countOviUsersByTutor(dni);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "tutor/list_minors";
    }
    // MÉTODOS PARA AÑADIR MENORES DESDE EL TUTOR

    @RequestMapping(value="/add-minor", method = RequestMethod.GET)
    public String addMinor(Model model, HttpSession session) {
        // Recuperamos el usuario conectado de la sesión
        UserDetails user = (UserDetails) session.getAttribute("user");

        // Si no hay usuario o no tiene permisos, lo mandamos al login
        if (user == null) {
            return "redirect:/login";
        }

        OviUser oviUser = new OviUser();


        model.addAttribute("oviUser", oviUser);
        return "technician/add_minor";
    }

    @RequestMapping(value="/add-minor", method = RequestMethod.POST)
    public String processAddMinorSubmit(@ModelAttribute("oviUser") OviUser oviUser,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "tutor/add-minor";

        oviUser.setStatus("accepted");

        oviUserDao.addOviUser(oviUser);

        return "redirect:/dashboard";
    }

    //
    @RequestMapping("/pending")
    public String listPendingTutors(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        model.addAttribute("tutors", tutorDao.getTutorsByStatusPaginated("in progress", pageSize, offset));

        int totalItems = tutorDao.countTutorsByStatus("in progress");
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "technician/tutor/pending";
    }
    @RequestMapping("/accepted")
    public String listAcceptedTutors(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        // Recuperamos solo los tutores que ya han sido aceptados
        List<Tutor> acceptedTutors = tutorDao.getTutorsByStatusPaginated("accepted", pageSize, offset);
        model.addAttribute("tutors", acceptedTutors);

        // Lógica para calcular las páginas totales
        int totalItems = tutorDao.countTutorsByStatus("accepted");
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        // Retorna el nuevo HTML que crearemos en el Paso 2
        return "technician/tutor/accepted";
    }

    @RequestMapping(value="/accept/{dni}", method = RequestMethod.GET)
    public String confirmAcceptTutor(Model model, @PathVariable String dni) {
        model.addAttribute("tutor", tutorDao.getTutor(dni));
        return "technician/tutor/accept";
    }

    @RequestMapping(value="/accept/execute/{dni}", method = RequestMethod.GET)
    public String executeAcceptTutor(@PathVariable String dni) {
        Tutor tutor = tutorDao.getTutor(dni);
        if (tutor != null) {
            tutor.setStatus("accepted");
            tutorDao.updateTutor(tutor);
        }
        return "redirect:/tutor/pending";
    }

    @RequestMapping(value="/reject/{dni}", method = RequestMethod.GET)
    public String confirmRejectTutor(Model model, @PathVariable String dni) {
        model.addAttribute("tutor", tutorDao.getTutor(dni));
        return "technician/tutor/reject";
    }

    @RequestMapping(value="/reject/execute/{dni}", method = RequestMethod.GET)
    public String executeRejectTutor(@PathVariable String dni) {
        Tutor tutor = tutorDao.getTutor(dni);
        if (tutor != null) {
            tutor.setStatus("refused");
            tutorDao.updateTutor(tutor);
        }
        return "redirect:/tutor/pending";
    }
    @RequestMapping(value="/reject/execute/{dni}", method = RequestMethod.POST)
    public String executeRejectTutor(@PathVariable String dni, @RequestParam("rejectReason") String rejectReason) {
        Tutor tutor = tutorDao.getTutor(dni);
        if (tutor != null) {
            tutor.setStatus("refused");
            tutor.setRejectReason(rejectReason);
            tutorDao.updateTutor(tutor);
        }
        return "redirect:/tutor/pending";
    }
    @RequestMapping("/refused")
    public String listRefusedTutors(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 6;
        int offset = (page - 1) * pageSize;

        List<Tutor> refusedList = tutorDao.getTutorsByStatusPaginated("refused", pageSize, offset);
        int totalItems = tutorDao.countTutorsByStatus("refused");

        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("tutors", refusedList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "technician/tutor/refused";
    }
}
