package poseidon.controllers.main;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.dao.DataIntegrityViolationException;
import poseidon.DAO._Interfaces.*;
import poseidon.DTO.Kurzus;
import poseidon.DTO.User;
import poseidon.DTO._Interfaces.IKurzus;
import poseidon.DTO._Interfaces.IUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import poseidon.Exceptions.QueryException;
import poseidon.UserRoles;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

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
    private final BCryptPasswordEncoder _encoder;
    private IUser _user;

    private static final List<String> workDays = List.of("Hétfő", "Kedd", "Szerda", "Csütörtök", "Péntek");

    public AdminController(IUserDAO userDAO,
                           IKommentDAO kommentDAO,
                           IKurzusDAO kurzusDAO,
                           IPosztDAO posztDAO,
                           ISzakDAO szakDAO,
                           ITantargyDAO tantargyDAO,
                           ITeremDAO teremDAO,
                           BCryptPasswordEncoder encoder) {
        _userDAO = userDAO;
        _kommentDAO = kommentDAO;
        _kurzusDAO = kurzusDAO;
        _posztDAO = posztDAO;
        _szakDAO = szakDAO;
        _tantargyDAO = tantargyDAO;
        _teremDAO = teremDAO;
        _encoder = encoder;
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

    //region Update
    @PostMapping("/admin/edit-user")
    public String saveUser(
            @RequestParam("psCode") String psCode,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("password2") String password2,
            @RequestParam("szakId") Integer szakId,
            @RequestParam("role") String role,
            @RequestParam("kezdes_ev") Integer kezdEv,
            @RequestParam("vegzes_ev") Integer vegzEv,
            Model model) {

        if (!_encoder.matches(password2, password)) {
            model.addAttribute("register_error", "The two passwords should be the same!");
            return "main/auth";
        }

        var roleEnum = Objects.equals(role, "ROLE_ADMIN") ? UserRoles.ROLE_ADMIN : UserRoles.ROLE_USER;

        IUser newUser = new User()
                .setPsCode(psCode)
                .setName(name)
                .setEmail(email)
                .setPassword(_encoder.encode(password))
                .setSzakId(szakId)
                .setRole(roleEnum)
                .setKezdesEve(kezdEv)
                .setVegzesEve(vegzEv);
        _userDAO.save(newUser);

        return "redirect:/admin";
    }

    @PostMapping("/admin/confirm-register")
    public String confirmRegisterDetails(@RequestParam("psCode") String psCode,
                                         @RequestParam("name") String name,
                                         @RequestParam("email") String email,
                                         @RequestParam("password") String password,
                                         @RequestParam("szakId") Integer szakId,
                                         @RequestParam("role") String role,
                                         @RequestParam("kezdes_ev") Integer kezdEv,
                                         @RequestParam("vegzes_ev") Integer vegzEv,
                                         Model model) {
        if (kezdEv == null || kezdEv == 0) {
            model.addAttribute("error", "Starting year must be given!");
            return "main/error";
        }

        if (vegzEv == null || vegzEv == 0) {
            vegzEv = kezdEv + 3;
        }

        if (_szakDAO.getById(szakId) == null) {
            model.addAttribute("error", "Selected szak not found!");
            return "main/error";
        }
        if (kezdEv > vegzEv) {
            model.addAttribute("error", "Ending year must be bigger than starting year!");
            return "main/error";
        }
        if (vegzEv - kezdEv > 100) {
            model.addAttribute("error", "One does not simply go to university for over a century!");
            return "main/error";
        }

        var roleEnum = Objects.equals(role, "ROLE_ADMIN") ? UserRoles.ROLE_ADMIN : UserRoles.ROLE_USER;

        IUser newUser = new User()
                .setPsCode(psCode)
                .setName(name)
                .setEmail(email)
                .setPassword(_encoder.encode(password))
                .setSzakId(szakId)
                .setRole(roleEnum)
                .setKezdesEve(kezdEv)
                .setVegzesEve(vegzEv);

        model.addAttribute("confirmObj", newUser);

        return "main/admin";
    }

    @PostMapping("/admin/edit-kurzus")
    public String saveKurzus(
            @RequestParam("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam("oktato") String oktato,
            @RequestParam("kezdesNap") String kezdesNap,
            @RequestParam("kezdesIdo") Integer kezdesIdo,
            @RequestParam("tantargy") Integer tantargyId,
            @RequestParam("terem") Integer teremId,
            @RequestParam(value = "isFelveheto", required = false) Boolean isFelveheto,
            @RequestParam(value = "isVizsga", required = false) Boolean isVizsga,
            Model model) {
        if (id == null || id <= 0) {
            model.addAttribute("error", "Id must be given!");
            return "main/error";
        }

        if (name == null || name.isEmpty()) {
            model.addAttribute("error", "Name must be given!");
            return "main/error";
        }

        if (oktato == null || oktato.isEmpty()) {
            model.addAttribute("error", "Name must be given!");
            return "main/error";
        }
        if (_userDAO.getByPsCode(oktato) == null) {
            model.addAttribute("error", "Oktato not found! Please provide a valid PS-code.");
            return "main/error";
        }

        if (kezdesNap == null || kezdesNap.isEmpty()) {
            model.addAttribute("error", "Starting day must be given!");
            return "main/error";
        }
        if (!workDays.contains(kezdesNap)) {
            model.addAttribute("error", "Starting day is not a valid work day!");
            return "main/error";
        }

        if (kezdesIdo == null || kezdesIdo < 8 || kezdesIdo > 15) {
            model.addAttribute("error", "Starting time is not valid!");
            return "main/error";
        }
        if (tantargyId == null || tantargyId <= 0) {
            model.addAttribute("error", "Tantargy id must be given!");
            return "main/error";
        }
        if (_tantargyDAO.getById(tantargyId) == null) {
            model.addAttribute("error", "Tantargy not found! Please provide a valid PS-code.");
            return "main/error";
        }

        if (teremId == null || teremId <= 0) {
            model.addAttribute("error", "Terem id must be given!");
            return "main/error";
        }
        if (_teremDAO.getById(teremId) == null) {
            model.addAttribute("error", "Terem not found! Please provide a valid PS-code.");
            return "main/error";
        }

        if (isFelveheto == null) {
            isFelveheto = false;
        }
        if (isVizsga == null) {
            isVizsga = false;
        }

        IKurzus kurzus = new Kurzus()
                //.setKurzusId(id)
                .setNev(name)
                .setOktato(oktato)
                .setKezdesNapja(kezdesNap)
                .setKezdesIdopontja(kezdesIdo)
                .setTantargyId(tantargyId)
                .setTeremId(teremId)
                .setIsFelveheto(isFelveheto)
                .setIsVizsga(isVizsga);
        if (_kurzusDAO.getById(id) != null) {
            kurzus = kurzus.setKurzusId(id);
        }

        _kurzusDAO.save(kurzus);

        return "redirect:/admin";
    }
    //endregion Update

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
