package poseidon.controllers.main;

import poseidon.DAO._Interfaces.ISzakDAO;
import poseidon.DTO.Kurzus;
import poseidon.DTO._Interfaces.IKurzus;
import poseidon.DTO._Interfaces.IUser;
import org.springframework.security.access.prepost.PreAuthorize;
import poseidon.DAO._Interfaces.IUserDAO;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static poseidon.Constants.*;

@Controller
@PreAuthorize("authentication.principal.username != null")
public class IndexController {
    private final IUserDAO _userDAO;
    private final ISzakDAO _szakDAO;
    private IUser _user;

    public IndexController(IUserDAO userDAO, ISzakDAO szakDAO) {
        _userDAO = userDAO;
        _szakDAO = szakDAO;
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

        // degree progress
        var allRequiredCredits = _szakDAO.getRequiredClassesCount(szak);
        var allCompletedCredits = _userDAO.finishedCoursesCount(_user);
        model.addAttribute("requiredCredits", allRequiredCredits);
        model.addAttribute("completedCredits", allCompletedCredits);
        model.addAttribute("completionRate", (float)allCompletedCredits/ allRequiredCredits);

        // averages for current user
        var avgAll = _szakDAO.getAveragesForAll(szak);
        model.addAttribute("userNormalAvg", avgAll.get(_user.getPsCode()).toString());

        // averages for all users
        model.addAttribute("allNormalAvg", avgAll.values().stream()
                                                                      .mapToDouble(a -> a)
                                                                      .average().getAsDouble());
        var completedCoursesForSzak = _szakDAO.finishedCoursesCountForSzak(szak);
        model.addAttribute("completedCoursesForSzak", completedCoursesForSzak);
        var usersCount = _szakDAO.getAllUsersForSzak(szak).size();
        model.addAttribute("averageCoursesForUserInSzak", (float)completedCoursesForSzak/usersCount);

        return "main/index";
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
            switch (course.getKezdesNapja()){
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
