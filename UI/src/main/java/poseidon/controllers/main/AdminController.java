package poseidon.controllers.main;

import org.springframework.dao.DataIntegrityViolationException;
import poseidon.DAO._Interfaces.*;
import poseidon.DTO._Interfaces.IUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import poseidon.Exceptions.QueryException;

import java.sql.SQLException;

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    private final IUserDAO _userDAO;
    private final IKommentDAO _kommentDAO;
    private final IKurzusDAO _kurzusDAO;
    private final IPosztDAO _posztDAO;
    private final ISzakDAO _szakDAO;
    private final ITantargyDAO _tantargyDAO;
    private final ITeremDAO _teremDAO;
    private IUser _user;

    public AdminController(IUserDAO userDAO,
                           IKommentDAO kommentDAO,
                           IKurzusDAO kurzusDAO,
                           IPosztDAO posztDAO,
                           ISzakDAO szakDAO,
                           ITantargyDAO tantargyDAO,
                           ITeremDAO teremDAO) {
        _userDAO = userDAO;
        _kommentDAO = kommentDAO;
        _kurzusDAO = kurzusDAO;
        _posztDAO = posztDAO;
        _szakDAO = szakDAO;
        _tantargyDAO = tantargyDAO;
        _teremDAO = teremDAO;
    }

    @GetMapping("/admin")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            _user = _userDAO.getByPsCode(currentUserName);
        }
        model.addAttribute("currentUser", _user);
        model.addAttribute("users", _userDAO.getAllUsers());
        model.addAttribute("kommentek", _kommentDAO.getAll());
        model.addAttribute("kurzusok", _kurzusDAO.getAll());
        model.addAttribute("posztok", _posztDAO.getAll());
        model.addAttribute("szakok", _szakDAO.getAll());
        model.addAttribute("tantargyak", _tantargyDAO.getAll());
        model.addAttribute("termek", _teremDAO.getAll());
        return "main/admin";
    }

    //region Delete
    @PostMapping("/admin/delete-user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteUser(@RequestParam("key") String key, Model model) {
        try {
            _userDAO.remove(_userDAO.getByPsCode(key));
        } catch (QueryException e) {
            model.addAttribute("error", "Could not remove user from database." + e.getMessage());
            return "main/error";
        } catch (SQLException e) {
            model.addAttribute("error", "Could not remove user from database. Check NOT NULL. " + e.getMessage());
            return "main/error";
        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/delete-kurzus")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteKurzus(@RequestParam("key") Integer key, Model model) {
        try {
            _kurzusDAO.remove(_kurzusDAO.getById(key));
        } catch (QueryException e) {
            model.addAttribute("error", "Could not remove user from database." + e.getMessage());
            return "main/error";
        } //catch (SQLException e) {
//            model.addAttribute("error", "Could not remove user from database. Check NOT NULL. " + e.getMessage());
//            return "main/error";
//        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/delete-szak")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteSzak(@RequestParam("key") Integer key, Model model) {
        try {
            _szakDAO.remove(_szakDAO.getById(key));
        } catch (QueryException e) {
            model.addAttribute("error", "Could not remove user from database." + e.getMessage());
            return "main/error";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "A törölni kívánt rekordhoz egy része még szerepel értékként más táblában!" + e.getMessage());
            return "main/error";
        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/delete-tantargy")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteTantargy(@RequestParam("key") Integer key, Model model) {
        try {
            _tantargyDAO.remove(_tantargyDAO.getById(key));
        } catch (QueryException e) {
            model.addAttribute("error", "Could not remove user from database." + e.getMessage());
            return "main/error";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "A törölni kívánt rekordhoz egy része még szerepel értékként más táblában! " + e.getMessage());
            return "main/error";
        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/delete-terem")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteTerem(@RequestParam("key") Integer key, Model model) {
        try {
            _teremDAO.remove(_teremDAO.getById(key));
        } catch (QueryException e) {
            model.addAttribute("error", "Could not remove user from database." + e.getMessage());
            return "main/error";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "A törölni kívánt rekordhoz egy része még szerepel értékként más táblában! " + e.getMessage());
            return "main/error";
        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/delete-poszt")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deletePoszt(@RequestParam("key") Integer key, Model model) {
        try {
            _posztDAO.remove(_posztDAO.getById(key));
        } catch (QueryException e) {
            model.addAttribute("error", "Could not remove user from database." + e.getMessage());
            return "main/error";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "A törölni kívánt rekordhoz egy része még szerepel értékként más táblában! " + e.getMessage());
            return "main/error";
        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/delete-komment")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteKomment(@RequestParam("key") Integer key, Model model) {
        try {
            _kommentDAO.remove(_kommentDAO.getById(key));
        } catch (QueryException e) {
            model.addAttribute("error", "Could not remove user from database." + e.getMessage());
            return "main/error";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "A törölni kívánt rekordhoz egy része még szerepel értékként más táblában! " + e.getMessage());
            return "main/error";
        }

        return "redirect:/admin";
    }
    //endregion Delete
}
