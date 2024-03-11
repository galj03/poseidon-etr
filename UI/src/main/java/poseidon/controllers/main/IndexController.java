package poseidon.controllers.main;

import poseidon.DTO._Interfaces.IUser;
import poseidon.Exceptions.QueryException;
import org.springframework.security.access.prepost.PreAuthorize;
import poseidon.DAO._Interfaces.IUserDAO;
import poseidon.DAO._Interfaces.IWorldDAO;
import poseidon.DTO._Interfaces.IWorld;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@PreAuthorize("authentication.principal.username != null")
public class IndexController {
    private IWorldDAO _worldDAO;
    private IUserDAO _userDAO;
    private IUser _user;

    public IndexController(IWorldDAO worldDAO, IUserDAO userDAO) {
        _worldDAO = worldDAO;
        _userDAO = userDAO;
    }

    @GetMapping("/")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            _user = _userDAO.getBySearchText(currentUserName);
        }
        List<IWorld> invites = _worldDAO.getAllByInvited(_user);
        model.addAttribute("invites", invites);
        model.addAttribute("_userDAO", _userDAO);
        List<IWorld> worlds = _worldDAO.getAllByJoined(_user);
        worlds.addAll(_worldDAO.getAllByOwner(_user));
        model.addAttribute("worlds", worlds);
        model.addAttribute("user", _user);
        return "main/index";
    }

    @GetMapping(value = "/accept")
    public String accept(@RequestParam(value = "acceptButton") Integer id, Model model) {
        if (id == null || id < 0) {
            model.addAttribute("error", "Cannot accept invite: \nWorld id is invalid!");
            return "main/error";
        }

        IWorld invite;
        try {
            invite = _worldDAO.getById(id);
        } catch (QueryException e) {
            model.addAttribute("error", "Cannot accept invite: \nCould not find world with given id in the database.");
            return "main/error";
        }
        if (invite == null) {
            model.addAttribute("error", "Cannot accept invite: \nCould not find world with given id in the database.");
            return "main/error";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            _user = _userDAO.getBySearchText(currentUserName);
        }

        _userDAO.acceptInvite(_user, invite);
        return "redirect:/";
    }

    @GetMapping(value = "/decline")
    public String decline(@RequestParam(value = "declineButton") Integer id, Model model) {
        if (id == null || id < 0) {
            model.addAttribute("error", "Cannot decline invite: \nWorld id is invalid!");
            return "main/error";
        }

        IWorld invite;
        try {
            invite = _worldDAO.getById(id);
        } catch (QueryException e) {
            model.addAttribute("error", "Cannot decline invite: \nCould not find world with given id in the database.");
            return "main/error";
        }
        if (invite == null) {
            model.addAttribute("error", "Cannot decline invite: \nCould not find world with given id in the database.");
            return "main/error";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            _user = _userDAO.getBySearchText(currentUserName);
        }

        _userDAO.declineInvite(_user, invite);
        return "redirect:/";
    }
}
