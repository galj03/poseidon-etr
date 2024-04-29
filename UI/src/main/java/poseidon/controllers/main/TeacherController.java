package poseidon.controllers.main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import poseidon.DAO.OracleDBKurzusDAO;
import poseidon.DAO.OracleDBTantargyDAO;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

@Controller
public class TeacherController {

    private final OracleDBTantargyDAO _tantargyDAO;
    private final OracleDBKurzusDAO _kurzusDAO;

    private final AdminController _adminController;

    public TeacherController(OracleDBKurzusDAO kurzusDAO,
                             OracleDBTantargyDAO tantargyDAO,
                             AdminController adminController) {
        _tantargyDAO = tantargyDAO;
        _kurzusDAO = kurzusDAO;
        _adminController = adminController;
    }

    @GetMapping("/teacher")
    public String openTeacherPage(Model model, Principal principal) {

        model.addAttribute("tanitottTargyak", _tantargyDAO.getTeachingSubjects(principal.getName()).entrySet());
        model.addAttribute("oktatottKurzusok", _kurzusDAO.getTeachingCourses(principal.getName()).entrySet());
        model.addAttribute("allTargy", _tantargyDAO.getAll());
        return "main/teacher";
    }

    @PostMapping("/teacher/save-required-subjects")
    public String saveRequiredSubjects(@RequestParam(value = "elofeltetel", required = false) List<Integer> elofeltetelek,
                                       @RequestParam("tantargyId") Integer tantargyId) {
        if (elofeltetelek == null) {
            _tantargyDAO.removeAllRequiredSubjects(_tantargyDAO.getById(tantargyId));
            return "redirect:/teacher";
        }

        _tantargyDAO.removeAllRequiredSubjects(_tantargyDAO.getById(tantargyId));
        _tantargyDAO.saveRequiredSubjects(_tantargyDAO.getById(tantargyId),
            elofeltetelek.stream().map(
                _tantargyDAO::getById
            ).collect(Collectors.toList()));

        return "redirect:/teacher";
    }

    @PostMapping("/teacher/modify-kurzus")
    public String modifyKurzus(@RequestParam("kurzusId") Integer id,
                               @RequestParam("name") String name,
                               @RequestParam("oktato") String oktato,
                               @RequestParam("kezdesNap") String kezdesNap,
                               @RequestParam("kezdesIdo") Integer kezdesIdo,
                               @RequestParam("tantargy") Integer tantargyId,
                               @RequestParam("terem") Integer teremId,
                               @RequestParam(value = "isFelveheto", required = false) Boolean isFelveheto,
                               @RequestParam(value = "isVizsga", required = false) Boolean isVizsga,
                               Model model) {

        _adminController.saveKurzus(id, name, oktato, kezdesNap, kezdesIdo, tantargyId, teremId, isFelveheto != null, isVizsga != null, model);

        return "redirect:/teacher";
    }

    @PostMapping("/teacher/update-grade")
    public String updateGrade(@RequestParam("tantargyId") Integer tantargyId,
                              @RequestParam("ps_kod") String psKod,
                              @RequestParam("grade") String grade) {

        _kurzusDAO.saveGrade(psKod, tantargyId, "Nincs jegy".equals(grade) ? null : parseInt(grade));

        return "redirect:/teacher";
    }

}
