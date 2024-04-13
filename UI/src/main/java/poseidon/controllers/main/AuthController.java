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
            defaultValue = "not found") String isDarkModeEnabled, HttpServletResponse response, Model model) {
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
}