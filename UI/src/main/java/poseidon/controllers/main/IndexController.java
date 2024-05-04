package poseidon.controllers.main;

import poseidon.DAO._Interfaces.IKommentDAO;
import poseidon.DAO._Interfaces.IPosztDAO;
import poseidon.DAO._Interfaces.ISzakDAO;
import poseidon.DTO.Komment;
import poseidon.DTO.Kurzus;
import poseidon.DTO.Poszt;
import poseidon.DTO._Interfaces.IKomment;
import poseidon.DTO._Interfaces.IKurzus;
import poseidon.DTO._Interfaces.IPoszt;
import poseidon.DTO._Interfaces.IUser;
import org.springframework.security.access.prepost.PreAuthorize;
import poseidon.DAO._Interfaces.IUserDAO;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static poseidon.Constants.*;

@Controller
@PreAuthorize("authentication.principal.username != null")
public class IndexController {
    private final IUserDAO _userDAO;
    private final ISzakDAO _szakDAO;
    private final IPosztDAO _posztDAO;
    private final IKommentDAO _kommentDAO;
    private IUser _user;

    public IndexController(IUserDAO userDAO,
                           ISzakDAO szakDAO,
                           IPosztDAO posztDAO,
                           IKommentDAO kommentDAO) {
        _userDAO = userDAO;
        _szakDAO = szakDAO;
        _posztDAO = posztDAO;
        _kommentDAO = kommentDAO;
    }

    @GetMapping("/")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            _user = _userDAO.getByPsCode(currentUserName);
        }
        model.addAttribute("_userDAO", _userDAO);
        model.addAttribute("user", _user);

        // timetable
        var userCourses = _userDAO.currentCourses(_user);
        var splittedCourses = splitCoursesForTimetable(userCourses);
        model.addAttribute("userCourses", splittedCourses);

        var szak = _szakDAO.getById(_user.getSzakId());
        model.addAttribute("szak", szak);

        // degree progress
        var allRequiredCredits = _szakDAO.getRequiredClassesCount(szak);
        var allCompletedCredits = _userDAO.finishedCoursesCount(_user);
        model.addAttribute("requiredCredits", allRequiredCredits);
        model.addAttribute("completedCredits", allCompletedCredits);
        model.addAttribute("completionRate", (float) allCompletedCredits / allRequiredCredits);

        // averages for current user
        var avgAll = _szakDAO.getAveragesForAll(szak);
        model.addAttribute("userNormalAvg", avgAll.get(_user.getPsCode()).toString());

        // averages for all users
        var normalAvg = avgAll.values().stream()
                .mapToDouble(a -> a)
                .average();
        model.addAttribute("allNormalAvg", normalAvg.isPresent() ? normalAvg.getAsDouble() : 0);
        var completedCoursesForSzak = _szakDAO.finishedCoursesCountForEvfolyam(szak, _user.getKezdesEve());
        model.addAttribute("completedCoursesForSzak", completedCoursesForSzak);
        var usersCount = _szakDAO.getAllUsersForSzak(szak).size();
        model.addAttribute("averageCoursesForUserInSzak", (float) completedCoursesForSzak / usersCount);

        // graduates
        var graduates = _szakDAO.getAllYearlyGraduatesForSzak(szak, LocalDateTime.now().getYear());
        model.addAttribute("graduateCount", graduates.size());
        var avgGraduates = _userDAO.graduatesAverage(szak, LocalDateTime.now().getYear());
        var gradAvg = avgGraduates.values().stream()
                .mapToDouble(a -> a)
                .average();
        model.addAttribute("gradAvg", gradAvg.isPresent() ? gradAvg.getAsDouble() : 0);

        // graduates
        var graduates = _szakDAO.getAllYearlyGraduatesForSzak(szak, LocalDateTime.now().getYear());
        model.addAttribute("graduateCount", graduates.size());
        var avgGraduates =_userDAO.graduatesAverage(szak, LocalDateTime.now().getYear());
        var gradAvg = avgGraduates.values().stream()
                .mapToDouble(a -> a)
                .average();
        model.addAttribute("gradAvg", gradAvg.isPresent() ? gradAvg.getAsDouble() : 0);

        return "main/index";
    }

    @GetMapping("/forum")
    public String forum(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            _user = _userDAO.getByPsCode(currentUserName);
        }
        model.addAttribute("currentUser", _user);
        model.addAttribute("kommentek", _kommentDAO.getAll());
        model.addAttribute("posztok", _posztDAO.getAll());
        return "main/forum";
    }

    @PostMapping("/forum/add-poszt")
    public String savePoszt(
            @RequestParam("tartalom") String tartalom,
            Principal principal,
            Model model) {
        if (tartalom == null || tartalom.isEmpty()) {
            model.addAttribute("error", "Tartalom must be given!");
            return "main/error";
        }
        IPoszt poszt = new Poszt()
                .setPsCode(principal.getName())
                .setTartalom(tartalom)
                .setIsForBoard(false);

        _posztDAO.save(poszt);

        return "redirect:/forum";
    }

    @PostMapping("/forum/add-komment")
    public String saveKomment(
            @RequestParam("posztId") Integer posztId,
            @RequestParam("tartalom") String tartalom,
            Principal principal,
            Model model) {
        if (posztId == null || posztId <= 0) {
            model.addAttribute("error", "Post id is invalid!");
            return "main/error";
        }
        if (_posztDAO.getById(posztId) == null) {
            model.addAttribute("error", "Post with given identifier not found!");
            return "main/error";
        }


        if (tartalom == null || tartalom.isEmpty()) {
            model.addAttribute("error", "Tartalom must be given!");
            return "main/error";
        }

        IKomment komment = new Komment()
                .setPosztId(posztId)
                .setPsCode(principal.getName())
                .setTartalom(tartalom);

        _kommentDAO.save(komment);

        return "redirect:/forum";
    }

    private List<List<IKurzus>> splitCoursesForTimetable(List<IKurzus> courses) {
        var result = new ArrayList<List<IKurzus>>(15 - 8 + 1);
        for (int i = 8; i < 16; i++) {
            var l = new ArrayList<IKurzus>(5);
            for (int j = 0; j < 5; j++) {
                l.add(new Kurzus());
            }
            result.add(l);
        }

        for (var course : courses) {
            var index = 0;
            switch (course.getKezdesNapja()) {
                case TUESDAY -> index = 1;
                case WEDNESDAY -> index = 2;
                case THURSDAY -> index = 3;
                case FRIDAY -> index = 4;
                default -> index = 0;
            }
            result.get(course.getKezdesIdopontja() - 8).set(index, course);
        }

        return result;
    }
}
