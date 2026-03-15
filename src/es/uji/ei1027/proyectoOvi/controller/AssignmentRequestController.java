package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.AssignmentRequestDao;

@Controller
@RequestMapping("/assignmentRequest")
public class AssignmentRequestController {
    private AssignmentRequestDao assignmentRequestDao;

    @AutoWired
    public void setAssignmentRequestDao(AssignmentRequestDao assignmentRequestDao){
        this.assignmentRequestDao=assignmentRequestDao;
    }

    @RequestMapping("/list")
    public String listAssignmentRequests(Model model){
        model.addAttribute("assignmentRequests", assignmentRequestDao.getAssignmentRequests());
        return "assignmentRequest/list";
    }
}
