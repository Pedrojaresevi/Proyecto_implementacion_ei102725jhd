package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.AssignmentRequestDao;
import es.uji.ei1027.proyectoOvi.models.AssignmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/assignmentRequest")
public class AssignmentRequestController {
    private AssignmentRequestDao assignmentRequestDao;

    @Autowired
    public void setAssignmentRequestDao(AssignmentRequestDao assignmentRequestDao){
        this.assignmentRequestDao=assignmentRequestDao;
    }

    @RequestMapping("/list")
    public String listAssignmentRequests(Model model){
        model.addAttribute("assignmentRequests", assignmentRequestDao.getAssignmentRequests());
        return "assignmentRequest/list";
    }

    @RequestMapping(value="/add")
    public String addNegotiation(Model model) {
        model.addAttribute("assignmentRequest", new AssignmentRequest());
        return "assignmentRequest/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("assignmentRequest") AssignmentRequest assignmentRequest,
                                   BindingResult bindingResult) {
        AssignmentRequestValidator assignmentRequestValidator = new AssignmentRequestValidator();
        assignmentRequestValidator.validate(assignmentRequest, bindingResult);

        if (bindingResult.hasErrors()) {
            return "assignmentRequest/add";
        }

        try {
            assignmentRequestDao.addAssignmentRequest(assignmentRequest);
        } catch (DuplicateKeyException e) {
            bindingResult.rejectValue("request_Id", "duplicat",
                    "Ya existe una solicitud con este ID");
            return "assignmentRequest/add";
        }

        return "redirect:list";
    }

    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editNegotiation(Model model, @PathVariable String id) {
        model.addAttribute("assignmentRequest", assignmentRequestDao.getAssignmentRequest(id));
        return "assignmentRequest/update";
    }

    @RequestMapping(value="/update", method=RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("assignmentRequest") AssignmentRequest assignmentRequest,
                                      BindingResult bindingResult, Model model) {
        AssignmentRequestValidator assignmentRequestValidator = new AssignmentRequestValidator();
        assignmentRequestValidator.validate(assignmentRequest, bindingResult);

        if (bindingResult.hasErrors()) {
            return "assignmentRequest/update";
        }
        assignmentRequestDao.updateAssignmentRequest(assignmentRequest);

        return "redirect:list";
    }

    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable String id) {
        assignmentRequestDao.deleteAssignmentRequest(id);
        return "redirect:../list";
    }
}
