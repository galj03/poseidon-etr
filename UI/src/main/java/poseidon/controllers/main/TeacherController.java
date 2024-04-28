package poseidon.controllers.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import poseidon.Constants;
import poseidon.DAO.BaseDAO;
import poseidon.DAO.OracleDBKurzusDAO;
import poseidon.DAO.OracleDBTantargyDAO;
import poseidon.DAO._Interfaces.IKurzusDAO;
import poseidon.DAO._Interfaces.ITantargyDAO;
import poseidon.DTO.Kurzus;
import poseidon.DTO.Tantargy;
import poseidon.DTO.User;
import poseidon.DTO._Interfaces.IKurzus;
import poseidon.DTO._Interfaces.ITantargy;
import poseidon.DTO._Interfaces.IUser;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TeacherController {

    private final OracleDBTantargyDAO _tantargyDAO;
    private final OracleDBKurzusDAO _kurzusDAO;

    public TeacherController(OracleDBKurzusDAO kurzusDAO,
                             OracleDBTantargyDAO tantargyDAO) {
        _tantargyDAO = tantargyDAO;
        _kurzusDAO = kurzusDAO;
    }

    @GetMapping("/teacher")
    public String openTeacherPage(Model model, Principal principal) {

        model.addAttribute("tanitottTargyak", _tantargyDAO.getTeachingSubjects(principal.getName()).entrySet());
        model.addAttribute("oktatottKurzusok", _kurzusDAO.getTeachingCourses(principal.getName()).entrySet());
        model.addAttribute("allTargy", _tantargyDAO.getAll());
        return "main/teacher";
    }

}
