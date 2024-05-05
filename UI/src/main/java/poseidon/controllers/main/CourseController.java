package poseidon.controllers.main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import poseidon.DAO._Interfaces.*;
import poseidon.DTO._Interfaces.IKurzus;
import poseidon.DTO._Interfaces.IKurzusData;
import poseidon.DTO._Interfaces.ITantargyData;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class CourseController {
    private IUserDAO _userDAO;
    private IKurzusDAO _kurzusDAO;
    private ITantargyDAO _tantargyDAO;
    private ISzakDAO _szakDAO;

    public CourseController(IUserDAO userDAO, IKurzusDAO kurzusDAO, ITantargyDAO tantargyDAO, ISzakDAO szakDAO) {
        this._userDAO = userDAO;
        this._kurzusDAO = kurzusDAO;
        this._tantargyDAO = tantargyDAO;
        this._szakDAO = szakDAO;
    }

    @GetMapping("/subjectlisting")
    public String subjectListing(Model model, Principal principal) {
        String loggedInUserPsCode = principal.getName();
        Integer szakID = _userDAO.getByPsCode(loggedInUserPsCode).getSzakId();

        List<ITantargyData> tantargyak = _szakDAO.kotelezokGetAll(loggedInUserPsCode, szakID);
        model.addAttribute("tantargyak", tantargyak);
        return "main/subjectlisting";
    }

    @GetMapping("/subjectlisting/courselisting")
    public String courseListing(@RequestParam("tantargyId") Integer tantargyId, Model model, Principal principal) {
        List<IKurzusData> kurzusok = _kurzusDAO.getAllCoursesOfSubject(tantargyId, principal.getName());
        String tantargyNev = _tantargyDAO.getById(tantargyId).getNev();

        model.addAttribute("kurzusok", kurzusok);
        model.addAttribute("tantargyNev", tantargyNev);
        return "main/courselisting";
    }

    @GetMapping("/enrollcourse")
    public String enrollCourse(@RequestParam("kurzusId") Integer kurzusId, Model model, Principal principal) {
        Set<Integer> prerequisities = _kurzusDAO.getAllPrerequisities(_kurzusDAO.getTantargyIdByKurzusId(kurzusId));
        Set<Integer> completed = _kurzusDAO.getAllCompletedSubjectsByUser(principal.getName());

        if (!_kurzusDAO.checkPrerequisitesCompleted(prerequisities, completed).isEmpty()) {
            model.addAttribute("error", "Nem teljesülnek az előfeltételek! Ellenőrizd a tantervet! A tantárgyak ID-jai: ");
            model.addAttribute("notCompletedSubjects", _kurzusDAO.checkPrerequisitesCompleted(prerequisities, completed));
            return "main/error";
        }

        _kurzusDAO.enrollCourse(kurzusId, principal.getName());
        return "redirect:/subjectlisting";
    }

    @GetMapping("/leavecourse")
    public String leaveCourse(@RequestParam("kurzusId") Integer kurzusId, Model model, Principal principal) {
        _kurzusDAO.removeFromCourse(principal.getName(), kurzusId);
        return "redirect:/subjectlisting";
    }
}
