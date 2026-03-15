package es.uji.ei1027.proyectoOvi.controller;

import es.uji.ei1027.proyectoOvi.dao.NegotiationDao;

@Controller
@RequestMapping("/negotiation")
public class NegotiationController {
    private NegotiationDao negotiationDao;

    @Autowired
    public void setNegotiationDao(NegotiationDao negotiationDao){
        this.negotiationDao = negotiationDao;
    }

    @RequestMapping("/list")
    public String listNegotiations(Model model){
        model.addAttribute("negotiations", negotiationDao.getNegotiations());
        return "negotiation/list";
    }
}
