package poseidon.controllers.main;

import poseidon.DTO._Interfaces.IUser;
import org.springframework.security.access.prepost.PreAuthorize;
import poseidon.DAO._Interfaces.IUserDAO;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        //TODO: test by listing all users from db
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            _user = _userDAO.getByEmail(currentUserName);
        }
        model.addAttribute("_userDAO", _userDAO);
        model.addAttribute("user", _user);
        return "main/index";
    }
}
