package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.AssignmentRequestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
