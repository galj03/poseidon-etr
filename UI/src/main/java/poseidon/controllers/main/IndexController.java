package poseidon.controllers.main;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static poseidon.Constants.*;

@Controller
@PreAuthorize("authentication.principal.username != null")
public class IndexController {
    private IUserDAO _userDAO;
    private IUser _user;

    public IndexController(IUserDAO userDAO) {
        _userDAO = userDAO;
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

        var userCourses = _userDAO.currentCourses(_user);
        var splittedCourses = splitList(userCourses);
        model.addAttribute("userCourses", splittedCourses);

        return "main/index";
    }

    private List<List<IKurzus>> splitList(List<IKurzus> courses) {
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
