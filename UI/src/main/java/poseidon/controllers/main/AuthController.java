package poseidon.controllers.main;

import poseidon.DAO._Interfaces.IPosztDAO;
import poseidon.DAO._Interfaces.IUserDAO;
import poseidon.DTO.User;
import poseidon.DTO._Interfaces.IPoszt;
import poseidon.DTO._Interfaces.IUser;
import poseidon.Exceptions.QueryException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
public class AuthController {
    private IUserDAO _userDAO;
    private IPosztDAO _posztDAO;
    private BCryptPasswordEncoder _encoder;

    public AuthController(IUserDAO userDAO, BCryptPasswordEncoder encoder, IPosztDAO posztDAO) {
        _userDAO = userDAO;
        _encoder = encoder;
        _posztDAO = posztDAO;
    }

    @GetMapping("/auth")
    public String auth(@CookieValue(value = "isDarkModeEnabled",
            defaultValue = "not found") String isDarkModeEnabled, HttpServletResponse response, Model model, Principal principal) {
        Cookie cookie = new Cookie("isDarkModeEnabled", "false");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setPath("/");

        Iterable<IPoszt> posts = _posztDAO.getAll();

        Map<String, String> user_content = new HashMap<>();

        for (IPoszt post: posts) {
            String author = "";
            String content = post.getTartalom();
            IUser user = _userDAO.getByPsCode(post.getPsCode());
            author += user.getName() + " (" + post.getPsCode() + "):";
            user_content.put(author, content);
        }

        model.addAttribute("posts", user_content);

        if (isDarkModeEnabled == "not found") {
            response.addCookie(cookie);
        }
        return "main/auth";
    }

    @GetMapping("/logout")
    public String logout(Model model) {
        return "redirect:/auth";
    }

    //TODO: consider moving this elsewhere
    @PostMapping("/auth/register")
    public String registerUser(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("password2") String password2,
            Model model) {
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

        if (existingUserByUsername != null) {
            model.addAttribute("register_error", "Username already in use!");
            return "main/auth";
        }
        if (existingUserByEmail != null) {
            model.addAttribute("register_error", "E-mail already in use!");
            return "main/auth";
        }

        if (!Objects.equals(password, password2)) {
            model.addAttribute("register_error", "The two passwords should be the same!");
            return "main/auth";
        }

        //ez tenyleg kell? :D
        //TODO: jelszó tartalmazzon az angol abc-ből kis- és nagybetűt, számot, és speciális karaktert, valamint legalább 6 karakter hosszú legyen.

//        IUser newUser = new User()
//                .setName(username)
//                .setEmail(email)
//                .setPassword(_encoder.encode(password));
//        _userDAO.save(newUser);

        return "redirect:/auth?success=true";
    }
}