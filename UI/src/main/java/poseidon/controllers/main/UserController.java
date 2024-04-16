package poseidon.controllers.main;

import poseidon.DAO._Interfaces.IUserDAO;
import poseidon.DTO._Interfaces.IUser;
import poseidon.Exceptions.QueryException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.Objects;

@Controller
@PreAuthorize("authentication.principal.username != null")
public class UserController {

    private IUserDAO _userDAO;
    private IUser _user;
    private BCryptPasswordEncoder _encoder;

    public UserController(IUserDAO userDAO, BCryptPasswordEncoder encoder) {
        _userDAO = userDAO;
        _encoder = encoder;
    }

    @GetMapping("/change-theme")
    public String changeTheme(@CookieValue(value = "isDarkModeEnabled",
            defaultValue = "not found") String isDarkModeEnabled,
                              HttpServletResponse response,
                              Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            _user = _userDAO.getByEmail(currentUserName);
        }

        String newValue = Objects.equals(isDarkModeEnabled, "true") ? "false" : "true";
        Cookie cookie = new Cookie("isDarkModeEnabled", newValue);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setPath("/");

        response.addCookie(cookie);

        return "redirect:/";
    }

    @GetMapping("/user")
    public String user(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            _user = _userDAO.getByEmail(currentUserName);
        }
        model.addAttribute("user", _user);
        return "main/user";
    }

    @GetMapping("/edit-user")
    public String editUser(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            _user = _userDAO.getByEmail(currentUserName);
        }
        model.addAttribute("user", _user);
        return "main/edituser";
    }

    @PostMapping("/edit-user/process")
    public String processEditUser(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("new_password") String new_password,
            @RequestParam("new_password2") String new_password2,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            _user = _userDAO.getByEmail(currentUserName);
        }
        model.addAttribute("user", _user);

        IUser existingUserByUsername;
        IUser existingUserByEmail;

        try {
            existingUserByUsername = _userDAO.getByEmail(username);
        } catch (QueryException e) {
            existingUserByUsername = null;
        }
        try {
            existingUserByEmail = _userDAO.getByEmail(email);
        } catch (QueryException e) {
            existingUserByEmail = null;
        }

        if (existingUserByUsername != null && !Objects.equals(_user.getUsername(), username)) {
            model.addAttribute("edit_error", "Username already in use!");
            return "main/edituser";
        }
        if (existingUserByEmail != null && !Objects.equals(_user.getEmail(), email)) {
            model.addAttribute("edit_error", "E-mail already in use!");
            return "main/edituser";
        }


        if ((new_password.trim().equals("") ^ new_password2.trim().equals(""))) {
            model.addAttribute("edit_error", "You have to give the new password twice!");
            return "main/edituser";
        }

        if (!Objects.equals(new_password, new_password2)) {
            model.addAttribute("edit_error", "The two passwords should be the same!");
            return "main/edituser";
        }

        if (!new_password.trim().equals("")) {
            _user.setPassword(_encoder.encode(new_password));
            _user = _userDAO.save(_user);
        }

        if (!email.trim().equals("")) {
            _user.setEmail(email);
            _user = _userDAO.save(_user);
        }

        if (!Objects.equals(_user.getUsername(), username) && !username.equals("")) {
            _user.setName(username);
            _user = _userDAO.save(_user);
            return "redirect:/auth";
        }

        return "main/user";
    }

    @GetMapping("/edit-user/delete")
    public String deleteUser(Model model) {
        try {
            _userDAO.remove(_user);
        } catch (QueryException e) {
            model.addAttribute("edit_error", "Could not remove user from database.");
            return "main/edituser";
        } catch (SQLException e) {
            model.addAttribute("edit_error", "Could not remove user from database. Check NOT NULL " + e.getMessage());
            return "main/edituser";
        }

        return "redirect:/auth";
    }
}